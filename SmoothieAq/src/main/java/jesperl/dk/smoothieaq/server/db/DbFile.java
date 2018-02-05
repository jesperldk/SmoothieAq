package jesperl.dk.smoothieaq.server.db;

import static jesperl.dk.smoothieaq.util.shared.error.Errors.*;

import java.nio.*;
import java.nio.channels.*;
import java.nio.file.*;
import java.util.*;
import java.util.logging.*;

import jesperl.dk.smoothieaq.shared.model.db.*;
import jesperl.dk.smoothieaq.util.shared.error.*;
import rx.Observable;
import rx.Observer;
import rx.observables.*;

public class  DbFile<DBO extends DbObject> {
	private final static Logger log = Logger.getLogger(DbFile.class .getName());
//	
	public static final int bufSize = 2*1024; // must be greater than the largest object size
	public static final String header = "SmoothieAq 00.01";
	public static final int headerSize = header.getBytes().length;
	
	private Class<DBO> cls;
	private Path path;
	private boolean fixedDboSize;
	private int dboSize = 0;
	private DbContext context;
	private boolean withStamp;
	private boolean withId;
	private boolean withParrentId;
	
	private DbFile() {}
	
	static public <D extends DbObject> DbFile<D> create(Class<D> cls, boolean fixedDboSize, DbContext context) {
		return create(cls, fixedDboSize, FileSystems.getDefault().getPath(context.getDbRoot(),cls.getSimpleName()+".smoothieAqDb"), context);
	}
	
	static public <D extends DbObject> DbFile<D> create(Class<D> cls, boolean fixedDboSize, Path path, DbContext context) {
		DbFile<D> dbFile = new DbFile<>();
		dbFile.cls = cls;
		dbFile.fixedDboSize = fixedDboSize;
		dbFile.path = path;
		dbFile.context = context;
		dbFile.withStamp = false;
		dbFile.withId = false;
		dbFile.withParrentId = false;
		if (DbWithStamp.class.isAssignableFrom(cls)) {
			dbFile.withStamp = true;
			if (DbWithId.class.isAssignableFrom(cls)) dbFile.withId = true;
			else if (DbWithParrentId.class.isAssignableFrom(cls)) dbFile.withParrentId = true;
		}
		return dbFile;
	}
//	
	public Observable<DBO> stream() {
		return stream(0,0,-1);
	}
	
	private static class StreamState {
		FileChannel	channel = null;
		int p;
		MappedByteBuffer map;
		int[] lookbackPs;
		boolean scanning;
		int nNewer;
		int nOlder;
		int lookbackPp = 0;
	}
	public Observable<DBO> stream(long fromNewestNotIncl, int countNewer, int countOlder) {
		assert fromNewestNotIncl == 0 || withStamp;
		long from = (fromNewestNotIncl == 0) ? Long.MAX_VALUE : fromNewestNotIncl;
		return Observable.create(SyncOnSubscribe.createSingleState(
			() -> {
				StreamState s = new StreamState();
				try { 
					s.channel = FileChannel.open(path ,StandardOpenOption.READ);
					if (s.channel.size() > Integer.MAX_VALUE) throw error(log, 110104, Severity.fatal,"File to large for memory map {0}", path);
					s.p = (int) s.channel.size();
					s.map = s.channel.map(FileChannel.MapMode.READ_ONLY, 0, s.p);
					s.nNewer = countNewer;
					s.nOlder = countOlder;
					s.lookbackPs = new int[s.nNewer];
					s.scanning = fromNewestNotIncl != 0;
				} catch (NoSuchFileException e) {
					throw error(log, e, 110101, Severity.info, "File not found {0} - {1}", path, e.toString());
				} catch (Throwable e) {
					if (s.channel != null) doNoException(() -> s.channel.close());
					throw error(log, e, 110102, Severity.fatal, "Error opening file {0} - {1}", path, e.toString());
				}
				return s;
			}, 
			(s,o) -> {
				try {
					if (s.lookbackPp >= 0) {
						// TODO
					}
					while (true) { // would have been more natural with a do-while, but my Eclipse dies with that!?
						if (s.p <= headerSize) {
							o.onCompleted();
							s.scanning = false;
						} else {
							s.map.position(s.p-2);
							int dboSize = s.map.getShort();
							if (fixedDboSize && this.dboSize == 0) this.dboSize = dboSize;
							s.map.position(s.p = s.p-2-dboSize);
							DBO dbo = deserialize(s);
							o.onNext(dbo);
						}
						break;
					} 
				} catch (Throwable e) {
					error(log, e, 110103, Severity.fatal, "Error reading file {0} - {1}", path, e.toString());
					o.onError(e);
				}
			}, 
			s -> {
				if (s.channel != null) doNoException(() -> s.channel.close());
			}
		));
	}

	private DBO deserialize(StreamState s) throws InstantiationException, IllegalAccessException {
		DBO dbo = cls.newInstance();
		if (withStamp) {
			((DbWithStamp) dbo).stamp = s.map.getLong();
			if (withId)
				((DbWithId)dbo).id = s.map.getInt();
			else if (withParrentId)
				((DbWithParrentId)dbo).id = s.map.getInt();
		}
		dbo.deserialize(s.map.get(), s.map, context);
		log.fine("desialized "+cls.getSimpleName());
		return dbo;
	}
	
	public Observer<List<DBO>> drain() {
		return new Observer<List<DBO>>() {
			FileChannel channel = null;
			ByteBuffer buf = ByteBuffer.allocateDirect(bufSize);

			@Override public void onCompleted() {
				doGuarded(() -> { if (channel != null) channel.close(); });
			}
			@Override public void onError(Throwable e) { 
				log.log(Level.WARNING,"onError on "+cls.getSimpleName()+" - "+e.toString(),e);
				onCompleted();
			}

			@Override public void onNext(List<DBO> t) {
				if (channel == null) openAppend();
				t.forEach(dbo -> {
					if (dboSize > 0 && buf.remaining() < dboSize) writeBuf();
					int p = buf.position();
					if (dbo instanceof DbWithStamp) {
						buf.putLong(((DbWithStamp) dbo).stamp);
						if (dbo instanceof DbWithId)
							buf.putInt(((DbWithId)dbo).id);
						else if (dbo instanceof DbWithParrentId)
							buf.putInt(((DbWithParrentId)dbo).id);
					}
					dbo.serialize(buf, context);
					log.fine("serialized "+dbo.getClass().getSimpleName());
					buf.putShort((short) (buf.position()-p));
					if (fixedDboSize && dboSize == 0) dboSize = buf.position()-p;
				});
				writeBuf();
			}

			private void openAppend() {
				doGuarded(() -> {
					channel = FileChannel.open(path, StandardOpenOption.APPEND,StandardOpenOption.WRITE,StandardOpenOption.CREATE);
					if (channel.size() == 0) {
						buf.put(header.getBytes());
						writeBuf();
					}
				});
			}
			private void writeBuf() {
				doGuarded(() -> {
					buf.flip();
					channel.write(buf);
					buf.limit(bufSize); buf.position(0);
				});
			}
		};
	}
	
}

package jesperl.dk.smoothieaq.server.db;

import static jesperl.dk.smoothieaq.shared.error.Errors.*;

import java.nio.*;
import java.nio.channels.*;
import java.nio.file.*;
import java.util.*;
import java.util.logging.*;

import jesperl.dk.smoothieaq.shared.model.db.*;
import rx.Observable;
import rx.Observer;

public class DbFile<DBO extends DbObject> {
	private final static Logger log = Logger.getLogger(DbFile.class.getName());
	
	public static final int bufSize = 2*1024; // must be greater than the largest object size
	public static final String header = "SmoothieAq 00.01";
	public static final int headerSize = header.getBytes().length;
	
	private Class<DBO> cls;
//	private DbSerializer serializer;
	private Path path;
	private boolean fixedDboSize;
	private int dboSize = 0;
	private DbContext context;
	
	private DbFile() {}
	
	static public <D extends DbObject> DbFile<D> create(Class<D> cls, boolean fixedDboSize, DbContext context) {
		return create(cls, fixedDboSize, FileSystems.getDefault().getPath(context.getDbRoot(),cls.getSimpleName()+".smoothieAqDb"), context);
	}
	
	static public <D extends DbObject> DbFile<D> create(Class<D> cls, boolean fixedDboSize, Path path, DbContext context) {
		DbFile<D> dbFile = new DbFile<>();
		dbFile.cls = cls;
//		dbFile.serializer = context.getSerializer(cls);
		dbFile.fixedDboSize = fixedDboSize;
		dbFile.path = path;
		dbFile.context = context;
		return dbFile;
	}
	
	public Observable<DBO> stream() {
		return Observable.create(s -> {
			try ( 
				FileChannel	channel = FileChannel.open(path ,StandardOpenOption.READ);
			) {
				int p = (int) channel.size();
				MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_ONLY, 0, p);
				while (p > headerSize) {
					if (s.isUnsubscribed()) return;
					map.position(p-2);
					int dboSize = map.getShort();
					if (fixedDboSize && this.dboSize == 0) this.dboSize = dboSize;
					map.position(p = p-2-dboSize);
					DBO dbo = cls.newInstance();
					if (dbo instanceof DbWithStamp) {
						((DbWithStamp) dbo).stamp = map.getLong();
						if (dbo instanceof DbWithId)
							((DbWithId)dbo).id = map.getShort();
						else if (dbo instanceof DbWithParrentId)
							((DbWithParrentId)dbo).id = map.getShort();
					}
					dbo.deserialize(map.get(), map, context);
					s.onNext(dbo);
				}
				s.onCompleted();
			} catch (Exception e) {
				log.warning("exception reading - "+e.getMessage());
				s.onError(e);
			}
		});
	}
	
	public Observer<List<DBO>> drain() {
		return new Observer<List<DBO>>() {
			FileChannel channel = null;
			ByteBuffer buf = ByteBuffer.allocateDirect(bufSize);

			@Override public void onCompleted() {
				doGuarded(() -> { if (channel != null) channel.close(); });
			}
			@Override public void onError(Throwable e) { 
				log.warning("onError on "+cls.getSimpleName()+" - "+e.getMessage());
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
							buf.putShort(((DbWithId)dbo).id);
						else if (dbo instanceof DbWithParrentId)
							buf.putShort(((DbWithParrentId)dbo).id);
					}
					dbo.serialize(buf, context);
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
	
//	public void x() throws IOException {
//	Path testSaq = FileSystems.getDefault().getPath("test.saq");
//	
////	String tempdir = System.getProperty("java.io.tempdir");
////	System.out.println("tempdir: "+tempdir);
//	
//	try (
////			RandomAccessFile file = new RandomAccessFile(new File(tempdir,"test.saq"),"rw");
//			FileChannel channela = FileChannel.open(testSaq, StandardOpenOption.APPEND,StandardOpenOption.WRITE,StandardOpenOption.CREATE);
//			FileChannel channelr = FileChannel.open(testSaq ,StandardOpenOption.READ);
//	) {
//		int k=1024;
//		ByteBuffer buf = ByteBuffer.allocateDirect(10*k);
//		long length = channela.size();
//		if (length == 0) {
//			buf.limit(10*k); buf.position(0); 
//			buf.put("SmoothieAq 00.01".getBytes());
//			buf.flip();
//			channela.write(buf);
//		}
//		MappedByteBuffer map = channelr.map(FileChannel.MapMode.READ_ONLY, 0, length);
//		System.out.println("len: "+length);
//		if (length == 0) {
//			put(channela, buf,0);
//		} else if (length < 70) {
//			put(channela, buf,100);
//			map.position(16);
//			for (int i = 0; i < 10; i++) System.out.println(map.getInt());
//		} else {
//			map.position(16+4*10);
//			for (int i = 0; i < 10; i++) System.out.println(map.getInt());
//		}
//	}
//	}
//	
//	public static void put(FileChannel channel, ByteBuffer buf, int base) throws IOException {
//		int k=1024;
//		buf.limit(10*k); buf.position(0);
//		for (int i = base; i < base+10; i++) buf.putInt(i);
//		buf.flip();
//		channel.write(buf);
//	}

}

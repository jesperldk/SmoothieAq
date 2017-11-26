package jesperl.dk.smoothieaq.server.db;

public class DbSerializer {/*
	
	public static class Header {
		public long stamp;
		public int id;
		public short type;
		public byte ver;
		public Header in(ByteBuffer in, DbSerializer serializer) {
			in1(in, serializer);
			in2(in, serializer);
			return this;
		}
		public Header in1(ByteBuffer in, DbSerializer serializer) {
			stamp = serializer.withStamp ? in.getLong() : 0;
			id = serializer.withId ? in.getInt() : 0;
			return this;
		}
		public Header in2(ByteBuffer in, DbSerializer serializer) {
			type = serializer.withType ? in.getShort() : 0;
			ver = serializer.withVer ? in.get() : 0;
			return this;
		}
		public Header out(ByteBuffer out, DbSerializer serializer) {
			if (serializer.withStamp) out.putLong(stamp);
			if (serializer.withId) out.putInt(id);
			if (serializer.withType) out.putShort(type);
			if (serializer.withVer) out.put(ver);
			return this;
		}
	}

	private Class<?> serializeFor;
	private DbSerializer parrent;
	
	private boolean withStamp = false;
	private boolean withId = false;
	private boolean withType = false;
	private boolean withVer = false;
	private short type;
	private byte ver;
	
	public interface FieldIn {
		public void in(ByteBuffer in, DbContext context, Object o) throws Exception;
	}
	public interface FieldOut {
		public void out(ByteBuffer out, DbContext context, Object o) throws Exception;
	}
	public interface AFieldIn {
		public void in(ByteBuffer in, DbContext context, Object a, int idx) throws Exception;
	}
	public interface AFieldOut {
		public void out(ByteBuffer out, DbContext context, Object a, int idx) throws Exception;
	}
	public static class SerializeField {
		public final Field field;
		public final FieldIn fieldIn;
		public final FieldOut fieldOut;
		public SerializeField(Field field, FieldIn fieldIn, FieldOut fieldOut) {
			this.field = field;
			this.fieldIn = fieldIn;
			this.fieldOut = fieldOut;
		}
	}
	private List<SerializeField> serializeFields;
	
	protected DbSerializer() {}
	
	static public DbSerializer create(Class<?> serializeFor, DbContext context) {
		DbSerializer serializer = new DbSerializer();
		serializer.serializeFor = serializeFor;
		Class<?> superCls = serializeFor.getSuperclass();
		if (superCls.equals(DbWithId.class)) {
			serializer.withId = true;
			serializer.withStamp = true;
			serializer.withVer = true;
		} else if (superCls.equals(DbWithStamp.class)) {
			serializer.withStamp = true;
			serializer.withVer = true;
		} else if (superCls.equals(DbObject.class)) {
			serializer.withVer = true;
		} else if (!superCls.equals(Object.class)) {
			serializer.parrent = context.getSerializer(superCls);
			serializer.withStamp = serializer.parrent.withStamp;
			serializer.withId = serializer.parrent.withId;
		}
		if (serializeFor.getAnnotation(JsonTypeInfo.class) != null) { 
			serializer.withType = true;
			serializer.type = context.state().getClassId(serializeFor);
		}
		serializer.ver = funcNotNull(serializeFor.getAnnotation(DbVersion.class),(byte)1,a -> (byte)a.value());
		serializer.serializeFields = createSerializeFields(serializeFor, context);
		return serializer;
	}
	
	static protected List<SerializeField> createSerializeFields(Class<?> serializeFor, DbContext context) {
		List<SerializeField> serializeFields = new ArrayList<>();
		for (Field field : serializeFor.getDeclaredFields()) {
			int modifiers = field.getModifiers();
			if ((modifiers & (ABSTRACT + FINAL + NATIVE + STATIC + TRANSIENT)) > 0) continue;
			if ((modifiers & PUBLIC) == 0) continue;
			serializeFields.add(createSerializeField(field, context));
		}
		return serializeFields;
	}

	static protected SerializeField createSerializeField(Field field, DbContext context) {
		Class<?> type = field.getType();
		if (type.isArray()) return createSerializeArrayField(field, context, type);
		else return createSerializeSimpleField(field, context, type);
	}

	protected static SerializeField createSerializeArrayField(Field field, DbContext context, Class<?> type) {
		final Class<?> ctype = type.getComponentType();
		final AFieldIn fin;
		final AFieldOut fout;
		if (ctype == boolean.class) {
			fin = (in,c,a,i) -> Array.setBoolean(a, i, in.get() > 0);
			fout = (out,c,a,i) -> out.put((byte)(Array.getBoolean(a,i) ? 1 : 0));
		} else if (ctype == float.class) {
			fin = (in,c,a,i) -> Array.setFloat(a, i, in.getFloat());
			fout = (out,c,a,i) -> out.putFloat(Array.getFloat(a,i));
		} else if (ctype == int.class) {
			fin = (in,c,a,i) -> Array.setInt(a, i, in.getInt());
			fout = (out,c,a,i) -> out.putInt(Array.getInt(a,i));
		} else if (ctype == long.class) {
			fin = (in,c,a,i) -> Array.setLong(a, i, in.getLong());
			fout = (out,c,a,i) -> out.putLong(Array.getLong(a,i));
		} else if (ctype == short.class) {
			fin = (in,c,a,i) -> Array.setShort(a, i, in.getShort());
			fout = (out,c,a,i) -> out.putShort(Array.getShort(a,i));
		} else if (ctype == String.class) {
			fin = (in,c,a,i) -> Array.set(a, i, getString(in));
			fout = (out,c,a,i) -> putString(out,(String) Array.get(a,i));
		} else if (Object.class.isAssignableFrom(ctype)) {
			final DbSerializer s = context.getSerializer(ctype);
			fin = (in,c,a,i) -> Array.set(a, i, getObject(s, in, c));
			fout = (out,c,a,i) -> setObject(s, out, c, Array.get(a,i));
		} else {
			throw error(110100,major,"Can not serialize {0}",ctype);
		}
		return new SerializeField(field, (in,c,o) -> field.set(o,getArray(in, c, ctype, fin)), (out,c,o) -> putArray(out, c, ctype, fout, field.get(o)));
	}
	
	protected static Object getArray(ByteBuffer in, DbContext context, Class<?> ctype, AFieldIn fin) throws Exception {
		int len = in.getShort();
		if (len == -1) return null;
		Object array = Array.newInstance(ctype, len);
		for (int i = 0; i < len; i++) fin.in(in, context, array, i);
		return array;
	}
	protected static void putArray(ByteBuffer out, DbContext context, Class<?> ctype, AFieldOut fout, Object array) throws Exception {
		if (array == null) {
			out.putShort((short) -1);
		} else {
			short len = (short) Array.getLength(array);
			out.putShort(len);
			for (int i = 0; i < len; i++) fout.out(out, context, array, i);
		}
	}

	protected static SerializeField createSerializeSimpleField(Field field, DbContext context, Class<?> type) {
		final FieldIn fin;
		final FieldOut fout;
		if (type == boolean.class) {
			fin = (in,c,o) -> field.setBoolean(o, in.get() > 0);
			fout = (out,c,o) -> out.put((byte)(field.getBoolean(o) ? 1 : 0));
		} else if (type == float.class) {
			fin = (in,c,o) -> field.setFloat(o, in.getFloat());
			fout = (out,c,o) -> out.putFloat(field.getFloat(o));
		} else if (type == int.class) {
			fin = (in,c,o) -> field.setInt(o, in.getInt());
			fout = (out,c,o) -> out.putInt(field.getInt(o));
		} else if (type == long.class) {
			fin = (in,c,o) -> field.setLong(o, in.getLong());
			fout = (out,c,o) -> out.putLong(field.getLong(o));
		} else if (type == short.class) {
			fin = (in,c,o) -> field.setShort(o, in.getShort());
			fout = (out,c,o) -> out.putShort(field.getShort(o));
		} else if (type == String.class) {
			fin = (in,c,o) -> field.set(o, getString(in));
			fout = (out,c,o) -> putString(out, (String) field.get(o));
		} else if (Object.class.isAssignableFrom(type)) {
			final DbSerializer s = context.getSerializer(type);
			fin = (in,c,o) -> field.set(o, getObject(s, in, c));
			fout = (out,c,o) -> setObject(s, out, c, field.get(o));
		} else {
			throw error(110100,major,"Can not serialize {0}",type);
		}
		return new SerializeField(field, fin, fout);
	}

	protected static void setObject(DbSerializer s, ByteBuffer out, DbContext c, Object v) {
		if (v == null) {
			out.put((byte) 0);
		} else {
			out.put((byte) 1);
			s.out(out, v, c);
		}
	}
	protected static Object getObject(DbSerializer s, ByteBuffer in, DbContext c) {
		return (in.get() == 0) ? null : s.in(in,c);
	}
	protected static void putString(ByteBuffer out, String str) {
		if (str == null) {
			out.putShort((short) -1);
		} else {
			byte[] buf = str.getBytes();
			out.putShort((short) buf.length);
			out.put(buf);
		}
	}
	protected static String getString(ByteBuffer in) {
		int len = in.getShort();
		if (len == -1) return null;
		byte buf[] = new byte[len];
		in.get(buf);
		return new String(buf);
	}

	public Class<?> getSerializeFor() { return serializeFor; }
	public DbSerializer getParrent() { return parrent; }
	public List<SerializeField> getSerializeFields() { return serializeFields; }
	
	public Object in(ByteBuffer in, DbContext context) { return in(in, new Header().in(in, this), context); }
	public Object in(ByteBuffer in, Header h, DbContext context) {
		if (withType) return context.getSerializer(h.type).in(in, h, context);
		Object o;
		try { 
			o = serializeFor.newInstance(); 
		} catch (Exception e) { throw error(e,110103,fatal,"Can not instantiate {0} - {1}",serializeFor.getSimpleName(),e.getMessage()); }
		try { 
			if (withStamp) DbContext.stampField.setLong(o, h.stamp);
			if (withId) DbContext.idField.setLong(o, h.id);
		} catch (Exception e) { throw error(e,110105,fatal,"Can not write stamp or id - {0}",e.getMessage()); } 
		in(in,o,context);
		return o;
		
	}
	public void in(ByteBuffer in, Object o, DbContext context) {
		if (parrent != null) parrent.in(in, o, context);
		for (SerializeField sfield: serializeFields) { 
			try { sfield.fieldIn.in(in, context, o); } catch (Exception e) { throw error(e,110101,fatal,"Can not read {0}.{1} - {2}",serializeFor.getSimpleName(),sfield.field.getName(),e.getMessage()); } 
		};
	}
	
	public void out(ByteBuffer out, Object o, DbContext context) {
		Header h = new Header();
		try { 
			h.stamp = withStamp ? DbContext.stampField.getLong(o) : 0;
			h.id = withId ? DbContext.idField.getInt(o) : 0;
		} catch (Exception e) { throw error(110104,fatal,"Can not read stamp or id - {0}",e.getMessage()); } 
		h.type = withType ? type : 0;
		h.ver = withVer ? ver : 0;
		h.out(out, this);
		out(out,h,o,context);
	}
	public void out(ByteBuffer out, Header h, Object o, DbContext context) {
		if (parrent != null) parrent.out(out, o, context);
		for (SerializeField sfield: serializeFields) { 
			try { sfield.fieldOut.out(out, context, o); } catch (Exception e) { throw error(e,110102,fatal,"Can not write {0}.{1} - {2}",serializeFor.getSimpleName(),sfield.field.getName(),e.getMessage()); } 
		}; 
	}
	
*/
}

package doit;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.file.*;

public class Do3 {

	public static final int k = 1024;
	public static final int m = k*k;

	public static void main(String[] args) throws FileNotFoundException, IOException {
		
		Path testSaq = FileSystems.getDefault().getPath("test.saq");
		
//		String tempdir = System.getProperty("java.io.tempdir");
//		System.out.println("tempdir: "+tempdir);
		
		try (
//				RandomAccessFile file = new RandomAccessFile(new File(tempdir,"test.saq"),"rw");
				FileChannel channela = FileChannel.open(testSaq, StandardOpenOption.APPEND,StandardOpenOption.WRITE,StandardOpenOption.CREATE);
				FileChannel channelr = FileChannel.open(testSaq ,StandardOpenOption.READ);
		) {
			ByteBuffer buf = ByteBuffer.allocateDirect(10*k);
			long length = channela.size();
			if (length == 0) {
				buf.limit(10*k); buf.position(0);
				buf.put("SmoothieAq 00.01".getBytes());
				buf.flip();
				channela.write(buf);
			}
			MappedByteBuffer map = channelr.map(FileChannel.MapMode.READ_ONLY, 0, length);
			System.out.println("len: "+length);
			if (length == 0) {
				put(channela, buf,0);
			} else if (length < 70) {
				put(channela, buf,100);
				map.position(16);
				for (int i = 0; i < 10; i++) System.out.println(map.getInt());
			} else {
				map.position(16+4*10);
				for (int i = 0; i < 10; i++) System.out.println(map.getInt());
			}
		}
	}

	public static void put(FileChannel channel, ByteBuffer buf, int base) throws IOException {
		buf.limit(10*k); buf.position(0);
		for (int i = base; i < base+10; i++) buf.putInt(i);
		buf.flip();
		channel.write(buf);
	}
}

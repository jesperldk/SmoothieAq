package jesperl.dk.smoothieaq.server.util;

import static jesperl.dk.smoothieaq.util.shared.error.Errors.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.jar.*;
import java.util.logging.*;

import rx.*;
import rx.Observable;

public class  FindClass {
	private final static Logger log = Logger.getLogger(FindClass.class .getName());
	
	public static Observable<Class<?>> create(final String packageName) {
		return Observable.create(subscriber -> {
			subscriber.onStart();
			log.fine("findClass("+packageName+")");
			try {
			    Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(packageName.replace('.', '/'));
			    while (resources.hasMoreElements()) {
					URL url = resources.nextElement();
					if (url == null) continue;
					URLConnection connection = url.openConnection();
					if (connection instanceof JarURLConnection) findInJar((JarURLConnection)connection,packageName,subscriber);
					else findInDir(new File(URLDecoder.decode(url.getPath(),"UTF-8")),packageName,subscriber);
//					String name = url.getFile();
//					findClasses(new File(name.replace("%20", " ")+"/"), packageName, subscriber);
				}
			    subscriber.onCompleted();
			} catch (Exception e) {
				subscriber.onError(e);
			}
		});
	}

	private static void findInJar(JarURLConnection connection, String packageName, Subscriber<? super Class<?>> subscriber) throws Exception {
	    JarFile jarFile = connection.getJarFile();
	    final Enumeration<JarEntry> entries = jarFile.entries();
	    while (entries.hasMoreElements()) {
	    	JarEntry jarEntry = entries.nextElement();
	    	if (jarEntry == null) continue;
	        String entryName = jarEntry.getName();
	        if (entryName.endsWith(".class") && !entryName.contains("$")) {
	            String name = entryName.substring(0, entryName.length() - 6).replace('/', '.');
	            if (name.contains(packageName)) 
		        	doNoException(() -> subscriber.onNext(Class.forName(name)));
	        }
	    }
	}

	private static void findInDir(File directory, String packageName, Subscriber<? super Class<?>> subscriber) throws Exception {
	    if (!directory.exists()) return;
	    for (File file : directory.listFiles()) {
	        String name = file.getName();
			if (file.isDirectory())
				findInDir(file, packageName + "." + name,subscriber);
	        else if (name.endsWith(".class") && !name.contains("$"))
	        	doNoException(() -> subscriber.onNext(Class.forName(packageName + '.' + name.substring(0, name.length() - 6))));
	    }
	}

}

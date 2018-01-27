package jesperl.dk.gwtjsontypeprocessor;

import static java.util.Collections.*;
import static javax.lang.model.element.Modifier.*;

import java.io.*;
import java.util.*;
import java.util.Map.*;
import java.util.function.*;
import java.util.regex.*;
import java.util.stream.*;

import javax.annotation.processing.*;
import javax.lang.model.*;
import javax.lang.model.element.*;
import javax.lang.model.type.*;
import javax.tools.*;
import javax.tools.Diagnostic.*;

import com.fasterxml.jackson.annotation.*;
import com.google.common.base.*;
import com.squareup.javapoet.*;

import jsinterop.annotations.*;

/**
 * with some inspiration from https://github.com/atteo/classindex
 * @author jesper Lauritsen (jesperl.dk)
 *
 */
public class  GwtJsonTypeProcessor extends AbstractProcessor {

	private Map<String, Set<String>> instanceOf = new HashMap<>();
	private Set<String> abstractCls = new HashSet<>();
	private Set<TypeElement> rootClasses = new HashSet<>();

	@Override public Set<String> getSupportedOptions() { return singleton("debug"); }

    @Override public Set<String> getSupportedAnnotationTypes() { return Arrays.asList(JsonTypeInfo.class ,JsType.class ).stream().map(c->c.getName()).collect(Collectors.toSet()); }

    @Override public SourceVersion getSupportedSourceVersion() { return SourceVersion.latestSupported(); }

    @Override public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    	log("1.2.1 process JsonType, processingOver="+roundEnv.processingOver());
        roundEnv.getElementsAnnotatedWith(JsType.class ).stream()
                .filter(e -> e.getKind().isClass() && !e.getKind().isInterface() && !e.getSimpleName().toString().endsWith("_Db") && e instanceof TypeElement)//.map(e -> (TypeElement) e)
                .forEach(jsType -> {
                    try {
                        processElement(jsType.asType());
                       	generateHelper(jsType);
                    } catch (Exception e) {
                        error("uncaught exception processing JsType " + jsType + ": " + e + "\n"
                                + Throwables.getStackTraceAsString(e));
                    }
                });
        
		if (!roundEnv.processingOver())
			return false;
		
		rootClasses.forEach(root -> {
			try {
				processRoot(root);
			} catch (Exception e) {
                error("uncaught exception generating helper for " + root + ": " + e + "\n"
                        + Throwables.getStackTraceAsString(e));
			}
		});
		
        return false;
    }

	void generateHelper(Element jsType) throws Exception {
		log("  Helper "+((TypeElement)jsType).getQualifiedName());
		String clsName = jsType.getSimpleName().toString();
		if (clsName.equals("DbObject")) return;
		String helperName = clsName+"_Helper";
		String pckg = pckg((TypeElement) jsType);
		Element enclosingElement = jsType.getEnclosingElement();
		if (enclosingElement.getKind().isClass() || enclosingElement.getKind().isInterface()) {
			clsName = enclosingElement.getSimpleName().toString()+"."+clsName;
			helperName = enclosingElement.getSimpleName().toString()+helperName;
			pckg = pckg((TypeElement) enclosingElement);
		}
		JavaFileObject sf = processingEnv.getFiler().createSourceFile(pckg+"."+helperName);
		try (Writer w = sf.openWriter()) {
			new ProcessFields(w,jsType,pckg,clsName,helperName).p();
			w.close();
		}
	}

	private String pckg(TypeElement t) {
		String pckg = t.getQualifiedName().toString();
    	pckg = pckg.substring(0, pckg.lastIndexOf("."));
		return pckg;
	}

    private void processElement(TypeMirror jsType) {
		log("looking at "+jsType);
		TypeElement jsTypeElm = (TypeElement) ((DeclaredType)jsType).asElement();
		if (jsTypeElm.getAnnotation(JsonTypeInfo.class ) != null) {
			log("root "+jsType);
			rootClasses.add(jsTypeElm);
		} else {
			Set<String> superClasses = getSuperClassesIfJsonTypeInfo(jsType);
			if (!superClasses.isEmpty()) {
				instanceOf.put(jsType.toString(), superClasses);
				if (jsTypeElm.getModifiers().contains(Modifier.ABSTRACT)) {
					log("abstract "+jsType);
					abstractCls.add(jsType.toString());
				}
			}
		}
	}
		
	private Set<String> getSuperClassesIfJsonTypeInfo(TypeMirror type) {
		return processingEnv.getTypeUtils().directSupertypes(type).stream()
				.map(this::getClassesIfJsonTypeInfo).collect(HashSet::new,HashSet::addAll,HashSet::addAll);
	}
	
	private Set<String> getClassesIfJsonTypeInfo(TypeMirror type) {
		log("  looking at super "+type);
		if (type.getKind() != TypeKind.DECLARED)
			return Collections.emptySet();
		TypeElement typeElm = (TypeElement) ((DeclaredType)type).asElement();
		if (typeElm.getAnnotation(JsonTypeInfo.class ) != null) {
			rootClasses.add(typeElm);
			return Collections.singleton(type.toString());
		}
		Set<String> superClasses = getSuperClassesIfJsonTypeInfo(type);
		if (!superClasses.isEmpty())
			superClasses.add(type.toString());
		return superClasses;
	}

	// Intellij and Eclipse hacks courtesy of ClassIndex
	private FileObject readOldHelper(String helperPkg, String helperName) throws IOException {
		Reader reader = null;
		try {
			log("trying to read old helper "+helperPkg+"."+helperName);
			final FileObject file = processingEnv.getFiler().getResource(StandardLocation.SOURCE_OUTPUT, helperPkg, helperName+".java");
			log("trying to read file "+file.getName());
			reader = file.openReader(true);
			readOldHelper(reader);
			return file;
		} catch (FileNotFoundException e) {
			/**
			 * Ugly hack for Intellij IDEA incremental compilation.
			 * The problem is that it throws FileNotFoundException on the files, if they were not created during the
			 * current session of compilation.
			 */
			final String realPath = e.getMessage();
			log("retrying to read file "+realPath);
			if (new File(realPath).exists())
				try (Reader fileReader = new FileReader(realPath)) {
					readOldHelper(fileReader);
				}
		} catch (IOException e) {
			// Thrown by Eclipse JDT when not found
		} catch (UnsupportedOperationException e) {
			// Java6 does not support reading old 
		} finally {
			if (reader != null)	reader.close();
		}
		return null;
	}

	private void readOldHelper(Reader reader) throws IOException {
		log("reading old helper");
		try (BufferedReader bufferedReader = new BufferedReader(reader)) {
			bufferedReader.lines().map(this::match$class ).filter(l -> !l.isEmpty())
				.filter(l -> !instanceOf.containsKey(l.get(1)))
				.forEach(l -> {
					instanceOf.put(l.get(1), new HashSet<>(l.subList(2, l.size())));
					if (l.get(0).equals("aclass")) abstractCls.add(l.get(1));
				});
		}
	}
	
	static Pattern pCls = Pattern.compile("\\$(a?class )\\((\\\".*)\\)");
	static Pattern pStr = Pattern.compile("\"([^\"]*)\"");
	private List<String> match$class (String l) { // TODO ugh! This is ugly! gotta find some streamified regexp library
		List<String> match = new ArrayList<>();
		Matcher mCls = pCls.matcher(l);
		if (mCls.find()) {
			match.add(mCls.group(1));
			Matcher mStr = pStr.matcher(mCls.group(2));
			while (mStr.find())
				match.add(mStr.group(1));
		}
		return match;
	}

	// Intellij and Eclipse hacks courtesy of ClassIndex
	private void writeHelper(String helperPkg, String helperName, TypeElement root, FileObject oldHelperFile) throws IOException {
		if (oldHelperFile != null) {
			log("rewriting to "+oldHelperFile.getName());
			try (Writer writer = oldHelperFile.openWriter()) {
				writeHelper(helperPkg, helperName, root, writer);
				return;
			} catch (Exception e) {
			}
		}
		FileObject file	= processingEnv.getFiler().createSourceFile(helperPkg+"."+helperName, root);
		log("writing to "+file.getName());
		try (Writer writer = file.openWriter()) {
			writeHelper(helperPkg, helperName, root, writer);
		}
	}

	private void writeHelper(String helperPkg, String helperName, TypeElement root, Writer writer) throws IOException {
		log("writing now");
		String rootType = minimalClass(root.toString());

		writer.write("package "+helperPkg+";\n");
		writer.write("\n");
		writer.write("import static java.util.Arrays.*;\n");
		writer.write("import static java.util.stream.Collectors.*;\n");
		writer.write("\n");
		writer.write("import java.util.*;\n");
		writer.write("import java.util.function.*;\n");
		writer.write("\n");
		writer.write("import jsinterop.annotations.*;\n");
		writer.write("\n");
		writer.write("@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = \"Object\")\n");
		writer.write("public interface "+helperName+" {\n");
		writer.write("\n");

		if (root.getAnnotationMirrors().stream().anyMatch(am -> am.getAnnotationType().asElement().getSimpleName().contentEquals("DbVersion"))) {
			writer.write("	@JsOverlay\n");
			writer.write("	static Map<String,Function<"+rootType+","+rootType+">> $copy = $buildCopy(\n");
	
			for (Map.Entry<String, Set<String>> e : ofType(root)) {
				String cls = e.getKey();
				if (!abstractCls.contains(cls)) writer.write("\t\t$copyFunc(\""+cls+"\",(s)->"+cls+"_Db.copy(new "+cls+"(),("+cls+")s)),\n");
			}
	
			writer.write("	null);\n");
			writer.write("\n");
			writer.write("	@JsOverlay\n");
			writer.write("	static Object[] $copyFunc(String cls, Function<"+rootType+","+rootType+"> func) {\n");
			writer.write("		Object[] obj = new Object[2];\n");
			writer.write("		obj[0] = cls; obj[1] = func;\n");
			writer.write("	return obj;\n");
			writer.write("	}\n");
			writer.write("\n");
			writer.write("	@JsOverlay @SuppressWarnings(\"unchecked\")\n");
			writer.write("	static Map<String,Function<"+rootType+","+rootType+">> $buildCopy(Object[]... $copyFuncs) {\n");
			writer.write("		Map<String,Function<"+rootType+","+rootType+">> map = new HashMap<>();\n");
			writer.write("		stream($copyFuncs).filter(c -> c != null).forEach($copyFunc -> {\n");
			writer.write("			map.put($minimalClass((String) $copyFunc[0]), (Function<"+rootType+", "+rootType+">) $copyFunc[1]);\n");
			writer.write("		});\n");
			writer.write("		return map;\n");
			writer.write("	}\n");
			writer.write("\n");
			writer.write("	@JsOverlay\n");
			writer.write("	static "+rootType+" $copy("+rootType+" s) {\n");
			writer.write("		if (s == null) return null;\n");
			writer.write("		"+rootType+" c = $copy.get(s.$type).apply(s);\n");
			writer.write("		c.$type = s.$type;\n");
			writer.write("		return c;\n");
			writer.write("	}\n");
			writer.write("\n");
		}
			
		writer.write("	@JsOverlay\n");
		writer.write("	static Map<String,Set<String>> $instanceOf = $buildMap(\n");
		
		for (Map.Entry<String, Set<String>> e : ofType(root)) {
			writer.write("\t\t$"+(abstractCls.contains(e.getKey())?"aclass":"class ")+"("+
					stringify(Collections.singleton(e.getKey()))+","+
					stringify(e.getValue())+"),\n");
		}
		
		writer.write("	null);\n");
		writer.write("\n");
		writer.write("	@JsOverlay\n");
		writer.write("	static String[] $aclass(String... subThenSuperclasses) {\n");
		writer.write("		return subThenSuperclasses;\n");
		writer.write("	}\n");
		writer.write("\n");
		writer.write("	@JsOverlay\n");
		writer.write("	static String[] $class (String... subThenSuperclasses) {\n");
		writer.write("		return subThenSuperclasses;\n");
		writer.write("	}\n");
		writer.write("\n");
		writer.write("	@JsOverlay\n");
		writer.write("	static Map<String,Set<String>> $buildMap(String[]... $classes) {\n");
		writer.write("		Map<String,Set<String>> map = new HashMap<>();\n");
		writer.write("		stream($classes).filter(c -> c != null).forEach($class  -> {\n");
		writer.write("			map.put($minimalClass($class [0]), stream($class ).map(s -> $minimalClass(s)).collect(toSet()));\n");
		writer.write("		});\n");
		writer.write("		return map;\n");
		writer.write("	}\n");
		writer.write("\n");
		writer.write("	@JsOverlay\n");
		writer.write("	static String $minimalClass(String className) {\n");
		writer.write("		String[] split = className.split(\"\\\\.\");\n");
		writer.write("		return \".\"+split[split.length-1];\n");
		writer.write("	}\n");
		writer.write("\n");
		writer.write("	@JsOverlay\n");
		writer.write("	static boolean $instanceOf(String thisType, String instanceOfType) {\n");
		writer.write("		if (thisType == null) return false;\n");
		writer.write("		Set<String> set = $instanceOf.get(thisType);\n");
		writer.write("		return set != null && set.contains(instanceOfType);\n");
		writer.write("	}\n");
		writer.write("\n");
		writer.write("	@JsOverlay\n");
		writer.write("	static <T extends "+rootType+"> T $create(T t, String type) {\n");
		writer.write("		t.$type = type;\n");
		writer.write("		return t;\n");
		writer.write("	}\n");
		writer.write("\n");

		for (Map.Entry<String, Set<String>> e : ofType(root)) {
			String type = minimalClass(e.getKey());
			writer.write("	@JsOverlay default boolean instanceOf"+type+"() { return $instanceOf((("+rootType+")this).$type, \"."+type+"\"); }\n");
			writer.write("	@JsOverlay default "+type+" as"+type+"() { return ("+type+")this; }\n");
			if (!abstractCls.contains(e.getKey()))
				writer.write("	@JsOverlay static "+type+" create"+type+"() { return $create(new "+type+"(), \"."+type+"\"); }\n");
			writer.write("\n");
		}
		
		writer.write("}\n");
	}

	protected List<Entry<String, Set<String>>> ofType(TypeElement root) {
		return instanceOf.entrySet().stream().filter(e -> e.getValue().contains(root.toString())).collect(Collectors.toList());
	}

	private String minimalClass(String className) {
		String[] split = className.split("\\.");
		return split[split.length-1];
	}

	private String stringify(Set<String> classes) {
		return String.join(", ", classes.stream().map(c -> "\""+c+"\"").collect(Collectors.toList()));
	}

	private void processRoot(TypeElement root) throws Exception {
        ClassName rootName = ClassName.get(root); 
        log("root class : " + root); 

        String helperPkg = rootName.packageName();
        String helperName = rootName.simpleName() + "_HelperInheritace"; 
        log("root helper: "+helperPkg+"."+helperName);

        FileObject file = readOldHelper(helperPkg, helperName);
        writeHelper(helperPkg, helperName, root, file);
    }
	
    enum Type { 
    	booleanT(""), floatT("Float"), intT("Int"), longT("Long"), shortT("Short"), stringT(null), enumT(null), arrayT(null), objectT(null);
    	String bufTyp;
    	private Type(String bufTyp) { this.bufTyp = bufTyp; }
    };
    
    class  ProcessFields {
    	Writer w;
    	TypeElement t;
    	String pckg;
    	String className;
    	String helperName;
    	
    	ProcessFields(Writer w, Element t, String pckg, String className, String helperName) {
	    	this.w = w;
	    	this.t = (TypeElement) t;
	    	this.pckg = pckg;
	    	this.className = className;
	    	this.helperName = helperName;
	    }
	
	    void w(String s) { try { w.append(s); } catch (IOException e) { throw new RuntimeException(e); } }
	    
		TypeElement getTypeElement(TypeMirror tm) { return (TypeElement) ((DeclaredType)tm).asElement(); }
		String getTypeStr(TypeMirror tm) {
			TypeKind tk = tm.getKind();
			if (tk == TypeKind.BOOLEAN) return "Boolean";
	    	if (tk == TypeKind.FLOAT) return "Float";
	    	if (tk == TypeKind.INT) return "Integer";
	    	if (tk == TypeKind.LONG) return "Long";
	    	if (tk == TypeKind.SHORT) return "Short";
	    	if (tk == TypeKind.ARRAY) return "Object"; // TODO
	    	if (tk == TypeKind.DECLARED) {
	    		TypeElement typeElement = getTypeElement(tm);
				if (typeElement.getSimpleName().toString().equals("String")) 
					return "String";
				else if (getTypeElement(typeElement.getSuperclass()).getSimpleName().toString().equals("Enum"))
					return typeElement.getQualifiedName().toString();
	    		else 
					return typeElement.getQualifiedName().toString();
	    	}
	    	return null;
		}
		
		String getFieldClass(TypeMirror tm) {
			if (tm.getKind() != TypeKind.DECLARED) return "Field";
    		TypeElement typeElement = getTypeElement(tm);
			if (!getTypeElement(typeElement.getSuperclass()).getSimpleName().toString().equals("Enum")) return "Field";
			return "EnumField";
		}
		
	    void p() throws Exception {
			w("package "+pckg+";\n");
			w("\n");
			w("import java.util.*;\n");
			w("\n");
			w("import jesperl.dk.smoothieaq.shared.model.db.*;\n");
			w("import jsinterop.annotations.*;\n");
			w("\n");
			w("\n@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = \"Object\")\n");
			w("public interface "+helperName+" extends jesperl.dk.smoothieaq.shared.model.db.DbObject_Helper {\n\n");
			w("\n");
			
			forEachField(w, t, (e) -> outField(e.getSimpleName().toString(), getTypeStr(e.asType()),getFieldClass(e.asType())));

			w("}\n");
	    }

	    private void outField(String fieldName, String fieldType, String fieldClass) {
	    	if (fieldType.equals("Object")) return;
	    	String clsFieldName = className+"."+fieldName;
			w("\t@SuppressWarnings(\"unchecked\")\n");
			w("\t@JsOverlay default Field<"+fieldType+"> "+fieldName+"() {\n");
			w("\t	Field<"+fieldType+"> field = (Field<"+fieldType+">) $fields().get(\""+clsFieldName+"\");\n");
			w("\t	if (field == null)\n");
			w("\t		$fields().put(\""+clsFieldName+"\", field = new "+fieldClass+"<>(()->(("+className+")this)."+fieldName+", v->(("+className+")this)."+fieldName+" = v, \""+clsFieldName+"\", "+fieldType+".class));\n");
			w("\t	return field; \n");
			w("\t}\n\n");
	    }

	 	void forEachField(Writer w, Element t, Consumer<Element> doField) {
			t.getEnclosedElements().stream()
				.filter(e -> e.getKind().isField() && e.getModifiers().contains(PUBLIC) && !e.getModifiers().contains(TRANSIENT))
				.forEach(e -> doField.accept(e));
		}
   }
    
   private void log(String msg) {
//        if (processingEnv.getOptions().containsKey("debug")) {
            processingEnv.getMessager().printMessage(Kind.NOTE, msg);
//        }
    }

    private void error(String msg) {
        processingEnv.getMessager().printMessage(Kind.ERROR, msg);
    }
}

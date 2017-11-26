package jesperl.dk.gwtjsontypeprocessor;

import static java.util.Collections.*;

import java.io.*;
import java.util.*;
import java.util.Map.*;
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
    	log("process, processingOver="+roundEnv.processingOver());
        roundEnv.getElementsAnnotatedWith(JsType.class ).stream()
                .filter(e -> e.getKind().isClass() && e instanceof TypeElement)//.map(e -> (TypeElement) e)
                .forEach(jsType -> {
                    try {
                        processElement(jsType.asType());
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

    private void processElement(TypeMirror jsType) {
		log("looking at "+jsType);
		TypeElement jsTypeElm = (TypeElement) ((DeclaredType)jsType).asElement();
		if (jsTypeElm.getAnnotation(JsonTypeInfo.class ) != null) {
			log("root "+jsType);
			rootClasses.add(jsTypeElm);
//			instanceOf.put(jsType.toString(), Collections.emptySet());
		} else {
			Set<String> superClasses = getSuperClassesIfJsonTypeInfo(jsType);
			if (!superClasses.isEmpty()) {
				instanceOf.put(jsType.toString(), superClasses);
//				log("abstract? "+jsTypeElm.getModifiers()+" "+java.lang.reflect.Modifier.ABSTRACT);
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

//	private void buildMap(String superc, CodeBlock.Builder builder) {
//		instanceOf.entrySet().stream().filter(e -> e.getValue().contains(superc)).forEach(e -> {
//			builder.add("$class ($S",e.getKey());
//			e.getValue().stream().forEach(s -> builder.add(",$S",s));
//			builder.add("),\n");
//		});
//	}
	
	// Intellij and Eclipse hacks courtesy of ClassIndex
	private FileObject readOldHelper(String helperPkg, String helperName) throws IOException {
		@SuppressWarnings("resource")
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
		writer.write("\n");
		writer.write("import jsinterop.annotations.*;\n");
		writer.write("\n");
		writer.write("@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = \"Object\")\n");
		writer.write("public interface "+helperName+" {\n");
		writer.write("\n");
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

//	private void writeIndexFile(Set<String> entries, String resourceName, FileObject overrideFile) throws IOException {
//		FileObject file = overrideFile;
//		if (file == null) {
//			file = processingEnv.getFiler().createSourceFile(StandardLocation.CLASS_OUTPUT, "", resourceName);
//		}
//		try (Writer writer = file.openWriter()) {
//			for (String entry : entries) {
//				writer.write(entry);
//				writer.write("\n");
//			}
//		}
//	}
//
//	private void writeSimpleNameIndexFile(Set<String> elementList, String resourceName)
//			throws IOException {
//		FileObject file = readOldIndexFile(elementList, resourceName);
//		if (file != null) {
//			/**
//			 * Ugly hack for Eclipse JDT incremental compilation.
//			 * Eclipse JDT can't createResource() after successful getResource().
//			 * But we can file.openWriter().
//			 */
//			try {
//				writeIndexFile(elementList, resourceName, file);
//				return;
//			} catch (IllegalStateException e) {
//				// Thrown by HotSpot Java Compiler
//			}
//		}
//		writeIndexFile(elementList, resourceName, null);
//	}

	private void processRoot(TypeElement root) throws Exception {
        ClassName rootName = ClassName.get(root); 
        log("root class : " + root); 

        String helperPkg = rootName.packageName();
        String helperName = rootName.simpleName() + "_Helper";
        log("root helper: "+helperPkg+"."+helperName);

        FileObject file = readOldHelper(helperPkg, helperName);
        writeHelper(helperPkg, helperName, root, file);

//        TypeSpec.Builder modelTypeBuilder = TypeSpec.interfaceBuilder(helperName.simpleName())
//                .addOriginatingElement(root)
//                .addModifiers(PUBLIC)
////                .add
//                .addJavadoc("Generated by $L",this.getClass().getName());
//        	
////        buildMap(root.toString(), modelTypeBuilder);
//
//        modelTypeBuilder.addMethod(MethodSpec.methodBuilder("tt")
////                .addAnnotation(Inject.class )
//                .addModifiers(PUBLIC,DEFAULT)
//                .returns(TypeName.INT)
////                .addParameter(TypeName.get(ResourceVisitor.Supplier.class ), "parent", FINAL)
//                .addParameter(TypeName.INT, "i")
////                .addStatement("super(new $T() { public $T get() { return $L.get().path($S); } })",
////                        ResourceVisitor.Supplier.class , ResourceVisitor.class , "parent", rsPath)
//                .addStatement("return 1")
//                .build());

//        List<ExecutableElement> methods = restService.getEnclosedElements().stream()
//                .filter(e -> e.getKind() == ElementKind.METHOD && e instanceof ExecutableElement)
//                .map(e -> (ExecutableElement) e)
//                .filter(method -> !(method.getModifiers().contains(STATIC) || method.isDefault()))
//                .collect(Collectors.toList());
//
//        Set<String> methodImports = new HashSet<>();
//        for (ExecutableElement method : methods) {
//            String methodName = method.getSimpleName().toString();
//
//            if (isIncompatible(method)) {
//                modelTypeBuilder.addMethod(MethodSpec.overriding(method)
//                        .addStatement("throw new $T(\"$L\")", UnsupportedOperationException.class , methodName)
//                        .build());
//                continue;
//            }
//
//            CodeBlock.Builder builder = CodeBlock.builder().add("$[return ");
//            {
//                // method type
//                builder.add("method($L)", methodImport(methodImports, method.getAnnotationMirrors().stream()
//                        .map(a -> asElement(a.getAnnotationType()).getAnnotation(HttpMethod.class ))
//                        .filter(a -> a != null).map(HttpMethod::value).findFirst().orElse(GET)));
//                // resolve paths
//                builder.add(".path($L)", Arrays
//                        .stream(ofNullable(method.getAnnotation(Path.class )).map(Path::value).orElse("").split("/"))
//                        .filter(s -> !s.isEmpty()).map(path -> !path.startsWith("{") ? "\"" + path + "\"" : method
//                                .getParameters().stream()
//                                .filter(a -> ofNullable(a.getAnnotation(PathParam.class )).map(PathParam::value)
//                                        .map(v -> path.equals("{" + v + "}")).orElse(false))
//                                .findFirst().map(VariableElement::getSimpleName).map(Object::toString)
//                                .orElse("null /* path param " + path + " does not match any argument! */"))
//                        .collect(Collectors.joining(", ")));
//                // query params
//                method.getParameters().stream().filter(p -> p.getAnnotation(QueryParam.class ) != null).forEach(p ->
//                        builder.add(".param($S, $L)", p.getAnnotation(QueryParam.class ).value(), p.getSimpleName()));
//                // header params
//                method.getParameters().stream().filter(p -> p.getAnnotation(HeaderParam.class ) != null).forEach(p ->
//                        builder.add(".header($S, $L)", p.getAnnotation(HeaderParam.class ).value(), p.getSimpleName()));
//                // form params
//                method.getParameters().stream().filter(p -> p.getAnnotation(FormParam.class ) != null).forEach(p ->
//                        builder.add(".form($S, $L)", p.getAnnotation(FormParam.class ).value(), p.getSimpleName()));
//                // data
//                method.getParameters().stream().filter(this::isParam).findFirst()
//                        .ifPresent(data -> builder.add(".data($L)", data.getSimpleName()));
//            }
//            builder.add(".as($T.class , $T.class );\n$]",
//                    processingEnv.getTypeUtils().erasure(method.getReturnType()),
//                    MoreTypes.asDeclared(method.getReturnType()).getTypeArguments().stream().findFirst()
//                            .map(TypeName::get).orElse(TypeName.get(Void.class )));
//
//            modelTypeBuilder.addMethod(MethodSpec.overriding(method).addCode(builder.build()).build());
//        }

//        Filer filer = processingEnv.getFiler();
//        JavaFile.Builder file = JavaFile.builder(rootName.packageName(), modelTypeBuilder.build());
////        for (String methodImport : methodImports) file.addStaticImport(HttpMethod.class , methodImport);
//        file.build().writeTo(filer);
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

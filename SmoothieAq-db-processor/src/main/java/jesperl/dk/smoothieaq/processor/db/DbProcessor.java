package jesperl.dk.smoothieaq.processor.db;

import static java.util.Collections.*;
import static javax.lang.model.element.Modifier.*;

import java.io.*;
import java.util.*;
import java.util.function.*;

import javax.annotation.processing.*;
import javax.lang.model.*;
import javax.lang.model.element.*;
import javax.lang.model.type.*;
import javax.tools.*;
import javax.tools.Diagnostic.*;

public class  DbProcessor extends AbstractProcessor { 

	@Override public Set<String> getSupportedOptions() { return singleton("debug"); }
    @Override public Set<String> getSupportedAnnotationTypes() { return Collections.singleton("jesperl.dk.smoothieaq.shared.model.db.DbVersion");}//"jesperl.dk.smoothieaq.shared.model.db.DbVersion"); }
    @Override public SourceVersion getSupportedSourceVersion() { return SourceVersion.latestSupported(); }

    @Override public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    	if (annotations == null || annotations.isEmpty()) return false;
    	log("DB1.3 process, processingOver="+roundEnv.processingOver()+" "+annotations.iterator().next());
		try {
			roundEnv.getElementsAnnotatedWith(annotations.iterator().next()).stream()
    		.filter(e -> e.getKind().isClass() && e instanceof TypeElement)
    		.forEach(t -> {
    			log("DB "+t.getSimpleName().toString());
    			try {
//    				PackageElement pkElm = (PackageElement)t.getEnclosingElement();
//					JavaFileObject sf = processingEnv.getFiler().createSourceFile(pkElm.getQualifiedName()+"."+t.getSimpleName()+"_Db");
					JavaFileObject sf = processingEnv.getFiler().createSourceFile(t+"_Db");
					log("  DB "+sf.getName());
					try (Writer w = sf.openWriter()) {
						new Process(w,t).p();
						w.close();
					}
				} catch (Exception e) {
					error(e.toString()+" xxx1 "+e.getMessage());
				}
    		});
		} catch (Exception e) {
			error(e.toString()+" xxx2 "+e.getMessage());
		}
//		if (!roundEnv.processingOver()) return false;
		return true;
    }
    
    enum Type { 
    	booleanT(""), floatT("Float"), intT("Int"), longT("Long"), shortT("Short"), stringT(null), enumT(null), arrayT(null), objectT(null);
    	String bufTyp;
    	private Type(String bufTyp) { this.bufTyp = bufTyp; }
    };
    
    class  Process {
    	Writer w;
    	Element t;
		TypeElement superCls;
		String superName;
		String baseName;
		String thisName;
    	String baseVar;
    	String dbName;
    	
	    Process(Writer w, Element t) {
	    	this.w = w;
	    	this.t = t;
			superCls = getTypeElement(((TypeElement)t).getSuperclass());
			superName = superCls.getSimpleName().toString();
			baseName = t.getSimpleName().toString();
			thisName = "this"+baseName;
	    	baseVar = baseName.substring(0, 1).toLowerCase()+baseName.substring(1);
	    	dbName = baseName+"_Db";
	    }
	
	    void w(String s) { try { w.append(s); } catch (IOException e) { throw new RuntimeException(e); } }
	    
	//    private Element getDeclaredType(Element e) { return ((DeclaredType)e.asType()).asElement(); }
	    TypeMirror getArrayType(Element e) { return ((ArrayType)e.asType()).getComponentType(); }
	    TypeMirror getEnumType(Element e) { return (DeclaredType)e.asType(); }
		TypeElement getTypeElement(TypeMirror tm) { return (TypeElement) ((DeclaredType)tm).asElement(); }
		Type getType(TypeMirror tm) {
			TypeKind tk = tm.getKind();
			if (tk == TypeKind.BOOLEAN) return Type.booleanT;
	    	if (tk == TypeKind.FLOAT) return Type.floatT;
	    	if (tk == TypeKind.INT) return Type.intT;
	    	if (tk == TypeKind.LONG) return Type.longT;
	    	if (tk == TypeKind.SHORT) return Type.shortT;
	    	if (tk == TypeKind.ARRAY) return Type.arrayT;
	    	if (tk == TypeKind.DECLARED) {
	    		TypeElement typeElement = getTypeElement(tm);
				if (typeElement.getSimpleName().toString().equals("String")) 
					return Type.stringT;
				else if (getTypeElement(typeElement.getSuperclass()).getSimpleName().toString().equals("Enum"))
					return Type.enumT;
	    		else 
	    			return Type.objectT;
	    	}
	    	return null;
		}
		
		String getVer(Element t) {
			return getAnnotation(t, "DbVersion").getElementValues().values().iterator().next().getValue().toString();
		}
		
		AnnotationMirror getAnnotation(Element t, String annoName) {
			for (AnnotationMirror am: t.getAnnotationMirrors())
				if (am.getAnnotationType().asElement().getSimpleName().toString().equals(annoName)) return am;
			TypeElement supert = getTypeElement(((TypeElement)t).getSuperclass());
			if (!supert.getSimpleName().toString().equals("Object")) return getAnnotation(supert, annoName);
			return null;
		}
		
	    void p() throws Exception {
	    	String pckg = ((TypeElement)t).getQualifiedName().toString();
	    	pckg = pckg.substring(0, pckg.lastIndexOf("."));
			w("package "+pckg+";\n");
			w("\n");
			w("import java.nio.*;\n");
			w("\n");
			w("import com.google.gwt.core.shared.*;\n");
			w("\n");
			w("import jesperl.dk.smoothieaq.server.db.*;\n");
			w("import jesperl.dk.smoothieaq.shared.model.db.*;\n");
			w("import jsinterop.annotations.*;\n");
			w("\n@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = \"Object\")\n");
			w("public class "+dbName+" {\n");
			w("	\n");
	
			copy();
			in();
			inFields();
			out();
			outFields();
	
			w("\n");
			w("}\n");
		}

		void out(String type, String value) { w("\t\t\tout.put"+type+"(("+(type.isEmpty()?"byte":type.toLowerCase())+")"+value+");\n");}
		void outField(Element e,  String fieldName, Type type) {
			String baseField = baseVar+"."+fieldName;
			if (type == Type.objectT) {
				w("\t\tif ("+baseField+" == null)\n"); out("","0");
				w("\t\telse\n\t\t\t"+baseVar+".serialize(out,context);\n");
			} else if (type == Type.enumT) {
				w("\t\tif ("+baseField+" == null)\n"); out("","0");
				w("\t\telse\n"); out("",baseField+".getId()");
			} else if (type == Type.stringT) {
				w("\t\tif ("+baseField+" == null)\n"); out("Short","0");
				w("\t\telse {\n");
				out("Short",baseField+".length()");
				w("\t\t\tout.put("+baseField+".getBytes());\n");
				w("\t\t}\n");
			} else if (type == Type.arrayT) {
				TypeMirror arrayType = getArrayType(e);
				w("\t\tif ("+baseField+" == null)\n"); out("Short","0");
				w("\t\telse {\n");
				out("Short",baseField+".length");
				w("\t\t\tfor (int $i = 0; $i < "+baseField+".length; $i++)\n");
				outField(e, fieldName+"[$i]", getType(arrayType));
				w("\t\t}\n");
			} else if (type == Type.booleanT) {
				out("","("+baseField+"?1:0)");
			} else {
				out(type.bufTyp,baseField);
			}
		}
		void outFields() {
			w("	@JsOverlay @GwtIncompatible\n");
			w("	static public void serializeFields("+baseName+" "+baseVar+", ByteBuffer out, DbContext context) {\n");
			if (!superEnd()) w("\t\t"+superCls+"_Db.serializeFields("+baseVar+",out,context);\n");
			forEachField(w, t, baseName, baseVar, (e) -> 
				outField(e, e.getSimpleName().toString(), getType(e.asType()))
			);
			w("	}\n");
			w("\n");
		}
		void out() {
			w("	@JsOverlay @GwtIncompatible\n");
			w("	static public void serialize("+baseName+" "+baseVar+", ByteBuffer out, DbContext context) {\n");
			
			w("\t\tout.put((byte)"+getVer(t)+");\n");
			if (getAnnotation(t, "JsonTypeInfo") != null) w("\t\tout.putShort(context.state().getClassId("+baseVar+".getClass()));\n");
			w("\t\tserializeFields("+baseVar+",out,context);\n");
			
			w("	}\n");
			w("\n");
		}

		void in(String type, String field) { w("\t\t\t"+field+" = in.get"+type+"();\n");}
		void inField(Element e,  String fieldName, Type type) {
			String baseField = baseVar+"."+fieldName;
			if (type == Type.objectT) {
				w("\t\t"+baseField+" = "+e.asType()+"_Db.deserialize(in,context);\n");
			} else if (type == Type.enumT) {
				w("\t\t"+baseField+" = context.getEnum("+getEnumType(e)+".class ,in.get());\n");
			} else if (type == Type.stringT) {
				w("\t\tint "+fieldName+"$len = in.getShort();\n");
				w("\t\tif ("+fieldName+"$len == 0)\n\t\t\t"+baseField+" = null;\n");
				w("\t\telse {\n");
				w("\t\t\tbyte "+fieldName+"$buf[] = new byte["+fieldName+"$len];\n");
				w("\t\t\tin.get("+fieldName+"$buf);\n");
				w("\t\t\t"+baseField+" = new String("+fieldName+"$buf);\n");
				w("\t\t}\n");
			} else if (type == Type.arrayT) {
				TypeMirror arrayType = getArrayType(e);
				w("\t\tint "+fieldName+"$len = in.getShort();\n");
				w("\t\tif ("+fieldName+"$len == 0)\n\t\t\t"+baseField+" = null;\n");
				w("\t\telse {\n");
				w("\t\t\t"+baseField+" = new "+arrayType+"["+fieldName+"$len];\n");
				w("\t\t\tfor (int $i = 0; $i < "+fieldName+"$len; $i++)\n");
				inField(e, fieldName+"[$i]", getType(arrayType));
				w("\t\t}\n");
			} else if (type == Type.booleanT) {
				w("\t\t"+baseField+" = (in.get() == 1);\n");
			} else {
				in(type.bufTyp,baseField);
			}
		}
		void in() {
			w("\t@JsOverlay @GwtIncompatible\n");
			w("\tstatic public "+baseName+" deserialize(ByteBuffer in, DbContext context) {\n");
			w("\t\tint $ver = in.get();\n");
			w("\t\tif ($ver == 0) return null;\n");
			if (getAnnotation(t, "JsonTypeInfo") != null) {
				w("\t\ttry {\n");
				w("\t\t\treturn (("+baseName+")context.state().getClass(in.getShort()).newInstance()).deserialize($ver,in,context);\n");
				w("\t\t} catch (Exception e) { throw new RuntimeException(e); }\n");
			} else if (!t.getModifiers().contains(ABSTRACT)) {
				w("\t\treturn new "+baseName+"().deserialize($ver,in,context);\n");
			} else {
				w("\t\treturn null;\n");
			}
			w("\t}\n");
			w("\n");
		}
		void inFields() {
			w("\t@JsOverlay @GwtIncompatible\n");
			w("\tstatic public "+baseName+" deserializeFields("+baseName+" "+baseVar+", ByteBuffer in, DbContext context) {\n");
			if (!superEnd()) w("\t\t"+superCls+"_Db.deserializeFields("+baseVar+",in,context);\n");
			forEachField(w, t, baseName, baseVar, (e) -> 
				inField(e, e.getSimpleName().toString(), getType(e.asType()))
			);
			w("\t\treturn "+baseVar+";\n");
			w("\t}\n");
			w("\n");
		}

		void copy(String to, String from) { w("\t\t\t"+to+" = "+from+";\n");}
		void copyField(Element e, String fieldName, Type type) {
			String baseField = baseVar+"."+fieldName;
			String thisField = thisName+"."+fieldName;
			if (type == Type.objectT) {
				copy(baseField,thisField+" == null ? null : ("+e.asType()+")"+thisField+".copy()");
			} else if (type == Type.arrayT) {
				TypeMirror arrayType = getArrayType(e);
				w("\t\tif ("+thisField+" == null) "+baseField+" = null;\n");
				w("\t\telse {\n");
				w("\t\t\t"+baseField+" = new "+arrayType+"["+thisField+".length];\n");
				w("\t\t\tfor (int $i = 0; $i < "+thisField+".length; $i++)\n");
				copyField(e, fieldName+"[$i]", getType(arrayType));
				w("\t\t}\n");
			} else {
				copy(baseField,thisName+"."+fieldName);
			}
		}
		void copy() {
			w("	@JsOverlay\n");
			w("	static public "+baseName+" copy("+baseName+" "+baseVar+", "+baseName+" "+thisName+") {\n");
			
			if (!superName.equals("DbObject")) w("\t\t"+superCls+"_Db.copy("+baseVar+","+thisName+");\n");
			if (superName.equals("DbStamp")) w("\t\t"+baseVar+".stamp = System.currentTimeMillis();\n");
			
			forEachField(w, t, baseName, baseVar, (e) -> 
				copyField(e, e.getSimpleName().toString(), getType(e.asType()))
			);
			w("		return "+baseVar+";\n");
			w("	}\n");
			w("\n");
		}
	
		boolean superEnd() {
			return superName.equals("DbObject") || superName.equals("DbWithId") || superName.equals("DbWithParrentId") || superName.equals("DbWithStamp");
		}
		void forEachField(Writer w, Element t, String baseName, String baseVar, Consumer<Element> doField) {
			t.getEnclosedElements().stream()
				.filter(e -> e.getKind().isField() && e.getModifiers().contains(PUBLIC) && !e.getModifiers().contains(TRANSIENT))
				.forEach(e -> doField.accept(e));
		}
    }

    void log(String msg) {
//        if (processingEnv.getOptions().containsKey("debug")) {
            processingEnv.getMessager().printMessage(Kind.NOTE, msg);
//        }
    }

    void error(String msg) {
        processingEnv.getMessager().printMessage(Kind.ERROR, msg);
    }
}

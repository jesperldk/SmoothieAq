package jesperl.dk.smoothieaq.client.inheritancetypes;

import java.util.function.*;

import gwt.material.design.client.base.*;

public class InheritanceTypeInfo<T> {
	public final Supplier<T> createNew;
	public final BiConsumer<T,MaterialWidget> addFields;
	public final Function<T, String> format;
	public InheritanceTypeInfo(Supplier<T> createNew, BiConsumer<T, MaterialWidget> addFields, Function<T, String> format) {
		this.createNew = createNew; this.addFields = addFields; this.format = format;
	}
	public T create() { return createNew.get(); }
	public void addFields(T t, MaterialWidget w) { addFields.accept(t, w); }
	public String format(T t) { return format.apply(t); }
}

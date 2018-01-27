package jesperl.dk.smoothieaq.client.inheritancetypes;

import java.util.function.*;

import gwt.material.design.client.base.*;

public class InheritanceTypeInfo<T> {
	public final Supplier<T> createNew;
	public final BiConsumer<T,MaterialWidget> addFields;
	public InheritanceTypeInfo(Supplier<T> createNew, BiConsumer<T, MaterialWidget> addFields) {
		this.createNew = createNew; this.addFields = addFields;
	}
	public T create() { return createNew.get(); }
	public void addFields(T t, MaterialWidget w) { addFields.accept(t, w); }
}

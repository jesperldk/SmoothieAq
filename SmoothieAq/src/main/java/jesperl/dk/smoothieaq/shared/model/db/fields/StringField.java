package jesperl.dk.smoothieaq.shared.model.db.fields;

import jesperl.dk.smoothieaq.shared.model.db.*;

public class StringField extends Field<String> {
	private String value = null;
	public StringField(String key) {
		super(null, null, key, String.class);
		get = () -> value; 
		set = s -> value = s;
	}
}

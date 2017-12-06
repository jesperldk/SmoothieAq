package jesperl.dk.smoothieaq.client.components;

import static jesperl.dk.smoothieaq.util.shared.Objects.*;

import com.google.gwt.user.client.ui.*;

import gwt.material.design.addins.client.combobox.*;
import gwt.material.design.client.ui.*;
import gwt.material.design.client.ui.html.*;
import jesperl.dk.smoothieaq.client.components.modal.*;
import jesperl.dk.smoothieaq.shared.model.db.*;
import jesperl.dk.smoothieaq.util.shared.error.Errors.*;

public class GuiUtil {
	
	public static Widget wModal(Widget title, Widget body, Action okAction) {
		return new ModalView(title, body, okAction);
	}

	public static MaterialTextBox wTextBox(Field<String> field) {
		MaterialTextBox textBox = new MaterialTextBox() {
			protected void onLoad() {
				super.onLoad();
				setValue(field.get());
				addChangeHandler(evt -> field.set(getValue()));
			};
		};
		textBox.setLabel(field.getKey());
		return textBox;
	}
	
	public static <T extends Enum<T>> MaterialComboBox<String> wComboBox(Field<T> field) {
		MaterialComboBox<String> comboBox = new MaterialComboBox<String>() {
			protected void onLoad() {
				super.onLoad();
				setValue(funcNotNull(field.get(), e -> e.name()));
				addValueChangeHandler(evt -> field.set(Enum.valueOf(field.getType(), getValue())));
			}
		};
		stream(field.getType().getEnumConstants()).forEach(e -> {
			Option option = new Option();
			option.setValue(e.name());
			option.setText(e.name()+"?");
			comboBox.add(option);
		});
		comboBox.setLabel(field.getKey());
		return comboBox;
	}
}

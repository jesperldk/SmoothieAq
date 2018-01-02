package jesperl.dk.smoothieaq.client.components;

import static jesperl.dk.smoothieaq.util.shared.Objects.*;

import java.util.*;
import java.util.stream.*;

import com.google.gwt.i18n.client.*;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.datepicker.client.*;

import gwt.material.design.addins.client.combobox.*;
import gwt.material.design.client.constants.*;
import gwt.material.design.client.ui.*;
import jesperl.dk.smoothieaq.client.components.modal.*;
import jesperl.dk.smoothieaq.client.enums.*;
import jesperl.dk.smoothieaq.shared.model.db.*;
import jesperl.dk.smoothieaq.util.shared.*;
import jesperl.dk.smoothieaq.util.shared.error.Errors.*;
import rx.*;

public class GuiUtil {
	
	public static MaterialIcon wIcon(IconType iconType) { return new MaterialIcon(iconType); }
	
	public static Widget wModal(Widget title, Widget body, Action okAction) {
		return new ModalView(title, body, okAction);
	}

	public static MaterialAnchorButton wFloatButton(EnumInfo enumInfo, Action doOnClick) {
		return wFloatButton(enumInfo.icon, enumInfo.bgColor, enumInfo.hoverTxt, doOnClick);
	}
	public static MaterialAnchorButton wFloatButton(IconType iconType, Color bgColor, String hoverTxt, Action doOnClick) {
		MaterialAnchorButton btn = new MaterialAnchorButton(ButtonType.FLOATING) {
			@Override protected void onLoad() {
				super.onLoad();
				if (doOnClick != null) addClickHandler(e -> doOnClick.doit());
			}
		};
		btn.setIconType(iconType);
		btn.setBackgroundColor(bgColor);
		if (hoverTxt != null)  btn.setTooltip(hoverTxt);
		if (doOnClick != null) btn.setWaves(WavesType.DEFAULT);
		return btn;
	}
	public static MaterialButton wButton(IconType iconType, boolean primary, String text, String hoverTxt, Action doOnClick) {
		MaterialButton btn = new MaterialButton() {
			@Override protected void onLoad() {
				super.onLoad();
				if (doOnClick != null) addClickHandler(e -> doOnClick.doit());
			}
		};
		btn.setText(text);
		if (iconType != null) btn.setIconType(iconType);
		if (!primary) { btn.setBackgroundColor(Color.WHITE); btn.setTextColor(Color.BLACK); }
		if (hoverTxt != null)  btn.setTooltip(hoverTxt);
		if (doOnClick != null) btn.setWaves(WavesType.LIGHT);
		return btn;
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
	
	public static MaterialIntegerBox wShortBox(Field<Short> field) {
		MaterialIntegerBox integerBox = new MaterialIntegerBox() {
			protected void onLoad() {
				super.onLoad();
				setValue(funcNotNull(field.get(), Short::intValue));
				addChangeHandler(evt -> field.set(funcNotNull(getValue(), Integer::shortValue)));
			};
		};
		integerBox.setLabel(field.getKey());
		return integerBox;
	}
	
	public static MaterialFloatBox wFloatBox(Field<Float> field) {
		MaterialFloatBox floatBox = new MaterialFloatBox() {
			protected void onLoad() {
				super.onLoad();
				setValue(field.get());
				addChangeHandler(evt -> field.set(getValue()));
			};
		};
		floatBox.setLabel(field.getKey());
		return floatBox;
	}
	
	public static <T extends Enum<T>> MaterialComboBox<String> wComboBox(Field<T> field) { return wComboBox(field, wOptions(field.getType())); }
	
	public static <T> MaterialComboBox<String> wComboBox(Field<T> field, WOptions<T> options) {
		MaterialComboBox<String> comboBox = new MaterialComboBox<String>() {
			protected void onLoad() {
				super.onLoad();
				setSingleValue(options.key(field.get()));
				addValueChangeHandler(evt -> field.set(options.value(getSingleValue())));
			}
		};
		options.options.forEach(o -> comboBox.addItem(o.text, o.key));
		comboBox.setLabel(field.getKey());
		return comboBox;
	}
	
	public static <T extends Enum<T>> WOptions<T> wOptions(Class<T> enumClass) {
		return new WOptions<T>(stream(enumClass.getEnumConstants()).map(e -> pair(e, enumClass.getSimpleName()+"."+e.name())));
	}
	public static class WOption<T> {
		public final T value;
		public final String key;
		public final String text;
		public WOption(T value, String key, String text) { this.value = value; this.key = key; this.text = text; }
	}
	public static class WOptions<T> {
		private List<WOption<T>> options = new ArrayList<>();
		private Map<String,T> toValue = new HashMap<>();
		private Map<T,String> toKey = new HashMap<>();
		
		public WOptions(Stream<Pair<T, String>> valueAndTexts) {
			options.add(new WOption<>(null, "", ""));
			toValue.put("", null);
			valueAndTexts.forEach(with((value, text) -> {
				String key = strv(options.size());
				options.add(new WOption<>(value, key, text));
				toValue.put(key, value);
				toKey.put(value, key);
			}));
		}
		
		List<WOption<T>> options() { return options; }
		T value(String key) { return toValue.get(key); }
		String key(T value) { return value == null ? "" : toKey.get(value); }
	}
	
	public static <T extends Enum<T>> MaterialListValueBox<String> wListBox(Field<T> field) { return wListBox(field, wOptions(field.getType())); }
	
	public static <T> MaterialListValueBox<String> wListBox(Field<T> field, WOptions<T> options) {
		MaterialListValueBox<String> listBox = new MaterialListValueBox<String>() {
			protected void onLoad() {
				super.onLoad();
				setValue(options.key(field.get()));
				addValueChangeHandler(evt -> field.set(options.value(getValue())));
			}
		};
		options.options.forEach(o -> listBox.addItem(o.key, o.text));
		listBox.setPlaceholder(field.getKey());
		return listBox;
	}
	public static <T> MaterialListValueBox<String> wListBox(Field<T> field, Single<WOptions<T>> options) {
		MaterialListValueBox<String> listBox = new MaterialListValueBox<String>() {
			protected void onLoad() {
				super.onLoad();
				options.subscribe(os -> {
					setValue(os.key(field.get()));
					addValueChangeHandler(evt -> field.set(os.value(getValue())));
					os.options.forEach(o -> addItem(o.key, o.text));
				});
			}
		};
		listBox.setPlaceholder(field.getKey());
		return listBox;
	}
	
	private static final DateTimeFormat weekdayFmt = DateTimeFormat.getFormat("EEE");
	private static final DateTimeFormat dateFmt = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_SHORT);
	private static final DateTimeFormat timeFmt = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.TIME_MEDIUM);
	public static String formatStamp(long stamp) {
		StringBuilder buf = new StringBuilder();
		Date nowDate = new Date();
		Date stampDate = new Date(stamp);
		
		int daysBetween = CalendarUtil.getDaysBetween(stampDate, nowDate);
		if (daysBetween == 0) buf.append("today ");
		else if (daysBetween == 1) buf.append("yesterday");
		else if (daysBetween < 7) buf.append(weekdayFmt.format(stampDate)).append("&nbsp;").append(dateFmt.format(stampDate));
		else buf.append(dateFmt.format(stampDate));
		
		Date stampReset = new Date(stamp); CalendarUtil.resetTime(stampReset);
		if (stampReset.getTime() != stamp) buf.append(" ").append(timeFmt.format(stampDate));
		
		return buf.toString();
	}
}
	
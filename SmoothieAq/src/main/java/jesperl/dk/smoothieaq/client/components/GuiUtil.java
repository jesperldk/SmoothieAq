package jesperl.dk.smoothieaq.client.components;

import static jesperl.dk.smoothieaq.client.text.EnumMessages.*;
import static jesperl.dk.smoothieaq.client.text.FieldMessages.*;
import static jesperl.dk.smoothieaq.client.text.InheritanceTypeMessages.*;
import static jesperl.dk.smoothieaq.client.text.MsgMessages.*;
import static jesperl.dk.smoothieaq.shared.util.Objects2.*;
import static jesperl.dk.smoothieaq.util.shared.Objects.*;
import static jesperl.dk.smoothieaq.util.shared.Objects.stream;

import java.util.*;
import java.util.stream.*;

import com.google.gwt.i18n.client.*;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.datepicker.client.*;

import gwt.material.design.addins.client.combobox.*;
import gwt.material.design.client.base.*;
import gwt.material.design.client.constants.*;
import gwt.material.design.client.ui.*;
import gwt.material.design.client.ui.html.*;
import jesperl.dk.smoothieaq.client.enums.*;
import jesperl.dk.smoothieaq.shared.model.db.*;
import jesperl.dk.smoothieaq.util.shared.*;
import jesperl.dk.smoothieaq.util.shared.error.*;
import jesperl.dk.smoothieaq.util.shared.error.Errors.*;
import rx.*;
import rx.Observable;

public class GuiUtil {

	public static WSingle wSingle(Observable<? extends Widget> observable) { return new WSingle(observable); }
	
	public static MaterialIcon wIcon(IconType iconType) { return new MaterialIcon(iconType); }
	
	public static Widget wModal(Widget body, Action okAction) {
		return new ModalView(body, okAction);
	}
	public static <W extends HasReadOnly> W wRo(W w, boolean readOnly) { w.setReadOnly(readOnly); return w; }
	
	private static int dropdownNo = 1;
	public static MaterialWidget wDropdown(IconType iconType, Color iconColor, String tooltip, MaterialWidget... links) {
		String activator = "A"+(dropdownNo++);
		Span span = new Span();
		MaterialIcon icon = wIconButton(iconType, iconColor, tooltip, () -> {});
		icon.setActivates(activator);
		span.add(icon);
		MaterialDropDown dropDown = new MaterialDropDown(activator);
		dropDown.setConstrainWidth(false);
		for (MaterialWidget link: links) dropDown.add(link);
		span.add(dropDown);
		return span;
	}

	public static MaterialAnchorButton wFloatButton(EnumInfo enumInfo, Action doOnClick) {
		return wFloatButton(enumInfo.icon, enumInfo.bgColor, enumInfo.hoverTxt, doOnClick);
	}
	public static MaterialAnchorButton wFloatButton(IconType iconType, Color bgColor, String tooltip, Action doOnClick) {
		MaterialAnchorButton btn = new MaterialAnchorButton(ButtonType.FLOATING) {
			@Override protected void onLoad() {
				super.onLoad();
				if (doOnClick != null) addClickHandler(e -> doOnClick.doit());
			}
		};
		btn.setIconType(iconType);
		btn.setBackgroundColor(bgColor);
		if (tooltip != null)  btn.setTooltip(tooltip);
		if (doOnClick != null) btn.setWaves(WavesType.DEFAULT);
		return btn;
	}
	public static MaterialWidget wBadge(MaterialWidget icon, Observable<Color> color, Observable<Float> no, boolean hideWhenZero) {
		Span span = new Span();
		span.addStyleName("badgeable");
		MaterialBadge  badge = new MaterialBadge() {
			Subscriptions subscriptions = new Subscriptions();
			@Override protected void onLoad() {
				super.onLoad();
				if (hideWhenZero) span.addStyleName("hide");
				addStyleName("hide");
				subscriptions.subscripe(no, n -> {
					if (n < 0.01) {
						if (hideWhenZero) span.addStyleName("hide");
						addStyleName("hide");
					} else {
						if (hideWhenZero) span.removeStyleName("hide");
						removeStyleName("hide");
						setText(strv(n.intValue()));
					}
				});
				setBackgroundColor(Color.RED);
				if (color != null) subscriptions.subscripe(color, c -> setBackgroundColor(c));
			}
			@Override protected void onUnload() {
				subscriptions.unsubscripe();
				super.onUnload();
			}
		};
		badge.addStyleName("smallbadge");
//		badge.setCircle(true);
		span.add(badge);
		span.add(icon);
		return span;
	}
	public static MaterialIcon wIconButton(IconType iconType, Color iconColor, String tooltip, Action doOnClick) {
		MaterialIcon icon = new MaterialIcon(iconType) {
			@Override protected void onLoad() {
				super.onLoad();
				if (doOnClick != null) addClickHandler(e -> doOnClick.doit());
			}
		};
		icon.setCircle(true);
		if (iconColor != null) icon.setIconColor(iconColor);
		if (tooltip != null)  icon.setTooltip(tooltip);
		if (doOnClick != null) icon.setWaves(WavesType.DEFAULT);
		return icon;
	}
	public static MaterialButton wButton(IconType iconType, boolean primary, String text, String tooltip, Action doOnClick) {
		MaterialButton btn = new MaterialButton() {
			@Override protected void onLoad() {
				super.onLoad();
				if (doOnClick != null) addClickHandler(e -> doOnClick.doit());
			}
		};
		btn.setText(text);
		if (iconType != null) btn.setIconType(iconType);
		if (!primary) { btn.setBackgroundColor(Color.WHITE); btn.setTextColor(Color.BLACK); }
		if (tooltip != null)  btn.setTooltip(tooltip);
		if (doOnClick != null) btn.setWaves(WavesType.LIGHT);
		return btn;
	}
	public static MaterialLink wLink(boolean primary, String text, String tooltip, Action doOnClick) {
		return wLink(null, primary, text, tooltip, doOnClick);
	}
	public static MaterialLink wLink(IconType iconType, boolean primary, String text, String tooltip, Action doOnClick) {
		MaterialLink btn = new MaterialLink() {
			@Override protected void onLoad() {
				super.onLoad();
				if (doOnClick != null) addClickHandler(e -> doOnClick.doit());
			}
		};
		btn.setText(text);
//		if (!primary) { btn.setBackgroundColor(Color.WHITE); btn.setTextColor(Color.BLACK); }
		if (iconType != null) { btn.setIconType(iconType); btn.setIconPosition(IconPosition.LEFT); }
		if (tooltip != null)  btn.setTooltip(tooltip);
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
		setLabel(textBox,field);
		return textBox;
	}
	protected static void setLabel(MaterialValueBox<?> widget, Field<?> field) {
		String name = fieldMsg.name(field);
		widget.setPlaceholder(name);
		String help = fieldMsg.help(field);
		if (isNotEmpty(help) && !help.equals(name)) widget.setTooltip(help);
	}
	
	public static MaterialIntegerBox wShortBox(Field<Short> field) {
		MaterialIntegerBox integerBox = new MaterialIntegerBox() {
			protected void onLoad() {
				super.onLoad();
				setValue(funcNotNull(field.get(), Short::intValue));
				addChangeHandler(evt -> field.set(funcNotNull(getValue(), Integer::shortValue)));
			};
		};
		setLabel(integerBox,field);
		return integerBox;
	}
	
	public static MaterialIntegerBox wIntegerBox(Field<Integer> field) {
		MaterialIntegerBox integerBox = new MaterialIntegerBox() {
			protected void onLoad() {
				super.onLoad();
				setValue(field.get());
				addChangeHandler(evt -> field.set(getValue()));
			};
		};
		setLabel(integerBox,field);
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
		setLabel(floatBox,field);
		return floatBox;
	}
	
	public static <T extends Enum<T>> MaterialComboBox<String> wComboBox(Field<T> field) { return wComboBox(field, wOptions(field.getType())); }
	public static <T> MaterialComboBox<String> wComboBox(Field<T> field, WOptions<T> options) { return wComboBox(field, Single.just(options)); }
	public static <T> MaterialComboBox<String> wComboBox(Field<T> field, Single<WOptions<T>> options) {
		MaterialComboBox<String> comboBox = new MaterialComboBox<String>() {
			Subscription subscription;
			protected void onLoad() {
				super.onLoad();
				subscription = options.subscribe(os -> {
					addValueChangeHandler(evt -> field.set(os.value(getSingleValue())));
					os.options.forEach(o -> {
						Option option = addItem(o.text, o.key);
						if (o.help != null && !o.text.equals(o.help)) option.setTitle(o.help);
					});
					setSingleValue(os.key(field.get()));
				});
			}
			@Override protected void onUnload() {
				subscription = unsubscribe(subscription);
				super.onUnload();
			}
		};
		String name = fieldMsg.name(field);
		comboBox.setPlaceholder(name);
		String help = fieldMsg.help(field);
		if (isNotEmpty(help) && !help.equals(name)) comboBox.setTooltip(help);
		return comboBox;
	}
	
	public static WOptions<String> wTypeOptions(Set<String> types) { 
		return new WOptions<String>(types.stream().map(triple(s -> capitalize(typeMsg.typeLongName(s)), s -> typeMsg.typeHelp(s))));
	}
	public static <T extends Enum<T>> WOptions<T> wOptions(Class<T> enumClass) { return wOptions(stream(enumClass.getEnumConstants())); }
	public static <T extends Enum<T>> WOptions<T> wOptions(Set<T> enums) { return wOptions(enums.stream()); }
	public static <T extends Enum<T>> WOptions<T> wOptions(Stream<T> enums) {
		return new WOptions<T>(enums.map(triple(e -> capitalize(enumMsg.valueLongName(e)), e -> enumMsg.valueHelp(e))));
	}
	public static class WOption<T> {
		public final T value;
		public final String key;
		public final String text;
		public final String help;
		public WOption(T value, String key, String text, String help) { this.value = value; this.key = key; this.text = text; this.help = help; }
	}
	public static class WOptions<T> {
		private List<WOption<T>> options = new ArrayList<>();
		private Map<String,T> toValue = new HashMap<>();
		private Map<T,String> toKey = new HashMap<>();
		
		public WOptions(Stream<Triple<T, String, String>> valueAndTexts) {
			options.add(new WOption<>(null, "", "", null));
			toValue.put("", null);
			valueAndTexts.forEach(with((value, text, help) -> {
				String key = strv(options.size());
				options.add(new WOption<>(value, key, text, help));
				toValue.put(key, value);
				toKey.put(value, key);
			}));
		}
		
		List<WOption<T>> options() { return options; }
		T value(String key) { return toValue.get(key); }
		String key(T value) { return value == null ? "" : toKey.get(value); }
	}
	
	public static <T extends Enum<T>> MaterialListValueBox<String> wListBox(Field<T> field) { return wListBox(field, wOptions(field.getType())); }
	public static <T> MaterialListValueBox<String> wListBox(Field<T> field, WOptions<T> options) { return wListBox(field, Single.just(options)); }
	public static <T> MaterialListValueBox<String> wListBox(Field<T> field, Single<WOptions<T>> options) {
		MaterialListValueBox<String> listBox = new MaterialListValueBox<String>() {
			Subscription subscription;
			protected void onLoad() {
				super.onLoad();
				subscription = options.subscribe(os -> {
					addValueChangeHandler(evt -> field.set(os.value(getValue())));
					os.options.forEach(o -> addItem(o.key, o.text));
					setValue(os.key(field.get()));
				});
			}
			@Override protected void onUnload() {
				subscription = unsubscribe(subscription);
				super.onUnload();
			}
		};
		String name = fieldMsg.name(field);
		listBox.setPlaceholder(name);
		String help = fieldMsg.help(field);
		if (isNotEmpty(help) && !help.equals(name)) listBox.setTooltip(help);
		listBox.setPlaceholder(field.getKey());
		return listBox;
	}
	
	public static void wToast(String str) { MaterialToast.fireToast(str); }
	public static void wToastWarn(String str) { MaterialToast.fireToast(str); }
	public static void wToastError(String str) { MaterialToast.fireToast(str); }
	public static void wToastError(jesperl.dk.smoothieaq.util.shared.error.Error error) { MaterialToast.fireToast(msgMsg.format(error)); }
	public static void wToastError(ErrorException error) { MaterialToast.fireToast(msgMsg.format(error.getError())); }
	
	private static final DateTimeFormat weekdayFmt = DateTimeFormat.getFormat("EEE");
	private static final DateTimeFormat dateFmt = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_SHORT);
	private static final DateTimeFormat timeFmt = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.TIME_MEDIUM);
	private static final DateTimeFormat timeShortFmt = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.TIME_SHORT);
	public static String formatStamp(long stamp) { return formatStamp(stamp, false); }
	public static String formatStampMinutes(long stamp) { return formatStamp(stamp, true); }
	public static String formatStamp(long stamp, boolean noSeconds) {
		StringBuilder buf = new StringBuilder();
		Date nowDate = new Date();
		Date stampDate = new Date(stamp);
		
		int daysBetween = CalendarUtil.getDaysBetween(stampDate, nowDate);
		if (daysBetween == 0) buf.append("today ");
		else if (daysBetween == 1) buf.append("yesterday");
		else if (daysBetween < 7) buf.append(weekdayFmt.format(stampDate)).append("&nbsp;").append(dateFmt.format(stampDate));
		else buf.append(dateFmt.format(stampDate));
		
		Date stampReset = new Date(stamp); CalendarUtil.resetTime(stampReset);
		if (stampReset.getTime() != stamp) buf.append(" ").append((noSeconds?timeShortFmt:timeFmt).format(stampDate));
		
		return buf.toString();
	}
	
}
	
package jesperl.dk.smoothieaq.client.enums;

import gwt.material.design.client.constants.*;

public class EnumInfo {
	public final IconType icon;
	public final Color bgColor;
	public final String hoverTxt;
	public EnumInfo(IconType icon, Color bgColor, String hoverTxt) {
		this.icon = icon; this.bgColor = bgColor; this.hoverTxt = hoverTxt;
	}
	public EnumInfo(Enum<?> enm, IconType icon, Color bgColor) {
		this(icon, bgColor, enm.toString());
	}
}
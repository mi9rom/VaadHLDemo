package com.vaadHL.example.i18n;

import java.util.Locale;

import com.vaadHL.i18n.VaadHL18Def;
import com.vaadHL.i18n.VaadHLi18n;

public class MyI18 extends VaadHLi18n {
	public static String source() {
		return MyI18.class.getPackage().getName() + ".Strings";
	}

	public MyI18() {
		super();
		addBundleSourceF(VaadHL18Def.source());
		addBundleSourceF(source());
	}

	public MyI18(Locale locale) {
		super();
		addBundleSourceF(VaadHL18Def.source());
		addBundleSourceF(source());
		setLocale(locale);
	}
}

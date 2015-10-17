package com.vaadHL.example.base;

import java.util.TreeMap;

import com.vaadHL.window.customize.IWinCustomizerFactory;

public class CustomizerFactory implements IWinCustomizerFactory {

	TreeMap<String, Object> container = new TreeMap<>();
	
	public CustomizerFactory() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object getCustomizer(String winId) {
		return container.get(winId);
	}

	
	public void put(String winId, Object o)
	{
		container.put(winId, o);
	}
	
}

package com.vaadHL.example.base;

import com.vaadHL.i18n.I18Sup;
import com.vaadHL.utl.action.ActionsIds;

/**
 * For demo purposes only. You don't have to create this class
 * 
 */
public class MyActionsIds {
	public final static int MOCK_ID = 9999999;

	public MyActionsIds() {
		// TODO Auto-generated constructor stub
	}
	
	public static String getName(I18Sup i18,int ac) {
		switch (ac) {
		case MOCK_ID:
			return "Mock action";
		
		default:
			return ActionsIds.getName(i18,ac);
		}
	}
}

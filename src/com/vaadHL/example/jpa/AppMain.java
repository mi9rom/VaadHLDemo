/*
 * Copyright 2015 Mirosław Romaniuk (mi9rom@gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadHL.example.jpa;

import java.util.Locale;

import javax.servlet.annotation.WebServlet;

import com.vaadHL.AppContext;
import com.vaadHL.example.i18n.MyI18;
import com.vaadHL.utl.msgs.Msgs;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;

@SuppressWarnings("serial")
@Theme("oratstjpa")
public class AppMain extends UI {
	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = AppMain.class)
	public static class Servlet extends VaadinServlet {
	}

	private static final long serialVersionUID = 1891752310777496322L;

	public AppMain() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void init(VaadinRequest request) {
		setLocale(Locale.getDefault());
		setContent(new MainW(new AppContext(new Msgs(), new MyI18(
				getLocale()))));
	}

}

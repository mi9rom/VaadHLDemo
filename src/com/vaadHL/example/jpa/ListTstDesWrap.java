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

import com.vaadin.ui.ComboBox;

public class ListTstDesWrap extends ListTstDes {

	/**
	 * 
	 */
	private static final long serialVersionUID = -270393126615674803L;

	public ListTstDesWrap() {
		// TODO Auto-generated constructor stub
	}

	public com.vaadin.ui.Table getTable() {
		return table;
	}

	public com.vaadin.ui.TextField getTfFilter1() {
		return tfFilter1;
	}

	public com.vaadin.ui.TextField getTfDetail() {
		return tfDetail;
	}

	public ComboBox getCbSearchBy() {
		return cbSearchBy;
	}

	public com.vaadin.ui.Button getFbtGenPerm() {
		return btGenPerm;
	}
}

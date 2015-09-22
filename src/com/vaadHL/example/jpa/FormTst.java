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

import javax.persistence.EntityManager;

import org.vaadin.addons.lazyquerycontainer.LazyEntityContainer;

import com.vaadHL.utl.converter.StringToPlainIntegerConverter;
import com.vaadHL.utl.msgs.IMsgs;
import com.vaadHL.window.EM.SingIeItemFWindow;
import com.vaadHL.window.base.MWLaunchMode;
import com.vaadHL.window.base.perm.IWinPermChecker;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Component;
import com.vaadin.ui.TextField;

public class FormTst extends SingIeItemFWindow {

	private static final long serialVersionUID = -4219526966854534522L;
	private FormDesWrap mUI;

	@PropertyId("firstName")
	private TextField fFirstName;
	@PropertyId("lastName")
	private TextField tfLastName;
	@PropertyId("yearOfBirth")
	private TextField tfYearOfBirth;

	public FormTst(IWinPermChecker permChecker, CustomizeFWin customizeFWin,
			MWLaunchMode launchMode, EntityManager em,
			LazyEntityContainer<?> container, Object rowId, IMsgs msgs,
			boolean readOnlyW) {

		super("M001", "Form Window Title", permChecker, customizeFWin,
				launchMode, em, container, rowId, msgs, readOnlyW);
		
		//It is very important to include this
		if (!approvedToOpen)
			return;
		
		
		setWidth("800px");
		setHeight("500px");
		
		tfYearOfBirth.setConverter(new StringToPlainIntegerConverter());

		// field level validations
		fFirstName.addValidator(new StringLengthValidator(
				"The first name must be 1-15 letters ", 1, 15, false));
		fFirstName.setRequiredError("Value is required");

		tfLastName.addValidator(new StringLengthValidator(
				"The last name must be 1-20 letters ", 1, 20, false));
		tfLastName.setRequiredError("Value is required");

		String tfYearOfBirthMsg = "Year must be between 1920 and 2010";
		tfYearOfBirth.addValidator(new IntegerRangeValidator(tfYearOfBirthMsg,
				1920, 2010));
		tfYearOfBirth.setRequiredError(tfYearOfBirthMsg);
	}

	@Override
	public void initConstructorWidgets() {
		// -- In this example I utilize the design editor --
		mUI = new FormDesWrap();
		fFirstName = mUI.getFtfFirstName();
		tfLastName = mUI.getFtfLastName();
		tfYearOfBirth = mUI.getFtfYearOfBirth();
		/*
		 * dpDate = mUI.getFdpDate();
		 * dpDate.setDateFormat("yyyy-MM-dd HH:mm:ss");
		 */
		// ---------------------------------------------------
	}

	@Override
	public Component makeMiddleArea() {
		return mUI;
	}

	// is overriden to bind fields to the FieldGroup
	@Override
	protected void bind(Item item) {
		super.bind(item);
		binder.bindMemberFields(this);
	}

	/**
	 * Example item level edition checking
	 */
	@Override
	protected boolean canEditMsg() {
		String noEdit = "Abbassi";
		if (tfLastName.getValue().equals(noEdit)) {
			getMsgs().showInfo(
					"Edition of the " + noEdit
							+ " is forbidden.");
			return false;
		} else
			return true;
	}

	/**
	 * Example item level deletion checking
	 */
	@Override
	protected boolean canDeleteMsg() {
		String noEdit = "Abbassi";
		if (tfLastName.getValue().equals(noEdit)) {
			getMsgs().showInfo(
					"You are not allowed to delete " + noEdit
							+ " unless you will be deleted!");
			return false;
		} else
			return true;
	}

	/**
	 * Cross field validation
	 */
	@Override
	public boolean validateSave(boolean showMessages) {
		if (!super.validateSave(showMessages))
			return false;

		if (!(fFirstName.isValid() && tfLastName.isValid() && tfYearOfBirth
				.isValid())) {
			if (showMessages)
				getMsgs()
						.showWarning(
								"There is at least one invalid field. Correct please or discard changes.");
			return false;
		}

		try {
			if (tfLastName.getValue().equals("Aaron")
					&& Integer.parseInt(tfYearOfBirth.getValue()) < 1980) {
				if (showMessages)
					getMsgs().showWarning("Aaron can't be so old :)");
				return false;
			}
		} catch (NumberFormatException e) {
			if (showMessages)
				getMsgs().showError("wrong year", 1200);
			return false;
		}

		return true;
	}
}

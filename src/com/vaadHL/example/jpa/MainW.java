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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.vaadHL.test.tstPerm.TestPermChecker;
import com.vaadHL.test.tstPerm.WinPermCheckerBin;
import com.vaadHL.utl.helper.ComponentHelper;
import com.vaadHL.utl.helper.ItemHelper;
import com.vaadHL.window.base.BaseListWindow.ChoosingMode;
import com.vaadHL.window.base.ICustomizeEditWin.AutoSaveDiscard;
import com.vaadHL.window.base.ICustomizeListWindow;
import com.vaadHL.window.base.ICustomizeListWindow.DoubleClickAc;
import com.vaadHL.window.base.IListSelectionAction;
import com.vaadHL.window.base.LWCustomize;
import com.vaadHL.window.base.LWCustomizeLM;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItem;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.UI;

public class MainW extends MainDes {

	private static final long serialVersionUID = 438202101251810416L;
	private static String PERS_UNIT = "OraTstJPA";
	public static final EntityManagerFactory ENTITY_MANAGER_FACTORY = Persistence
			.createEntityManagerFactory(PERS_UNIT);
	public EntityManager em;
	private CustomizeFWin customizeFWin;
	ChoosingMode listChoosingMode;
	boolean listReadOnly;

	public MainW() {
		super();
		em = ENTITY_MANAGER_FACTORY.createEntityManager();
		String[] permisions = { "Open", "Edit", "Create", "Delete" , "Generic perm. test" };

		// ========== list window launch mode =============

		chkReadOnly.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				listReadOnly = (boolean) event.getProperty().getValue();

			}
		});

		String[] choosingModeS = { "no choose", "single choose",
				"multiple choose"};
		ComponentHelper.populateWIds(cbChoosingMode, choosingModeS);
		cbChoosingMode.setNullSelectionAllowed(false);
		cbChoosingMode.addValueChangeListener(new ValueChangeListener() {

			private static final long serialVersionUID = 2649106479371080195L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				int val = (int) cbChoosingMode.getValue();
				listChoosingMode = (ChoosingMode.values()[val]);
				if (val == 0) {
					laCustNCM.setVisible(true);
					laCustChM.setVisible(false);
				} else {
					laCustNCM.setVisible(false);
					laCustChM.setVisible(true);
				}
			}
		});
		cbChoosingMode.setValue(0);

		// ========== list window customization =============

		String[] doubleClickModeS = { "open the form in the details mode",
				"open the form in the view mode",
				"open the form in the edit mode", "delete the clicked record",
				"create a new record", "choose the selected record(s)", "do nothing" };

		// no-choose mode
		final LWCustomizeLM customizeLWNoCh = new LWCustomizeLM();
		BeanItem<LWCustomizeLM> customizeLWNoChBI = new BeanItem<LWCustomizeLM>(
				customizeLWNoCh);

		chkDetails.setPropertyDataSource(customizeLWNoChBI
				.getItemProperty("detailsFunc"));
		chkAddRec.setPropertyDataSource(customizeLWNoChBI
				.getItemProperty("addFunc"));
		chkDeleteRec.setPropertyDataSource(customizeLWNoChBI
				.getItemProperty("deleteFunc"));
		chkEditRec.setPropertyDataSource(customizeLWNoChBI
				.getItemProperty("editFunc"));
		chkViewRec.setPropertyDataSource(customizeLWNoChBI
				.getItemProperty("viewFunc"));

		ComponentHelper.populateWIdsSkip(cbDoubleClick, doubleClickModeS,
				new Integer[] { 5 });
		cbDoubleClick.addValueChangeListener(new ValueChangeListener() {

			private static final long serialVersionUID = 2649106479371080195L;

			@Override
			public void valueChange(ValueChangeEvent event) {

				customizeLWNoCh.setDoubleClickAc(((DoubleClickAc.values()[((int) cbDoubleClick
						.getValue())])));

			}
		});
		cbDoubleClick.setNullSelectionAllowed(false);
		cbDoubleClick.setValue(0);

		// choose mode
		final LWCustomizeLM customizeLWChMo = new LWCustomizeLM();
		BeanItem<LWCustomizeLM> customizeLWBIChMo = new BeanItem<LWCustomizeLM>(
				customizeLWChMo);

		chkDetailsChM.setPropertyDataSource(customizeLWBIChMo
				.getItemProperty("detailsFunc"));
		chkAddRecChM.setPropertyDataSource(customizeLWBIChMo
				.getItemProperty("addFunc"));
		chkDeleteRecChM.setPropertyDataSource(customizeLWBIChMo
				.getItemProperty("deleteFunc"));
		chkEditRecChM.setPropertyDataSource(customizeLWBIChMo
				.getItemProperty("editFunc"));
		chkViewRecChM.setPropertyDataSource(customizeLWBIChMo
				.getItemProperty("viewFunc"));

		ComponentHelper.populateWIds(cbDoubleClickChM, doubleClickModeS);
		cbDoubleClickChM.addValueChangeListener(new ValueChangeListener() {

			private static final long serialVersionUID = -5472665008056617982L;

			@Override
			public void valueChange(ValueChangeEvent event) {

				customizeLWChMo.setDoubleClickAc((DoubleClickAc.values()[((int) cbDoubleClickChM
						.getValue())]));

			}
		});
		cbDoubleClickChM.setNullSelectionAllowed(false);
		cbDoubleClickChM.setValue(5);

		final LWCustomize customizeLW = new LWCustomize(customizeLWChMo,
				customizeLWNoCh);

		// ========== list window permissions =============
		final WinPermCheckerBin winLPermChecker = new WinPermCheckerBin();
		ComponentHelper.populateWIds(ogPermLW, permisions);
		ogPermLW.setMultiSelect(true);
		ogPermLW.setValue(new HashSet<Integer>(Arrays.asList(0, 1, 2, 3)));
		winLPermChecker.fromSet((Set<Integer>) ogPermLW.getValue());
		ogPermLW.addValueChangeListener(new ValueChangeListener() {

			private static final long serialVersionUID = -8796909775190248110L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				Property<?> p = event.getProperty();
				Set<Integer> s = (Set<Integer>) p.getValue();
				winLPermChecker.fromSet(s);
			}
		});

		// ========== form window customization =============
		customizeFWin = new CustomizeFWin();
		final WinPermCheckerBin winFPermChecker = new WinPermCheckerBin();
		final BeanItem<CustomizeFWin> customizeFWinBI = new BeanItem<CustomizeFWin>(
				customizeFWin);
		chkAskCreate.setPropertyDataSource(customizeFWinBI
				.getItemProperty("askCreate"));
		chkAskDelete.setPropertyDataSource(customizeFWinBI
				.getItemProperty("askDelete"));
		chkAskDiscard.setPropertyDataSource(customizeFWinBI
				.getItemProperty("askDiscard"));
		chkAskSave.setPropertyDataSource(customizeFWinBI
				.getItemProperty("askSave"));
		chkPrevNext.setPropertyDataSource(customizeFWinBI
				.getItemProperty("prevNextFunc"));

		String[] autoSaveDiscard = {
				"ask to save or discard changes and close the window",
				"save changes without asking and close the window",
				"discard changes without asking and close the window",
				"show the message but do not close the window" };
		ComponentHelper.populateWIds(cbAutoSaveDiscard, autoSaveDiscard);
		cbAutoSaveDiscard.setNullSelectionAllowed(false);
		cbAutoSaveDiscard.setValue(0);
		cbAutoSaveDiscard.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = -5304522804592982630L;

			@Override
			public void valueChange(ValueChangeEvent event) {

				customizeFWin.setAutoSaveDiscard(AutoSaveDiscard.values()[((int) cbAutoSaveDiscard
						.getValue())]);
			}
		});

		String[] closinMethod = { "show the OK and CANCEL button",
				"show the CLOSE button only" };
		ComponentHelper.populateWIds(cbClosingMethod, closinMethod);
		cbClosingMethod.setValue(0);
		cbClosingMethod.setNullSelectionAllowed(false);
		cbClosingMethod.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = -8412527417505085156L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				customizeFWin
						.setShowOKCancel(((int) cbClosingMethod.getValue()) == 0 ? true
								: false);
			}
		});

		// ========== form window permissions =============
		ComponentHelper.populateWIds(ogPermFW, permisions);
		ogPermFW.setMultiSelect(true);
		ogPermFW.setValue(new HashSet<Integer>(Arrays.asList(0, 1, 2, 3)));
		winFPermChecker.fromSet((Set<Integer>) ogPermLW.getValue());
		ogPermFW.addValueChangeListener(new ValueChangeListener() {

			private static final long serialVersionUID = 6595195704541400511L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				Property<?> p = event.getProperty();
				@SuppressWarnings("unchecked")
				Set<Integer> s = (Set<Integer>) p.getValue();
				winFPermChecker.fromSet(s);
			}
		});

		// ========== mock permission setting =============
		final TestPermChecker testPermChecker = new TestPermChecker();
		testPermChecker.add("L001", winLPermChecker);
		testPermChecker.add("M001", winFPermChecker);

		// ==========List selection results =============
		final IListSelectionAction selAction = new IListSelectionAction() {

			@Override
			public void Confirm(Object items) {
				StringBuffer sb = new StringBuffer();
				sb.append("Choosed:");
				if (items == null)
					sb.append("nothing");
				else {
					Set<Object> se;
					if (items instanceof Set<?>)
						se = (Set<Object>) items;
					else {
						se = new HashSet<Object>();
						se.add(items);
					}

					for (Object it : se) {
						ItemHelper item = new ItemHelper((Item) it);
						sb.append('\n');
						sb.append(item.getString("firstName"));
						sb.append(" ");
						sb.append(item.getString("lastName"));
						sb.append(" ");
						sb.append(item.getString("yearOfBirth"));
					}

				}
				taChoosed.setReadOnly(false);
				taChoosed.setValue(sb.toString());
				taChoosed.setReadOnly(true);
			}

			@Override
			public void Cancel(Object items) {
				taChoosed.setReadOnly(false);
				taChoosed.setValue("Canceled:\n");
				taChoosed.setReadOnly(true);

			}

			@Override
			public void Exit(Object items) {
				taChoosed.setReadOnly(false);
				taChoosed.setValue("Closed in the no-selection mode");
				taChoosed.setReadOnly(true);

			}
		};

		// ========== run the test ============================
		btRunTest.addClickListener(new ClickListener() {
			private static final long serialVersionUID = -5384467222048646753L;

			@Override
			public void buttonClick(ClickEvent event) {

				ListTst sub = new ListTst(testPermChecker, customizeLW,
						listChoosingMode, listReadOnly, em, null,
						customizeFWin, testPermChecker, selAction);
				UI.getCurrent().addWindow(sub);
			}
		});

		// exit
		btExit.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 3384397496569551149L;

			@Override
			public void buttonClick(ClickEvent event) {
				for (final UI ui : VaadinSession.getCurrent().getUIs())
					ui.access(new Runnable() {

						@Override
						public void run() {
							ui.getPage().setLocation("/VaadHLSite");
						}
					});
				getSession().close();
			}
		});
		lbDemoVer.setValue(Version.getFullVersion());
		lbHLVer.setValue(com.vaadHL.Version.getFullVersion());
		lbVaadVer.setValue(com.vaadin.shared.Version.getFullVersion());
	}
}

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

import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.vaadHL.example.base.MyActionsIds;
import com.vaadHL.test.tstPerm.TestPermCheckerB;
import com.vaadHL.test.tstPerm.TestPermCheckerB.PermItem;
import com.vaadHL.utl.helper.ComponentHelper;
import com.vaadHL.utl.helper.ItemHelper;
import com.vaadHL.window.base.BaseListWindow.ChoosingMode;
import com.vaadHL.window.base.ICustomizeEditWin.AutoSaveDiscard;
import com.vaadHL.window.base.ICustomizeListWindow.DoubleClickAc;
import com.vaadHL.window.base.IListSelectionAction;
import com.vaadHL.window.base.LWCustomize;
import com.vaadHL.window.base.LWCustomizeLM;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.AbstractBeanContainer.BeanIdResolver;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.TableFieldFactory;
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
	private TestPermCheckerB permissions;

	public MainW() {
		super();
		em = ENTITY_MANAGER_FACTORY.createEntityManager();

		// ========== list window launch mode =============

		chkReadOnly.addValueChangeListener(new ValueChangeListener() {
		private static final long serialVersionUID = -5304522804592982630L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				listReadOnly = (boolean) event.getProperty().getValue();

			}
		});

		String[] choosingModeS = { "no choose", "single choose",
				"multiple choose" };
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
				"create a new record", "choose the selected record(s)",
				"do nothing" };

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

		// ========== form window customization =============
		customizeFWin = new CustomizeFWin();
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
				"ask to save or discard changes and close",
				"save changes without asking and close",
				"discard changes without asking and close",
				"show the message but do not close" };
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

		// ========== mock permissions =============
		permissions = new TestPermCheckerB();
		permissions.put("L001",MyActionsIds.MOCK_ID,false);
		
		BeanContainer<String, PermItem> permContainer = new BeanContainer<String, PermItem>(
				PermItem.class);
		permContainer
				.setBeanIdResolver(new BeanIdResolver<String, TestPermCheckerB.PermItem>() {
					private static final long serialVersionUID = -2858748109465649052L;

					@Override
					public String getIdForBean(PermItem bean) {
						return bean.getWinId()
								+ Integer.toString(bean.getPermId());
					}
				});
		permContainer.addAll(permissions.getContainer().values());

		tPerm.setContainerDataSource(permContainer);
		tPerm.setPageLength(15);

		// immediately
		tPerm.setEditable(true);
		tPerm.setSelectable(false);
		tPerm.setColumnReorderingAllowed(true);
		tPerm.setColumnCollapsingAllowed(true);

		tPerm.addGeneratedColumn("permission", new ColumnGenerator() {
		private static final long serialVersionUID = 8174249667866723293L;

			@Override
			public Object generateCell(Table source, Object itemId,
					Object columnId) {
				Item itm = source.getItem(itemId);
				int acId = (int) itm.getItemProperty("permId").getValue();

				return MyActionsIds.getName(acId);

			}
		});

		tPerm.addGeneratedColumn("winIdShow", new ColumnGenerator() {
			private static final long serialVersionUID = 7789570007862946545L;

			@Override
			public Object generateCell(Table source, Object itemId,
					Object columnId) {
				Item itm = source.getItem(itemId);
				String v = (String) itm.getItemProperty("winId").getValue();

				return v;

			}
		});

		tPerm.setTableFieldFactory(new TableFieldFactory() {
			private static final long serialVersionUID = 7459048926545235356L;

			public Field<?> createField(Container container, Object itemId,
					Object propertyId, Component uiContext) {
				Field<?> f = DefaultFieldFactory.get().createField(container,
						itemId, propertyId, uiContext);
				if ("enabled".equals(propertyId))
					f.setCaption("");
				return f;
			}
		});

		tPerm.setVisibleColumns("winIdShow", "permission", "enabled");
		tPerm.setColumnWidth("winIdShow", 90);
		tPerm.setColumnHeader("winIdShow", "Window Id");
		tPerm.setColumnWidth("permission", -1);
		tPerm.setColumnHeader("permission", "Permission");
		tPerm.setColumnWidth("enabled", 60);
		tPerm.setColumnHeader("enabled", "Active");

		// ==========List selection results =============
		final IListSelectionAction selAction = new IListSelectionAction() {

			@SuppressWarnings("unchecked")
			@Override
			public void Confirm(Object items) {
				StringBuffer sb = new StringBuffer();
				sb.append("Choosen:");
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

				ListTst sub = new ListTst(permissions, customizeLW,
						listChoosingMode, listReadOnly, em, null,
						customizeFWin, permissions, selAction);
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
		
		// versions info
		lbDemoVer.setValue(Version.getFullVersion());
		lbHLVer.setValue(com.vaadHL.Version.getFullVersion());
		lbVaadVer.setValue(com.vaadin.shared.Version.getFullVersion());
	}
}

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
import java.util.Locale;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.vaadHL.AppContext;
import com.vaadHL.example.base.MyActionsIds;
import com.vaadHL.example.base.Version;
import com.vaadHL.i18n.I18Sup;
import com.vaadHL.test.tstPerm.TestPermCheckerB;
import com.vaadHL.test.tstPerm.TestPermCheckerB.PermItem;
import com.vaadHL.utl.helper.ComponentHelper;
import com.vaadHL.utl.helper.ItemHelper;
import com.vaadHL.window.base.BaseListWindow;
import com.vaadHL.window.base.BaseListWindow.ChoosingMode;
import com.vaadHL.window.base.BaseListWindow.CloseCause;
import com.vaadHL.window.base.ICustomizeEditWin.AutoSaveDiscard;
import com.vaadHL.window.base.ICustomizeListWindow.DoubleClickAc;
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
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

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

	private final AppContext appContext;

	private volatile boolean disableListeners = false;
	private I18Sup m;

	public MainW(final AppContext appContext) {
		super();
		this.appContext = appContext;
		em = ENTITY_MANAGER_FACTORY.createEntityManager();
		m = appContext.getI18();

		// ========== list window launch mode =============

		chkReadOnly.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = -5304522804592982630L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				listReadOnly = (boolean) event.getProperty().getValue();

			}
		});

		ComponentHelper.populateWIds(cbChoosingMode,
				m.getArryString("choosingModeS"));
		cbChoosingMode.setNullSelectionAllowed(false);
		cbChoosingMode.addValueChangeListener(new ValueChangeListener() {

			private static final long serialVersionUID = 2649106479371080195L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				if (disableListeners)
					return;
				Object o = cbChoosingMode.getValue();
				if (o == null)
					return;
				int val = (int) o;
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

		String[] lanuageS = { "Default language", "Deutsch", "English",
				"Polski" };
		final Locale[] locale = { Locale.getDefault(), new Locale("de", "DE"),
				new Locale("en", "US"), new Locale("pl", "PL") };
		ComponentHelper.populateWIds(cbLanguage, lanuageS);
		cbLanguage.setValue(0);
		cbLanguage.setNullSelectionAllowed(false);
		cbLanguage.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				int val = (int) cbLanguage.getValue();
				setLocale(locale[val]);
			}
		});

		// ========== list window customization =============

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

		ComponentHelper.populateWIdsSkip(cbDoubleClick,
				m.getArryString("doubleClickModeS"), new Integer[] { 5 });
		cbDoubleClick.addValueChangeListener(new ValueChangeListener() {

			private static final long serialVersionUID = 2649106479371080195L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				if (disableListeners)
					return;
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

		cbDoubleClickChM.addValueChangeListener(new ValueChangeListener() {

			private static final long serialVersionUID = -5472665008056617982L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				if (disableListeners)
					return;
				customizeLWChMo.setDoubleClickAc((DoubleClickAc.values()[((int) cbDoubleClickChM
						.getValue())]));

			}
		});
		cbDoubleClickChM.setNullSelectionAllowed(false);

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

		ComponentHelper.populateWIds(cbAutoSaveDiscard,
				m.getArryString("autoSaveDiscard"));
		cbAutoSaveDiscard.setNullSelectionAllowed(false);
		cbAutoSaveDiscard.setValue(0);
		cbAutoSaveDiscard.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = -5304522804592982630L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				if (disableListeners)
					return;
				customizeFWin.setAutoSaveDiscard(AutoSaveDiscard.values()[((int) cbAutoSaveDiscard
						.getValue())]);
			}
		});

		ComponentHelper.populateWIds(cbClosingMethod,
				m.getArryString("closinMethod"));
		cbClosingMethod.setValue(0);
		cbClosingMethod.setNullSelectionAllowed(false);
		cbClosingMethod.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = -8412527417505085156L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				if (disableListeners)
					return;
				customizeFWin
						.setShowOKCancel(((int) cbClosingMethod.getValue()) == 0 ? true
								: false);
			}
		});

		// ========== mock permissions =============
		permissions = new TestPermCheckerB();
		permissions.put("L001", MyActionsIds.MOCK_ID, false);

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
				return MyActionsIds.getName(appContext.getI18(), acId);

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
		tPerm.setColumnWidth("permission", -1);
		tPerm.setColumnWidth("enabled", 60);

		// ==========List selection results =============

		// ========== run the test ============================
		btRunTest.addClickListener(new ClickListener() {
			private static final long serialVersionUID = -5384467222048646753L;

			@Override
			public void buttonClick(ClickEvent event) {

				ListTst sub = new ListTst(permissions, customizeLW,
						listChoosingMode, listReadOnly, em, appContext,
						customizeFWin, permissions);

				sub.addCloseListener(new CloseListener() {

					/**
					 * 
					 */
					private static final long serialVersionUID = -5625061490326702522L;

					@Override
					public void windowClose(CloseEvent e) {

						CloseCause clcs = ((BaseListWindow) e.getSource())
								.getCloseCause();
						Object items = clcs.addInfo;

						switch (clcs.cause) {
						case CHOOSE:
							StringBuffer sb = new StringBuffer();
							sb.append(m.getStringNE("ChoosenS"));
							if (items == null)
								sb.append(m.getStringNE("nothing"));
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
							taChoosen.setReadOnly(false);
							taChoosen.setValue(sb.toString());
							taChoosen.setReadOnly(true);
							break;
						case CANCEL:
							taChoosen.setReadOnly(false);
							taChoosen.setValue(m.getStringNE("canceledN"));
							taChoosen.setReadOnly(true);
							break;
						case NOCHOOSE:
							taChoosen.setReadOnly(false);
							taChoosen.setValue(m.getStringNE("clNoSel"));
							taChoosen.setReadOnly(true);
						default:
							break;
						}
					}
				});
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

	@Override
	public void attach() {
		// TODO Auto-generated method stub
		super.attach();
		// appContext.getI18().changeAll(MainW.this);
		setLocale(appContext.getI18().getLocale());
	}

	@Override
	public void setLocale(Locale locale) {
		super.setLocale(locale);
		appContext.getI18().setLocale(locale);
		I18Sup i18 = appContext.getI18();

		lbL001.setValue(i18.getStringNE("lbL001"));
		chkReadOnly.setCaption(i18.getStringNE("chkReadOnly"));
		cbChoosingMode.setCaption(i18.getStringNE("cbChoosingMode"));
		lbCustomization.setValue(i18.getStringNE("lbCustomization"));
		chkDetails.setCaption(i18.getStringNE("chkDetails"));
		chkAddRec.setCaption(i18.getStringNE("chkAddRec"));
		chkDeleteRec.setCaption(i18.getStringNE("chkDeleteRec"));
		chkEditRec.setCaption(i18.getStringNE("chkEditRec"));
		chkViewRec.setCaption(i18.getStringNE("chkViewRec"));
		cbDoubleClick.setCaption(i18.getStringNE("cbDoubleClick"));

		chkDetailsChM.setCaption(i18.getStringNE("chkDetails"));
		chkAddRecChM.setCaption(i18.getStringNE("chkAddRec"));
		chkDeleteRecChM.setCaption(i18.getStringNE("chkDeleteRec"));
		chkEditRecChM.setCaption(i18.getStringNE("chkEditRec"));
		chkViewRecChM.setCaption(i18.getStringNE("chkViewRec"));
		cbDoubleClickChM.setCaption(i18.getStringNE("cbDoubleClick"));

		chkAskSave.setCaption(i18.getStringNE("chkAskSave"));
		chkAskDiscard.setCaption(i18.getStringNE("chkAskDiscard"));
		chkAskDelete.setCaption(i18.getStringNE("chkAskDelete"));
		chkAskCreate.setCaption(i18.getStringNE("chkAskCreate"));
		chkPrevNext.setCaption(i18.getStringNE("chkPrevNext"));
		cbAutoSaveDiscard.setCaption(i18.getStringNE("cbAutoSaveDiscard"));
		cbClosingMethod.setCaption(i18.getStringNE("cbClosingMethod"));

		lbPerm.setValue(i18.getStringNE("lbPerm"));
		btRunTest.setCaption(i18.getStringNE("btRun"));
		btExit.setCaption(i18.getStringNE("btExit"));

		lbM001.setValue(i18.getStringNE("lbM001"));

		tPerm.setColumnHeader("winIdShow", m.getStringNE("Window_Id"));
		tPerm.setColumnHeader("permission", m.getStringNE("Permission"));
		tPerm.setColumnHeader("enabled", m.getStringNE("Active"));

		taChoosen.setCaption(m.getString("taChoosen"));
		// cbLanguage.setCaption(m.getStringNE("cbLanguage"));

		Object val;
		disableListeners = true;
		val = cbChoosingMode.getValue();
		ComponentHelper.populateWIds(cbChoosingMode,
				i18.getArryString("choosingModeS"));
		disableListeners = false;
		cbChoosingMode.setValue(val);

		disableListeners = true;
		val = cbDoubleClick.getValue();
		ComponentHelper.populateWIdsSkip(cbDoubleClick,
				i18.getArryString("doubleClickModeS"), new Integer[] { 5 });
		disableListeners = false;
		cbDoubleClick.setValue(val);

		disableListeners = true;
		val = cbAutoSaveDiscard.getValue();
		ComponentHelper.populateWIds(cbAutoSaveDiscard,
				i18.getArryString("autoSaveDiscard"));
		disableListeners = false;
		cbAutoSaveDiscard.setValue(val);

		disableListeners = true;
		val = cbDoubleClickChM.getValue();
		if (val == null)
			val = 5;
		ComponentHelper.populateWIds(cbDoubleClickChM,
				i18.getArryString("doubleClickModeS"));
		disableListeners = false;
		cbDoubleClickChM.setValue(val);

		disableListeners = true;
		val = cbClosingMethod.getValue();
		ComponentHelper.populateWIds(cbClosingMethod,
				i18.getArryString("closinMethod"));
		disableListeners = false;
		cbClosingMethod.setValue(val);

		tPerm.refreshRowCache();

	}

}

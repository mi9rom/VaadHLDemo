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

import org.vaadin.addons.lazyquerycontainer.LazyEntityContainer;
import org.vaadin.addons.lazyquerycontainer.LazyQueryView;

import com.vaadHL.example.jpa.model.Customer;
import com.vaadHL.utl.converter.StringToPlainIntegerConverter;
import com.vaadHL.utl.helper.ComponentHelper;
import com.vaadHL.utl.helper.ItemHelper;
import com.vaadHL.utl.helper.TableHelper;
import com.vaadHL.utl.msgs.IMsgs;
import com.vaadHL.window.EM.LEMWindow;
import com.vaadHL.window.base.ICustomizeLWMultiMode;
import com.vaadHL.window.base.IListSelectionAction;
import com.vaadHL.window.base.MWLaunchMode;
import com.vaadHL.window.base.perm.IWinPermChecker;
import com.vaadHL.window.base.perm.MWinPermChecker;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.filter.Like;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class ListTst extends LEMWindow {

	private static final long serialVersionUID = 1822887556576252090L;
	private ListTstDesWrap ml;
	private TextField filter1;
	private LazyEntityContainer<Customer> container;
	private TextField tfDetail;
	private ComboBox cbSearchBy;
	private Button btGenPerm;
	private CustomizeFWin customizeFWin;
	private IWinPermChecker permFW;

	public ListTst(IWinPermChecker permChecker,
			ICustomizeLWMultiMode customize, ChoosingMode chooseMode,
			boolean readOnly, EntityManager em, IMsgs msgs,
			CustomizeFWin customizeFWin, IWinPermChecker permFW,
			IListSelectionAction selAction) {
		super("L001", "List Window Title", permChecker, customize, chooseMode,
				readOnly, em, msgs, selAction);
		//It is very important to include this
		if (!approvedToOpen)
			return;

		this.customizeFWin = customizeFWin;
		this.permFW = permFW;

		setHeight("600px");
		setWidth("800px");

		setCompositeContent();

		// container settings
		container = new LazyEntityContainer<Customer>(em, Customer.class, 100,
				"custId", true, true, true);
		container.getQueryView().getQueryDefinition()
				.setMaxNestedPropertyDepth(2);
		container.addContainerProperty("lastName", String.class, "", false,
				true);
		container.addContainerProperty("firstName", String.class, "", false,
				true);
		container
				.addContainerProperty("yearOfBirth", int.class, 0, false, true);
		container.addContainerProperty("custId", Long.class, 0L, true, false);
		container.addContainerProperty("ver", Long.class, 0L, true, true);
		container.addContainerProperty(
				LazyQueryView.DEBUG_PROPERTY_ID_QUERY_INDEX, Integer.class, 0,
				true, false);
		container.addContainerProperty(
				LazyQueryView.DEBUG_PROPERTY_ID_BATCH_INDEX, Integer.class, 0,
				true, false);
		container.addContainerProperty(
				LazyQueryView.DEBUG_PROPERTY_ID_BATCH_QUERY_TIME, Long.class,
				0, true, false);

		// The table
		// table.setCaption("TST");
		table.setContainerDataSource(container);
		table.setPageLength(15);
		table.setColumnWidth("custID", 50);
		table.setImmediate(true); // Must be immediate to send the resize events
									// immediately
		table.setEditable(false);
		table.setSelectable(true);
		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);
		table.setColumnHeader("lastName", "Last name");
		table.setColumnHeader("firstName", "First name");
		table.setColumnHeader("yearOfBirth", "Year of birth");
		table.setConverter("yearOfBirth", new StringToPlainIntegerConverter());
		table.setNullSelectionAllowed(true);
		if (getChooseMode() == ChoosingMode.MULTIPLE_CHOOSE) {
			table.setMultiSelect(true);
		}
		// default sort
		Object[] defSort = { "lastName", "firstName", "yearOfBirth" };
		boolean[] ordering = { true, true, true };
		table.sort(defSort, ordering);
		table.addValueChangeListener(tableRowChangeListener);

		// the filter
		cbSearchBy.setNullSelectionAllowed(false);
		cbSearchBy.setItemCaptionMode(ItemCaptionMode.EXPLICIT_DEFAULTS_ID);
		ComponentHelper.populateWIds(cbSearchBy, "by beginning", "anywhere");
		cbSearchBy.setValue(0);
		cbSearchBy.addValueChangeListener(new ValueChangeListener() {

			private static final long serialVersionUID = 3099497000713330791L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				setFilter("", "");
			}
		});

		filter1.addTextChangeListener(new TextChangeListener() {

			private static final long serialVersionUID = -3590332303279354994L;

			@Override
			public void textChange(TextChangeEvent event) {
				/*
				 * nameFilterField.getValue(); dosn't get the current value ,
				 * instead returns the last value after exiting the field
				 */
				setFilter("lastName", event.getText());
			}
		});
		
		btGenPerm.addClickListener(new com.vaadin.ui.Button.ClickListener() {
			
			private static final long serialVersionUID = 920725430798619475L;

			@Override
			public void buttonClick(ClickEvent event) {
				
				getMsgs().showInfo(ListTst.this.permFW.canDo(getWinId(), "CANDO")?"Allowed":"Denied");	
				
			}
		});
	}

	public void clearFilter() {
		container.removeAllContainerFilters();

	}

	public void setFilter() {
		setFilter("", "");
	}

	protected void setFilter(String field, Object val) {
		final String nameFilter;
		container.removeAllContainerFilters();

		/* can't use getValue when fired inside a TextChangeListener */
		if (field.equals("lastName"))
			nameFilter = (String) val;
		else
			nameFilter = filter1.getValue();

		if (nameFilter != null && nameFilter.length() != 0) {
			String search = null;
			Object oid = cbSearchBy.getValue();
			if (oid != null)
				if ((int) oid == 1)
					search = "%" + nameFilter + "%";
			if (search == null)
				search = nameFilter + "%";
			container.addContainerFilter(new Like("lastName", search));
		}

	}

	private void fillDetail(Item item) {
		tfDetail.setReadOnly(false);
		if (item != null) {
			ItemHelper ith = new ItemHelper(item);
			tfDetail.setValue(ith.getString("firstName") + " "
					+ ith.getString("lastName"));
		} else {
			tfDetail.setReadOnly(false);
			tfDetail.setValue("");
			tfDetail.setReadOnly(true);
		}

		tfDetail.setReadOnly(true);
	}

	private void fillDetailSelected(Object items) {
		StringBuffer sb = new StringBuffer();
		if (items != null) {
			Set<Object> se;
			if (items instanceof Set<?>)
				se = (Set<Object>) items;
			else {
				se = new HashSet<Object>();
				se.add(items);
			}

			for (Object it : se) {
				ItemHelper item = new ItemHelper((Item) it);

				sb.append(item.getString("firstName"));
				sb.append(" ");
				sb.append(item.getString("lastName"));
				sb.append(", ");

			}
		}

		tfDetail.setReadOnly(false);
		tfDetail.setValue(sb.toString());
		tfDetail.setReadOnly(true);
	}

	// fill the bottom detail caption
	ValueChangeListener tableRowChangeListener = new ValueChangeListener() {

		private static final long serialVersionUID = -1878503340033308870L;

		@Override
		public void valueChange(ValueChangeEvent event) {
			// new Msgs().showInfo(("ValueChangeEvent: " + event.));
			fillDetailSelected(tableHelper.getSelectedItems());
		}
	};

	@Override
	public Component makeMiddleArea() {
		return ml;
	}

	@Override
	public void callForm(MWLaunchMode launchModeMW, Object mRowid) {

		Object rowId = null;

		if (launchModeMW != MWLaunchMode.NEW_REC) {
			rowId = getCallFormSelIdMsg(mRowid);
			if (rowId == null)
				return;
		}

		FormTst sub = new FormTst(new MWinPermChecker(getWinId(), permFW),
				customizeFWin, launchModeMW, em, container, rowId, getMsgs(),
				isReadOnlyWin());
		sub.setModal(true);
		UI.getCurrent().addWindow(sub);

	}

	@Override
	public void initConstructorWidgets() {
		// -- In this example I utilize the design editor --
		ml = new ListTstDesWrap();
		table = ml.getTable();
		filter1 = ml.getTfFilter1();
		tfDetail = ml.getTfDetail();
		cbSearchBy = ml.getCbSearchBy();
		btGenPerm = ml.getFbtGenPerm();
		// ---------------------------------------------------
	}

}

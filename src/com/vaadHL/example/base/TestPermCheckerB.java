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

package com.vaadHL.example.base;

import java.io.Serializable;
import java.util.TreeMap;

import com.vaadHL.utl.action.ActionsIds;
import com.vaadHL.window.base.perm.AbstractWinPermChecker;
import com.vaadHL.window.base.perm.IWinPermChecker;
import com.vaadHL.window.base.perm.IWinPermFactory;

/**
 * Mock class simulating permission checking.
 * 
 * @author Miroslaw Romaniuk
 *
 */
public class TestPermCheckerB implements IWinPermFactory {

	/**
	 * 
	 * Permission item key
	 *
	 */
	class PermItemKey implements Serializable, Comparable<PermItemKey> {
		public PermItemKey(String winId, int permId) {
			super();
			this.winId = winId;
			this.permId = permId;
		}

		private static final long serialVersionUID = 7257687683713514170L;
		public String winId;
		public int permId;

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof PermItemKey) {
				return ((permId == ((PermItemKey) obj).permId) && (winId
						.equals(((PermItemKey) obj).winId)));
			} else
				return false;
		}

		@Override
		public int compareTo(PermItemKey o) {
			int comp = winId.compareTo(((PermItemKey) o).winId);
			if (comp != 0)
				return comp;
			return (permId - ((PermItemKey) o).permId);
		}

		@Override
		public int hashCode() {
			return winId.hashCode() + permId;
		}
	}

	/**
	 * Permission item.
	 *
	 */
	public class PermItem implements Serializable {

		private static final long serialVersionUID = 4287056022120719693L;
		String winId;
		int permId;
		boolean enabled;

		public PermItem(String winId, int permId, boolean enabled) {
			super();
			this.winId = winId;
			this.permId = permId;
			this.enabled = enabled;
		}

		public String getWinId() {
			return winId;
		}

		public void setWinId(String winId) {
			this.winId = winId;
		}

		public int getPermId() {
			return permId;
		}

		public void setPermId(int permId) {
			this.permId = permId;
		}

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

		public PermItemKey getKey() {
			return new PermItemKey(winId, permId);
		}
	}

	TreeMap<PermItemKey, PermItem> container = new TreeMap<TestPermCheckerB.PermItemKey, TestPermCheckerB.PermItem>();

	public TreeMap<PermItemKey, PermItem> getContainer() {
		return container;
	}

	public void put(String winId, int permId, boolean enabled) {
		PermItem pi = new PermItem(winId, permId, enabled);
		container.put(pi.getKey(), pi);
	}

	void fillDefault(String winId) {
		put(winId, ActionsIds.AC_OPEN, true);
		put(winId, ActionsIds.AC_CREATE, true);
		put(winId, ActionsIds.AC_DELETE, true);
		put(winId, ActionsIds.AC_EDIT, true);
		put(winId, ActionsIds.AC_NEXT_ITM, true);
		put(winId, ActionsIds.AC_PREV_ITM, true);
	}

	public TestPermCheckerB() {
		super();

		fillDefault("L001");
		fillDefault("M001");

	}

	private boolean canDo(String winId, int actionId, boolean defValue) {
		PermItem pi = container.get(new PermItemKey(winId, actionId));
		if (pi == null)
			return defValue;
		else
			return pi.isEnabled();
	}

	public class WinPermChecker extends AbstractWinPermChecker {

		public WinPermChecker(String winId) {
			super(winId);
		}

		@Override
		public boolean canDo(int actionId, boolean defValue) {
			return TestPermCheckerB.this.canDo(winId, actionId, defValue);
		}

		@Override
		public boolean canDo(int actionId) {
			return canDo(actionId, false);
		}

	}

	@Override
	public IWinPermChecker getChecker(String winId) {
		return new WinPermChecker(winId);
	}

}

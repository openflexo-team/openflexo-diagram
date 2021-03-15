/**
 * 
 * Copyright (c) 2013-2017, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Flexovieweditor, a component of the software infrastructure 
 * developed at Openflexo.
 * 
 * 
 * Openflexo is dual-licensed under the European Union Public License (EUPL, either 
 * version 1.1 of the License, or any later version ), which is available at 
 * https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 * and the GNU General Public License (GPL, either version 3 of the License, or any 
 * later version), which is available at http://www.gnu.org/licenses/gpl.html .
 * 
 * You can redistribute it and/or modify under the terms of either of these licenses
 * 
 * If you choose to redistribute it and/or modify under the terms of the GNU GPL, you
 * must include the following additional permission.
 *
 *          Additional permission under GNU GPL version 3 section 7
 *
 *          If you modify this Program, or any covered work, by linking or 
 *          combining it with software containing parts covered by the terms 
 *          of EPL 1.0, the licensors of this Program grant you additional permission
 *          to convey the resulting work. * 
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE. 
 *
 * See http://www.openflexo.org/license.html for details.
 * 
 * 
 * Please contact Openflexo (openflexo-contacts@openflexo.org)
 * or visit www.openflexo.org if you need additional information.
 * 
 */

package org.openflexo.technologyadapter.diagram.controller;

import java.util.logging.Logger;

import org.openflexo.diana.DianaPrefs;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.prefs.GeneralPreferences;
import org.openflexo.prefs.Preferences;
import org.openflexo.prefs.TechnologyAdapterPreferences;
import org.openflexo.technologyadapter.diagram.DiagramTechnologyAdapter;

/**
 * Contains preferences for Diagram Technology Adapter
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@ImplementationClass(DiagramPreferences.DiagramPreferencesImpl.class)
@XMLElement(xmlTag = "DiagramPreferences")
@Preferences(
		shortName = "Diagram",
		longName = "Diagram TechnologyAdapter Preferences",
		FIBPanel = "Fib/Prefs/DiagramPreferences.fib",
		smallIcon = "Icons/Diagram-16x16.png",
		bigIcon = "Icons/Diagram-64x64.png")
public interface DiagramPreferences extends TechnologyAdapterPreferences<DiagramTechnologyAdapter> {

	public static final String HANDLE_LONG_FOCUSING = "handleLongFocusing";
	public static final String LONG_FOCUSING_DELAY = "longFocusingDelay";
	public static final String HANDLE_TRAILING_FOCUSING = "handleTrailingFocusing";
	public static final String TRAILING_FOCUSING_DELAY = "trailingFocusingDelay";

	@Getter(value = HANDLE_LONG_FOCUSING, defaultValue = "true")
	@XMLAttribute
	public boolean getHandleLongFocusing();

	@Setter(HANDLE_LONG_FOCUSING)
	public void setHandleLongFocusing(boolean handleLongFocusing);

	@Getter(value = LONG_FOCUSING_DELAY, defaultValue = "1000")
	@XMLAttribute
	public int getLongFocusingDelay();

	@Setter(LONG_FOCUSING_DELAY)
	public void setLongFocusingDelay(int delay);

	@Getter(value = HANDLE_TRAILING_FOCUSING, defaultValue = "true")
	@XMLAttribute
	public boolean getHandleTrailingFocusing();

	@Setter(HANDLE_TRAILING_FOCUSING)
	public void setHandleTrailingFocusing(boolean handleLongFocusing);

	@Getter(value = TRAILING_FOCUSING_DELAY, defaultValue = "1500")
	@XMLAttribute
	public int getTrailingFocusingDelay();

	@Setter(TRAILING_FOCUSING_DELAY)
	public void setTrailingFocusingDelay(int delay);

	public abstract class DiagramPreferencesImpl extends PreferencesContainerImpl implements DiagramPreferences {

		@SuppressWarnings("unused")
		private static final Logger logger = Logger.getLogger(GeneralPreferences.class.getPackage().getName());

		@Override
		public boolean getHandleLongFocusing() {
			return DianaPrefs.HANDLE_LONG_FOCUSING;
		}

		@Override
		public void setHandleLongFocusing(boolean handleLongFocusing) {
			if (handleLongFocusing != getHandleLongFocusing()) {
				DianaPrefs.HANDLE_LONG_FOCUSING = handleLongFocusing;
				getPropertyChangeSupport().firePropertyChange(HANDLE_LONG_FOCUSING, !handleLongFocusing, handleLongFocusing);
			}
		}

		@Override
		public int getLongFocusingDelay() {
			return DianaPrefs.LONG_FOCUSING_DELAY;
		}

		@Override
		public void setLongFocusingDelay(int longFocusingDelay) {
			if (longFocusingDelay != getLongFocusingDelay()) {
				int oldValue = getLongFocusingDelay();
				DianaPrefs.LONG_FOCUSING_DELAY = longFocusingDelay;
				getPropertyChangeSupport().firePropertyChange(LONG_FOCUSING_DELAY, oldValue, longFocusingDelay);
			}
		}

		@Override
		public boolean getHandleTrailingFocusing() {
			return DianaPrefs.HANDLE_TRAILING_FOCUSING;
		}

		@Override
		public void setHandleTrailingFocusing(boolean handleTrailingFocusing) {

			System.out.println("setHandleTrailingFocusing : " + handleTrailingFocusing);
			Thread.dumpStack();

			if (handleTrailingFocusing != getHandleTrailingFocusing()) {
				DianaPrefs.HANDLE_TRAILING_FOCUSING = handleTrailingFocusing;
				getPropertyChangeSupport().firePropertyChange(HANDLE_TRAILING_FOCUSING, !handleTrailingFocusing, handleTrailingFocusing);
				System.out.println("Hop maintenant getHandleTrailingFocusing()=" + getHandleTrailingFocusing());
			}
		}

		@Override
		public int getTrailingFocusingDelay() {
			return DianaPrefs.TRAILING_FOCUSING_DELAY;
		}

		@Override
		public void setTrailingFocusingDelay(int trailingFocusingDelay) {
			if (trailingFocusingDelay != getTrailingFocusingDelay()) {
				int oldValue = getTrailingFocusingDelay();
				DianaPrefs.TRAILING_FOCUSING_DELAY = trailingFocusingDelay;
				getPropertyChangeSupport().firePropertyChange(TRAILING_FOCUSING_DELAY, oldValue, trailingFocusingDelay);
			}
		}
	}
}

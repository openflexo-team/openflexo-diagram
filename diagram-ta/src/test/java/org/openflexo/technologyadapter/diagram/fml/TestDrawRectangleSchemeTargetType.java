/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Cartoeditor, a component of the software infrastructure 
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

package org.openflexo.technologyadapter.diagram.fml;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.junit.Test;
import org.openflexo.foundation.fml.FMLCompilationUnit;
import org.openflexo.foundation.fml.FMLPropertyValue;
import org.openflexo.foundation.fml.FlexoBehaviour;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.test.parser.FMLParserTestCase;

/**
 * A {@link DrawRectangleScheme} written without <code>targetType</code> draws at top level: it declares no target type. It used to
 * answer the type of the top level virtual model, so the FML attribute looked declared: a property value no node of the parsed text
 * stood for (logged "Cannot find P2PPNode"), written back on save.
 */
public class TestDrawRectangleSchemeTargetType extends FMLParserTestCase {

	@Test
	public void noTargetTypeWhenNoneIsWritten() throws Exception {
		List<String> warnings = new ArrayList<>();
		Handler handler = new Handler() {
			@Override
			public void publish(LogRecord record) {
				if (record.getLevel().intValue() >= Level.WARNING.intValue()) {
					warnings.add(record.getMessage());
				}
			}

			@Override
			public void flush() {
			}

			@Override
			public void close() {
			}
		};
		Logger.getLogger("").addHandler(handler);
		try {
			instanciateTestServiceManager();
			VirtualModel virtualModel = serviceManager.getVirtualModelLibrary()
					.getVirtualModel("http://openflexo.org/test/TestResourceCenter/TestDrawRectangleScheme.fml");
			assertNotNull(virtualModel);
			FMLCompilationUnit compilationUnit = virtualModel.getCompilationUnit();

			FlexoBehaviour behaviour = virtualModel.getFlexoConcept("Group").getFlexoBehaviour("drawGroup", String.class);
			assertTrue(behaviour instanceof DrawRectangleScheme);
			DrawRectangleScheme scheme = (DrawRectangleScheme) behaviour;
			assertNull("A scheme written without targetType declares none", scheme.getTargetType());
			assertNotNull(scheme.getChildrenType());

			// What is written is what the FML attributes show
			for (FMLPropertyValue<?, ?> value : scheme.getFMLPropertyValues(compilationUnit.getFMLModelFactory())) {
				assertFalse("No property value for targetType: " + value, "targetType".equals(value.getPropertyName()));
			}
			String printed = compilationUnit.getFMLPrettyPrint();
			assertTrue(printed, printed.contains("with DrawRectangleScheme(childrenType=Box,selectObjects=true)"));
			for (String line : printed.split("\n")) {
				if (line.contains("DrawRectangleScheme(")) {
					assertFalse(line, line.contains("targetType"));
				}
			}
		} finally {
			Logger.getLogger("").removeHandler(handler);
		}
		for (String warning : warnings) {
			assertFalse("Unexpected warning: " + warning, warning != null && warning.contains("Cannot find P2PPNode") && warning.contains("targetType"));
		}
	}
}

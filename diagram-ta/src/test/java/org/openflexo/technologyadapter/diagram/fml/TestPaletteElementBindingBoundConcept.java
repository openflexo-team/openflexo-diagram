/**
 * 
 * Copyright (c) 2014-2015, Openflexo
 * 
 * This file is part of Flexodiagram, a component of the software infrastructure 
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.test.OpenflexoTestCase;
import org.openflexo.technologyadapter.diagram.DiagramTechnologyAdapter;
import org.openflexo.technologyadapter.diagram.TypedDiagramModelSlot;

/**
 * A palette element binding read from FML - <code>call=new TutuGR::dropTutuGRAtTopLevel()</code> - must tell which concept it is bound to.
 *
 * <p>
 * The free modelling editor drops a palette element by asking its binding for the concept, and falls back on creating an unclassified
 * element when there is none. The concept used to be known only to a binding that had been given its drop scheme by program: one loaded
 * from a file resolved the drop scheme from its call, but not the concept.
 */
public class TestPaletteElementBindingBoundConcept extends OpenflexoTestCase {

	@Test
	public void testBindingsReadFromFMLKnowTheirConcept() throws Exception {

		instanciateTestServiceManager(DiagramTechnologyAdapter.class);
		VirtualModel virtualModel = serviceManager.getVirtualModelLibrary()
				.getVirtualModel("http://openflexo.org/test/TestResourceCenter/TestDiagramVM2.fml");
		assertNotNull(virtualModel);

		TypedDiagramModelSlot modelSlot = FMLControlledDiagramVirtualModelNature.getTypedDiagramModelSlot(virtualModel);
		assertNotNull(modelSlot);
		assertEquals(2, modelSlot.getPaletteElementBindings().size());

		for (FMLDiagramPaletteElementBinding binding : modelSlot.getPaletteElementBindings()) {
			// Asked FIRST: nothing must have resolved the drop scheme beforehand
			assertNotNull("No concept bound to " + binding.getPaletteElementId(), binding.getBoundFlexoConcept());
			assertTrue(binding.getPaletteElementId().endsWith(
					binding.getBoundFlexoConcept().getName().equals("TutuGR") ? "#TestPaletteElement1" : "#TestPaletteElement2"));
			assertEquals(binding.getDropScheme().getFlexoConcept(), binding.getBoundFlexoConcept());
		}
	}
}

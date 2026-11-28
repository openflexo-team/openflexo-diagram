/*
 * Copyright (c) 2013-2017, Openflexo
 *
 * This file is part of Flexo-foundation, a component of the software infrastructure
 * developed at Openflexo.
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
 *           Additional permission under GNU GPL version 3 section 7
 *           If you modify this Program, or any covered work, by linking or
 *           combining it with software containing parts covered by the terms
 *           of EPL 1.0, the licensors of this Program grant you additional permission
 *           to convey the resulting work.
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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.foundation.fml.CreationScheme;
import org.openflexo.foundation.fml.FMLTechnologyAdapter;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.action.CreateContainedVirtualModel;
import org.openflexo.foundation.fml.action.CreateTopLevelVirtualModel;
import org.openflexo.technologyadapter.diagram.TypedDiagramModelSlot;
import org.openflexo.technologyadapter.diagram.fml.action.FMLControlledDiagramVirtualModelTemplate;
import org.openflexo.technologyadapter.diagram.fml.action.FMLControlledDiagramVirtualModelTemplate.DiagramSpecificationChoice;
import org.openflexo.technologyadapter.diagram.metamodel.DiagramSpecification;
import org.openflexo.technologyadapter.diagram.rm.DiagramSpecificationResource;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

/**
 * Test the creation of a VirtualModel controlling a diagram through {@link FMLControlledDiagramVirtualModelTemplate}: top-level with a
 * new diagram specification, then contained with the existing one
 */
@RunWith(OrderedRunner.class)
public class TestFMLControlledDiagramVirtualModelTemplate extends DiagramTestCase {

	private static VirtualModel topLevelVirtualModel;
	private static DiagramSpecificationResource specificationResource;

	@Test
	@TestOrder(1)
	public void testCreateTopLevelWithNewDiagramSpecification() throws Exception {
		FMLTechnologyAdapter fmlTA = serviceManager.getTechnologyAdapterService().getTechnologyAdapter(FMLTechnologyAdapter.class);
		CreateTopLevelVirtualModel action = CreateTopLevelVirtualModel.actionType
				.makeNewAction(fmlTA.getGlobalRepository(newResourceCenter).getRootFolder(), null, editor);
		FMLControlledDiagramVirtualModelTemplate template = action.getAvailableTemplate(FMLControlledDiagramVirtualModelTemplate.class);
		assertNotNull(template);
		action.setTemplate(template);
		action.setNewVirtualModelName("MyDiagramVM");
		assertEquals("MyDiagramVMSpec", template.getNewDiagramSpecificationName());
		assertEquals(DiagramSpecificationChoice.CreateNewDiagramSpecification, template.getChoice());
		assertNotNull(template.getRepositoryFolder());
		template.setNewDiagramSpecificationURI("http://openflexo.org/test/MyDiagramVMSpec.diagramspecification");
		assertTrue(template.getConfigurationErrorMessage(), template.isValid());
		assertTrue(action.isValid());

		action.doAction();
		assertTrue(action.hasActionExecutionSucceeded());

		topLevelVirtualModel = action.getNewVirtualModel();
		specificationResource = template.getDiagramSpecificationResource();
		assertNotNull(specificationResource);
		DiagramSpecification specification = specificationResource.getDiagramSpecification();
		assertEquals(1, specification.getExampleDiagrams().size());
		assertEquals(1, specification.getPalettes().size());
		checkDiagramVirtualModel(topLevelVirtualModel, specificationResource);
	}

	@Test
	@TestOrder(2)
	public void testCreateContainedWithExistingDiagramSpecification() throws Exception {
		// Focused on the VirtualModel itself (not its compilation unit), as from its node in a browser
		assertTrue(CreateContainedVirtualModel.actionType.isEnabled(topLevelVirtualModel, null));
		CreateContainedVirtualModel action = CreateContainedVirtualModel.actionType.makeNewAction(topLevelVirtualModel, null, editor);
		assertSame(topLevelVirtualModel.getCompilationUnit(), action.getContainerCompilationUnit());
		action.setNewVirtualModelName("MyContainedDiagramVM");
		FMLControlledDiagramVirtualModelTemplate template = action.getAvailableTemplate(FMLControlledDiagramVirtualModelTemplate.class);
		assertNotNull(template);
		action.setTemplate(template);
		template.setChoice(DiagramSpecificationChoice.UseExistingDiagramSpecification);
		assertTrue(!template.isValid());
		template.setDiagramSpecificationResource(specificationResource);
		assertTrue(action.isValid());

		action.doAction();
		assertTrue(action.hasActionExecutionSucceeded());

		VirtualModel contained = action.getNewVirtualModel();
		assertSame(topLevelVirtualModel, contained.getContainerVirtualModel());
		checkDiagramVirtualModel(contained, specificationResource);
	}

	private static void checkDiagramVirtualModel(VirtualModel virtualModel, DiagramSpecificationResource expectedSpecification) {
		assertNotNull(virtualModel);
		assertEquals(1, virtualModel.getModelSlots(TypedDiagramModelSlot.class).size());
		TypedDiagramModelSlot slot = virtualModel.getModelSlots(TypedDiagramModelSlot.class).get(0);
		assertEquals("diagram", slot.getName());
		assertSame(expectedSpecification, slot.getMetaModelResource());
		CreationScheme init = null;
		for (CreationScheme cs : virtualModel.getCreationSchemes()) {
			if ("init".equals(cs.getName())) {
				init = cs;
			}
		}
		assertNotNull(init);
		assertTrue(init.getFMLPrettyPrint(), init.getFMLPrettyPrint().contains("CreateDiagram"));
	}

}

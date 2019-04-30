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

import java.io.FileNotFoundException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.diana.ConnectorGraphicalRepresentation;
import org.openflexo.diana.connectors.ConnectorSymbol.EndSymbolType;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoServiceManager;
import org.openflexo.foundation.fml.FlexoConcept;
import org.openflexo.foundation.fml.FlexoRole;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.VirtualModelLibrary;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.test.OpenflexoTestCase;
import org.openflexo.technologyadapter.diagram.DiagramTechnologyAdapter;
import org.openflexo.technologyadapter.diagram.TypedDiagramModelSlot;
import org.openflexo.technologyadapter.diagram.model.DiagramType;
import org.openflexo.technologyadapter.diagram.rm.DiagramSpecificationRepository;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

/**
 * Test the BindingModel management of some diagram-specific features
 * 
 * @author sylvain
 * 
 */
@RunWith(OrderedRunner.class)
public class TestDiagramConnectorRoles extends OpenflexoTestCase {

	private final String VIEWPOINT_URI = "http://openflexo.org/test/TestResourceCenter/TestControlledDiagramViewPoint.fml";
	private final String BASIC_CONCEPT_NAME = "TestConcept";
	private final String LINK_CONCEPT_NAME = "TestLink";
	private final String CONNECTOR_ROLE_NAME = "connector";

	public static DiagramTechnologyAdapter technologicalAdapter;
	public static FlexoServiceManager applicationContext;
	public static FlexoResourceCenter<?> resourceCenter;
	public static DiagramSpecificationRepository repository;
	public static FlexoEditor editor;

	public static VirtualModel viewPoint;
	public static TypedDiagramModelSlot typedDiagramModelSlot;
	public static VirtualModel virtualModel;
	public static FlexoConcept flexoConcept;
	public static FlexoConcept linkConcept;
	public static DropScheme dropScheme;
	public static LinkScheme linkScheme;

	/**
	 * Initialize
	 */
	@Test
	@TestOrder(1)
	public void testInitialize() {

		log("testInitialize()");

		applicationContext = instanciateTestServiceManager(DiagramTechnologyAdapter.class);

		technologicalAdapter = applicationContext.getTechnologyAdapterService().getTechnologyAdapter(DiagramTechnologyAdapter.class);
		resourceCenter = applicationContext.getResourceCenterService().getResourceCenters().get(0);

		repository = technologicalAdapter.getDiagramSpecificationRepository(resourceCenter);

		editor = new FlexoTestEditor(null, applicationContext);

		assertNotNull(applicationContext);
		assertNotNull(technologicalAdapter);
		assertNotNull(resourceCenter);
		assertNotNull(repository);
	}

	/**
	 * Retrieve the ViewPoint
	 * 
	 * @throws FlexoException
	 * @throws ResourceLoadingCancelledException
	 * @throws FileNotFoundException
	 */
	@Test
	@TestOrder(2)
	public void testLoadViewPointAndVirtualModel() throws FileNotFoundException, ResourceLoadingCancelledException, FlexoException {
		instanciateTestServiceManager(DiagramTechnologyAdapter.class);
		VirtualModelLibrary vpLib = serviceManager.getVirtualModelLibrary();
		assertNotNull(vpLib);
		viewPoint = vpLib.getVirtualModel(VIEWPOINT_URI);
		assertNotNull(viewPoint);

		virtualModel = viewPoint.getVirtualModelNamed("TestVirtualModel");
		assertNotNull(virtualModel);
		assertTrue(virtualModel.hasNature(FMLControlledDiagramVirtualModelNature.INSTANCE));

		assertNotNull(virtualModel.getBindingModel());
		assertEquals(3, virtualModel.getBindingModel().getBindingVariablesCount());
		assertNotNull(virtualModel.getBindingModel().bindingVariableNamed("diagram"));
		assertTrue(virtualModel.getBindingModel().bindingVariableNamed("diagram").getType() instanceof DiagramType);

		flexoConcept = virtualModel.getFlexoConcepts().get(0);
		assertNotNull(flexoConcept);

		dropScheme = (DropScheme) flexoConcept.getFlexoBehaviours().get(0);
		assertNotNull(dropScheme);

	}

	/**
	 * Test Binding Model
	 */
	@Test
	@TestOrder(3)
	public void testVirtualModelBindingModel() {

		assertNotNull(virtualModel.getBindingModel());
		assertEquals(3, virtualModel.getBindingModel().getBindingVariablesCount());
		assertNotNull(virtualModel.getBindingModel().bindingVariableNamed("diagram"));
		assertTrue(virtualModel.getBindingModel().bindingVariableNamed("diagram").getType() instanceof DiagramType);

		flexoConcept = virtualModel.getFlexoConcepts().get(0);
		assertNotNull(flexoConcept);

		dropScheme = (DropScheme) flexoConcept.getFlexoBehaviours().get(0);
		assertNotNull(dropScheme);

	}

	/**
	 * Test Binding Model
	 */
	@Test
	@TestOrder(4)
	public void testRetrieveConcept() {

		flexoConcept = virtualModel.getFlexoConcepts().get(0);
		assertNotNull(flexoConcept);
		assertTrue(flexoConcept.getName().equals(BASIC_CONCEPT_NAME));

		linkConcept = virtualModel.getFlexoConcept(LINK_CONCEPT_NAME);
		assertNotNull(linkConcept);
		assertTrue(linkConcept.getName().equals(LINK_CONCEPT_NAME));

		assertNotNull(flexoConcept.getBindingModel());

		dropScheme = (DropScheme) flexoConcept.getFlexoBehaviours().get(0);
		assertNotNull(dropScheme);

	}

	/**
	 * Test Connector Role
	 */
	@Test
	@TestOrder(5)
	public void testConnectorRole() {

		FlexoRole<?> role = linkConcept.getAccessibleRole(CONNECTOR_ROLE_NAME);
		assertNotNull(role);

		assertTrue(role instanceof ConnectorRole);

		ConnectorRole connectorRole = (ConnectorRole) role;
		ConnectorGraphicalRepresentation grRep = connectorRole.getGraphicalRepresentation();
		assertTrue(grRep.getConnectorSpecification().getEndSymbol() == EndSymbolType.ARROW);

	}

}

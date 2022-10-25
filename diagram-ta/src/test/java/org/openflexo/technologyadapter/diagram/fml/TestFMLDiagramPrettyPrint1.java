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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.foundation.DefaultFlexoEditor;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.fml.FMLCompilationUnit;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.VirtualModelLibrary;
import org.openflexo.foundation.fml.parser.ParseException;
import org.openflexo.foundation.fml.parser.fmlnodes.FMLCompilationUnitNode;
import org.openflexo.foundation.fml.parser.fmlnodes.VirtualModelNode;
import org.openflexo.foundation.fml.rm.CompilationUnitResource;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.test.parser.FMLParserTestCase;
import org.openflexo.p2pp.RawSource;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

/**
 * Parse a FML file, perform some edits and checks that pretty-print is correct
 * 
 * @author sylvain
 *
 */
@RunWith(OrderedRunner.class)
public class TestFMLDiagramPrettyPrint1 extends FMLParserTestCase {

	private static FMLCompilationUnit compilationUnit;
	private static VirtualModel virtualModel;

	static FlexoEditor editor;

	private static CompilationUnitResource fmlResource;
	private static VirtualModelNode vmNode;

	@Test
	@TestOrder(1)
	public void initServiceManager() throws ParseException, ModelDefinitionException, IOException {
		instanciateTestServiceManager();

		editor = new DefaultFlexoEditor(null, serviceManager);
		assertNotNull(editor);

	}


	@Test
	@TestOrder(2)
	public void loadInitialVersion() throws ParseException, ModelDefinitionException, IOException, ResourceLoadingCancelledException, FlexoException {
		instanciateTestServiceManager();

		log("Initial version");

		/*for (FlexoResource<?> resource : serviceManager.getResourceManager().getRegisteredResources()) {
			System.out.println(" > "+resource);
		}
		DiagramTechnologyAdapter diagramTechnologyAdapter = serviceManager.getTechnologyAdapterService().getTechnologyAdapter(DiagramTechnologyAdapter.class);
		//serviceManager.getTechnologyAdapterService().activateTechnologyAdapter(diagramTechnologyAdapter,true);
		for (FlexoResource<?> resource : serviceManager.getResourceManager().getRegisteredResources()) {
			System.out.println(" > "+resource);
		}*/
		
		VirtualModelLibrary vpLib = serviceManager.getVirtualModelLibrary();
		assertNotNull(vpLib);
		VirtualModel rootVM = vpLib.getVirtualModel("http://openflexo.org/test/TestResourceCenter/TestControlledDiagramViewPoint.fml/TestVirtualModel.fml");
		assertNotNull(rootVM);
		VirtualModel virtualModel = vpLib.getVirtualModel("http://openflexo.org/test/TestResourceCenter/TestControlledDiagramViewPoint.fml/TestVirtualModel.fml");
		assertNotNull(virtualModel);

		fmlResource = virtualModel.getResource();
		assertNotNull(fmlResource);

		compilationUnit = fmlResource.getCompilationUnit();

		assertNotNull(virtualModel = compilationUnit.getVirtualModel());
		assertEquals("TestVirtualModel", virtualModel.getName());

		assertNotNull(rootNode = (FMLCompilationUnitNode) compilationUnit.getPrettyPrintDelegate());
		assertNotNull(vmNode = (VirtualModelNode) rootNode.getObjectNode(virtualModel));

		RawSource rawSource = rootNode.getRawSource();
		System.out.println(rawSource.debug());
		debug(rootNode, 0);

		/*FlexoConcept transitionConcept = virtualModel.getFlexoConcept("Transition");
		FlexoConceptNode conceptNode = (FlexoConceptNode) rootNode.getObjectNode(transitionConcept);
		FlexoConceptInstanceRole incomings = (FlexoConceptInstanceRole) transitionConcept.getDeclaredProperty("incomings");
		FlexoRolePropertyNode incomingsNode = (FlexoRolePropertyNode) rootNode.getObjectNode(incomings);
		assertEquals("(28:2)-(28:92)", incomingsNode.getLastParsedFragment().toString());
		FlexoConceptInstanceRole outgoings = (FlexoConceptInstanceRole) transitionConcept.getDeclaredProperty("outgoings");
		FlexoRolePropertyNode outgoingsNode = (FlexoRolePropertyNode) rootNode.getObjectNode(outgoings);
		assertEquals("(29:2)-(29:92)", outgoingsNode.getLastParsedFragment().toString());*/

		System.out.println("FML=\n" + compilationUnit.getFMLPrettyPrint());
		testFMLPrettyPrintEquals(compilationUnit, "TestFMLDiagramPrettyPrint1/Step1PrettyPrint.fml");

		System.out.println("Normalized=\n" + compilationUnit.getNormalizedFML());
		testNormalizedFMLRepresentationEquals(compilationUnit, "TestFMLDiagramPrettyPrint1/Step1Normalized.fml");

	}

}

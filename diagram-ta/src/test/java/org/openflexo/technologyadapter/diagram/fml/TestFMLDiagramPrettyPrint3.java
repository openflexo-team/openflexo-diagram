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
import org.openflexo.connie.DataBinding;
import org.openflexo.foundation.DefaultFlexoEditor;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.fml.FMLCompilationUnit;
import org.openflexo.foundation.fml.FMLInstancesListPropertyValue;
import org.openflexo.foundation.fml.FMLSimplePropertyValue;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.VirtualModelLibrary;
import org.openflexo.foundation.fml.WrappedFMLObject;
import org.openflexo.foundation.fml.parser.ParseException;
import org.openflexo.foundation.fml.parser.fmlnodes.FMLCompilationUnitNode;
import org.openflexo.foundation.fml.parser.fmlnodes.FMLInstancesListPropertyValueNode;
import org.openflexo.foundation.fml.parser.fmlnodes.FMLSimplePropertyValueNode;
import org.openflexo.foundation.fml.parser.fmlnodes.ModelSlotPropertyNode;
import org.openflexo.foundation.fml.parser.fmlnodes.VirtualModelNode;
import org.openflexo.foundation.fml.rm.CompilationUnitResource;
import org.openflexo.foundation.fml.rt.FlexoConceptInstance;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.test.parser.FMLParserTestCase;
import org.openflexo.p2pp.RawSource;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.technologyadapter.diagram.TypedDiagramModelSlot.TypedDiagramModelSlotImpl;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

/**
 * Parse a FML file, perform some edits and checks that pretty-print is correct
 * 
 * @author sylvain
 *
 */
@RunWith(OrderedRunner.class)
public class TestFMLDiagramPrettyPrint3 extends FMLParserTestCase {

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
	public void loadInitialVersion()
			throws ParseException, ModelDefinitionException, IOException, ResourceLoadingCancelledException, FlexoException {
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
		VirtualModel virtualModel = vpLib.getVirtualModel("http://openflexo.org/test/TestResourceCenter/TestDiagramVM2.fml");
		assertNotNull(virtualModel);

		fmlResource = virtualModel.getResource();
		assertNotNull(fmlResource);

		compilationUnit = fmlResource.getCompilationUnit();

		assertNotNull(virtualModel = compilationUnit.getVirtualModel());
		assertEquals("TestDiagramVM2", virtualModel.getName());

		assertNotNull(rootNode = (FMLCompilationUnitNode) compilationUnit.getPrettyPrintDelegate());
		assertNotNull(vmNode = (VirtualModelNode) rootNode.getObjectNode(virtualModel));

		RawSource rawSource = rootNode.getRawSource();
		System.out.println(rawSource.debug());
		debug(rootNode, 0);

		TypedDiagramModelSlotImpl modelSlot = (TypedDiagramModelSlotImpl) virtualModel.getModelSlots().get(0);
		assertNotNull(modelSlot);
		ModelSlotPropertyNode msNode = checkNodeForObject("(12:8)-(21:19)", "(12:8)-(12:8)", "(21:19)-(22:0)", null, modelSlot);

		FMLSimplePropertyValue dsPropertyValue = (FMLSimplePropertyValue) modelSlot.getFMLPropertyValues(modelSlot.getFMLModelFactory())
				.get(0);
		assertNotNull(dsPropertyValue);
		FMLSimplePropertyValueNode dsPropertyValueNode = checkNodeForObject("(13:16)-(13:58)", null, "(13:58)-(13:59)", null,
				dsPropertyValue);

		FMLInstancesListPropertyValue bindingsPropertyValue = (FMLInstancesListPropertyValue) modelSlot
				.getFMLPropertyValues(modelSlot.getFMLModelFactory()).get(1);
		assertNotNull(bindingsPropertyValue);
		FMLInstancesListPropertyValueNode bindingsPropertyValueNode = checkNodeForObject("(14:16)-(21:17)", null, null, null,
				bindingsPropertyValue);

		assertEquals(2, bindingsPropertyValue.getInstances().size());

		WrappedFMLObject<FMLDiagramPaletteElementBinding> binding1 = (WrappedFMLObject<FMLDiagramPaletteElementBinding>) bindingsPropertyValue
				.getInstances().get(0);
		WrappedFMLObject<FMLDiagramPaletteElementBinding> binding2 = (WrappedFMLObject<FMLDiagramPaletteElementBinding>) bindingsPropertyValue
				.getInstances().get(1);

		DataBinding<FlexoConceptInstance> call = binding1.getObject().getCall();
		System.out.println("hop: call=" + call);
		call.debug();
		System.out.println("valid: " + call.isValid());
		System.out.println("reason: " + call.invalidBindingReason());

		System.out.println("call.getOwner()=" + call.getOwner());
		System.out.println("BM=" + call.getOwner().getBindingModel());

		FMLDiagramPaletteElementBinding b = (FMLDiagramPaletteElementBinding) call.getOwner();
		System.out.println("ms:" + b.getDiagramModelSlot());

		System.out.println("FML=\n" + compilationUnit.getFMLPrettyPrint());
		testFMLPrettyPrintEquals(compilationUnit, "TestFMLDiagramPrettyPrint3/Step1PrettyPrint.fml");

		System.out.println("Normalized=\n" + compilationUnit.getNormalizedFML());
		testNormalizedFMLRepresentationEquals(compilationUnit, "TestFMLDiagramPrettyPrint3/Step1Normalized.fml");

	}

}

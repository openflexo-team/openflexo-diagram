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
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.connie.DataBinding;
import org.openflexo.foundation.DefaultFlexoEditor;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.fml.CreationScheme;
import org.openflexo.foundation.fml.FMLCompilationUnit;
import org.openflexo.foundation.fml.FlexoConcept;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.VirtualModelLibrary;
import org.openflexo.foundation.fml.action.CreateEditionAction;
import org.openflexo.foundation.fml.action.CreateFlexoBehaviour;
import org.openflexo.foundation.fml.action.CreateFlexoConcept;
import org.openflexo.foundation.fml.action.CreateModelSlot;
import org.openflexo.foundation.fml.action.CreateTechnologyRole;
import org.openflexo.foundation.fml.editionaction.AssignationAction;
import org.openflexo.foundation.fml.parser.ParseException;
import org.openflexo.foundation.fml.parser.fmlnodes.FMLCompilationUnitNode;
import org.openflexo.foundation.fml.parser.fmlnodes.VirtualModelNode;
import org.openflexo.foundation.fml.rm.CompilationUnitResource;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.test.parser.FMLParserTestCase;
import org.openflexo.p2pp.RawSource;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.technologyadapter.diagram.DiagramTechnologyAdapter;
import org.openflexo.technologyadapter.diagram.TypedDiagramModelSlot;
import org.openflexo.technologyadapter.diagram.fml.editionaction.AddShape;
import org.openflexo.technologyadapter.diagram.fml.editionaction.CreateDiagram;
import org.openflexo.technologyadapter.diagram.metamodel.DiagramPaletteElement;
import org.openflexo.technologyadapter.diagram.model.Diagram;
import org.openflexo.technologyadapter.diagram.model.DiagramShape;
import org.openflexo.technologyadapter.diagram.rm.DiagramPaletteResource;
import org.openflexo.technologyadapter.diagram.rm.DiagramResource;
import org.openflexo.technologyadapter.diagram.rm.DiagramSpecificationResource;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

/**
 * Parse a FML file, perform some edits and checks that pretty-print is correct
 * 
 * @author sylvain
 *
 */
@RunWith(OrderedRunner.class)
public class TestFMLDiagramPrettyPrint2 extends FMLParserTestCase {

	private static FMLCompilationUnit compilationUnit;
	private static VirtualModel virtualModel;

	static FlexoEditor editor;

	private static CompilationUnitResource fmlResource;
	private static VirtualModelNode vmNode;

	private static DiagramSpecificationResource dsResource;
	private static Diagram exampleDiagram;

	private static DiagramTechnologyAdapter diagramTA;

	static private TypedDiagramModelSlot diagramModelSlot;
	static private FlexoConcept conceptA, conceptB;
	static private DropScheme dropSchemeA, dropSchemeB;
	static private DiagramPaletteElement paletteElement1, paletteElement2;

	@Test
	@TestOrder(1)
	public void initServiceManager()
			throws ParseException, ModelDefinitionException, IOException, ResourceLoadingCancelledException, FlexoException {
		instanciateTestServiceManager();

		editor = new DefaultFlexoEditor(null, serviceManager);
		assertNotNull(editor);

		diagramTA = serviceManager.getTechnologyAdapterService().getTechnologyAdapter(DiagramTechnologyAdapter.class);
		serviceManager.getTechnologyAdapterService().activateTechnologyAdapter(diagramTA, true);

		dsResource = (DiagramSpecificationResource) serviceManager.getResourceManager()
				.getResource("http://openflexo.org/test/TestDiagramSpecification");
		assertNotNull(dsResource);

		DiagramResource diagramResource = dsResource.getExampleDiagramResources().get(0);
		exampleDiagram = diagramResource.getResourceData();
		assertNotNull(exampleDiagram);

		DiagramPaletteResource paletteResource = dsResource.getDiagramPaletteResources().get(0);
		paletteElement1 = paletteResource.getDiagramPalette().getElements().get(0);
		assertNotNull(paletteElement1);
		paletteElement2 = paletteResource.getDiagramPalette().getElements().get(1);
		assertNotNull(paletteElement2);

	}

	@Test
	@TestOrder(2)
	public void loadInitialVersion()
			throws ParseException, ModelDefinitionException, IOException, ResourceLoadingCancelledException, FlexoException {
		instanciateTestServiceManager();

		log("Initial version");

		VirtualModelLibrary vpLib = serviceManager.getVirtualModelLibrary();
		assertNotNull(vpLib);
		virtualModel = vpLib.getVirtualModel("http://openflexo.org/test/TestResourceCenter/TestDiagramVM1.fml");
		assertNotNull(virtualModel);

		fmlResource = virtualModel.getResource();
		assertNotNull(fmlResource);

		compilationUnit = fmlResource.getCompilationUnit();

		assertNotNull(virtualModel = compilationUnit.getVirtualModel());
		assertEquals("TestDiagramVM1", virtualModel.getName());

		assertNotNull(rootNode = (FMLCompilationUnitNode) compilationUnit.getPrettyPrintDelegate());
		assertNotNull(vmNode = (VirtualModelNode) rootNode.getObjectNode(virtualModel));

		RawSource rawSource = rootNode.getRawSource();
		System.out.println(rawSource.debug());
		debug(rootNode, 0);

		System.out.println("FML=\n" + compilationUnit.getFMLPrettyPrint());
		testFMLPrettyPrintEquals(compilationUnit, "TestFMLDiagramPrettyPrint2/Step1PrettyPrint.fml");

		System.out.println("Normalized=\n" + compilationUnit.getNormalizedFML());
		testNormalizedFMLRepresentationEquals(compilationUnit, "TestFMLDiagramPrettyPrint2/Step1Normalized.fml");
	}

	@Test
	@TestOrder(3)
	public void createModelSlot() {

		log("createModelSlot()");
		CreateModelSlot action = CreateModelSlot.actionType.makeNewAction(virtualModel, null, editor);
		action.setModelSlotName("diagram");
		action.setTechnologyAdapter(diagramTA);
		action.setModelSlotClass(TypedDiagramModelSlot.class);
		action.setMmRes(dsResource);
		action.doAction();
		diagramModelSlot = (TypedDiagramModelSlot) action.getNewModelSlot();

		System.out.println("FML=\n" + compilationUnit.getFMLPrettyPrint());
		testFMLPrettyPrintEquals(compilationUnit, "TestFMLDiagramPrettyPrint2/Step2PrettyPrint.fml");

		System.out.println("Normalized=\n" + compilationUnit.getNormalizedFML());
		testNormalizedFMLRepresentationEquals(compilationUnit, "TestFMLDiagramPrettyPrint2/Step2Normalized.fml");

	}

	@Test
	@TestOrder(4)
	public void createCreationScheme() {

		log("createCreationScheme()");
		// Create init behaviour
		CreateFlexoBehaviour createCreationScheme = CreateFlexoBehaviour.actionType.makeNewAction(virtualModel, null, editor);
		createCreationScheme.setFlexoBehaviourClass(CreationScheme.class);
		// createCreationScheme.setFlexoBehaviourName("init");
		createCreationScheme.doAction();
		CreationScheme initBehaviour = (CreationScheme) createCreationScheme.getNewFlexoBehaviour();

		CreateEditionAction configureModelSlotAction = CreateEditionAction.actionType.makeNewAction(initBehaviour.getControlGraph(), null,
				editor);
		configureModelSlotAction.setModelSlot(diagramModelSlot);
		configureModelSlotAction.setEditionActionClass(CreateDiagram.class);
		configureModelSlotAction.setAssignation(new DataBinding<>("diagram"));
		configureModelSlotAction.doAction();

		CreateDiagram createDiagramAction = (CreateDiagram) ((AssignationAction<?>) configureModelSlotAction.getNewEditionAction())
				.getAssignableAction();
		createDiagramAction.setDiagramSpecificationResource(dsResource);
		createDiagramAction.setDiagramName(new DataBinding<String>("\"diagram\""));
		createDiagramAction.setResourceCenter(new DataBinding<FlexoResourceCenter<?>>("this.resourceCenter"));
		createDiagramAction.setRelativePath("/Diagrams");

		System.out.println("FML=\n" + compilationUnit.getFMLPrettyPrint());
		testFMLPrettyPrintEquals(compilationUnit, "TestFMLDiagramPrettyPrint2/Step3PrettyPrint.fml");

		System.out.println("Normalized=\n" + compilationUnit.getNormalizedFML());
		testNormalizedFMLRepresentationEquals(compilationUnit, "TestFMLDiagramPrettyPrint2/Step3Normalized.fml");

	}

	@Test
	@TestOrder(5)
	public void createFlexoConceptA() {

		log("createFlexoConceptA()");

		CreateFlexoConcept addConceptA = CreateFlexoConcept.actionType.makeNewAction(virtualModel, null, editor);
		addConceptA.setNewFlexoConceptName("FlexoConceptA");
		addConceptA.doAction();
		conceptA = addConceptA.getNewFlexoConcept();

		CreateTechnologyRole createShapeRole = CreateTechnologyRole.actionType.makeNewAction(conceptA, null, editor);
		createShapeRole.setRoleName("shape");
		createShapeRole.setFlexoRoleClass(ShapeRole.class);
		createShapeRole.doAction();
		assertTrue(createShapeRole.hasActionExecutionSucceeded());

		ShapeRole role = (ShapeRole) createShapeRole.getNewFlexoRole();

		DiagramShape shape = exampleDiagram.getShapes().get(0);
		role.bindTo(shape);

		System.out.println("FML=\n" + compilationUnit.getFMLPrettyPrint());
		testFMLPrettyPrintEquals(compilationUnit, "TestFMLDiagramPrettyPrint2/Step4PrettyPrint.fml");

		System.out.println("Normalized=\n" + compilationUnit.getNormalizedFML());
		testNormalizedFMLRepresentationEquals(compilationUnit, "TestFMLDiagramPrettyPrint2/Step4Normalized.fml");

	}

	@Test
	@TestOrder(6)
	public void createDropScheme() {

		log("createDropScheme()");

		CreateFlexoBehaviour createDropScheme = CreateFlexoBehaviour.actionType.makeNewAction(conceptA, null, editor);
		createDropScheme.setFlexoBehaviourName("drop");
		createDropScheme.setFlexoBehaviourClass(DropScheme.class);
		createDropScheme.doAction();
		assertTrue(createDropScheme.hasActionExecutionSucceeded());
		dropSchemeA = (DropScheme) createDropScheme.getNewFlexoBehaviour();

		CreateEditionAction createAddShape = CreateEditionAction.actionType.makeNewAction(dropSchemeA.getControlGraph(), null, editor);
		// createAddShape.actionChoice =
		// CreateEditionActionChoice.ModelSlotSpecificAction;
		createAddShape.setModelSlot(diagramModelSlot);
		createAddShape.setEditionActionClass(AddShape.class);
		createAddShape.setAssignation(new DataBinding<>("shape"));
		createAddShape.doAction();
		assertTrue(createAddShape.hasActionExecutionSucceeded());

		FMLDiagramPaletteElementBinding newBinding = virtualModel.getFMLModelFactory().newInstance(FMLDiagramPaletteElementBinding.class);
		newBinding.setDiagramModelSlot(diagramModelSlot);
		newBinding.setPaletteElement(paletteElement1);
		newBinding.setBoundFlexoConcept(conceptA);
		newBinding.setDropScheme(dropSchemeA);

		diagramModelSlot.addToPaletteElementBindings(newBinding);

		System.out.println("FML=\n" + compilationUnit.getFMLPrettyPrint());
		testFMLPrettyPrintEquals(compilationUnit, "TestFMLDiagramPrettyPrint2/Step5PrettyPrint.fml");

		System.out.println("Normalized=\n" + compilationUnit.getNormalizedFML());
		testNormalizedFMLRepresentationEquals(compilationUnit, "TestFMLDiagramPrettyPrint2/Step5Normalized.fml");

	}

	@Test
	@TestOrder(6)
	public void createFlexoConceptB() {

		log("createFlexoConceptB()");

		CreateFlexoConcept addConceptB = CreateFlexoConcept.actionType.makeNewAction(virtualModel, null, editor);
		addConceptB.setNewFlexoConceptName("FlexoConceptB");
		addConceptB.doAction();
		conceptB = addConceptB.getNewFlexoConcept();

		CreateTechnologyRole createShapeRole = CreateTechnologyRole.actionType.makeNewAction(conceptB, null, editor);
		createShapeRole.setRoleName("shape");
		createShapeRole.setFlexoRoleClass(ShapeRole.class);
		createShapeRole.doAction();
		assertTrue(createShapeRole.hasActionExecutionSucceeded());

		ShapeRole role = (ShapeRole) createShapeRole.getNewFlexoRole();

		DiagramShape shape = exampleDiagram.getShapes().get(1);
		System.out.println("shape=" + shape);
		role.bindTo(shape);

		CreateFlexoBehaviour createDropScheme = CreateFlexoBehaviour.actionType.makeNewAction(conceptB, null, editor);
		createDropScheme.setFlexoBehaviourName("drop");
		createDropScheme.setFlexoBehaviourClass(DropScheme.class);
		createDropScheme.doAction();
		assertTrue(createDropScheme.hasActionExecutionSucceeded());
		dropSchemeB = (DropScheme) createDropScheme.getNewFlexoBehaviour();
		dropSchemeB.setTargetFlexoConcept(conceptA);

		CreateEditionAction createAddShape = CreateEditionAction.actionType.makeNewAction(dropSchemeB.getControlGraph(), null, editor);
		// createAddShape.actionChoice =
		// CreateEditionActionChoice.ModelSlotSpecificAction;
		createAddShape.setModelSlot(diagramModelSlot);
		createAddShape.setEditionActionClass(AddShape.class);
		createAddShape.setAssignation(new DataBinding<>("shape"));
		createAddShape.doAction();
		assertTrue(createAddShape.hasActionExecutionSucceeded());

		FMLDiagramPaletteElementBinding newBinding = virtualModel.getFMLModelFactory().newInstance(FMLDiagramPaletteElementBinding.class);
		newBinding.setDiagramModelSlot(diagramModelSlot);
		newBinding.setPaletteElement(paletteElement2);
		newBinding.setBoundFlexoConcept(conceptB);
		newBinding.setDropScheme(dropSchemeB);

		diagramModelSlot.addToPaletteElementBindings(newBinding);

		System.out.println("FML=\n" + compilationUnit.getFMLPrettyPrint());
		testFMLPrettyPrintEquals(compilationUnit, "TestFMLDiagramPrettyPrint2/Step6PrettyPrint.fml");

		System.out.println("Normalized=\n" + compilationUnit.getNormalizedFML());
		testNormalizedFMLRepresentationEquals(compilationUnit, "TestFMLDiagramPrettyPrint2/Step6Normalized.fml");

	}

}

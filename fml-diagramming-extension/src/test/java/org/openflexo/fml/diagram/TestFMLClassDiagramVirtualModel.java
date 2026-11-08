/**
 * 
 * Copyright (c) 2026, Openflexo
 * 
 * This file is part of Fml-diagramming-extension, a component of the software infrastructure 
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

package org.openflexo.fml.diagram;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.fml.CreationScheme;
import org.openflexo.foundation.fml.FMLModelFactory;
import org.openflexo.foundation.fml.FlexoBehaviour;
import org.openflexo.foundation.fml.FlexoConcept;
import org.openflexo.foundation.fml.PrimitiveRole;
import org.openflexo.connie.type.PrimitiveType;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.rm.CompilationUnitResource;
import org.openflexo.foundation.fml.rm.CompilationUnitResourceFactory;
import org.openflexo.foundation.fml.rt.FMLRTVirtualModelInstance;
import org.openflexo.foundation.fml.rt.FlexoConceptInstance;
import org.openflexo.foundation.fml.rt.action.CreateBasicVirtualModelInstance;
import org.openflexo.technologyadapter.diagram.fml.DropScheme;
import org.openflexo.technologyadapter.diagram.model.Diagram;
import org.openflexo.technologyadapter.diagram.model.action.DropSchemeAction;
import org.openflexo.foundation.fml.FMLTechnologyAdapter;
import org.openflexo.foundation.test.OpenflexoProjectAtRunTimeTestCase;
import org.openflexo.pamela.validation.ValidationReport;
import org.openflexo.technologyadapter.diagram.DiagramTechnologyAdapter;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

/**
 * Loads and validates the VirtualModel {@link FMLDiagrammingPlugin#FML_CLASS_DIAGRAM_VIRTUAL_MODEL_URI} the plugin instantiates to
 * represent a VirtualModel as a class diagram, and checks that its concepts and behaviours were really parsed.
 */
@RunWith(OrderedRunner.class)
public class TestFMLClassDiagramVirtualModel extends OpenflexoProjectAtRunTimeTestCase {

	private static VirtualModel classDiagram;

	@BeforeClass
	public static void setupClass() {
		instanciateTestServiceManager(DiagramTechnologyAdapter.class, FMLTechnologyAdapter.class);
	}

	@Test
	@TestOrder(1)
	public void loadVirtualModel() throws Exception {
		CompilationUnitResource resource = serviceManager.getVirtualModelLibrary()
				.getCompilationUnitResource(FMLDiagrammingPlugin.FML_CLASS_DIAGRAM_VIRTUAL_MODEL_URI);
		assertNotNull(resource);
		classDiagram = resource.getCompilationUnit().getVirtualModel();
		assertNotNull(classDiagram);
		System.out.println(classDiagram.getFMLPrettyPrint());
	}

	@Test
	@TestOrder(2)
	public void checkContents() {
		List<String> concepts = new ArrayList<>();
		for (FlexoConcept concept : classDiagram.getAllRootFlexoConcepts()) {
			collect(concept, concepts);
		}
		System.out.println("Concepts: " + concepts);
		assertEquals(7, concepts.size());
		assertEquals(1, classDiagram.getCreationSchemes().size());

		FlexoConcept conceptGR = classDiagram.getFlexoConcept("FlexoConceptGR");
		assertNotNull(conceptGR);
		assertEquals(1, conceptGR.getParentFlexoConcepts().size());
		assertEquals("ContainerGR", conceptGR.getParentFlexoConcepts().get(0).getName());
		assertNotNull(conceptGR.getFlexoBehaviour("updateProperties"));
		assertNotNull(conceptGR.getFlexoBehaviour("dropFlexoConceptGRAtTopLevel"));
		assertNotNull(conceptGR.getFlexoBehaviour("representFlexoConceptGRAtTopLevel"));
		assertNotNull(conceptGR.getAccessibleProperty("flexoConcept"));
		assertNotNull(classDiagram.getFlexoConcept("FlexoPropertyGR"));
	}

	private static void collect(FlexoConcept concept, List<String> names) {
		StringBuilder sb = new StringBuilder(concept.getName()).append("(");
		for (FlexoBehaviour b : concept.getDeclaredFlexoBehaviours()) {
			sb.append(b.getName()).append(" ");
		}
		names.add(sb.append(")").toString());
		for (FlexoConcept child : concept.getEmbeddedFlexoConcepts()) {
			collect(child, names);
		}
	}

	@Test
	@TestOrder(3)
	public void validate() {
		ValidationReport report = validate(classDiagram.getCompilationUnit());
		System.out.println("Errors: " + report.getErrorsCount() + " warnings: " + report.getWarningsCount());
		assertEquals(0, report.getErrorsCount());
	}

	private static FlexoEditor editor;
	private static VirtualModel represented;
	private static FlexoConcept person;
	private static FMLRTVirtualModelInstance classDiagramInstance;

	/**
	 * A project holding the VirtualModel to represent: one concept Person with two properties
	 */
	@Test
	@TestOrder(4)
	public void createRepresentedVirtualModel() throws Exception {
		editor = createStandaloneProject("TestFMLClassDiagram");
		FMLTechnologyAdapter fmlTA = serviceManager.getTechnologyAdapterService().getTechnologyAdapter(FMLTechnologyAdapter.class);
		CompilationUnitResourceFactory factory = fmlTA.getCompilationUnitResourceFactory();
		CompilationUnitResource resource = factory.makeTopLevelCompilationUnitResource("Library", "http://openflexo.org/test/Library.fml",
				fmlTA.getGlobalRepository(editor.getProject()).getRootFolder(), true);
		represented = resource.getLoadedResourceData().getVirtualModel();
		FMLModelFactory fmlFactory = represented.getFMLModelFactory();
		person = fmlFactory.newFlexoConcept();
		person.setName("Person");
		represented.addToFlexoConcepts(person);
		for (String name : new String[] { "firstName", "lastName" }) {
			PrimitiveRole<?> role = fmlFactory.newPrimitiveRole();
			role.setName(name);
			role.setPrimitiveType(PrimitiveType.String);
			person.addToFlexoProperties(role);
		}
		assertEquals(2, person.getDeclaredProperties().size());
	}

	/**
	 * What CreateFMLClassDiagram does: instantiate the class diagram with its creation scheme 'init'
	 */
	@Test
	@TestOrder(5)
	public void instantiateClassDiagram() throws Exception {
		CreateBasicVirtualModelInstance action = CreateBasicVirtualModelInstance.actionType
				.makeNewAction(editor.getProject().getVirtualModelInstanceRepository().getRootFolder(), null, editor);
		action.setNewVirtualModelInstanceName("LibraryClassDiagram");
		action.setNewVirtualModelInstanceTitle("LibraryClassDiagram");
		action.setVirtualModel(classDiagram);
		CreationScheme init = classDiagram.getCreationSchemes().get(0);
		action.setCreationScheme(init);
		action.setParameterValue(init.getParameters().get(0), represented.getResource());
		action.doAction();
		assertTrue(action.hasActionExecutionSucceeded());
		classDiagramInstance = action.getNewVirtualModelInstance();
		assertNotNull(classDiagramInstance);
		assertTrue(classDiagramInstance.execute("diagram") instanceof Diagram);
		assertEquals(represented, classDiagramInstance.execute("virtualModel"));
	}

	/**
	 * Represent the concept Person: one FlexoConceptGR, and one FlexoPropertyGR per property, matched again (not duplicated) when
	 * the properties are updated
	 */
	@Test
	@TestOrder(6)
	public void representConcept() throws Exception {
		DropScheme represent = (DropScheme) classDiagram.getFlexoConcept("FlexoConceptGR")
				.getFlexoBehaviour("representFlexoConceptGRAtTopLevel");
		DropSchemeAction action = new DropSchemeAction(represent, classDiagramInstance, null, editor);
		action.setDropLocation(new DianaPoint(100, 100));
		action.setParameterValue(represent.getParameters().get(0), person);
		action.doAction();
		assertTrue(action.hasActionExecutionSucceeded());

		List<FlexoConceptInstance> conceptGRs = classDiagramInstance.getFlexoConceptInstances("FlexoConceptGR");
		assertEquals(1, conceptGRs.size());
		assertEquals(person, conceptGRs.get(0).execute("flexoConcept"));
		assertNotNull(conceptGRs.get(0).execute("propertyCompartment"));
		assertEquals(2, classDiagramInstance.getFlexoConceptInstances("FlexoPropertyGR").size());

		conceptGRs.get(0).execute("this.updateProperties()");
		assertEquals(2, classDiagramInstance.getFlexoConceptInstances("FlexoPropertyGR").size());
	}

	/**
	 * The palette element: dropping a FlexoConceptGR creates a new concept in the represented VirtualModel
	 */
	@Test
	@TestOrder(7)
	public void dropNewConcept() throws Exception {
		DropScheme drop = (DropScheme) classDiagram.getFlexoConcept("FlexoConceptGR").getFlexoBehaviour("dropFlexoConceptGRAtTopLevel");
		DropSchemeAction action = new DropSchemeAction(drop, classDiagramInstance, null, editor);
		action.setDropLocation(new DianaPoint(300, 100));
		action.doAction();
		assertTrue(action.hasActionExecutionSucceeded());
		assertEquals(2, classDiagramInstance.getFlexoConceptInstances("FlexoConceptGR").size());
		assertEquals(2, represented.getFlexoConcepts().size());
		assertNotNull(represented.getFlexoConcept("Concept"));
	}
}

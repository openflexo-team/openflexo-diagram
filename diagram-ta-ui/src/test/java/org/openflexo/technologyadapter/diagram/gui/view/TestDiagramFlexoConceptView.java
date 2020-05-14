/**
 * 
 * Copyright (c) 2014-2015, Openflexo
 * 
 * This file is part of Fml-technologyadapter-ui, a component of the software infrastructure 
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

package org.openflexo.technologyadapter.diagram.gui.view;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openflexo.fml.controller.FMLFIBController;
import org.openflexo.foundation.DefaultFlexoEditor;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.fml.FlexoConcept;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.VirtualModelLibrary;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.gina.ApplicationFIBLibrary.ApplicationFIBLibraryImpl;
import org.openflexo.gina.swing.utils.FIBJPanel;
import org.openflexo.gina.test.OpenflexoFIBTestCase;
import org.openflexo.gina.test.SwingGraphicalContextDelegate;
import org.openflexo.gina.utils.InspectorGroup;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.technologyadapter.diagram.fml.FMLControlledDiagramVirtualModelNature;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;
import org.openflexo.test.UITest;

/**
 * Test StandardFlexoConceptView fib
 * 
 * @author sylvain
 * 
 */
@RunWith(OrderedRunner.class)
public class TestDiagramFlexoConceptView extends OpenflexoFIBTestCase {

	private static SwingGraphicalContextDelegate gcDelegate;

	private static Resource fibResource;

	static FlexoEditor editor;

	static VirtualModel viewPoint;
	static VirtualModel virtualModel;

	static FlexoConcept testConcept;
	static FlexoConcept testConcept2;
	static FlexoConcept testLink;

	@BeforeClass
	public static void setupClass() {
		instanciateTestServiceManager();
		initGUI();
	}

	@Test
	@TestOrder(1)
	@Category(UITest.class)
	public void testLoadWidget() {

		fibResource = ResourceLocator.locateResource("Fib/FML/StandardFlexoConceptView.fib");
		assertTrue(fibResource != null);
	}

	@Test
	@TestOrder(2)
	@Category(UITest.class)
	public void testValidateWidget() throws InterruptedException {

		validateFIB(fibResource);
	}

	@Test
	@TestOrder(3)
	@Category(UITest.class)
	public void loadConcepts() throws FileNotFoundException, ResourceLoadingCancelledException, FlexoException {

		VirtualModelLibrary vpLib = serviceManager.getVirtualModelLibrary();
		assertNotNull(vpLib);
		viewPoint = vpLib.getVirtualModel("http://openflexo.org/test/TestResourceCenter/TestControlledDiagramViewPoint2.fml");
		assertNotNull(viewPoint);
		assertFalse(viewPoint.hasNature(FMLControlledDiagramVirtualModelNature.INSTANCE));

		virtualModel = viewPoint.getVirtualModelNamed("TestVirtualModel");
		assertNotNull(virtualModel);
		assertTrue(virtualModel.hasNature(FMLControlledDiagramVirtualModelNature.INSTANCE));
		assertNotNull(FMLControlledDiagramVirtualModelNature.getTypedDiagramModelSlot(virtualModel));

		testConcept = virtualModel.getFlexoConcept("TestConcept");
		System.out.println("flexoConcept=" + testConcept);
		assertNotNull(testConcept);

		testConcept2 = virtualModel.getFlexoConcept("TestConcept2");
		System.out.println("flexoConcept=" + testConcept2);
		assertNotNull(testConcept2);

		testLink = virtualModel.getFlexoConcept("TestLink");
		System.out.println("flexoConcept=" + testLink);
		assertNotNull(testLink);

		editor = new DefaultFlexoEditor(null, serviceManager);
		assertNotNull(editor);

	}

	private static InspectorGroup fmlInspectorGroup;

	@Test
	@TestOrder(4)
	@Category(UITest.class)
	public void testInstanciateWidgetForConcept1() {

		FIBJPanel<FlexoConcept> widget = instanciateFIB(fibResource, testConcept, FlexoConcept.class);

		FMLFIBController fibController = (FMLFIBController) widget.getController();
		fmlInspectorGroup = new InspectorGroup(ResourceLocator.locateResource("Inspectors/FML"), ApplicationFIBLibraryImpl.instance(),
				null);
		fibController.setDefaultInspectorGroup(fmlInspectorGroup);

		gcDelegate.addTab("TestConcept", widget.getController());
	}

	@Test
	@TestOrder(5)
	@Category(UITest.class)
	public void testInstanciateWidgetForConcept2() {

		FIBJPanel<FlexoConcept> widget = instanciateFIB(fibResource, testConcept2, FlexoConcept.class);
		FMLFIBController fibController = (FMLFIBController) widget.getController();
		fibController.setDefaultInspectorGroup(fmlInspectorGroup);

		gcDelegate.addTab("TestConcept2", widget.getController());
	}

	@Test
	@TestOrder(6)
	@Category(UITest.class)
	public void testInstanciateWidgetForConcept3() {

		FIBJPanel<FlexoConcept> widget = instanciateFIB(fibResource, testLink, FlexoConcept.class);
		FMLFIBController fibController = (FMLFIBController) widget.getController();
		fibController.setDefaultInspectorGroup(fmlInspectorGroup);

		gcDelegate.addTab("TestLink", widget.getController());
	}

	public static void initGUI() {
		gcDelegate = new SwingGraphicalContextDelegate(TestDiagramFlexoConceptView.class.getSimpleName());
	}

	@AfterClass
	public static void waitGUI() {
		gcDelegate.waitGUI();
	}

	@Before
	public void setUp() {
		gcDelegate.setUp();
	}

	@Override
	@After
	public void tearDown() throws Exception {
		gcDelegate.tearDown();
	}

}

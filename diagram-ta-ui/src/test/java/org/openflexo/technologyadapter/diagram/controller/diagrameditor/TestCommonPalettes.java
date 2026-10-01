/**
 * 
 * Copyright (c) 2026, Openflexo
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

package org.openflexo.technologyadapter.diagram.controller.diagrameditor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import javax.swing.SwingUtilities;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openflexo.diana.BackgroundImageBackgroundStyle;
import org.openflexo.diana.DianaUtils;
import org.openflexo.diana.ShapeGraphicalRepresentation;
import org.openflexo.diana.control.PaletteElement;
import org.openflexo.diana.palettes.DianaPalettes;
import org.openflexo.diana.swing.control.SwingToolFactory;
import org.openflexo.diana.swing.control.tools.JDianaPalette;
import org.openflexo.diana.swing.control.tools.JDianaPaletteGroup;
import org.openflexo.foundation.DefaultFlexoEditor;
import org.openflexo.foundation.FlexoServiceManager;
import org.openflexo.foundation.resource.DirectoryResourceCenter;
import org.openflexo.foundation.test.OpenflexoTestCase;
import org.openflexo.technologyadapter.diagram.DiagramTechnologyAdapter;
import org.openflexo.technologyadapter.diagram.model.Diagram;
import org.openflexo.technologyadapter.diagram.model.DiagramShape;
import org.openflexo.technologyadapter.diagram.model.action.AddShape;
import org.openflexo.technologyadapter.diagram.rm.DiagramRepository;
import org.openflexo.technologyadapter.diagram.rm.DiagramResource;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;
import org.openflexo.test.UITest;

/**
 * The common tab of the diagram editor shows the palettes shipped with DIANA, as collapsible panels; a shape of the Emoji palette
 * keeps its image and its floating label once its diagram is saved and reloaded
 */
@RunWith(OrderedRunner.class)
public class TestCommonPalettes extends OpenflexoTestCase {

	private static final String DIAGRAM_URI = "http://openflexo.org/test/CommonPalettes";

	private static FlexoServiceManager applicationContext;
	private static DirectoryResourceCenter resourceCenter;
	private static DiagramResource diagramResource;
	private static FreeDiagramEditor editor;

	@Test
	@TestOrder(1)
	@Category(UITest.class)
	public void testInitialize() throws Exception {
		applicationContext = instanciateTestServiceManager(DiagramTechnologyAdapter.class);
		resourceCenter = makeNewDirectoryResourceCenter(applicationContext);
		DiagramTechnologyAdapter diagramTA = applicationContext.getTechnologyAdapterService()
				.getTechnologyAdapter(DiagramTechnologyAdapter.class);
		DiagramRepository<?> repository = diagramTA.getDiagramRepository(resourceCenter);
		diagramResource = diagramTA.getDiagramResourceFactory().makeDiagramResource("CommonPalettes", DIAGRAM_URI, null,
				repository.getRootFolder(), true);
		diagramResource.save();
	}

	@Test
	@TestOrder(2)
	@Category(UITest.class)
	public void testCommonPaletteGroup() throws Exception {
		SwingUtilities.invokeAndWait(() -> editor = new FreeDiagramEditor(diagramResource.getDiagram(), false, null, SwingToolFactory.DEFAULT));
		JDianaPaletteGroup group = editor.getCommonPaletteGroup();
		assertNotNull(group);
		assertEquals(DianaPalettes.PALETTES.size(), group.getPalettes().size());
		for (int i = 0; i < group.getPalettes().size(); i++) {
			JDianaPalette palette = group.getPalettes().get(i);
			assertEquals(DianaPalettes.PALETTES.get(i).getTitle(), palette.getPalette().getTitle());
			assertTrue(palette.getPalette().getTitle() + " is empty", palette.getPalette().getElements().size() > 0);
			assertEquals(palette.getPalette().getTitle() + " is not attached", editor, palette.getEditor());
			// Basic only is opened
			assertEquals(palette.getPalette().getTitle(), i == 0, group.isOpened(i));
		}
		assertEquals(group.getPalettes().get(0), editor.getCommonPalette());
		int commonTab = editor.getPaletteView().indexOfTab(editor.getCommonPaletteTitle());
		assertTrue(commonTab >= 0);
		assertEquals(group.getComponent(), editor.getPaletteView().getComponentAt(commonTab));
	}

	private static PaletteElement emoji(String name) {
		for (JDianaPalette palette : editor.getCommonPaletteGroup().getPalettes()) {
			if (palette.getPalette().getTitle().equals("Emoji")) {
				for (PaletteElement element : palette.getPalette().getElements()) {
					if (element.getName().equals(name)) {
						return element;
					}
				}
			}
		}
		throw new AssertionError("No emoji " + name);
	}

	/**
	 * Add a shape as the common palettes drop an element (see DiagramEditorPaletteModel), then save and reload the diagram
	 */
	@Test
	@TestOrder(3)
	@Category(UITest.class)
	public void testEmojiSurvivesReload() throws Exception {
		Diagram diagram = diagramResource.getDiagram();
		ShapeGraphicalRepresentation shapeGR = diagramResource.getFactory()
				.makeShapeGraphicalRepresentation(emoji("Laptop").getGraphicalRepresentation());
		DianaUtils.fitInBox(shapeGR, 50, 40);
		assertEquals(40, shapeGR.getWidth(), 0.001);
		assertEquals(20, shapeGR.getAbsoluteTextX(), 0.001);

		AddShape action = AddShape.actionType.makeNewAction(diagram, null, new DefaultFlexoEditor(null, applicationContext));
		action.setGraphicalRepresentation(shapeGR);
		action.setNewShapeName("Laptop");
		action.doAction();
		assertTrue(action.hasActionExecutionSucceeded());
		diagramResource.save();

		File diagramFile = (File) diagramResource.getIODelegate().getSerializationArtefact();
		String serialized = new String(Files.readAllBytes(diagramFile.toPath()), StandardCharsets.UTF_8);
		assertTrue("Image not stored by its classpath path", serialized.contains("classpath:Images/FluentEmoji/Laptop.png"));
		assertFalse("Image stored as an absolute jar URL", serialized.contains("jar:file:"));

		// Reload from the resource center directory, in a new service manager
		FlexoServiceManager reloadContext = instanciateTestServiceManager(DiagramTechnologyAdapter.class);
		DirectoryResourceCenter reloadedCenter = DirectoryResourceCenter.instanciateNewDirectoryResourceCenter(testResourceCenterDirectory,
				reloadContext.getResourceCenterService());
		reloadContext.getResourceCenterService().addToResourceCenters(reloadedCenter);
		reloadedCenter.performDirectoryWatchingNow();
		DiagramResource reloaded = reloadContext.getTechnologyAdapterService().getTechnologyAdapter(DiagramTechnologyAdapter.class)
				.getDiagramRepository(reloadedCenter).getResource(DIAGRAM_URI);
		assertNotNull(reloaded);
		assertEquals(1, reloaded.getDiagram().getShapes().size());
		DiagramShape shape = reloaded.getDiagram().getShapes().get(0);
		assertTrue(shape.getGraphicalRepresentation().getBackground() instanceof BackgroundImageBackgroundStyle);
		BackgroundImageBackgroundStyle image = (BackgroundImageBackgroundStyle) shape.getGraphicalRepresentation().getBackground();
		assertNotNull("Image not read back", image.getImageResource());
		assertNotNull("Image not loaded", image.getImage());
		assertTrue(shape.getGraphicalRepresentation().getIsFloatingLabel());
		assertEquals(20, shape.getGraphicalRepresentation().getAbsoluteTextX(), 0.001);
	}
}

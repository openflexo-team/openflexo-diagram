/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Openflexo-technology-adapters-ui, a component of the software infrastructure 
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

package org.openflexo.technologyadapter.diagram.controller.action;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.openflexo.foundation.action.FlexoActionFactory;
import org.openflexo.foundation.action.FlexoActionRunnable;
import org.openflexo.foundation.fml.rt.FlexoConceptInstance;
import org.openflexo.icon.IconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.swing.FlexoFileChooser;
import org.openflexo.technologyadapter.diagram.controller.diagrameditor.DiagramEditor;
import org.openflexo.technologyadapter.diagram.controller.diagrameditor.DiagramView;
import org.openflexo.technologyadapter.diagram.controller.diagrameditor.FMLControlledDiagramModuleView;
import org.openflexo.technologyadapter.diagram.model.action.ExportFMLControlledDiagramToSVG;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

public class ExportFMLControlledDiagramToSVGInitializer
		extends ActionInitializer<ExportFMLControlledDiagramToSVG, FlexoConceptInstance, FlexoConceptInstance> {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	public ExportFMLControlledDiagramToSVGInitializer(ControllerActionInitializer actionInitializer) {
		super(ExportFMLControlledDiagramToSVG.actionType, actionInitializer);
	}

	@Override
	protected FlexoActionRunnable<ExportFMLControlledDiagramToSVG, FlexoConceptInstance, FlexoConceptInstance> getDefaultInitializer() {

		return (e, action) -> {
			FlexoFileChooser fileChooser;
			fileChooser = new FlexoFileChooser(getController().getFlexoFrame());
			fileChooser.setCurrentDirectory(getController().getApplicationContext().getAdvancedPrefs().getLastVisitedDirectory());
			fileChooser.setDialogTitle(getController().getFlexoLocales().localizedForKey("enter_file_to_export_(png,gif,jpg)"));
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setFileFilter(new SVGFileFilter());

			if (fileChooser.showSaveDialog() == JFileChooser.APPROVE_OPTION) {

				File f = fileChooser.getSelectedFile();

				if (!f.getName().endsWith(".svg")) {
					f = new File(f.getParentFile(), f.getName() + ".svg");
				}

				action.setExportFile(f);
				if (f.exists()) {
					if (FlexoController.confirm(getController().getFlexoLocales().localizedForKey("file_exists_overwrite_?"))) {
						return true;
					}
					else {
						return false;
					}
				}
				return true;
			}

			return false;
		};

	}

	@Override
	protected FlexoActionRunnable<ExportFMLControlledDiagramToSVG, FlexoConceptInstance, FlexoConceptInstance> getDefaultFinalizer() {

		return (e, action) -> {
			System.out.println("On exporte vers " + action.getExportFile());

			System.out.println(
					"ModuleView = " + getController().getCurrentModuleView() + " of " + getController().getCurrentModuleView().getClass());

			if (getController().getCurrentModuleView() instanceof FMLControlledDiagramModuleView) {
				FMLControlledDiagramModuleView moduleView = (FMLControlledDiagramModuleView) getController().getCurrentModuleView();
				DiagramEditor editor = moduleView.getEditor();
				DiagramView drawingView = editor.getDrawingView();

				// Get a DOMImplementation.
				DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();

				// Create an instance of org.w3c.dom.Document.
				String svgNS = "http://www.w3.org/2000/svg";
				Document document = domImpl.createDocument(svgNS, "svg", null);

				// Create an instance of the SVG Generator.
				SVGGraphics2D svgGenerator = new SVGGraphics2D(document);

				// Ask the test to render into the SVG Graphics2D implementation.
				// TestSVGGen test = new TestSVGGen();
				// drawingView.invalidate();
				drawingView.paint(svgGenerator);

				// Finally, stream out SVG to the standard output using
				// UTF-8 encoding.
				boolean useCSS = true; // we want to use CSS style attributes
				try {
					FileOutputStream os = new FileOutputStream(action.getExportFile());
					Writer out = new OutputStreamWriter(os, "UTF-8");
					svgGenerator.stream(out, useCSS);
					out.close();
					os.close();

					System.out.println("Written " + action.getExportFile());

					System.out.println(FileUtils.fileContents(action.getExportFile()));
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (SVGGraphics2DIOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}

			return true;
		};
	}

	@Override
	protected Icon getEnabledIcon(
			FlexoActionFactory<ExportFMLControlledDiagramToSVG, FlexoConceptInstance, FlexoConceptInstance> actionType) {
		return IconLibrary.EXPORT_ICON;
	}

	public static class SVGFileFilter extends FileFilter {

		@Override
		public boolean accept(File f) {
			if (f.isDirectory()) {
				return false;
			}
			return f.getName().endsWith(".svg");
		}

		@Override
		public String getDescription() {
			return FlexoLocalization.getMainLocalizer().localizedForKey("svg_files");
		}

	}

}

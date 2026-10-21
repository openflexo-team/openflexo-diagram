/**
 * 
 * Copyright (c) 2014, Openflexo
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

package org.openflexo.technologyadapter.diagram.fml.action;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import org.apache.poi.ddf.EscherPropertyTypes;
import org.apache.poi.hslf.usermodel.HSLFAutoShape;
import org.apache.poi.hslf.usermodel.HSLFLine;
import org.apache.poi.hslf.usermodel.HSLFMasterSheet;
import org.apache.poi.hslf.usermodel.HSLFPictureShape;
import org.apache.poi.hslf.usermodel.HSLFShape;
import org.apache.poi.hslf.usermodel.HSLFGroupShape;
import org.apache.poi.hslf.usermodel.HSLFSimpleShape;
import org.apache.poi.hslf.usermodel.HSLFSlide;
import org.apache.poi.hslf.usermodel.HSLFTable;
import org.apache.poi.hslf.usermodel.HSLFTableCell;
import org.apache.poi.hslf.usermodel.HSLFTextBox;
import org.apache.poi.hslf.usermodel.HSLFTextParagraph;
import org.apache.poi.hslf.usermodel.HSLFTextShape;
import org.apache.poi.hslf.usermodel.HSLFTextRun;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.sl.usermodel.Background;
import org.openflexo.diana.BackgroundImageBackgroundStyle;
import org.openflexo.diana.ConnectorGraphicalRepresentation;
import org.openflexo.diana.DrawingGraphicalRepresentation;
import org.openflexo.diana.GraphicalRepresentation;
import org.openflexo.diana.ShapeGraphicalRepresentation;
import org.openflexo.diana.TextStyle;
import org.openflexo.diana.ForegroundStyle.DashStyle;
import org.openflexo.diana.GraphicalRepresentation.HorizontalTextAlignment;
import org.openflexo.diana.GraphicalRepresentation.ParagraphAlignment;
import org.openflexo.diana.GraphicalRepresentation.VerticalTextAlignment;
import org.openflexo.diana.ShapeGraphicalRepresentation.DimensionConstraints;
import org.openflexo.diana.connectors.ConnectorSpecification.ConnectorType;
import org.openflexo.diana.connectors.ConnectorSymbol.EndSymbolType;
import org.openflexo.diana.connectors.ConnectorSymbol.StartSymbolType;
import org.openflexo.diana.shapes.Rectangle;
import org.openflexo.diana.shapes.ShapeSpecification.ShapeType;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionFactory;
import org.openflexo.foundation.fml.FMLObject;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.swing.ImageUtils;
import org.openflexo.swing.ImageUtils.ImageType;
import org.openflexo.technologyadapter.diagram.model.Diagram;
import org.openflexo.technologyadapter.diagram.model.DiagramConnector;
import org.openflexo.technologyadapter.diagram.model.DiagramContainerElement;
import org.openflexo.technologyadapter.diagram.model.DiagramElement;
import org.openflexo.technologyadapter.diagram.model.DiagramFactory;
import org.openflexo.technologyadapter.diagram.model.DiagramShape;
import org.openflexo.technologyadapter.diagram.model.action.CreateDiagram;
import org.openflexo.technologyadapter.diagram.rm.DiagramResource;
import org.openflexo.technologyadapter.diagram.rm.DiagramResourceFactory;
import org.openflexo.toolbox.JavaUtils;
import org.openflexo.toolbox.StringUtils;

public abstract class AbstractCreateDiagramFromPPTSlide<A extends AbstractCreateDiagramFromPPTSlide<A, T>, T extends FlexoObject>
		extends FlexoAction<A, T, FMLObject> {

	private static final Logger logger = Logger.getLogger(CreateDiagram.class.getPackage().getName());

	public DrawingGraphicalRepresentation graphicalRepresentation;
	private String diagramName;
	private String diagramTitle;
	private String diagramURI;
	private DiagramResource diagramResource;
	// private File diagramFile;
	private Diagram diagram;

	private HSLFSlideShow selectedSlideShow;
	private ArrayList<HSLFSlide> currentSlides;
	private File file;
	private HSLFSlide slide;

	protected AbstractCreateDiagramFromPPTSlide(FlexoActionFactory actionType, T focusedObject, Vector<FMLObject> globalSelection,
			FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	public DrawingGraphicalRepresentation getGraphicalRepresentation() {
		return graphicalRepresentation;
	}

	public void setGraphicalRepresentation(DrawingGraphicalRepresentation graphicalRepresentation) {
		this.graphicalRepresentation = graphicalRepresentation;
	}

	public DiagramResource getDiagramResource() {
		return diagramResource;
	}

	public void setDiagramResource(DiagramResource diagramResource) {
		if ((diagramResource == null && this.diagramResource != null)
				|| (diagramResource != null && !diagramResource.equals(this.diagramResource))) {

			DiagramResource oldValue = this.diagramResource;
			this.diagramResource = diagramResource;
			getPropertyChangeSupport().firePropertyChange("diagramResource", oldValue, diagramResource);
			Diagram diag = getDiagram();
			getPropertyChangeSupport().firePropertyChange("diagram", null, diag);
			if (diag != null)
				getPropertyChangeSupport().firePropertyChange("diagramTitle", null, diag.getTitle());
			getPropertyChangeSupport().firePropertyChange("isValid", null, isValid());
			getPropertyChangeSupport().firePropertyChange("errorMessage", null, getErrorMessage());
		}
	}

	public Diagram getDiagram() {
		if (getDiagramResource() != null) {
			return getDiagramResource().getDiagram();
		}
		return diagram;
	}

	public void setDiagram(Diagram diagram) {

		if ((diagram == null && this.diagram != null) || (diagram != null && !diagram.equals(this.diagram))) {

			Diagram oldValue = this.diagram;
			this.diagram = diagram;
			if (diagram != null) {
				diagramResource = (DiagramResource) diagram.getResource();
			}
			getPropertyChangeSupport().firePropertyChange("diagram", oldValue, getDiagram());
			if (diagram != null) {
				getPropertyChangeSupport().firePropertyChange("diagramTitle", null, diagram.getTitle());
			}

			getPropertyChangeSupport().firePropertyChange("isValid", null, isValid());
			getPropertyChangeSupport().firePropertyChange("errorMessage", null, getErrorMessage());
		}

	}

	public List<HSLFShape> getPoiShapes() {
		return poiShapes;
	}

	public void setPoiShapes(List<HSLFShape> poiShapes) {
		this.poiShapes = poiShapes;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/*
	 * Diagram Configuration
	 */

	private String errorMessage;

	public String getErrorMessage() {
		return errorMessage;
	}

	@Override
	public boolean isValid() {
		if (getDiagram() == null) {
			if (StringUtils.isEmpty(getDiagramName())) {
				errorMessage = noNameMessage();
				return false;
			}
			if (!getDiagramName().equals(JavaUtils.getClassName(getDiagramName()))
					&& !getDiagramName().equals(JavaUtils.getVariableName(getDiagramName()))) {
				errorMessage = invalidNameMessage();
				return false;
			}
			/*
			if (StringUtils.isEmpty(getDiagramTitle())) {
				errorMessage = noTitleMessage();
				return false;
			}
			*/
		}

		if (getFile() == null) {
			errorMessage = noFileMessage();
			return false;
		}

		if (getSlide() == null) {
			errorMessage = noSlideMessage();
			return false;
		}

		errorMessage = "";
		System.out.println("VALID");

		return true;
	}

	public String noDiagramSpecificationSelectedMessage() {
		return getLocales().localizedForKey("no_diagram_type_selected");
	}

	public String noTitleMessage() {
		return getLocales().localizedForKey("no_diagram_title_defined");
	}

	public String noFileMessage() {
		return getLocales().localizedForKey("no_ppt_file_defined");
	}

	public String existingFileMessage() {
		return getLocales().localizedForKey("file_already_existing");
	}

	public String noNameMessage() {
		return getLocales().localizedForKey("no_diagram_name_defined");
	}

	public String noSlideMessage() {
		return getLocales().localizedForKey("no_slide_defined");
	}

	public String invalidNameMessage() {
		return getLocales().localizedForKey("invalid_name_for_new_diagram");
	}

	public String duplicatedNameMessage() {
		return getLocales().localizedForKey("a_diagram_with_that_name_already_exists");
	}

	public String getDiagramName() {
		if (getDiagram() != null) {
			return getDiagram().getName();
		}
		return diagramName;
	}

	public void setDiagramName(String diagramName) {

		if (diagramName == null) {
			return;
		}

		if (!diagramName.endsWith(DiagramResourceFactory.DIAGRAM_SUFFIX)) {
			diagramName = diagramName + DiagramResourceFactory.DIAGRAM_SUFFIX;
		}

		boolean wasValid = isValid();
		if (getDiagram() != null) {
			getDiagram().setName(diagramName);
		}

		this.diagramName = diagramName;
		getPropertyChangeSupport().firePropertyChange("diagramName", null, diagramName);
		getPropertyChangeSupport().firePropertyChange("isValid", wasValid, isValid());
		getPropertyChangeSupport().firePropertyChange("errorMessage", null, getErrorMessage());

	}

	public String getDiagramTitle() {
		if (getDiagram() != null) {
			return getDiagram().getTitle();
		}
		return diagramTitle;
	}

	public void setDiagramTitle(String diagramTitle) {
		if (getDiagram() != null) {
			getDiagram().setTitle(diagramTitle);
		}
		this.diagramTitle = diagramTitle;
		getPropertyChangeSupport().firePropertyChange("diagramTitle", null, diagramTitle);
		getPropertyChangeSupport().firePropertyChange("isValid", null, isValid());
		getPropertyChangeSupport().firePropertyChange("errorMessage", null, getErrorMessage());
	}

	public String getDiagramURI() {
		return diagramURI;
	}

	public void setDiagramURI(String diagramURI) {
		this.diagramURI = diagramURI;
	}

	/*public File getDiagramFile() {
		return diagramFile;
	}
	
	public void setDiagramFile(File diagramFile) {
		this.diagramFile = diagramFile;
	}*/

	/*
	 * PPT Configuration
	 */

	public DiagramFactory getDiagramFactory() {
		return getDiagram().getDiagramFactory();
	}

	public void loadSlideShow() {
		try (FileInputStream fis = new FileInputStream(getFile())) {
			selectedSlideShow = new HSLFSlideShow(fis);
			if (currentSlides == null) {
				currentSlides = new ArrayList<>();
			}
			else {
				currentSlides.clear();
			}
			for (HSLFSlide slide : selectedSlideShow.getSlides()) {
				currentSlides.add(slide);
			}
			setCurrentSlides(currentSlides);
			getPropertyChangeSupport().firePropertyChange("selectedSlideShow", null, selectedSlideShow);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
		if (file != null) {
			loadSlideShow();
		}
		getPropertyChangeSupport().firePropertyChange("file", null, file);
		getPropertyChangeSupport().firePropertyChange("currentSlides", null, getCurrentSlides());
		getPropertyChangeSupport().firePropertyChange("isValid", null, isValid());
		getPropertyChangeSupport().firePropertyChange("errorMessage", null, getErrorMessage());
		getPropertyChangeSupport().firePropertyChange("valid", null, isValid());
	}

	public HSLFSlide getSlide() {
		return slide;
	}

	public void setSlide(HSLFSlide slide) {
		this.slide = slide;
		// Unused boolean wasValid = isValid();
		getPropertyChangeSupport().firePropertyChange("slide", null, getSlide());
		getPropertyChangeSupport().firePropertyChange("isValid", null, isValid());
		getPropertyChangeSupport().firePropertyChange("errorMessage", null, getErrorMessage());
	}

	public HSLFSlideShow getSelectedSlideShow() {
		return selectedSlideShow;
	}

	public void setSelectedSlideShow(HSLFSlideShow selectedSlideShow) {
		this.selectedSlideShow = selectedSlideShow;
	}

	public ArrayList<HSLFSlide> getCurrentSlides() {
		return currentSlides;
	}

	public void setCurrentSlides(ArrayList<HSLFSlide> currentSlides) {
		this.currentSlides = currentSlides;
	}

	public ImageIcon getMiniature(HSLFSlide s) {
		return getScreenShot(s, 75);
	}

	public ImageIcon getOverview(HSLFSlide s) {
		return getScreenShot(s, 400);
	}

	public ImageIcon getScreenShot(HSLFSlide s, double size) {
		if (s != null && s.getSlideShow() != null) {
			try {
				Dimension d = s.getSlideShow().getPageSize();
				BufferedImage i = new BufferedImage((int) size, (int) (size * d.height / d.width), BufferedImage.TYPE_INT_RGB);
				Graphics2D graphics = i.createGraphics();
				graphics.transform(AffineTransform.getScaleInstance(size / d.width, size / d.width));
				s.draw(graphics);
				return new ImageIcon(i);
			} catch (ArrayIndexOutOfBoundsException e) {
				logger.warning("Some fonts are cannot be previewed (Calibri, Gothic MS)");
			} catch (Exception e) {
				logger.warning("Unable to create a preview for the slide " + s.getSlideNumber());
			}
		}
		return null;
	}

	public <I> I saveImageFile(BufferedImage image, String name) {
		FlexoResourceCenter<I> rc = (FlexoResourceCenter<I>) getDiagramResource().getResourceCenter();
		I serializationArtefact = rc.createEntry(JavaUtils.getClassName(name) + ".diagram-element" + ".png",
				rc.getContainer((I) getDiagramResource().getIODelegate().getSerializationArtefact()));

		if (serializationArtefact instanceof File) {
			try {
				ImageUtils.saveImageToFile(image, (File) serializationArtefact, ImageType.PNG);
				return serializationArtefact;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}

		/*File imageFile = new File(getDiagramFile().getParent(), JavaUtils.getClassName(name) + ".diagram-element" + ".png");
		try {
			ImageUtils.saveImageToFile(image, imageFile, ImageType.PNG);
			return imageFile;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}*/
		return null;
	}

	private HashMap<DiagramShape, HSLFShape> shapesMap;
	private HashMap<DiagramConnector, HSLFShape> connectorsMap;
	private List<HSLFShape> poiShapes;

	// This cannot be determinated safely
	private final boolean computeOrderOfShape = true;
	// This cannot be determinated safely
	private final boolean computeStartEndShapesOfConnectors = true;

	public HashMap<DiagramShape, HSLFShape> getDiagramShapesMap() {
		return shapesMap;
	}

	public void setDiagramShapesMap(HashMap<DiagramShape, HSLFShape> shapesMap) {
		this.shapesMap = shapesMap;
	}

	public HashMap<DiagramConnector, HSLFShape> getDiagramConnectorsMap() {
		return connectorsMap;
	}

	public void setDiagramConnectorsMap(HashMap<DiagramConnector, HSLFShape> connectorsMap) {
		this.connectorsMap = connectorsMap;
	}

	/*
	 * Transfo PPT to Diagram
	 */
	public void convertSlideToDiagram(HSLFSlide slide) {
		HSLFMasterSheet master = slide.getMasterSheet();
		shapesMap = new LinkedHashMap<>();
		connectorsMap = new LinkedHashMap<>();
		poiShapes = new ArrayList<>();

		// Retrieve all transformable elements
		if (slide.getFollowMasterObjects()) {
			if (master.getShapes() != null) {
				List<HSLFShape> sh = master.getShapes();
				for (int i = sh.size() - 1; i >= 0; i--) {
					if (sh.get(i).isPlaceholder()) {
						continue;
					}
					poiShapes.add(sh.get(i));
				}
			}
		}
		for (HSLFShape shape : slide.getShapes()) {
			poiShapes.add(shape);
		}

		// Transform POI shapes into Flexo Shapes
		for (HSLFShape shape : poiShapes) {
			transformPPTShapeInDiagramElement(getDiagram(), shape);
		}

		if (computeOrderOfShape) {
			computeLayers();
		}

		// Attach connectors.
		if (computeStartEndShapesOfConnectors) {
			for (Map.Entry<DiagramConnector, HSLFShape> diagramConnectorEntry : getDiagramConnectorsMap().entrySet()) {
				attachConnector(diagramConnectorEntry.getKey(), diagramConnectorEntry.getValue());
			}
		}

	}

	private void computeLayers() {
		int currentLayer = 0;// getDiagramShapesMap().size() + getDiagramConnectorsMap().size();
		for (Map.Entry<DiagramShape, HSLFShape> diagramShapeEntry : getDiagramShapesMap().entrySet()) {
			diagramShapeEntry.getKey().getGraphicalRepresentation().setLayer(currentLayer++);
		}
		for (Map.Entry<DiagramConnector, HSLFShape> diagramConnectorEntry : getDiagramConnectorsMap().entrySet()) {
			diagramConnectorEntry.getKey().getGraphicalRepresentation().setLayer(currentLayer++);
		}
	}

	/**
	 * Transform a POI shape in a DiagramElement
	 * 
	 * @param container
	 * @param shape
	 * @return
	 */
	private DiagramElement<?> transformPPTShapeInDiagramElement(DiagramContainerElement<?> container, HSLFShape shape) {
		DiagramElement<?> diagramElement = null;
		if (shape instanceof HSLFSimpleShape) {
			diagramElement = transformSimpleShape((HSLFSimpleShape) shape);

		}
		else if (shape instanceof Background) {
			diagramElement = transformBackground((Background) shape);
		}
		else if (shape instanceof HSLFGroupShape) {
			diagramElement = transformShapeGroup((HSLFGroupShape) shape);
		}
		return diagramElement;
	}

	private DiagramElement<?> transformSimpleShape(HSLFSimpleShape shape) {
		DiagramElement<?> diagramElement = null;
		if (shape instanceof HSLFLine) {
			diagramElement = makeDiagramConnector(shape);
		}
		else if (shape instanceof HSLFPictureShape) {
			diagramElement = transformPicture((HSLFPictureShape) shape);
		}
		else if (shape instanceof HSLFTextShape) {
			diagramElement = transformTextShape((HSLFTextShape) shape);
		}
		return diagramElement;
	}

	private DiagramElement<?> transformPicture(HSLFPictureShape shape) {
		DiagramElement<?> diagramElement = makePictureShape(shape);
		return diagramElement;
	}

	private DiagramElement<?> transformTextShape(HSLFTextShape shape) {
		DiagramElement<?> diagramElement = null;
		if (shape instanceof HSLFAutoShape) {
			diagramElement = transformAutoShape((HSLFAutoShape) shape);
		}
		else if (shape instanceof HSLFTextBox) {
			diagramElement = makeTextBox((HSLFTextBox) shape);
		}
		return diagramElement;
	}

	private DiagramElement<?> transformAutoShape(HSLFAutoShape shape) {
		DiagramElement<?> diagramElement = null;
		if (isConnector(shape.getShapeType())) {
			diagramElement = makeDiagramConnector(shape);
			applySimpleShapeGr(diagramElement.getGraphicalRepresentation(), shape);
		}
		else {
			diagramElement = makeDiagramShape(shape);
			applyTextShapeGr((ShapeGraphicalRepresentation) diagramElement.getGraphicalRepresentation(), shape);
		}
		return diagramElement;
	}

	private static DiagramElement<?> transformBackground(Background shape) {
		DiagramElement<?> diagramElement = null;
		return diagramElement;
	}

	private DiagramElement<?> transformShapeGroup(HSLFGroupShape shape) {
		DiagramElement<?> diagramElement = null;
		if (shape instanceof HSLFTable) {
			diagramElement = makeTable((HSLFTable) shape);
		}
		else
			diagramElement = makeGroupShape(shape);
		return diagramElement;
	}

	private DiagramShape makeTextBox(HSLFTextBox textBox) {
		DiagramShape newShape = makeDiagramShape(textBox);
		applyTextShapeGr(newShape.getGraphicalRepresentation(), textBox);
		newShape.getGraphicalRepresentation().setLayer(2);
		return newShape;
	}

	// To optimize the group shape convertion, if there is a textbox and a shape, then the shape is the container of the text
	// This should cover a large set of cases
	private DiagramElement<?> makeGroupShape(HSLFGroupShape shapeGroup) {
		// If we find a shape without text and a text, then it could be optimized as a shape containing this text
		boolean isShapeAndText = false;
		if (shapeGroup.getShapes().size() == 2) {
			HSLFTextShape textShape = null;
			HSLFSimpleShape containerShape = null;
			for (HSLFShape shape : shapeGroup.getShapes()) {
				if ((shape instanceof HSLFTextShape) && (hasText((HSLFTextShape) shape))) {
					textShape = (HSLFTextShape) shape;
				}
				else if ((shape instanceof HSLFTextShape) && (!hasText((HSLFTextShape) shape))) {
					containerShape = (HSLFTextShape) shape;
				}
				else if ((shape instanceof HSLFSimpleShape)) {
					containerShape = (HSLFSimpleShape) shape;
				}
			}
			if (containerShape != null && textShape != null) {
				DiagramElement<?> shape = null;
				shape = transformTextShape(textShape);
				if (isConnector(containerShape.getShapeType())) {
					shape = makeDiagramConnector(containerShape);
					applySimpleShapeGr(shape.getGraphicalRepresentation(), containerShape);
				}
				else {
					if (containerShape instanceof HSLFPictureShape) {
						applyPictureGr((ShapeGraphicalRepresentation) shape.getGraphicalRepresentation(), (HSLFPictureShape) containerShape);
					}
					else {
						applySimpleShapeGr(shape.getGraphicalRepresentation(), containerShape);
					}
				}

				return shape;
			}
		}
		if (!isShapeAndText) {
			// Transform shapes
			for (HSLFShape shape : shapeGroup.getShapes()) {
				transformPPTShapeInDiagramElement(getDiagram(), shape);
			}
		}
		return null;
	}

	private DiagramShape makeTable(HSLFTable table) {

		DiagramShape shape = makeDiagramShape(table);
		applyTableGr(shape.getGraphicalRepresentation(), table);
		for (int col = 0; col < table.getNumberOfColumns(); col++) {
			for (int row = 0; row < table.getNumberOfRows(); row++) {
				HSLFTableCell cell = table.getCell(row, col);
				if (cell != null) {
					DiagramShape newCell = makeTextBox(cell);
					applyTableCellGr(newCell.getGraphicalRepresentation(), cell);
					getDiagram().addToShapes(newCell);
				}
			}
		}
		return shape;
	}

	private DiagramShape makePictureShape(HSLFPictureShape pictureShape) {
		DiagramShape shape = makeDiagramShape(pictureShape);
		applyPictureGr(shape.getGraphicalRepresentation(), pictureShape);
		return shape;
	}

	/* Unused 
	private DiagramShape makeFreeformShape(Freeform freeformShape) {
	DiagramShape newShape = getDiagramFactory().makeNewShape(getShapeName(freeformShape), getDiagram());
	ShapeGraphicalRepresentation gr = newShape.getGraphicalRepresentation();
	gr.setShapeType(ShapeType.CUSTOM_POLYGON);
	Polygon ss = ((Polygon) gr.getShapeSpecification());
	PathIterator pi = freeformShape.getPath().getPathIterator(null);
	double[] coordinates = new double[6];
	while (pi.isDone() == false) {
		int type = pi.currentSegment(coordinates);
		switch (type) {
			case PathIterator.SEG_MOVETO:
				ss.addToPoints(new DianaPoint(coordinates[0], coordinates[1]));
				ss.getPoints().add(new DianaPoint(coordinates[0], coordinates[1]));
				break;
			case PathIterator.SEG_LINETO:
				ss.addToPoints(new DianaPoint(coordinates[0], coordinates[1]));
				ss.getPoints().add(new DianaPoint(coordinates[0], coordinates[1]));
				break;
			case PathIterator.SEG_QUADTO:
				ss.addToPoints(new DianaPoint(coordinates[0], coordinates[1]));
				ss.getPoints().add(new DianaPoint(coordinates[0], coordinates[1]));
				break;
			case PathIterator.SEG_CUBICTO:
				ss.addToPoints(new DianaPoint(coordinates[0], coordinates[1]));
				ss.getPoints().add(new DianaPoint(coordinates[0], coordinates[1]));
				break;
			case PathIterator.SEG_CLOSE:
				ss.addToPoints(new DianaPoint(coordinates[0], coordinates[1]));
				ss.getPoints().add(new DianaPoint(coordinates[0], coordinates[1]));
				break;
			default:
				break;
		}
		pi.next();
	}
	return newShape;
	}
	*/

	private DiagramShape makeDiagramShape(HSLFShape shape) {
		DiagramShape newShape = getDiagramFactory().makeNewShape(getShapeName(shape), getDiagram());
		shapesMap.put(newShape, shape);
		return newShape;
	}

	/**
	 * A line is without start/end shapes
	 * 
	 * @param line
	 * @return
	 */
	private DiagramConnector makeDiagramConnector(HSLFSimpleShape line) {
		DiagramConnector newConnector = getDiagramFactory().makeNewConnector(getShapeName(line), null, null, getDiagram());
		connectorsMap.put(newConnector, line);
		return newConnector;
	}

	private static boolean hasText(HSLFTextShape shape) {
		return shape.getTextParagraphs() != null && !shape.getTextParagraphs().isEmpty();
	}

	private static String getShapeName(HSLFShape shape) {
		if (shape != null && shape instanceof HSLFTextShape && ((HSLFTextShape) shape).getText() != null) {
			return ((HSLFTextShape) shape).getText();
		}
		return "";
	}

	private void attachConnector(DiagramConnector connector, HSLFShape poiConnector) {
		DiagramShape sourceShape = null, targetShape = null;
		List<DiagramShape> potentialShapes = new ArrayList<>();

		// We must count the size of the arrow head
		double arrowStartWidth = poiConnector.getEscherProperty(EscherPropertyTypes.LINESTYLE__LINESTARTARROWWIDTH) + 1;
		double arrowEndWidth = poiConnector.getEscherProperty(EscherPropertyTypes.LINESTYLE__LINEENDARROWWIDTH) + 1 + arrowStartWidth;
		// Connectors which are closed to the shape are considered as linked to this shape.
		Rectangle2D connectorBorder = null;
		if (poiConnector instanceof HSLFLine) {
			connectorBorder = new Rectangle2D.Double(poiConnector.getAnchor().getX() - arrowStartWidth,
					poiConnector.getAnchor().getY() - arrowStartWidth,
					poiConnector.getAnchor().getWidth() + arrowEndWidth,
					poiConnector.getAnchor().getHeight() + arrowEndWidth);
		}
		else {
			connectorBorder = new Rectangle2D.Double(poiConnector.getAnchor().getX() - arrowStartWidth,
					poiConnector.getAnchor().getY() - arrowStartWidth, poiConnector.getAnchor().getWidth() + arrowEndWidth,
					poiConnector.getAnchor().getHeight() + arrowEndWidth);
		}

		// Find the closest source and target shape
		for (Map.Entry<DiagramShape, HSLFShape> diagramShapeEntry : getDiagramShapesMap().entrySet()) {
			HSLFShape poiShape = diagramShapeEntry.getValue();
			DiagramShape diagramShape = diagramShapeEntry.getKey();
			if (poiShape.getAnchor().intersects(connectorBorder)) {
				potentialShapes.add(diagramShape);
			}
		}

		// Now the problem is what shape to connect? Because of overlapping shapes choose the most frontal ones.
		int layer = 0;
		for (DiagramShape potentialShape : potentialShapes) {
			if (layer <= potentialShape.getGraphicalRepresentation().getLayer()) {
				layer = potentialShape.getGraphicalRepresentation().getLayer();
				sourceShape = potentialShape;
			}
		}
		if (potentialShapes.size() > 1) {
			potentialShapes.remove(sourceShape);
			layer = 0;
			for (DiagramShape potentialShape : potentialShapes) {
				if (layer < potentialShape.getGraphicalRepresentation().getLayer()) {
					layer = potentialShape.getGraphicalRepresentation().getLayer();
					targetShape = potentialShape;
				}
			}
		}
		else if (potentialShapes.size() == 1) {
			targetShape = sourceShape;
		}

		// Create fictives end/start shapes
		else if (sourceShape == null && targetShape == null) {
			sourceShape = getDiagramFactory().makeNewShape("", getDiagram());
			targetShape = getDiagramFactory().makeNewShape("", getDiagram());
			ShapeGraphicalRepresentation sourceShapeGR = sourceShape.getGraphicalRepresentation();
			sourceShapeGR.setX(poiConnector.getAnchor().getMinX());
			sourceShapeGR.setY(poiConnector.getAnchor().getMaxY());
			sourceShapeGR.setWidth(2);
			sourceShapeGR.setHeight(2);
			sourceShapeGR.setDimensionConstraints(DimensionConstraints.UNRESIZABLE);
			ShapeGraphicalRepresentation targetShapeGR = targetShape.getGraphicalRepresentation();
			targetShapeGR.setX(poiConnector.getAnchor().getMaxX());
			targetShapeGR.setY(poiConnector.getAnchor().getMinY());
			targetShapeGR.setWidth(2);
			targetShapeGR.setHeight(2);
			targetShapeGR.setDimensionConstraints(DimensionConstraints.UNRESIZABLE);
		}

		connector.setStartShape(sourceShape);
		connector.setEndShape(targetShape);
	}

	private static void setDiagramElementType(GraphicalRepresentation gr, HSLFShape shape) {
		switch (shape.getShapeType()) {
			case CHEVRON:
				((ShapeGraphicalRepresentation) gr).setShapeType(ShapeType.CHEVRON);
				break;
			case PLUS:
				((ShapeGraphicalRepresentation) gr).setShapeType(ShapeType.PLUS);
				break;
			case RECT:
				((ShapeGraphicalRepresentation) gr).setShapeType(ShapeType.RECTANGLE);
				break;
			case ROUND_RECT:
				((ShapeGraphicalRepresentation) gr).setShapeType(ShapeType.RECTANGLE);
				((Rectangle) ((ShapeGraphicalRepresentation) gr).getShapeSpecification()).setIsRounded(true);
				((Rectangle) ((ShapeGraphicalRepresentation) gr).getShapeSpecification()).setArcSize(20);
				break;
			case STAR_5:
				((ShapeGraphicalRepresentation) gr).setShapeType(ShapeType.STAR);
				break;
			case ELLIPSE:
				((ShapeGraphicalRepresentation) gr).setShapeType(ShapeType.OVAL);
				break;
			case TRIANGLE:
				((ShapeGraphicalRepresentation) gr).setShapeType(ShapeType.TRIANGLE);
				break;
			case CURVED_CONNECTOR_2:
				((ConnectorGraphicalRepresentation) gr).setConnectorType(ConnectorType.CURVE);
				break;
			case CURVED_CONNECTOR_3:
				((ConnectorGraphicalRepresentation) gr).setConnectorType(ConnectorType.CURVE);
				break;
			case CURVED_CONNECTOR_4:
				((ConnectorGraphicalRepresentation) gr).setConnectorType(ConnectorType.CURVE);
				break;
			case CURVED_CONNECTOR_5:
				((ConnectorGraphicalRepresentation) gr).setConnectorType(ConnectorType.CURVE);
				break;
			case LINE:
				((ConnectorGraphicalRepresentation) gr).setConnectorType(ConnectorType.LINE);
				break;
			case STRAIGHT_CONNECTOR_1:
				((ConnectorGraphicalRepresentation) gr).setConnectorType(ConnectorType.LINE);
				break;
			case BENT_CONNECTOR_2:
				((ConnectorGraphicalRepresentation) gr).setConnectorType(ConnectorType.RECT_POLYLIN);
				break;
			case BENT_CONNECTOR_3:
				((ConnectorGraphicalRepresentation) gr).setConnectorType(ConnectorType.RECT_POLYLIN);
				break;
			case BENT_CONNECTOR_4:
				((ConnectorGraphicalRepresentation) gr).setConnectorType(ConnectorType.RECT_POLYLIN);
				break;
			case BENT_CONNECTOR_5:
				((ConnectorGraphicalRepresentation) gr).setConnectorType(ConnectorType.RECT_POLYLIN);
				break;
		}
	}

	private void applySimpleShapeGr(GraphicalRepresentation gr, HSLFSimpleShape shape) {

		if (gr instanceof ShapeGraphicalRepresentation) {
			((ShapeGraphicalRepresentation) gr).setX(shape.getAnchor().getX());
			((ShapeGraphicalRepresentation) gr).setY(shape.getAnchor().getY());
			((ShapeGraphicalRepresentation) gr).setWidth(shape.getAnchor().getWidth());
			((ShapeGraphicalRepresentation) gr).setHeight(shape.getAnchor().getHeight());
			// ((ShapeGraphicalRepresentation) gr).setBorder(getDiagramFactory().makeShapeBorder(0, 0, 0, 0));
			((ShapeGraphicalRepresentation) gr).setShadowStyle(getDiagramFactory().makeDefaultShadowStyle());
			((ShapeGraphicalRepresentation) gr).setForeground(getDiagramFactory().makeNoneForegroundStyle());
			((ShapeGraphicalRepresentation) gr).setShadowStyle(getDiagramFactory().makeNoneShadowStyle());
			if (shape.getLineColor() != null) {
				((ShapeGraphicalRepresentation) gr).setForeground(getDiagramFactory().makeForegroundStyle(shape.getLineColor(),
						(float) shape.getLineWidth(), convertDashLineStyles(shape.getLineDash())));
			}
			if (shape.getFillColor() != null) {
				((ShapeGraphicalRepresentation) gr).setBackground(getDiagramFactory().makeColoredBackground(shape.getFillColor()));
			}
			else {
				((ShapeGraphicalRepresentation) gr).setBackground(getDiagramFactory().makeEmptyBackground());
			}
		}
		if (gr instanceof ConnectorGraphicalRepresentation) {
			if (shape.getLineColor() != null) {
				((ConnectorGraphicalRepresentation) gr).setForeground(getDiagramFactory().makeForegroundStyle(shape.getLineColor(),
						(float) shape.getLineWidth(), convertDashLineStyles(shape.getLineDash())));
			}
			// For now it we don't know how to determinate to whom end of the connector is affected the symbol
			// Because in the case of connectors, we don't knwo the end/start shape, which is computed by intersection.
			// So we can only handle correctly two cases, when there is no arrows ends at all and when there is two arrows ends
			if (shape.getEscherProperty(EscherPropertyTypes.LINESTYLE__LINESTARTARROWHEAD) != 0
					&& shape.getEscherProperty(EscherPropertyTypes.LINESTYLE__LINEENDARROWHEAD) != 0) {
				if (shape.getEscherProperty(EscherPropertyTypes.LINESTYLE__LINESTARTARROWHEAD) != 0) {
					((ConnectorGraphicalRepresentation) gr).getConnectorSpecification().setStartSymbol(StartSymbolType.ARROW);
				}
				if (shape.getEscherProperty(EscherPropertyTypes.LINESTYLE__LINESTARTARROWWIDTH) != 0) {
					((ConnectorGraphicalRepresentation) gr).getConnectorSpecification()
							.setStartSymbolSize(shape.getEscherProperty(EscherPropertyTypes.LINESTYLE__LINESTARTARROWWIDTH) * 10);
				}
				if (shape.getEscherProperty(EscherPropertyTypes.LINESTYLE__LINEENDARROWHEAD) != 0) {
					((ConnectorGraphicalRepresentation) gr).getConnectorSpecification().setEndSymbol(EndSymbolType.ARROW);
				}
				if (shape.getEscherProperty(EscherPropertyTypes.LINESTYLE__LINEENDARROWWIDTH) != 0) {
					((ConnectorGraphicalRepresentation) gr).getConnectorSpecification()
							.setEndSymbolSize(shape.getEscherProperty(EscherPropertyTypes.LINESTYLE__LINEENDARROWWIDTH) * 10);
				}
			}
		}
		setDiagramElementType(gr, shape);
	}

	private void applyTextShapeGr(ShapeGraphicalRepresentation gr, HSLFTextShape shape) {
		applySimpleShapeGr(gr, shape);
		if (hasText(shape)) {
			List<HSLFTextRun> rts = shape.getTextParagraphs().get(0).getTextRuns();
			if (rts != null && rts.size() > 0) {
				HSLFTextRun rtr = rts.get(0);
				String fontName = rtr.getFontFamily();
				int fontSize = rtr.getFontSize() != null ? (int) Math.round(rtr.getFontSize()) : 12;
				Color color = rtr.getFontColor() != null ? rtr.getFontColor().getSolidColor().getColor() : null;
				int fontStyle = Font.PLAIN | (rtr.isBold() ? Font.BOLD : Font.PLAIN) | (rtr.isItalic() ? Font.ITALIC : Font.PLAIN);
				Font f = new Font(fontName, fontStyle, fontSize);
				TextStyle textStyle = getDiagramFactory().makeTextStyle(color, f);
				gr.setTextStyle(textStyle);
			}
		}
		if (gr != null) {
			gr.setIsFloatingLabel(false);
			gr.setRelativeTextX(0.5);
			gr.setRelativeTextY(0.5);
		}
		gr.setIsMultilineAllowed(true);
		try {
			// POI 3 exposed the anchor constants (AnchorTop...), POI 5 an enum: the whole family (centered, baseline) folds into it
			switch (shape.getVerticalAlignment()) {
				case TOP:
					gr.setVerticalTextAlignment(VerticalTextAlignment.BOTTOM);
					break;
				case MIDDLE:
					gr.setVerticalTextAlignment(VerticalTextAlignment.MIDDLE);
					break;
				case BOTTOM:
					gr.setVerticalTextAlignment(VerticalTextAlignment.TOP);
					break;
				default:
					break;
			}
		} catch (Exception e) {
			// Set by default
			gr.setVerticalTextAlignment(VerticalTextAlignment.MIDDLE);
		}

		try {
			// The alignment is a property of the paragraphs: use the first one
			switch (shape.getTextParagraphs().get(0).getTextAlign()) {
				case LEFT:
					gr.setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);
					gr.setParagraphAlignment(ParagraphAlignment.LEFT);
					break;
				case CENTER:
					gr.setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
					gr.setParagraphAlignment(ParagraphAlignment.CENTER);
					break;
				case RIGHT:
					gr.setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT);
					gr.setParagraphAlignment(ParagraphAlignment.RIGHT);
					break;
				case JUSTIFY:
					gr.setParagraphAlignment(ParagraphAlignment.RIGHT);
					gr.setParagraphAlignment(ParagraphAlignment.JUSTIFY);
					break;
				default:
					break;
			}
		} catch (Exception e) {
			gr.setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
			gr.setParagraphAlignment(ParagraphAlignment.RIGHT);
		}

		gr.setLineWrap(true);
	}

	private void applyTableGr(ShapeGraphicalRepresentation gr, HSLFTable shape) {
		gr.getForeground().setNoStroke(true);
		gr.setTransparency(1);
		gr.setBackground(getDiagramFactory().makeEmptyBackground());
	}

	private static void applyTableCellGr(ShapeGraphicalRepresentation gr, HSLFTableCell shape) {
		gr.getForeground().setNoStroke(false);
		gr.setDimensionConstraints(DimensionConstraints.UNRESIZABLE);
	}

	private void applyPictureGr(ShapeGraphicalRepresentation gr, HSLFPictureShape shape) {
		applySimpleShapeGr(gr, shape);
		int width = (int) shape.getAnchor().getWidth() < 0 ? 0 : (int) shape.getAnchor().getWidth();
		int height = (int) shape.getAnchor().getHeight() < 0 ? 0 : (int) shape.getAnchor().getHeight();
		int x = (int) shape.getAnchor().getX() < 0 ? 0 : (int) shape.getAnchor().getX();
		int y = (int) shape.getAnchor().getY() < 0 ? 0 : (int) shape.getAnchor().getY();
		if (width * height < Integer.MAX_VALUE) {
			gr.setX(x);
			gr.setY(y);
			gr.setWidth(width);
			gr.setHeight(height);
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics = image.createGraphics();
			graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
			graphics.translate(-x, -y);
			graphics.clipRect(x, y, width, height);
			graphics.setPaint(Color.WHITE);
			graphics.fillRect(x, y, width, height);
			org.apache.poi.sl.draw.DrawFactory.getInstance(graphics).getDrawable(shape).draw(graphics);
			File imageFile = saveImageFile(image, getDiagramName() + getSlide().getTitle() + shape.getShapeId());
			gr.setBackground(getDiagramFactory().makeImageBackground(ResourceLocator.locateResource(imageFile.getAbsolutePath())));
			gr.setForeground(getDiagramFactory().makeNoneForegroundStyle());
			gr.setShadowStyle(getDiagramFactory().makeNoneShadowStyle());
			((BackgroundImageBackgroundStyle) gr.getBackground()).setFitToShape(true);
		}
	}

	/*private void setTextProperties(DiagramElement<?> shape, HSLFTextShape textShape, boolean convertBulletsToShapes) {
		GraphicalRepresentation gr = shape.getGraphicalRepresentation();
		if (textShape.getTextRun() != null) {
			HSLFTextParagraph textRun = textShape.getTextRun();
			HSLFTextRun[] rts = textRun.getRichTextRuns();
			if (rts.length > 0) {
				HSLFTextRun rtr = rts[0];
				String fontName = rtr.getFontName();
				int fontSize = rtr.getFontSize();
				Color color = rtr.getFontColor();
				int fontStyle = Font.PLAIN | (rtr.isBold() ? Font.BOLD : Font.PLAIN) | (rtr.isItalic() ? Font.ITALIC : Font.PLAIN);
				Font f = new Font(fontName, fontStyle, fontSize);
				TextStyle textStyle = getDiagramFactory().makeTextStyle(color, f);
				gr.setTextStyle(textStyle);
			}
		}
		if (gr instanceof ShapeGraphicalRepresentation) {
			((ShapeGraphicalRepresentation) gr).setIsFloatingLabel(false);
			((ShapeGraphicalRepresentation) gr).setRelativeTextX(0.5);
			((ShapeGraphicalRepresentation) gr).setRelativeTextY(0.5);
		}
		gr.setIsMultilineAllowed(true);
		try {
			switch (textShape.getVerticalAlignment()) {
				case HSLFTextShape.AnchorTop:
					gr.setVerticalTextAlignment(VerticalTextAlignment.BOTTOM);
					break;
				case HSLFTextShape.AnchorMiddle:
					gr.setVerticalTextAlignment(VerticalTextAlignment.MIDDLE);
					break;
				case HSLFTextShape.AnchorBottom:
					gr.setVerticalTextAlignment(VerticalTextAlignment.TOP);
					break;
				case HSLFTextShape.AnchorTopCentered:
					gr.setVerticalTextAlignment(VerticalTextAlignment.BOTTOM);
					break;
				case HSLFTextShape.AnchorMiddleCentered:
					gr.setVerticalTextAlignment(VerticalTextAlignment.MIDDLE);
					break;
				case HSLFTextShape.AnchorBottomCentered:
					gr.setVerticalTextAlignment(VerticalTextAlignment.TOP);
					break;
				case HSLFTextShape.AnchorTopBaseline:
					gr.setVerticalTextAlignment(VerticalTextAlignment.BOTTOM);
					break;
				case HSLFTextShape.AnchorBottomBaseline:
					gr.setVerticalTextAlignment(VerticalTextAlignment.TOP);
					break;
				case HSLFTextShape.AnchorTopCenteredBaseline:
					gr.setVerticalTextAlignment(VerticalTextAlignment.BOTTOM);
					break;
				case HSLFTextShape.AnchorBottomCenteredBaseline:
					gr.setVerticalTextAlignment(VerticalTextAlignment.TOP);
					break;
			}
		} catch (Exception e) {
			// Set by default
			gr.setVerticalTextAlignment(VerticalTextAlignment.TOP);
		}
	
		try {
			switch (textShape.getHorizontalAlignment()) {
				case HSLFTextShape.AlignLeft:
					gr.setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT);
					gr.setParagraphAlignment(ParagraphAlignment.LEFT);
					break;
				case HSLFTextShape.AlignCenter:
					gr.setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
					gr.setParagraphAlignment(ParagraphAlignment.CENTER);
					break;
				case HSLFTextShape.AlignRight:
					gr.setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);
					gr.setParagraphAlignment(ParagraphAlignment.RIGHT);
					break;
				case HSLFTextShape.AlignJustify:
					gr.setParagraphAlignment(ParagraphAlignment.RIGHT);
					gr.setParagraphAlignment(ParagraphAlignment.JUSTIFY);
					break;
			}
		} catch (Exception e) {
			gr.setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);
			gr.setParagraphAlignment(ParagraphAlignment.RIGHT);
		}
	
		gr.setLineWrap(true);
	}*/

	private static DashStyle convertDashLineStyles(org.apache.poi.sl.usermodel.StrokeStyle.LineDash powerpointDashStyle) {

		if (powerpointDashStyle == null) {
			return null;
		}
		switch (powerpointDashStyle) {
			case DASH:
				return DashStyle.MEDIUM_DASHES;
			case DASH_DOT:
				return DashStyle.DOTS_DASHES;
			case SYS_DASH_DOT_DOT:
				return DashStyle.DOT_LINES_DASHES;
			case SYS_DASH_DOT:
				return DashStyle.SMALL_DASHES;
			case DOT:
				return DashStyle.DOTS_DASHES;
			case SYS_DOT:
				return DashStyle.DOTS_DASHES;
			case LG_DASH_DOT_DOT:
				return DashStyle.BIG_DASHES;
			case LG_DASH_DOT:
				return DashStyle.BIG_DASHES;
			case LG_DASH:
				return DashStyle.BIG_DASHES;
			case SYS_DASH:
				return DashStyle.SMALL_DASHES;
			case SOLID:
				return DashStyle.PLAIN_STROKE;
		}
		return null;
	}

	private static boolean isConnector(org.apache.poi.sl.usermodel.ShapeType shapeType) {
		switch (shapeType) {
			case CURVED_CONNECTOR_2:
				return true;
			case CURVED_CONNECTOR_3:
				return true;
			case CURVED_CONNECTOR_4:
				return true;
			case CURVED_CONNECTOR_5:
				return true;
			case LINE:
				return true;
			case STRAIGHT_CONNECTOR_1:
				return true;
			case BENT_CONNECTOR_2:
				return true;
			case BENT_CONNECTOR_3:
				return true;
			case BENT_CONNECTOR_4:
				return true;
			case BENT_CONNECTOR_5:
				return true;
		}
		return false;
	}

	/*private void setDefaultGraphicalProperties(GraphicalRepresentation returned, HSLFSimpleShape shape) {
	
	if (returned instanceof ShapeGraphicalRepresentation) {
	
	}
	returned.setX(shape.getLogicalAnchor2D().getX());
	returned.setY(shape.getLogicalAnchor2D().getY());
	returned.setWidth(shape.getLogicalAnchor2D().getWidth());
	returned.setHeight(shape.getLogicalAnchor2D().getHeight());
	returned.setBorder(getDiagramFactory().makeShapeBorder(0, 0, 0, 0));
	returned.setShadowStyle(getDiagramFactory().makeDefaultShadowStyle());
	if (shape instanceof HSLFSimpleShape) {
		if (shape.getLineColor() != null) {
			returned.setForeground(getDiagramFactory().makeForegroundStyle(shape.getLineColor(), (float) shape.getLineWidth(),
					convertDashLineStyles(shape.getLineDashing())));
		}
		else {
			returned.setForeground(getDiagramFactory().makeNoneForegroundStyle());
		}
		if (shape.getFillColor() != null) {
			returned.setBackground(getDiagramFactory().makeColoredBackground(shape.getFillColor()));
		}
		else {
			returned.setBackground(getDiagramFactory().makeEmptyBackground());
			returned.setShadowStyle(getDiagramFactory().makeNoneShadowStyle());
		}
	}
	else {
		returned.setForeground(getDiagramFactory().makeNoneForegroundStyle());
		returned.setBackground(getDiagramFactory().makeEmptyBackground());
		returned.setShadowStyle(getDiagramFactory().makeNoneShadowStyle());
	}
	}*/

	/*private DiagramShape makeShapeFromBullet(HSLFTextParagraph textRun, HSLFTextRun rtr, DiagramShape shape, HSLFTextShape textShape) {
		// Create Bullet
		DianaPoint bulletPosition = new DianaPoint(textRun.getTextRuler().getBulletOffsets()[0] + rtr.getIndentLevel(), textRun.getTextRuler()
				.getBulletOffsets()[1] + rtr.getLineSpacing() * 0.1 * rtr.getStartIndex());
		DiagramShape newBulletShape = getDiagramFactory().makeNewShape("", ShapeType.CIRCLE, bulletPosition, getDiagram());
		newBulletShape.getGraphicalRepresentation().setForeground(getDiagramFactory().makeNoneForegroundStyle());
		newBulletShape.getGraphicalRepresentation().setBackground(getDiagramFactory().makeColoredBackground(Color.BLACK));
		newBulletShape.getGraphicalRepresentation().setShadowStyle(getDiagramFactory().makeNoneShadowStyle());
		newBulletShape.getGraphicalRepresentation().setHeight(rtr.getBulletSize() * 0.1);
		newBulletShape.getGraphicalRepresentation().setWidth(rtr.getBulletSize() * 0.1);
	
		// Create Text
		DiagramShape newTextShape = getDiagramFactory().makeNewShape(rtr.getRawText(), getDiagram());
		newTextShape.getGraphicalRepresentation().setX(
				textRun.getTextRuler().getBulletOffsets()[0] + rtr.getIndentLevel() * rtr.getBulletSize());
		newTextShape.getGraphicalRepresentation().setY(
				textRun.getTextRuler().getBulletOffsets()[1] + rtr.getLineSpacing() * 0.1 * rtr.getStartIndex());
		newTextShape.getGraphicalRepresentation().setLayer(shape.getGraphicalRepresentation().getLayer() + 1);
		newTextShape.getGraphicalRepresentation().setForeground(getDiagramFactory().makeNoneForegroundStyle());
		newTextShape.getGraphicalRepresentation().setBackground(getDiagramFactory().makeEmptyBackground());
		newTextShape.getGraphicalRepresentation().setShadowStyle(getDiagramFactory().makeNoneShadowStyle());
		newTextShape.getGraphicalRepresentation().setHeight(rtr.getFontSize());
		newTextShape.getGraphicalRepresentation().setWidth(rtr.getLength());
	
		String fontName = rtr.getFontName();
		int fontSize = rtr.getFontSize();
		Color color = rtr.getFontColor();
		int fontStyle = Font.PLAIN | (rtr.isBold() ? Font.BOLD : Font.PLAIN) | (rtr.isItalic() ? Font.ITALIC : Font.PLAIN);
		Font f = new Font(fontName, fontStyle, fontSize);
		TextStyle textStyle = getDiagramFactory().makeTextStyle(color, f);
		newTextShape.getGraphicalRepresentation().setTextStyle(textStyle);
	
		getDiagram().addToShapes(newTextShape);
		getDiagram().addToShapes(newBulletShape);
		return null;
	}
	
	private boolean hasBullets(HSLFTextParagraph textRun) {
		boolean hasBullets = false;
		HSLFTextRun[] rts = textRun.getRichTextRuns();
		if (rts.length > 0) {
			for (int i = 0; i < rts.length; i++) {
				HSLFTextRun rtr = rts[i];
				if (rtr.isBullet() || rtr.isBulletHard()) {
					hasBullets = true;
				}
			}
		}
		return hasBullets;
	}*/

}

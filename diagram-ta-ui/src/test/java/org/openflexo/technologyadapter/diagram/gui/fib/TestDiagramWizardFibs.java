/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Fml-rt-technologyadapter-ui, a component of the software infrastructure 
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

package org.openflexo.technologyadapter.diagram.gui.fib;

import org.junit.Test;
import org.openflexo.gina.test.GenericFIBTestCase;
import org.openflexo.rm.FileResourceImpl;
import org.openflexo.rm.ResourceLocator;

public class TestDiagramWizardFibs extends GenericFIBTestCase {

	public static void main(String[] args) {
		System.out.println(
				generateFIBTestCaseClass(((FileResourceImpl) ResourceLocator.locateResource("Fib/Wizard")).getFile(), "Fib/Wizard/"));
	}

	@Test
	public void testChooseAndConfigureCreationSchemeForFMLControlledDiagramVirtualModel() {
		validateFIB("Fib/Wizard/ChooseAndConfigureCreationSchemeForFMLControlledDiagramVirtualModel.fib");
	}

	@Test
	public void testChooseFMLControlledDiagramVirtualModel() {
		validateFIB("Fib/Wizard/ChooseFMLControlledDiagramVirtualModel.fib");
	}

	@Test
	public void testCreateNewDiagramSpecification() {
		validateFIB("Fib/Wizard/CreateDiagramVirtualModel/CreateNewDiagramSpecification.fib");
	}

	@Test
	public void testDescribeFMLControlledDiagramVirtualModel() {
		validateFIB("Fib/Wizard/CreateDiagramVirtualModel/DescribeFMLControlledDiagramVirtualModel.fib");
	}

	@Test
	public void testUseExistingDiagramSpecification() {
		validateFIB("Fib/Wizard/CreateDiagramVirtualModel/UseExistingDiagramSpecification.fib");
	}

	@Test
	public void testDescribeDiagram() {
		validateFIB("Fib/Wizard/CreateElement/DescribeDiagram.fib");
	}

	@Test
	public void testDescribeDiagramPalette() {
		validateFIB("Fib/Wizard/CreateElement/DescribeDiagramPalette.fib");
	}

	@Test
	public void testDescribeDiagramSpecification() {
		validateFIB("Fib/Wizard/CreateElement/DescribeDiagramSpecification.fib");
	}

	@Test
	public void testDescribeExampleDiagram() {
		validateFIB("Fib/Wizard/CreateElement/DescribeExampleDiagram.fib");
	}

	@Test
	public void testConfigurePaletteElementForNewFlexoConcept() {
		validateFIB("Fib/Wizard/CreateFMLElement/ConfigurePaletteElementForNewFlexoConcept.fib");
	}

	@Test
	public void testChooseCreationStrategy() {
		validateFIB("Fib/Wizard/DeclareInFlexoConcept/ChooseCreationStrategy.fib");
	}

	@Test
	public void testConfigureBlankFlexoConceptFromConnectorCreationStrategyStep() {
		validateFIB("Fib/Wizard/DeclareInFlexoConcept/ConfigureBlankFlexoConceptFromConnectorCreationStrategyStep.fib");
	}

	@Test
	public void testConfigureBlankFlexoConceptFromShapeCreationStrategyStep() {
		validateFIB("Fib/Wizard/DeclareInFlexoConcept/ConfigureBlankFlexoConceptFromShapeCreationStrategyStep.fib");
	}

	@Test
	public void testConfigureMapConnectorToFlexoConceptlnstanceStep() {
		validateFIB("Fib/Wizard/DeclareInFlexoConcept/ConfigureMapConnectorToFlexoConceptlnstanceStep.fib");
	}

	@Test
	public void testConfigureMapConnectorToIndividualStep() {
		validateFIB("Fib/Wizard/DeclareInFlexoConcept/ConfigureMapConnectorToIndividualStep.fib");
	}

	@Test
	public void testConfigureMapShapeToFlexoConceptlnstanceStep() {
		validateFIB("Fib/Wizard/DeclareInFlexoConcept/ConfigureMapShapeToFlexoConceptlnstanceStep.fib");
	}

	@Test
	public void testConfigureMapShapeToIndividualStep() {
		validateFIB("Fib/Wizard/DeclareInFlexoConcept/ConfigureMapShapeToIndividualStep.fib");
	}

	@Test
	public void testCreateConnectorInExistingFlexoConcept() {
		validateFIB("Fib/Wizard/DeclareInFlexoConcept/CreateConnectorInExistingFlexoConcept.fib");
	}

	@Test
	public void testCreateShapeInExistingFlexoConcept() {
		validateFIB("Fib/Wizard/DeclareInFlexoConcept/CreateShapeInExistingFlexoConcept.fib");
	}

	@Test
	public void testReplaceConnectorInExistingFlexoConcept() {
		validateFIB("Fib/Wizard/DeclareInFlexoConcept/ReplaceConnectorInExistingFlexoConcept.fib");
	}

	@Test
	public void testReplaceShapeInExistingFlexoConcept() {
		validateFIB("Fib/Wizard/DeclareInFlexoConcept/ReplaceShapeInExistingFlexoConcept.fib");
	}

	@Test
	public void testPutToPaletteFromConceptOptions() {
		validateFIB("Fib/Wizard/PutToPalette/PutToPaletteFromConceptOptions.fib");
	}

	@Test
	public void testPutToPaletteFromShapeOptions() {
		validateFIB("Fib/Wizard/PutToPalette/PutToPaletteFromShapeOptions.fib");
	}

}

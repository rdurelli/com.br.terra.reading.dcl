package com.br.terra.reading.dcl.popup.actions;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.gmt.modisco.omg.kdm.action.ActionElement;
import org.eclipse.gmt.modisco.omg.kdm.action.ActionFactory;
import org.eclipse.gmt.modisco.omg.kdm.action.Calls;
import org.eclipse.gmt.modisco.omg.kdm.action.Creates;
import org.eclipse.gmt.modisco.omg.kdm.action.UsesType;
import org.eclipse.gmt.modisco.omg.kdm.code.CodeElement;
import org.eclipse.gmt.modisco.omg.kdm.code.CodeFactory;
import org.eclipse.gmt.modisco.omg.kdm.code.CodeModel;
import org.eclipse.gmt.modisco.omg.kdm.code.Extends;
import org.eclipse.gmt.modisco.omg.kdm.code.HasValue;
import org.eclipse.gmt.modisco.omg.kdm.code.Implements;
import org.eclipse.gmt.modisco.omg.kdm.code.Module;
import org.eclipse.gmt.modisco.omg.kdm.core.AggregatedRelationship;
import org.eclipse.gmt.modisco.omg.kdm.core.CoreFactory;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMRelationship;
import org.eclipse.gmt.modisco.omg.kdm.kdm.KdmFactory;
import org.eclipse.gmt.modisco.omg.kdm.kdm.KdmPackage;
import org.eclipse.gmt.modisco.omg.kdm.kdm.Segment;
import org.eclipse.gmt.modisco.omg.kdm.structure.AbstractStructureElement;
import org.eclipse.gmt.modisco.omg.kdm.structure.ArchitectureView;
import org.eclipse.gmt.modisco.omg.kdm.structure.Component;
import org.eclipse.gmt.modisco.omg.kdm.structure.Layer;
import org.eclipse.gmt.modisco.omg.kdm.structure.StructureFactory;
import org.eclipse.gmt.modisco.omg.kdm.structure.StructureModel;
import org.eclipse.gmt.modisco.omg.kdm.structure.Subsystem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;

import com.br.terra.dcl.DCLStandaloneSetup;
import com.br.terra.dcl.dCL.BasicType;
import com.br.terra.dcl.dCL.DCDecl;
import com.br.terra.dcl.dCL.DCLComponent;
import com.br.terra.dcl.dCL.DCLComponentInterface;
import com.br.terra.dcl.dCL.DCLLayer;
import com.br.terra.dcl.dCL.DCLModule;
import com.br.terra.dcl.dCL.DCLStructureElement;
import com.br.terra.dcl.dCL.DCLSubSystem;
import com.br.terra.dcl.dCL.ElementType;
import com.br.terra.dcl.dCL.EntityType;
import com.br.terra.dcl.dCL.Model;
import com.google.inject.Injector;

public class ReadinDSLVIew implements IObjectActionDelegate {

	private Shell shell;

	private IFile file;
	
	private Segment segment;
	
	private ArrayList<DCLLayer> allDclLayers = new ArrayList<DCLLayer>(); 
	
	ArrayList<AbstractStructureElement> allAbstractStructureElements = new ArrayList<AbstractStructureElement>();
	
	/**
	 * Constructor for Action1.
	 */
	public ReadinDSLVIew() {
		super();
	}


	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		
		IEditorPart editorPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		
		System.out.println(editorPart);
		

		
		IEditorSite iEditorSite = editorPart.getEditorSite();
		
		if (iEditorSite != null) {
			
			
			System.out.println("entrou");
			// get selection provider
			ISelectionProvider selectionProvider = iEditorSite
					.getSelectionProvider();

			if (selectionProvider != null) {
				
				System.out.println("entrou2");
				
				IFileEditorInput input = (IFileEditorInput) editorPart
						.getEditorInput();
				this.file = input.getFile();
				
				
				String dclFileTOBE = this.file.getRawLocationURI().toString();
				
				System.out.println(dclFileTOBE);
				
				//trava aqui
			//	new org.eclipse.emf.mwe.utils.StandaloneSetup().setPlatformUri("../"); pra que serve isso mesmo? hauihaiuhiua
				Injector injector = new DCLStandaloneSetup().createInjectorAndDoEMFRegistration();
				
				XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
				resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
				Resource resource = resourceSet.getResource(URI.createURI(dclFileTOBE), true);
				
				Model model = (Model ) resource.getContents().get(0); 
				
				createArchitecture(dclFileTOBE);
				
				EList<DCLStructureElement> allStructureElements = model.getStructureElements();
				
				StructureModel structureModel = (StructureModel) this.segment.getModel().get(0); 
								
				structureModel.getStructureElement().addAll(this.setCorrectStructuredElement(allStructureElements));
				
				EList<DCDecl> DCdecl = model.getDCDecl();		
				
				EList<AbstractStructureElement> auxList = structureModel.getStructureElement();
				
				for (AbstractStructureElement abstractStructureElement : auxList) {
					
					this.allAbstractStructureElements.add(abstractStructureElement);
					
				}
				
				createRestrictionToLayers();
				
				//this.createRestrictionToBeRepresentedInKDM(this.allAbstractStructureElements, DCdecl);
				
						
				//String path =  "file:/Users/rafaeldurelli/Documents/runtime-EclipseApplication/University/src/com/br/Examples/TOBE_KDM.xmi";
				String path =  "file:C:/Users/Fernando/Documents/runtime-EclipseApplication/TesteModisco/Examples/TOBE_KDM.xmi";
				
				
				System.out.println(path);
				this.save(this.segment, path);
				
			}

			
		}
		
	}
	
	/** 
	 * Esse metodo e responsavel por salvar uma instancia do KDM apos a realizacao de mudancas no mesmo.
	 * @param segment, representa uma instancia do KDM
	 * @param kdmPath representa o caminho do arquivo KDM
	 */
	public void save(Segment segment, String KDMPath) {

		KdmPackage.eINSTANCE.eClass();
		
		
		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		Map<String, Object> m = reg.getExtensionToFactoryMap();
		m.put("website", new XMIResourceFactoryImpl());

		// Obtain a new resource set
		ResourceSet resSet = new ResourceSetImpl();

		Resource resource = resSet.createResource(URI.createURI(KDMPath));

		resource.getContents().add(segment);

		try {

			resource.save(Collections.EMPTY_MAP);

		} catch (IOException e) {

		}

	}
	
	private void createArchitecture (String name) {
		
		this.segment = KdmFactory.eINSTANCE.createSegment();
		this.segment.setName(name);
		
		StructureModel structureModel = StructureFactory.eINSTANCE.createStructureModel(); 
		this.segment.getModel().add(structureModel);		
		
	}
	
	
	private ArrayList<AbstractStructureElement> setCorrectStructuredElement (EList<DCLStructureElement> allStructureElements) {
		
		
		System.err.println("entrou3");
		
		for (DCLStructureElement dclStructureElement : allStructureElements) {
			if (dclStructureElement instanceof DCLLayer) {
				Layer layer = StructureFactory.eINSTANCE.createLayer();
				layer.setName(dclStructureElement.getName());
				
				this.allDclLayers.add((DCLLayer)dclStructureElement);
				
				allAbstractStructureElements.add(layer);
			}
			else if (dclStructureElement instanceof DCLComponent) {
				Component component = StructureFactory.eINSTANCE.createComponent();
				component.setName(dclStructureElement.getName());
				allAbstractStructureElements.add(component);
			} else if (dclStructureElement instanceof DCLSubSystem) {
				Subsystem subSystem = StructureFactory.eINSTANCE.createSubsystem();
				subSystem.setName(dclStructureElement.getName());
				allAbstractStructureElements.add(subSystem);
			} else if (dclStructureElement instanceof DCLModule) {
				ArchitectureView architectureView = StructureFactory.eINSTANCE.createArchitectureView();
				architectureView.setName(dclStructureElement.getName());
				allAbstractStructureElements.add(architectureView);
			} else if (dclStructureElement instanceof DCLComponentInterface) {
				//TODO 
			}			
			
		}				
		
		return allAbstractStructureElements;
	}
	
	private void createRestrictionToLayers () {
		
		System.err.println("entrou4");
		
		for (int i = 0; i < this.allDclLayers.size(); i++) {
			
			//busca as layers 2, 3 e cria os relacionamento....
			if (this.allDclLayers.get(i).getLevel() > 1) {
				
				//quando encontrar o desejado, busca o anterior para criar os relacionamentos
				for (DCLLayer dclLayer : allDclLayers) {
					
					if (dclLayer.getLevel() == (this.allDclLayers.get(i).getLevel()-1)) {
						
						//busca o elemento estrutural descrito na restricao
						AbstractStructureElement from = this.getToORFrom(this.allDclLayers.get(i).getName(), allAbstractStructureElements);
						AbstractStructureElement to = this.getToORFrom(dclLayer.getName(), allAbstractStructureElements);
						
						ArrayList<KDMRelationship> lisfOfRelationshipsToAdd = createActionsExamples();
						
						searchAndCreateStructureElement(from, to, lisfOfRelationshipsToAdd);						
					}
					
				}
				
			}
			
		}
		
	}
	
	
	private ArrayList<KDMRelationship> createActionsExamples () {
		ArrayList<KDMRelationship> lisfOfRelationshipsToAdd = new ArrayList<KDMRelationship>();
		
		CodeModel elements = CodeFactory.eINSTANCE.createCodeModel(); 
		
		elements.setName("Elements Instances");
		
		this.segment.getModel().add(elements);
		
		Module module = CodeFactory.eINSTANCE.createModule();
		
		module.setName("Module Instance");
		
		elements.getCodeElement().add(module);
		
		ActionElement actionElement = ActionFactory.eINSTANCE.createActionElement();
		
		actionElement.setName("actionElement Instance");
		
		CodeElement codeElement = CodeFactory.eINSTANCE.createCodeElement();
		
		codeElement.setName("codeElement Instance");
		
		module.getCodeElement().add(codeElement);
		
		module.getCodeElement().add(actionElement);
		
		Calls relation = ActionFactory.eINSTANCE.createCalls();
		lisfOfRelationshipsToAdd.add(relation);												
		actionElement.getActionRelation().add(relation);
		
		UsesType relation2 = ActionFactory.eINSTANCE.createUsesType();
		lisfOfRelationshipsToAdd.add(relation2);
		actionElement.getActionRelation().add(relation2);
		
		Creates relation3 = ActionFactory.eINSTANCE.createCreates();
		lisfOfRelationshipsToAdd.add(relation3);
		actionElement.getActionRelation().add(relation3);
		
		Extends relation4 = CodeFactory.eINSTANCE.createExtends();
		lisfOfRelationshipsToAdd.add(relation4);
		codeElement.getCodeRelation().add(relation4);
		
		Implements relation5 = CodeFactory.eINSTANCE.createImplements();
		lisfOfRelationshipsToAdd.add(relation5);
		codeElement.getCodeRelation().add(relation5);
		
		HasValue relation6 = CodeFactory.eINSTANCE.createHasValue();
		lisfOfRelationshipsToAdd.add(relation6);
		codeElement.getCodeRelation().add(relation6);
	
		return lisfOfRelationshipsToAdd;
	}
	
	private void createRestrictionToBeRepresentedInKDM (ArrayList<AbstractStructureElement> allAbstractStructureElements, EList<DCDecl> dcDecl) {
		
		String dependence = null;
		
		ArrayList<KDMRelationship> lisfOfRelationshipsToAdd = new ArrayList<KDMRelationship>();
		
		CodeModel elements = CodeFactory.eINSTANCE.createCodeModel(); 
		
		elements.setName("Elements Instances");
		
		this.segment.getModel().add(elements);
		
		Module module = CodeFactory.eINSTANCE.createModule();
		
		module.setName("Module Instance");
		
		elements.getCodeElement().add(module);
		
		ActionElement actionElement = ActionFactory.eINSTANCE.createActionElement();
		
		actionElement.setName("actionElement Instance");
		
		CodeElement codeElement = CodeFactory.eINSTANCE.createCodeElement();
		
		codeElement.setName("codeElement Instance");
		
		module.getCodeElement().add(codeElement);
		
		module.getCodeElement().add(actionElement);
		
		for (DCDecl restrictions : dcDecl) {
			
			String structureElementNameTO = restrictions.getType().getName();
			String structureElementNameFROM = restrictions.getT().getName();
		
			//busca o elemento estrutural descrito na restricao
			AbstractStructureElement from = this.getToORFrom(structureElementNameFROM, allAbstractStructureElements);
			AbstractStructureElement to = this.getToORFrom(structureElementNameTO, allAbstractStructureElements);
			
			ElementType elementType = restrictions.getElementType();
			
			if (elementType instanceof BasicType) {
				BasicType basicType = (BasicType) elementType;
				dependence = basicType.getTypeName();
				
			} else if (elementType instanceof EntityType) {
				
				EntityType entityType = (EntityType) elementType;
				dependence = entityType.getEntity();
			}
			
			if (dependence.equals("access")) {			
				Calls relation = ActionFactory.eINSTANCE.createCalls();
				lisfOfRelationshipsToAdd.add(relation);												
				actionElement.getActionRelation().add(relation);
			} else if (dependence.equals("declare")) {
				UsesType relation = ActionFactory.eINSTANCE.createUsesType();
				lisfOfRelationshipsToAdd.add(relation);
				actionElement.getActionRelation().add(relation);
			} else if (dependence.equals("create")) {
				Creates relation = ActionFactory.eINSTANCE.createCreates();
				lisfOfRelationshipsToAdd.add(relation);
				actionElement.getActionRelation().add(relation);
			} else if (dependence.equals("handle")) {
				//Access + Declare
				Calls relation = ActionFactory.eINSTANCE.createCalls();
				lisfOfRelationshipsToAdd.add(relation);							
				actionElement.getActionRelation().add(relation);
				
				UsesType relation2 = ActionFactory.eINSTANCE.createUsesType();
				lisfOfRelationshipsToAdd.add(relation2);
				actionElement.getActionRelation().add(relation2);
			} else if (dependence.equals("depend")) {
				//TODOS
				Calls relation = ActionFactory.eINSTANCE.createCalls();
				lisfOfRelationshipsToAdd.add(relation);							
				actionElement.getActionRelation().add(relation);
				
				UsesType relation2 = ActionFactory.eINSTANCE.createUsesType();
				lisfOfRelationshipsToAdd.add(relation2);
				actionElement.getActionRelation().add(relation2);
				
				Creates relation3 = ActionFactory.eINSTANCE.createCreates();
				lisfOfRelationshipsToAdd.add(relation3);
				actionElement.getActionRelation().add(relation3);
				
				Extends relation4 = CodeFactory.eINSTANCE.createExtends();
				lisfOfRelationshipsToAdd.add(relation4);
				codeElement.getCodeRelation().add(relation4);
				
				Implements relation5 = CodeFactory.eINSTANCE.createImplements();
				lisfOfRelationshipsToAdd.add(relation5);
				codeElement.getCodeRelation().add(relation5);
				
				HasValue relation6 = CodeFactory.eINSTANCE.createHasValue();
				lisfOfRelationshipsToAdd.add(relation6);
				codeElement.getCodeRelation().add(relation6);
			} else if (dependence.equals("extend")) {
				Extends relation = CodeFactory.eINSTANCE.createExtends();
				lisfOfRelationshipsToAdd.add(relation);
				codeElement.getCodeRelation().add(relation);
			} else if (dependence.equals("implement")) {
				Implements relation = CodeFactory.eINSTANCE.createImplements();
				lisfOfRelationshipsToAdd.add(relation);
				codeElement.getCodeRelation().add(relation);
			} else if (dependence.equals("derive")) {
				//Extend + Implement
				Extends relation = CodeFactory.eINSTANCE.createExtends();
				lisfOfRelationshipsToAdd.add(relation);
				codeElement.getCodeRelation().add(relation);
				
				Implements relation2 = CodeFactory.eINSTANCE.createImplements();
				lisfOfRelationshipsToAdd.add(relation2);
				codeElement.getCodeRelation().add(relation2);
			} else if (dependence.equals("throw")) {
				Calls relation = ActionFactory.eINSTANCE.createCalls();
				lisfOfRelationshipsToAdd.add(relation);
				actionElement.getActionRelation().add(relation);
			} else if (dependence.equals("annotated")) {
				HasValue relation = CodeFactory.eINSTANCE.createHasValue();
				lisfOfRelationshipsToAdd.add(relation);
				codeElement.getCodeRelation().add(relation);
			}
			
			if (restrictions.getOnly() != null) {
				
				//TODO
				
			}else if (restrictions.getMust() != null) {
				
				//TODO
			} else if (restrictions.getCannot() != null) {
				
				//TODO
			} else {
				
				//System.out.println(abstractRelationship);
				
				searchAndCreateStructureElement(from, to, lisfOfRelationshipsToAdd);
			}
		}
		
	}
	
	private void searchAndCreateStructureElement (AbstractStructureElement from, AbstractStructureElement to, ArrayList<KDMRelationship> relations) {
							
		
		if (from.getAggregated().size() > 0) {
			//TODO
			System.out.println("MAIOR QUE 1, TODO");
			
			EList<AggregatedRelationship> aggregatedFROM = from.getAggregated();		
			
			
			for (int i = 0; i < aggregatedFROM.size(); i++) {
				
				if (to.getName().equals(aggregatedFROM.get(i).getTo().getName())) {
					
					//ADICIONAR
					
					aggregatedFROM.get(i).setDensity(aggregatedFROM.get(i).getDensity()+relations.size());
					aggregatedFROM.get(i).getRelation().addAll(relations);
					
					break;
				}
				
				//se chegar no œltimo e n‹o encontrar
				if (i == (aggregatedFROM.size()-1)) {
					
					AggregatedRelationship newRelationship = CoreFactory.eINSTANCE.createAggregatedRelationship();
					newRelationship.setDensity(relations.size());
					newRelationship.setFrom(from);
					newRelationship.setTo(to);
					newRelationship.getRelation().addAll(relations);
					from.getAggregated().add(newRelationship);
					break;
					
				}
				
				
			}
			
		} else {
			AggregatedRelationship newRelationship = CoreFactory.eINSTANCE.createAggregatedRelationship();
			newRelationship.setDensity(relations.size());
			newRelationship.setFrom(from);
			newRelationship.setTo(to);
			newRelationship.getRelation().addAll(relations);
			from.getAggregated().add(newRelationship);
		}
		
	}
	
	private AbstractStructureElement getToORFrom (String elementToFind, ArrayList<AbstractStructureElement> allAbstractStructureElements) {
		
		for (AbstractStructureElement abstractStructureElement : allAbstractStructureElements) {
			if (abstractStructureElement.getName().equals(elementToFind)) {
				
				return abstractStructureElement;
				
			}
		}
		return null;
		
	} 
	
	@Override
	public void selectionChanged(IAction action, ISelection selection) {

	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {


	}

}

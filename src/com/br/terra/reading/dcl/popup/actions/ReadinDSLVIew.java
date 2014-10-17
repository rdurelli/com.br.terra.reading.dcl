package com.br.terra.reading.dcl.popup.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;
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
import com.br.terra.dcl.dCL.Model;
import com.br.terra.dcl.dCL.Teste;
import com.google.inject.Injector;

public class ReadinDSLVIew implements IObjectActionDelegate {

	private Shell shell;

	private IFile file;
	
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
			
			// get selection provider
			ISelectionProvider selectionProvider = iEditorSite
					.getSelectionProvider();

			if (selectionProvider != null) {
				
				IFileEditorInput input = (IFileEditorInput) editorPart
						.getEditorInput();
				this.file = input.getFile();
				
				String dclFileTOBE = this.file.getRawLocationURI().toString();
				
				new org.eclipse.emf.mwe.utils.StandaloneSetup().setPlatformUri("../");
				Injector injector = new DCLStandaloneSetup().createInjectorAndDoEMFRegistration();
				
				XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
				resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
				Resource resource = resourceSet.getResource(URI.createURI(dclFileTOBE), true);
				
				Model model = (Model ) resource.getContents().get(0); 
				
//				/model.get/
				Teste teste = (Teste) model.getStructureElements().get(0);
				teste.getSe();
				
				
				EList<DCDecl> DCdecl = model.getDCDecl();
				
				for (DCDecl dcDecl2 : DCdecl) {
					System.out.println(dcDecl2);
					BasicType basit = (BasicType)dcDecl2.getElementType();
					dcDecl2.getElementType();
					
				}
				
				System.out.println(resource.getContents());
				
			}

			
		}
		
	}
	
	
	@Override
	public void selectionChanged(IAction action, ISelection selection) {

	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {


	}

}

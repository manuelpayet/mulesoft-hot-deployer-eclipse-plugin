package mulesofthotdeploy.views;

import java.nio.file.Path;
import java.util.Arrays;

import javax.management.RuntimeErrorException;

import org.apache.maven.DefaultMaven;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequest;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import component.viewer.DeploymentViewer;
import data.operations.DeploymentHandler;
import maven.ExecutionJob;
import maven.PomGenerator;
import utils.EclipsePluginHelper;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class MulesoftHotDeploy extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "mulesofthotdeploy.views.MulesoftHotDeploy";

	private DeploymentViewer viewer;
	private DeploymentHandler deploymentHandler;
	private Action mavenBuildSelectedProjects;
	private Action deploySelectedProjectsAction;

	/**
	 * The constructor.
	 */
	public MulesoftHotDeploy() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		viewer = new DeploymentViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		deploymentHandler = new DeploymentHandler(viewer);
		deploymentHandler.updateModulesFromCurrentState();
		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "mulesoft-hot-deploy.viewer");
		makeActions();
		contributeToActionBars();
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(deploySelectedProjectsAction);
		manager.add(new Separator());
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(deploySelectedProjectsAction);
		manager.add(mavenBuildSelectedProjects);
	}

	// .toFile();
	private void invokeMaven() {
		try {
			new ExecutionJob("toto", PomGenerator.INSTANCE.generatePomForEclipseProjects(deploymentHandler.getSelectedProjects()), Arrays.asList("clean", "package")).runInWorkspace(new NullProgressMonitor() {
				
			});
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}
		
	}

	private void makeActions() {
		mavenBuildSelectedProjects = new Action() {
			public void run() {
				invokeMaven();
			}
		};
		mavenBuildSelectedProjects.setText("Construire les projets sélectionnés");
		mavenBuildSelectedProjects.setToolTipText("Construire les projets sélectionnés");
		mavenBuildSelectedProjects.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_FILE));

		deploySelectedProjectsAction = new Action() {
			public void run() {
				showMessage("Action 1 executed");
			}
		};
		deploySelectedProjectsAction.setText("Déployer les projets sélectionnés");
		deploySelectedProjectsAction.setToolTipText("Action 1 tooltip");
		deploySelectedProjectsAction.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_UP));
	}

	private void showMessage(String message) {
		MessageDialog.openInformation(viewer.getControl().getShell(), "Mulesoft Hot Deploy", message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}
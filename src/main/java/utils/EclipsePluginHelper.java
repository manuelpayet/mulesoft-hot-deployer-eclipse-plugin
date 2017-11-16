package utils;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
public enum EclipsePluginHelper {
	INSTANCE();
	private EclipsePluginHelper() {};
	public final static String M2E_NATURE= "org.eclipse.m2e.core.maven2Nature";
	public List<IProject> listWorkspaceProjects() {
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		final IProject[] projects = workspaceRoot.getProjects();
		return Arrays.asList(projects);
	}
	
	public Path getProjectLocation(final IProject project) {
		return Paths.get(project.getLocation().toOSString());
	}
	
	public boolean hasNature(final IProject project, final String nature) {
		try {
			return project.hasNature(nature);
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}
	}
	
	public Path getWorkspaceLocation() {
		return Paths.get(ResourcesPlugin.getWorkspace().getRoot().getLocationURI());
	}
	
}

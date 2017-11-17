package maven;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionResult;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.jdt.MavenJdtPlugin;

public class ExecutionJob extends WorkspaceJob {

	private final Path pomLocation;
	private final List<String> goals;

	public ExecutionJob(String name, final Path pomLocation, final List<String> goals) {
		super(name);
		this.pomLocation = pomLocation;
		this.goals = goals;
		setRule(MavenPlugin.getProjectConfigurationManager().getRule());
	}

	@Override
	public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {

		IMaven maven = MavenPlugin.getMaven();
		MavenExecutionRequest request = maven.createExecutionRequest(monitor);
		request.setBaseDirectory(pomLocation.getParent().toFile());
		request.setPom(pomLocation.toFile());
		request.setGoals(goals);
		
		MavenExecutionResult result = maven.execute(request, monitor);

		if (result.hasExceptions()) {
			IStatus errorStatus;
			if (result.getExceptions().size() > 1) {
				ArrayList<IStatus> errors = new ArrayList<IStatus>();
				for (Throwable t : result.getExceptions()) {
					errors.add(toStatus(t));
				}
				errorStatus = new MultiStatus(MavenJdtPlugin.PLUGIN_ID, -1, errors.toArray(new IStatus[errors.size()]),
						"Unable to execute mvn " + goals, null);
			} else {
				errorStatus = toStatus(result.getExceptions().get(0));
			}
			return errorStatus;
		}
		return Status.OK_STATUS;
	}

	private Status toStatus(Throwable t) {
		return new Status(IStatus.ERROR, MavenJdtPlugin.PLUGIN_ID, t.getLocalizedMessage());
	}

}
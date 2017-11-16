package component.viewer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;

import component.event.ModelChangedEventListener;
import component.provider.ContentProvider;
import data.dto.Module;

public class DeploymentViewer extends TableViewer implements ModelUpdateListener {

	private Table table;
	private final DeploymentViewerBuilder deploymentViewerBuilder = new DeploymentViewerBuilder(this);

	public DeploymentViewer(Composite parent, int style) {
		super(parent, style);
		table = getTable();
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		table.setLayoutData(gridData);
		deploymentViewerBuilder.createColumns();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		setContentProvider(new ContentProvider<Module>() {
		});
	}

	private List<ModelChangedEventListener> lstChangedEvents = new ArrayList<>();

	public void registerModelChangedEventFromTableView(final ModelChangedEventListener changedEvent) {
		lstChangedEvents.add(changedEvent);
	}

	void hasModelChangedFromTableView(final Module module) {
		lstChangedEvents.forEach(eventListener -> eventListener.modelChanged(module));
	}

	@Override
	public void setModules(List<Module> lstModule) {
		final DeploymentViewer that = this;
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				that.setInput(lstModule);
				that.refresh();
			}
		});
	}

}

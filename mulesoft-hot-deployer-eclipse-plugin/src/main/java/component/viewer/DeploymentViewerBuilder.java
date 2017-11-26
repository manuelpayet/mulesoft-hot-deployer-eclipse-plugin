package component.viewer;

import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;

import component.provider.ColumnLabelProviderImpl;
import component.viewer.editorsupport.CheckboxEditor;
import data.dto.Module;

class DeploymentViewerBuilder {
	private final DeploymentViewer managedView;

	DeploymentViewerBuilder(DeploymentViewer managedView) {
		this.managedView = managedView;
	}

	public void createColumns() {
		String[] titles = { "A déployer", "Module", "Statut", "Responsable" };
		int[] bounds = { 150, 150, 150, 150 };
		int columnIdx = 0;
		TableViewerColumn column = createTableViewerColumn(titles[columnIdx], bounds[columnIdx], columnIdx++);
		column.setLabelProvider(new ColumnLabelProviderImpl<Module>() {
			@Override
			public String getTextFromType(Module element) {
				final String checkbox;
				if(element.isMulesoftManaged()) {
					checkbox = "[Gestion par Anypoint]";
				} else {
					checkbox = element.isToHotDeploy() ? Character.toString((char) 0x2611) : Character.toString((char) 0x2610);
				}
				return checkbox;
			}
		});

		column.setEditingSupport(new CheckboxEditor<Module>(managedView) {
			@Override
			protected boolean typedCanEdit(Module object) {
				return !object.isMulesoftManaged();
			}

			@Override
			protected Boolean typedGetValue(Module object) {
				return object.isMulesoftManaged() || object.isToHotDeploy();
			}

			@Override
			protected void typedSetValue(Module element, Boolean value) {
				element.setToHotDeploy(value);
				managedView.hasModelChangedFromTableView(element);
			}
		});

		column = createTableViewerColumn(titles[columnIdx], bounds[columnIdx], columnIdx++);
		column.setLabelProvider(new ColumnLabelProviderImpl<Module>() {
			@Override
			public String getTextFromType(Module element) {
				return element.getModuleName();
			}
		});

		column = createTableViewerColumn(titles[columnIdx], bounds[columnIdx], columnIdx++);
		column.setLabelProvider(new ColumnLabelProviderImpl<Module>() {
			@Override
			public String getTextFromType(Module element) {
				return element.getDeploymentStatus().getLabel();
			}
		});

		column = createTableViewerColumn(titles[columnIdx], bounds[columnIdx], columnIdx++);
		column.setLabelProvider(new ColumnLabelProviderImpl<Module>() {
			@Override
			public String getTextFromType(Module element) {
				return element.isMulesoftManaged() ? "Mulesoft" : "Plugin Hot Deploy";
			}
		});

	}

	private TableViewerColumn createTableViewerColumn(String header, int width, int idx) {
		TableViewerColumn column = new TableViewerColumn(managedView, SWT.LEFT, idx);
		column.getColumn().setText(header);
		column.getColumn().setWidth(width);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);

		return column;
	}
}

package component;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

import data.dto.DeploymentStatus;
import data.dto.Module;
import data.editorsupport.CheckboxEditor;
import data.provider.ColumnLabelProviderImpl;
import data.provider.ContentProvider;

public class DeploymentViewer extends TableViewer {
	public Table table;

	public DeploymentViewer(Composite parent, int style) {
		super(parent, style);
		table = getTable();
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		table.setLayoutData(gridData);
		createColumns();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		final List<Module> lst = new ArrayList<>();
		lst.add(new Module("clic-site", DeploymentStatus.DEPLOIEMENT_EN_COURS,true));
		lst.add(new Module("clic-validation", DeploymentStatus.DEPLOYE, false));
		lst.add(new Module("clic-site", DeploymentStatus.DEPLOIEMENT_EN_COURS,true));
		

		setContentProvider(new ContentProvider<Module>(){});
		this.setInput(lst);
	}

	private void createColumns() {
		String[] titles = { "A déployer", "Module", "Statut", "Responsable" };
		int[] bounds = { 150, 150, 150, 150 };
		int columnIdx = 0;
		TableViewerColumn column = createTableViewerColumn(titles[columnIdx], bounds[columnIdx], columnIdx++);
		column.setLabelProvider(new ColumnLabelProviderImpl<Module>() {
			@Override
			public String getTextFromType(Module element) {
				return element.isToHotDeploy()?Character.toString((char)0x2611):Character.toString((char)0x2610);
			}
		});
		
		column.setEditingSupport(new CheckboxEditor<Module>(this) {
			@Override
			protected boolean typedCanEdit(Module object) {
				return true;
			}

			@Override
			protected Boolean typedGetValue(Module object) {
				return object.isToHotDeploy();
			}

			@Override
			protected void typedSetValue(Module element, Boolean value) {
				element.setToHotDeploy(value);
				
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
		TableViewerColumn column = new TableViewerColumn(this, SWT.LEFT, idx);
		column.getColumn().setText(header);
		column.getColumn().setWidth(width);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);

		return column;
	}
}

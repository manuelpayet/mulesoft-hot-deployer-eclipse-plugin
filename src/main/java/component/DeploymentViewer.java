package component;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

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
		final List<String[]> lst = new ArrayList<>();
		lst.add(new String[] { "module1", "déployé" });
		lst.add(new String[] { "module2", "pas déployé" });

		setContentProvider(new ContentProvider<String[]>() {
			@Override
			public String[] getColumnsName(String[] line) {
				return line;
			}
		});
		this.setInput(lst);
	}

	private void createColumns() {
		String[] titles = { "Module", "Statut" };
		int[] bounds = { 150, 150, 100, 150, 100 };

		TableViewerColumn column = createTableViewerColumn(titles[0], bounds[0], 0);
		column.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				return super.getText("coucou");
			}
		});

		column = createTableViewerColumn(titles[1], bounds[1], 1);
		column.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				return super.getText("coucou2");
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

package data.editorsupport;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;

public abstract class CheckboxEditor<T> extends EditingSupport {

	private final TableViewer viewer;
	private final CellEditor cellEditor;
	
	public CheckboxEditor(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
		this.cellEditor = new CheckboxCellEditor(viewer.getTable());
	}

	protected abstract boolean typedCanEdit(T object);
	@SuppressWarnings("unchecked")
	@Override
	protected boolean canEdit(Object object) {
		System.out.println("canEdit");
		return typedCanEdit((T) object);
	}

	@Override
	protected CellEditor getCellEditor(Object arg0) {
		System.out.println("cellEditor");
		return cellEditor;
	}

	protected abstract Boolean typedGetValue(final T object);
	@SuppressWarnings("unchecked")
	@Override
	protected Object getValue(Object object) {
		return this.typedGetValue((T) object);
	}

	
	protected abstract void typedSetValue(final T element, final Boolean value);
	
	@SuppressWarnings("unchecked")
	@Override
	protected void setValue(Object element, Object newElement) {
		this.typedSetValue((T) element, (Boolean) newElement);
		viewer.update(element, null);
	}

}

package component;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.swt.SWT;

public abstract class CheckboxEditingSupport<T> extends EditingSupport {

	public CheckboxEditingSupport(ColumnViewer viewer) {
		super(viewer);
	}

	@Override
	protected boolean canEdit(Object arg0) {
		return true;
	}

	@Override
	protected CellEditor getCellEditor(Object arg0) {
		return new CheckboxCellEditor(null, SWT.CHECK | SWT.READ_ONLY);
	}

	@Override
	protected Object getValue(Object arg0) {
		return null;
	}
	

	@Override
	protected void setValue(Object arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}

}

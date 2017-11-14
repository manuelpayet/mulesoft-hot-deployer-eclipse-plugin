package data.provider;

import java.util.List;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
//http://o7planning.org/en/10251/eclipse-jface-tutorial
public abstract class ContentProvider<T> implements ITableLabelProvider, IStructuredContentProvider  {

	@Override
	public void addListener(ILabelProviderListener arg0) {
		
	}

	@Override
	public void dispose() {
		
	}

	@Override
	public boolean isLabelProperty(Object arg0, String arg1) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener arg0) {
		
	}

	@Override
	public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
		
	}

	@Override
	public Object[] getElements(Object elements) {
		@SuppressWarnings("unchecked")
		final List<T> castedElements = (List<T>) elements;
		return castedElements.toArray();
	}
	

	@Override
	public Image getColumnImage(Object arg0, int arg1) {
		return null;
	}

	@Override
	public String getColumnText(Object line, int index) {
		return getColumnsName((T) line)[index]; 
	}
	
	public abstract String[] getColumnsName(final T line);

}

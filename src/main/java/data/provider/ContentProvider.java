package data.provider;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
//http://o7planning.org/en/10251/eclipse-jface-tutorial
public abstract class ContentProvider<T> implements IStructuredContentProvider  {

	@Override
	public void dispose() {
		
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
	


}

package data.provider;

import org.eclipse.jface.viewers.ColumnLabelProvider;

public abstract class ColumnLabelProviderImpl<T> extends ColumnLabelProvider {
	
	@SuppressWarnings("unchecked")
	@Override
	public String getText(Object element) {
		return this.getTextFromType((T) element);
	}

	public abstract String getTextFromType(T element);
}

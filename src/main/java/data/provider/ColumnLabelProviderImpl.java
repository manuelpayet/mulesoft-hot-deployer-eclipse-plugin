package data.provider;

import org.eclipse.jface.viewers.ColumnLabelProvider;

public abstract class ColumnLabelProviderImpl<T> extends ColumnLabelProvider {
	
	public abstract String getTextFromTyped(T element);
	
	@SuppressWarnings("unchecked")
	public String getText(final Object object) {
		return this.getTextFromTyped((T) object);
	}
}

package component.event;

import data.dto.Module;

public interface ModelChangedEventListener{
	public void modelChanged(final Module newValue);
}

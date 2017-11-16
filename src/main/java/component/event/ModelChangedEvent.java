package component.event;

import data.dto.Module;

public interface ModelChangedEvent{
	public void modelChanged(final Module newValue);
}

package component.viewer;

import java.util.List;

import data.dto.Module;

public interface ModelUpdateListener {
	public void setModules(final List<Module> lstModule);
}

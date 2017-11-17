package component.viewer;

import java.util.List;

import data.dto.Module;

public interface ModelUpdateListener {
	public List<Module> getModules();
	public void setModules(final List<Module> lstModule);
}

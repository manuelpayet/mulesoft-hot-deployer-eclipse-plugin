package data.operations.impl;

import java.nio.file.Path;

public class ModuleSummary {
	private Path zipPath;
	private Path directoryPath;
	private Path anchorPath;

	@Override
	public String toString() {
		return "ModuleSummary [zipPath=" + zipPath + ", directoryPath=" + directoryPath + ", anchorPath=" + anchorPath
				+ "]";
	}

	public Path getZipPath() {
		return zipPath;
	}

	public void setZipPath(Path zipPath) {
		this.zipPath = zipPath;
	}

	public Path getDirectoryPath() {
		return directoryPath;
	}

	public void setDirectoryPath(Path directoryPath) {
		this.directoryPath = directoryPath;
	}

	public Path getAnchorPath() {
		return anchorPath;
	}

	public void setAnchorPath(Path anchorPath) {
		this.anchorPath = anchorPath;
	}

}

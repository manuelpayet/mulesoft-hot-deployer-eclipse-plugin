package data.operations.event;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

public interface FileChangedEventListener {
	public void fileChanged(final Path path, final WatchEvent.Kind<?> eventKind);
}

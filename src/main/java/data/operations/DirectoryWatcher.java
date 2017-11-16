package data.operations;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Arrays;
import java.util.List;

import data.operations.event.FileChangedEventListener;

public class DirectoryWatcher {
	private final WatchService watcher;
	private WatchKey key = null;
	private final Path pathToWatch;

	private final List<FileChangedEventListener> lstFileChangedEvents;

	public DirectoryWatcher(final Path pathToWatch, final FileChangedEventListener... changedEvents) {
		try {
			this.watcher = FileSystems.getDefault().newWatchService();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		this.pathToWatch = pathToWatch;
		this.lstFileChangedEvents = Arrays.asList(changedEvents);
	}

	public void startPolling() {

		if (null == key) {
			try {
				key = pathToWatch.register(watcher, StandardWatchEventKinds.ENTRY_CREATE,
						StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		new Thread(() -> {
			while (true) {
				final WatchKey key;
				try {
					key = watcher.take();
				} catch (InterruptedException x) {
					return;
				}
				for (WatchEvent<?> event : key.pollEvents()) {
					WatchEvent.Kind<?> kind = event.kind();

					// This key is registered only
					// for ENTRY_CREATE events,
					// but an OVERFLOW event can
					// occur regardless if events
					// are lost or discarded.
					if (kind == StandardWatchEventKinds.OVERFLOW) {
						continue;
					}

					// The filename is the
					// context of the event.
					@SuppressWarnings("unchecked")
					WatchEvent<Path> ev = (WatchEvent<Path>) event;
					Path filename = ev.context();

					// Resolve the filename against the directory.
					// If the filename is "test" and the directory is "foo",
					// the resolved name is "test/foo".
					Path child = pathToWatch.resolve(filename);
					lstFileChangedEvents
							.forEach(fileChangedEventListener -> fileChangedEventListener.fileChanged(child, kind));

					boolean valid = key.reset();
					if (!valid) {
						break;
					}
				}
			}
		}).start();

	}
}

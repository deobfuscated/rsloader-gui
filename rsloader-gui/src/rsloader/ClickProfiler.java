package rsloader;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class ClickProfiler {
	static final int OLDSCHOOL_PORT = 43594;
	static final int RS3_PORT = 443;
	static final int SOCKET_SEND_BYTE = 14;
	static final String HOST_SUFFIX = ".runescape.com";

	// private Socket socket;
	private Socket socket;
	private CompletableFuture<Void> connectionTask;

	public boolean isConnected() {
		// return socket != null && socket.isConnected();
		return socket != null && socket.isConnected() && !socket.isClosed();
	}

	public InetAddress getAddress() {
		return socket.getInetAddress();
	}

	/**
	 * Connects to an RS world.
	 * 
	 * @param world
	 *            the world, as a string.
	 *            For RS3, use a numeric string.
	 *            For oldschool, use "oldschool[world]".
	 * @return A future for the connection task.
	 */
	public CompletableFuture<Void> connect(String world) {
		if (connectionTask != null && !connectionTask.isDone()) {
			connectionTask.cancel(true);
		}

		disconnect();

		String host;
		int port;
		if (world.startsWith("oldschool")) {
			host = world + HOST_SUFFIX;
			port = OLDSCHOOL_PORT;
		} else {
			host = "world" + world + HOST_SUFFIX;
			port = RS3_PORT;
		}

		connectionTask = new CompletableFuture<>();
		CompletableFuture.runAsync(() -> {
			try {
				synchronized (this) {
					socket = new Socket(host, port);
				}
				connectionTask.complete(null);
			} catch (IOException e) {
				connectionTask.completeExceptionally(e);
			}
		});
		return connectionTask;
	}

	public synchronized void disconnect() {
		if (isConnected()) {
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public CompletableFuture<Duration> doClick() {
		long t0 = System.nanoTime();
		CompletableFuture<Duration> task = new CompletableFuture<>();
		CompletableFuture.runAsync(() -> {
			synchronized (this) {
				try {
					if (isConnected()) {
						socket.getOutputStream().write(SOCKET_SEND_BYTE);
						socket.getInputStream().read();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					task.completeExceptionally(e);
				}
			}
			long t1 = System.nanoTime();
			task.complete(Duration.ofNanos(t1 - t0));
		});
		return task;
	}
}

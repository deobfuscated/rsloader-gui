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
	 * @throws IOException
	 */
	public CompletableFuture<Void> connect(String world) throws IOException {
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
				socket = new Socket(host, port);
				connectionTask.complete(null);
			} catch (IOException e) {
				connectionTask.completeExceptionally(e);
			}
		});
		return connectionTask;
	}

	public void disconnect() throws IOException {
		if (isConnected()) {
			socket.close();
		}
	}

	public CompletableFuture<Duration> doClick() {
		if (isConnected()) {
			long t0 = System.nanoTime();
			return CompletableFuture.supplyAsync(() -> {
				try {
					socket.getOutputStream().write(SOCKET_SEND_BYTE);
					socket.getInputStream().read();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				long t1 = System.nanoTime();
				return Duration.ofNanos(t1 - t0);
			});
		}
		return CompletableFuture.completedFuture(Duration.ZERO);
	}
}

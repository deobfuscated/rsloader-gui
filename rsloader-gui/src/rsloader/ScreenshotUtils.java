package rsloader;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import javax.imageio.ImageIO;

public class ScreenshotUtils {
	/**
	 * Saves a screenshot of the component to the specified path.
	 * 
	 * @param component
	 *            The component to take the screenshot of.
	 * @param path
	 *            The path to save.
	 * @return The future of the file of the saved screenshot if successful, otherwise an exceptionally completed
	 *         future.
	 */
	public static CompletableFuture<File> saveScreenshot(Component component, String path) {
		final CompletableFuture<File> task = new CompletableFuture<>();
		CompletableFuture.runAsync(() -> {
			try {
				Rectangle bounds = new Rectangle(component.getLocationOnScreen(), component.getSize());
				BufferedImage image = new Robot().createScreenCapture(bounds);
				File availableFile = findAvailableFile(path);
				ImageIO.write(image, "png", availableFile);
				task.complete(availableFile);
			} catch (AWTException | IOException e) {
				task.completeExceptionally(e);
			}
		});
		return task;
	}

	private static File findAvailableFile(String path) {
		for (int i = 1;; i++) {
			File f = new File(path, i + ".png");
			if (!f.exists() && !f.isDirectory())
				return f;
		}
	}
}

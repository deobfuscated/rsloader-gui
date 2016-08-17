package rsloader;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ScreenshotUtils {
	/**
	 * Saves a screenshot of the component to the specified path.
	 * 
	 * @param component
	 *            The component to take the screenshot of.
	 * @param path
	 *            The path to save.
	 * @return The file of the saved screenshot if successful, otherwise null.
	 */
	public static File saveScreenshot(Component component, String path) {
		try {
			Rectangle bounds = new Rectangle(component.getLocationOnScreen(), component.getSize());
			BufferedImage image = new Robot().createScreenCapture(bounds);
			File availableFile = findAvailableFile(path);
			ImageIO.write(image, "png", availableFile);
			return availableFile;
		} catch (AWTException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private static File findAvailableFile(String path) {
		for (int i = 1;; i++) {
			File f = new File(path, i + ".png");
			if (!f.exists() && !f.isDirectory())
				return f;
		}
	}
}

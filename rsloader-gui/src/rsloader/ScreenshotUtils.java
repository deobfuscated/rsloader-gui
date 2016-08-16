package rsloader;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ScreenshotUtils {
	public static void saveScreenshot(Component component, String path) {
		try {
			Rectangle bounds = new Rectangle(component.getLocationOnScreen(), component.getSize());
			BufferedImage image = new Robot().createScreenCapture(bounds);
			ImageIO.write(image, "png", findAvailableFile(path));
		} catch (AWTException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static File findAvailableFile(String path) {
		for (int i = 1;; i++) {
			File f = new File(path, i + ".png");
			if (!f.exists() && !f.isDirectory())
				return f;
		}
	}
}

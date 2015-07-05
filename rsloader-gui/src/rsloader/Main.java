package rsloader;

import java.awt.Font;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
	public static final String[] languages = { "en", "de", "fr", "pt" };

	private static final String CONFIG_FILE_NAME = "config.ini";
	private static final String DEFAULT_BASE_URL = "http://www.runescape.com/";

	private static DialogFrame loadingDialog;
	private static Properties configuration;

	public static void main(String[] args) {
		// TODO: move these to a Configuration class.
		Properties defaultProps = new Properties();
		defaultProps.setProperty("width", "800");
		defaultProps.setProperty("height", "600");

		configuration = new Properties(defaultProps);
		try (FileInputStream in = new FileInputStream(CONFIG_FILE_NAME)) {
			configuration.load(in);
		} catch (IOException e) {
			// There is no config.ini, that's okay.
		}
		
		String baseUrl = args.length >= 1 ? args[0] : DEFAULT_BASE_URL;

		SwingUtilities.invokeLater(() -> {
			try {
				String className = UIManager.getSystemLookAndFeelClassName();
				UIManager.setLookAndFeel(className);
				if (className.contains("windows")) {
					Font font = new Font("Segoe UI", Font.PLAIN, 12);
					UIManager.getDefaults().put("Button.font", font);
					UIManager.getDefaults().put("TextField.font", font);
					UIManager.getDefaults().put("Label.font", font);
					UIManager.getDefaults().put("CheckBox.font", font);
				}
				loadingDialog = new DialogFrame();
				loadingDialog.setVisible(true);
				GameParameters params;
				params = GameParameters.parse(new URL(new URL(baseUrl), "jav_config.ws"));

				GamePack gamepack = GamePack.load(params);
				GameClassLoader gcl = new GameClassLoader(gamepack);
				GameStub stub = new GameStub(params);
				Game.launch(gcl, params, stub);
				loadingDialog.dispose();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public static DialogFrame getLoadingDialog() {
		return loadingDialog;
	}

	public static Properties getConfiguration() {
		return configuration;
	}

	public static void saveConfiguration() {
		try (FileOutputStream out = new FileOutputStream(CONFIG_FILE_NAME)) {
			configuration.store(out, "");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

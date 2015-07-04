package rsloader;

import java.net.URL;

public class Main {

	private static DialogFrame loadingDialog;

	public static void main(String[] args) throws Exception {
		loadingDialog = new DialogFrame();
		loadingDialog.setVisible(true);
		GameParameters params = GameParameters.parse(new URL(new URL(args[0]), "jav_config.ws"));
		GamePack gamepack = GamePack.load(params);
		GameClassLoader gcl = new GameClassLoader(gamepack);
		GameStub stub = new GameStub(params);
		Game.launch(gcl, params, stub);
		loadingDialog.dispose();
	}

	public static DialogFrame getLoadingDialog() {
		return loadingDialog;
	}

}

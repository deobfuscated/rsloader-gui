package rsloader;

import java.applet.Applet;
import java.applet.AppletStub;
import java.awt.Dimension;

import javax.swing.JFrame;

public class Game {

	public static void launch(GameClassLoader gcl, GameParameters params, AppletStub stub) throws Exception {
		Main.getLoadingDialog().setStatus("Launching game");
		Applet game = (Applet) gcl.loadClass(params.getInitialClass()).newInstance();
		game.setPreferredSize(new Dimension(Integer.parseInt(params.getMinWidth()), Integer.parseInt(params.getMinHeight())));
		game.setStub(stub);
		game.init();
		game.start();
		JFrame frame = new JFrame(params.getTitle());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(game);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

}

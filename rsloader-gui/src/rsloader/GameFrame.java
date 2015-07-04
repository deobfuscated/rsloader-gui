package rsloader;

import java.applet.Applet;
import java.awt.Container;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.*;

import javax.swing.*;

public class GameFrame extends JFrame {
	/**
	 * Required by Serializable.
	 */
	private static final long serialVersionUID = 1L;
	private Applet game;
	private GameParameters parameters;

	private JMenuBar menuBar;

	public GameFrame(Applet game, GameParameters parameters) {
		this.game = game;
		this.parameters = parameters;

		JTextField worldTextField = new JTextField(15);
		JButton loadButton = new JButton("Load");
		JMenu predefinedSizesMenu = new JMenu("Predefined Sizes");
		JMenuItem size800MenuItem = new JMenuItem("800 x 600");
		JMenuItem size1280MenuItem = new JMenuItem("1280 x 720");
		predefinedSizesMenu.add(size800MenuItem);
		predefinedSizesMenu.add(size1280MenuItem);
		predefinedSizesMenu.getPopupMenu().setLightWeightPopupEnabled(false);
		menuBar = new JMenuBar();
		menuBar.add(worldTextField);
		menuBar.add(loadButton);
		menuBar.add(predefinedSizesMenu);
		setJMenuBar(menuBar);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		Container cp = getContentPane();
		cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));
		cp.add(game);
		pack();
		updateTitle();

		// Update title when applet changes size
		game.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				updateTitle();
			}
		});

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// Only save window state if it is not maximized.
				if (getExtendedState() == NORMAL) {
					Main.getConfiguration().setProperty("width", String.valueOf(game.getWidth()));
					Main.getConfiguration().setProperty("height", String.valueOf(game.getHeight()));
					Main.getConfiguration().setProperty("x", String.valueOf(getX()));
					Main.getConfiguration().setProperty("y", String.valueOf(getY()));
					Main.saveConfiguration();
				}
			}
		});

		KeyboardFocusManager focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		focusManager.addKeyEventDispatcher(new KeyEventDispatcher() {
			@Override
			public boolean dispatchKeyEvent(KeyEvent e) {
				if (e.getID() == KeyEvent.KEY_TYPED && e.getKeyChar() == 'm' && e.isAltDown()) {
					menuBar.setVisible(!menuBar.isVisible());
					return true;
				}
				return false;
			}
		});
	}

	private void updateTitle() {
		setTitle(String.format("%s (%d x %d)", parameters.getTitle(), game.getWidth(), game.getHeight()));
	}
}

package rsloader;

import java.applet.Applet;
import java.awt.*;
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
		worldTextField.setMaximumSize(new Dimension(100, 0));
		JButton loadButton = new JButton("Load");
		JMenu predefinedSizesMenu = new JMenu("Predefined Sizes \u25be");
		JMenuItem size800MenuItem = new JMenuItem("800 x 600");
		size800MenuItem.setActionCommand("800,600");
		JMenuItem size1280MenuItem = new JMenuItem("1280 x 720");
		size1280MenuItem.setActionCommand("1280,720");
		predefinedSizesMenu.add(size800MenuItem);
		predefinedSizesMenu.add(size1280MenuItem);
		predefinedSizesMenu.getPopupMenu().setLightWeightPopupEnabled(false);
		menuBar = new JMenuBar();
		menuBar.add(worldTextField);
		menuBar.add(loadButton);
		menuBar.add(predefinedSizesMenu);
		menuBar.add(Box.createHorizontalGlue());
		setJMenuBar(menuBar);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		Container cp = getContentPane();
		cp.add(game);
		pack();
		updateTitle();
		
		ActionListener predefinedSizesListener = (e) -> {
			String[] arr = e.getActionCommand().split(",");
			int width = Integer.parseInt(arr[0]);
			int height = Integer.parseInt(arr[1]);
			game.setPreferredSize(new Dimension(width, height));
			pack();
		};
		size800MenuItem.addActionListener(predefinedSizesListener);
		size1280MenuItem.addActionListener(predefinedSizesListener);

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

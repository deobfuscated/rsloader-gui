package rsloader;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/**
 * The main game window.
 */
public class GameFrame extends JFrame {
	/**
	 * Required by Serializable.
	 */
	private static final long serialVersionUID = 1L;

	private static final Dimension[] PREDEFINED_SIZES = {
			new Dimension(765, 543),
			new Dimension(800, 600),
			new Dimension(1280, 720)
	};

	private Applet gameApplet;
	private GameParameters parameters;

	private JMenuBar menuBar;
	private JMenu predefinedSizesMenu;
	//private ProgressPanel progressPanel;

	public GameFrame(Applet gameApplet, GameParameters parameters) {
		this.gameApplet = gameApplet;
		this.parameters = parameters;

		JTextField worldTextField = new JTextField(15);
		worldTextField.setMaximumSize(new Dimension(100, Integer.MAX_VALUE));
		JButton loadButton = new JButton(new LoadGameAction("Load"));
		predefinedSizesMenu = new JMenu("Predefined Sizes \u25be");
		predefinedSizesMenu.getPopupMenu().setLightWeightPopupEnabled(false);

		for (Dimension dim : PREDEFINED_SIZES) {
			addPredefinedSize(dim.width, dim.height);
		}
		menuBar = new JMenuBar();
		menuBar.add(worldTextField);
		menuBar.add(loadButton);
		menuBar.add(predefinedSizesMenu);
		menuBar.add(Box.createHorizontalGlue());
		setJMenuBar(menuBar);

		// progressPanel = new ProgressPanel();

		setDefaultCloseOperation(EXIT_ON_CLOSE);

		Container cp = getContentPane();
		// cp.add(progressPanel);
		cp.add(gameApplet);
		pack();
		updateTitle();

		// Update title when applet changes size
		gameApplet.addComponentListener(new ComponentAdapter() {
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
					Main.getConfiguration().setProperty("width", String.valueOf(gameApplet.getWidth()));
					Main.getConfiguration().setProperty("height", String.valueOf(gameApplet.getHeight()));
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
		setTitle(String.format("%s (%d × %d)", parameters.getTitle(), gameApplet.getWidth(), gameApplet.getHeight()));
	}

	private JMenuItem addPredefinedSize(int width, int height) {
		JMenuItem menuItem = new JMenuItem(new PredefinedSizeAction(width, height));
		predefinedSizesMenu.add(menuItem);
		return menuItem;
	}

	/**
	 * The action that loads the game.
	 */
	private class LoadGameAction extends AbstractAction {
		/**
		 * Required by Serializable.
		 */
		private static final long serialVersionUID = 1L;

		public LoadGameAction(String name) {
			super(name);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO THIS DOESN'T WORK
			try {
				getContentPane().remove(gameApplet);
				gameApplet.destroy();
				gameApplet = null;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Encapsulates the action performed when a user clicks on a predefined size
	 * menu item.
	 */
	private class PredefinedSizeAction extends AbstractAction {
		/**
		 * Required by Serializable.
		 */
		private static final long serialVersionUID = 1L;

		private int width;
		private int height;

		public PredefinedSizeAction(int width, int height) {
			super(String.format("%d × %d", width, height));
			this.width = width;
			this.height = height;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			gameApplet.setPreferredSize(new Dimension(width, height));
			gameApplet.setSize(width, height);
			pack();
		}
	}
}

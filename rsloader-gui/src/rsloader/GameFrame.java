package rsloader;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

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
	private JButton screenshotButton;
	private PopupPanel screenshotPopupPanel = new PopupPanel(this, 3000);

	public GameFrame(Applet gameApplet, GameParameters parameters) {
		this.gameApplet = gameApplet;
		this.parameters = parameters;

		this.setIconImage(new ImageIcon(getClass().getResource("/yellow-orb-32x32.png")).getImage());

		predefinedSizesMenu = new JMenu("Predefined Sizes \u25be");
		predefinedSizesMenu.getPopupMenu().setLightWeightPopupEnabled(false);

		for (Dimension dim : PREDEFINED_SIZES) {
			addPredefinedSize(dim.width, dim.height);
		}
		menuBar = new JMenuBar();
		menuBar.add(predefinedSizesMenu);

		// menuBar.add(Box.createHorizontalGlue());
		screenshotButton = new JButton(new ScreenshotAction());
		menuBar.add(screenshotButton);

		setJMenuBar(menuBar);

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
		focusManager.addKeyEventDispatcher((KeyEvent e) -> {
			// Eat Ctrl+Shift+anything. Players cannot set Ctrl+Shift+anything as keybinds within RS.
			if (e.isControlDown() && e.isShiftDown()) {
				// We use KEY_RELEASED to do the actual actions to prevent key-repeats.
				// Key-repeats would be very bad for screenshots, for example.
				if (e.getID() == KeyEvent.KEY_RELEASED) {
					switch (e.getKeyCode()) {
					case KeyEvent.VK_M:
						setMenuBarVisible(!menuBar.isVisible());
						break;
					case KeyEvent.VK_S:
						screenshotButton.doClick();
						break;
					}
				}
				return true;
			}
			return false;
		});
	}

	private void setMenuBarVisible(boolean visible) {
		if (menuBar.isVisible() != visible) {
			menuBar.setVisible(visible);

			// Update height accordingly (if un-maximized)
			if (getExtendedState() == NORMAL) {
				int menuBarHeight = menuBar.getHeight();
				int deltaHeight = visible ? menuBarHeight : -menuBarHeight;
				Dimension size = getSize();
				setSize(size.width, size.height + deltaHeight);
			}
		}
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

	/**
	 * Encapsulates the action performed when a user clicks on a predefined size
	 * menu item.
	 */
	private class ScreenshotAction extends AbstractAction {
		/**
		 * Required by Serializable.
		 */
		private static final long serialVersionUID = 1L;

		public ScreenshotAction() {
			super("Screenshot");
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// Don't want screenshot to include the popup!
			// TODO: hide all popups (when they get implemented)
			screenshotPopupPanel.hidePopup();
			
			final File file = ScreenshotUtils.saveScreenshot(gameApplet, Main.getConfiguration().getProperty("screenshotPath"));
			if (file != null) {
				screenshotPopupPanel.setText("Screenshot saved to " + file.getName());
				screenshotPopupPanel.setClickAction(() -> {
					try {
						Desktop.getDesktop().open(file.getParentFile());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
			} else {
				screenshotPopupPanel.setText("Failed to save screenshot");
			}

			Point location = gameApplet.getLocationOnScreen();
			Dimension size = gameApplet.getSize();
			// Right align
			final int margin = 10;
			int x = location.x + size.width - screenshotPopupPanel.getPreferredSize().width - margin;
			int y = location.y + margin;
			screenshotPopupPanel.showPopup(x, y);
		}
	}
}

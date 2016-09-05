package rsloader;

import java.applet.Applet;
import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextField;

/**
 * The main game window.
 */
public class GameFrame extends JFrame {
	/**
	 * Required by Serializable.
	 */
	private static final long serialVersionUID = 1L;

	private static final Dimension[] PREDEFINED_SIZES = { new Dimension(765, 543), new Dimension(800, 600),
			new Dimension(1280, 720) };

	private static final int CLICK_THRESHOLD_GOOD = 150;
	private static final Color CLICK_THRESHOLD_GOOD_COLOR = new Color(128, 255, 128);
	private static final int CLICK_THRESHOLD_BAD = 750;
	private static final Color CLICK_THRESHOLD_BAD_COLOR = new Color(255, 128, 128);
	private static final Color CLICK_THRESHOLD_NEUTRAL_COLOR = new Color(255, 255, 255);

	private Applet gameApplet;
	private GameParameters parameters;

	private ClickProfiler clickProfiler = new ClickProfiler();

	private JMenuBar menuBar;
	private JMenu predefinedSizesMenu;
	private JMenuItem fullscreenMenuItem;
	private JButton screenshotButton;
	private JTextField profileWorldField;
	private PopupPanel infoPopupPanel = new PopupPanel(this, 3000);

	public GameFrame(Applet gameApplet, GameParameters parameters) {
		this.gameApplet = gameApplet;
		this.parameters = parameters;

		this.setIconImage(new ImageIcon(getClass().getResource("/yellow-orb-32x32.png")).getImage());

		predefinedSizesMenu = new JMenu("Predefined Sizes \u25be");
		predefinedSizesMenu.getPopupMenu().setLightWeightPopupEnabled(false);

		for (Dimension dim : PREDEFINED_SIZES) {
			addPredefinedSize(dim.width, dim.height);
		}
		fullscreenMenuItem = new JMenuItem(new FullscreenAction());

		predefinedSizesMenu.add(fullscreenMenuItem);

		menuBar = new JMenuBar();
		menuBar.add(predefinedSizesMenu);

		// menuBar.add(Box.createHorizontalGlue());
		screenshotButton = new JButton(new ScreenshotAction());
		menuBar.add(screenshotButton);

		menuBar.add(Box.createHorizontalGlue());

		JLabel profileWorldLabel = new JLabel("Profile click world: ");
		menuBar.add(profileWorldLabel);

		profileWorldField = new JTextField(10);
		profileWorldField.setMaximumSize(profileWorldField.getPreferredSize());
		menuBar.add(profileWorldField);

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
				}
				Main.saveConfiguration();
			}
		});

		profileWorldField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				connectProfiler();
			}
		});

		bindKeyboardShortcuts();
		bindMouseToProfileClicks();
	}

	private void connectProfiler() {
		infoPopupPanel.hidePopup();

		String world = profileWorldField.getText();
		if (world.isEmpty()) {
			clickProfiler.disconnect();
		} else {
			CompletableFuture<Void> task = clickProfiler.connect(world);
			task.whenComplete((aVoidThing, ex) -> {
				if (ex == null)
					showInfoPopup("Connected to " + clickProfiler.getAddress(), null);
				else
					showInfoPopup("Failed to connect", null);
			});
		}
	}

	private void bindMouseToProfileClicks() {
		Toolkit.getDefaultToolkit().addAWTEventListener((final AWTEvent e) -> {
			if (clickProfiler.isConnected() && e.getID() == MouseEvent.MOUSE_PRESSED) {
				final MouseEvent m = (MouseEvent) e;
				if (m.getButton() == MouseEvent.BUTTON1 && gameApplet.isAncestorOf(m.getComponent())) {
					clickProfiler.doClick().whenComplete((duration, ex) -> {
						if (ex != null || duration.isZero())
							return;

						final PopupPanel popupPanel = new PopupPanel(this, 600);
						long millis = duration.toMillis();
						if (millis <= CLICK_THRESHOLD_GOOD)
							popupPanel.setBackground(CLICK_THRESHOLD_GOOD_COLOR);
						else if (millis >= CLICK_THRESHOLD_BAD)
							popupPanel.setBackground(CLICK_THRESHOLD_BAD_COLOR);
						else
							popupPanel.setBackground(CLICK_THRESHOLD_NEUTRAL_COLOR);
						popupPanel.setText(millis + " ms");

						// Show on bottom-right
						Point location = gameApplet.getLocationOnScreen();
						Dimension size = gameApplet.getSize();
						final int margin = 10;
						int x = location.x + size.width - popupPanel.getPreferredSize().width - margin;
						int y = location.y + size.height - popupPanel.getPreferredSize().height - margin;
						popupPanel.showPopup(x, y);
					});
				}
			}

		}, AWTEvent.MOUSE_EVENT_MASK);
	}

	private void bindKeyboardShortcuts() {
		final KeyboardFocusManager focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		focusManager.addKeyEventDispatcher(e -> {
			// We use KEY_RELEASED to do the actual actions to prevent key-repeats.
			// Key-repeats would be very bad for screenshots, for example.
			if (e.getID() == KeyEvent.KEY_RELEASED) {
				if (e.isControlDown() && e.isShiftDown()) {
					switch (e.getKeyCode()) {
					case KeyEvent.VK_M:
						setMenuBarVisible(!menuBar.isVisible());
						return true;
					case KeyEvent.VK_S:
						screenshotButton.doClick();
						return true;
					}
				} else if (e.getKeyCode() == KeyEvent.VK_F11) {
					fullscreenMenuItem.getAction().actionPerformed(null);
					return true;
				}
			} else if (e.isControlDown() && e.isShiftDown()) {
				return true;
			}
			return false;
		});
	}

	private void showInfoPopup(String text, Runnable clickAction) {
		infoPopupPanel.setText(text);
		infoPopupPanel.setClickAction(clickAction);

		Point location = gameApplet.getLocationOnScreen();
		Dimension size = gameApplet.getSize();
		// Right align
		final int margin = 10;
		int x = location.x + size.width - infoPopupPanel.getPreferredSize().width - margin;
		int y = location.y + margin;
		infoPopupPanel.showPopup(x, y);
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
		public void actionPerformed(ActionEvent e) {
			// Don't want screenshot to include the popup!
			// TODO: hide all popups (when they get implemented)
			infoPopupPanel.hidePopup();

			CompletableFuture<File> result = ScreenshotUtils.saveScreenshot(gameApplet,
					Main.getConfiguration().getProperty("screenshotPath"));
			result.whenComplete((file, ex) -> {
				String text = "Failed to save screenshot";
				Runnable clickAction = null;

				if (file != null) {
					text = "Screenshot saved to " + file.getName();
					clickAction = () -> {
						try {
							Desktop.getDesktop().open(file.getParentFile());
						} catch (IOException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
					};
				}

				showInfoPopup(text, clickAction);
			});
		}
	}

	/**
	 * Encapsulates the action performed when a user clicks on the fullscreen
	 * menu item
	 */
	private class FullscreenAction extends AbstractAction {
		/**
		 * Required by Serializable.
		 */
		private static final long serialVersionUID = 1L;

		public FullscreenAction() {
			super("Fullscreen");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!isUndecorated()) {
				// windowed->fullscreen
				setMenuBarVisible(false);
				dispose();
				setUndecorated(true);
				setVisible(true);
				getGraphicsConfiguration().getDevice().setFullScreenWindow(GameFrame.this);
				showInfoPopup("Entered fullscreen. Press F11 to exit.", null);
			} else {
				// fullscreen->windowed
				getGraphicsConfiguration().getDevice().setFullScreenWindow(GameFrame.this);
				dispose();
				setUndecorated(false);
				setVisible(true);
				setMenuBarVisible(true);
			}
		}
	}
}

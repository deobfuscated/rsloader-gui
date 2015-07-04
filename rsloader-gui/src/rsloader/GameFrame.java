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

    private JPanel topPanel;

    public GameFrame(Applet game, GameParameters parameters) {
        this.game = game;
        this.parameters = parameters;

        topPanel = new JPanel();
        JTextField worldTextField = new JTextField(15);
        JButton loadButton = new JButton("Load");
        topPanel.add(worldTextField);
        topPanel.add(loadButton);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Container cp = getContentPane();
        cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));
        cp.add(topPanel);
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
                if (e.getID() == KeyEvent.KEY_PRESSED) {
                    if (e.getKeyChar() == 'm' && e.isAltDown()) {
                        topPanel.setVisible(!topPanel.isVisible());
                    }
                }
                return false;
            }
        });
    }

    private void updateTitle() {
        setTitle(String.format("%s (%d x %d)",
                parameters.getTitle(),
                game.getWidth(),
                game.getHeight()));
    }
}

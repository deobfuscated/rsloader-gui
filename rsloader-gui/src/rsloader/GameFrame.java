package rsloader;

import java.applet.Applet;
import java.awt.event.*;

import javax.swing.JFrame;

public class GameFrame extends JFrame {
    /**
     * Required by Serializable.
     */
    private static final long serialVersionUID = 1L;
    private Applet game;
    private GameParameters parameters;

    public GameFrame(Applet game, GameParameters parameters) {
        this.game = game;
        this.parameters = parameters;

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(game);
        pack();
        updateTitle();

        // Update title when applet changes size
        game.addComponentListener(new ComponentListener() {
            @Override
            public void componentShown(ComponentEvent e) {
            }

            @Override
            public void componentResized(ComponentEvent e) {
                updateTitle();
            }

            @Override
            public void componentMoved(ComponentEvent e) {
            }

            @Override
            public void componentHidden(ComponentEvent e) {
            }
        });

        addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }

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

            @Override
            public void windowClosed(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
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

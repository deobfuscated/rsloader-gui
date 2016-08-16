package rsloader;

import java.applet.Applet;
import java.applet.AppletStub;
import java.awt.Dimension;

public class Game {
    public static void launch(GameClassLoader gcl, GameParameters params, AppletStub stub) throws Exception {
        Main.getLoadingDialog().setStatus("Launching game");
        Applet gameApplet = (Applet) gcl.loadClass(params.getInitialClass()).newInstance();
        gameApplet.setPreferredSize(new Dimension(
                Integer.parseInt(Main.getConfiguration().getProperty("width")),
                Integer.parseInt(Main.getConfiguration().getProperty("height"))));
        gameApplet.setStub(stub);
        gameApplet.init();
        gameApplet.start();

        GameFrame window = new GameFrame(gameApplet, params);
        String xStr = Main.getConfiguration().getProperty("x");
        String yStr = Main.getConfiguration().getProperty("y");
        if (xStr == null || yStr == null) {
            window.setLocationRelativeTo(null);
        } else {
            window.setLocation(Integer.valueOf(xStr), Integer.valueOf(yStr));
        }
        window.setVisible(true);
    }
}

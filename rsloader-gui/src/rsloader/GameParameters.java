package rsloader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;

public class GameParameters {
    /**
     * The applet parameters.
     */
    private HashMap<String, String> params;
    /**
     * Other configuration parameters.
     */
    private HashMap<String, String> configuration;

    private GameParameters() {
        params = configuration = new HashMap<>();
    }

    public static GameParameters parse(URL url) throws IOException {
        Main.getLoadingDialog().setStatus("Parsing parameters");
        GameParameters p = new GameParameters();
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        String line;
        while ((line = br.readLine()) != null) {
            int idx = line.indexOf('=');
            if (idx != -1) {
                String key = line.substring(0, idx);
                String val = line.substring(idx + 1);
                if (key.equals("param")) {
                    idx = val.indexOf('=');
                    key = val.substring(0, idx);
                    val = val.substring(idx + 1);
                    p.params.put(key, val);
                } else {
                    p.configuration.put(key, val);
                }
            }
        }
        return p;
    }

    public String getCodeBase() {
        return configuration.get("codebase");
    }

    public String getCacheDir() {
        return configuration.get("cachedir");
    }

    public String getJar() {
        return configuration.get("initial_jar");
    }

    public String getInitialClass() {
        String initialClass = configuration.get("initial_class");
        initialClass = initialClass.substring(0, initialClass.length() - 6);
        return initialClass;
    }

    public String getMinWidth() {
        return configuration.get("applet_minwidth");
    }

    public String getMinHeight() {
        return configuration.get("applet_minheight");
    }

    public String getTitle() {
        return configuration.get("title");
    }

    public String getParameter(String key) {
        return params.get(key);
    }
}

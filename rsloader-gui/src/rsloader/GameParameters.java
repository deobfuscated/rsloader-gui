package rsloader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class GameParameters {

	private HashMap<String, String> params, cfg;
	
	private GameParameters() {
		params = cfg = new HashMap<String, String>();
	}
	
	public static GameParameters parse(URL url) throws IOException {
		Main.getLoadingDialog().setStatus("Parsing parameters");
		GameParameters p = new GameParameters();
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
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
					p.cfg.put(key, val);
				}
			}
		}
		return p;
	}
	
	public String getCodeBase() {
		return cfg.get("codebase");
	}
	
	public String getCacheDir() {
		return cfg.get("cachedir");
	}
	
	public String getJar() {
		return cfg.get("initial_jar");
	}

	public String getInitialClass() {
		String initialClass = cfg.get("initial_class");
		initialClass = initialClass.substring(0, initialClass.length() - 6);
		return initialClass;
	}

	public String getMinWidth() {
		return cfg.get("applet_minwidth");
	}

	public String getMinHeight() {
		return cfg.get("applet_minheight");
	}

	public String getTitle() {
		return cfg.get("title");
	}
	
	public String getParameter(String key) {
		return params.get(key);
	}
	
}

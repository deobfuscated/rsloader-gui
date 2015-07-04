package rsloader;

import java.applet.AppletContext;
import java.applet.AppletStub;
import java.net.MalformedURLException;
import java.net.URL;

public class GameStub implements AppletStub {
	private GameParameters params;

	public GameStub(GameParameters params) {
		this.params = params;
	}

	@Override
	public boolean isActive() {
		return false;
	}

	@Override
	public URL getDocumentBase() {
		return getCodeBase();
	}

	@Override
	public URL getCodeBase() {
		try {
			return new URL(params.getCodeBase());
		} catch (MalformedURLException e) {
			e.printStackTrace();
			System.exit(0);
		}
		return null;
	}

	@Override
	public String getParameter(String name) {
		return params.getParameter(name);
	}

	@Override
	public AppletContext getAppletContext() {
		return null;
	}

	@Override
	public void appletResize(int width, int height) {
	}
}

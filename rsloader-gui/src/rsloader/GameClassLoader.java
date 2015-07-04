package rsloader;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class GameClassLoader extends ClassLoader {
    private GamePack gamepack;

    public GameClassLoader(GamePack gamepack) {
        this.gamepack = gamepack;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        byte[] data = gamepack.getResource(name + ".class");
        if (data != null)
            return defineClass(name, data, 0, data.length);
        return super.findSystemClass(name);
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        byte[] data = gamepack.getResource(name);
        if (data != null) {
            return new ByteArrayInputStream(data);
        }
        return super.getResourceAsStream(name);
    }
}

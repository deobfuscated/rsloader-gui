package rsloader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class GamePack {
    private String game;
    private File local;
    private URL remote;
    private byte[] data;
    private HashMap<String, byte[]> resources = new HashMap<>();

    private GamePack() {
    }

    public static GamePack load(GameParameters params) throws IOException {
        Main.getLoadingDialog().setStatus("Loading gamepack");
        GamePack g = new GamePack();
        g.game = params.getCacheDir();
        g.local = new File(g.game + ".jar");
        g.remote = new URL(params.getCodeBase() + params.getJar());
        g.data = g.load();
        g.decompress();
        return g;
    }

    private byte[] load() throws IOException {
        if (!local.exists()) {
            return download();
        }
        Main.getLoadingDialog().setStatus("Checking for updates");
        HttpURLConnection con = (HttpURLConnection) remote.openConnection();
        con.setRequestMethod("HEAD");
        if (local.length() != con.getContentLength()) {
            return download();
        }
        return read("Reading gamepack (%d KB/s)", (int) local.length(), new FileInputStream(local));
    }

    private byte[] download() throws IOException {
        HttpURLConnection con = (HttpURLConnection) remote.openConnection();
        byte[] data = read("Downloading gamepack (%d KB/s)", con.getContentLength(), con.getInputStream());
        FileOutputStream fos = new FileOutputStream(local);
        fos.write(data);
        fos.close();
        return data;
    }

    private static byte[] read(String fmt, int length, InputStream in) throws IOException {
        byte[] b = new byte[length];
        int offset = 0;
        long time = System.nanoTime();
        while (offset < length) {
            offset += in.read(b, offset, length - offset);
            double progress = (double) offset / (double) length;
            long elapsed = System.nanoTime() - time;
            int rate = (int) ((offset / 1024.0) / (elapsed / 1000000000.0));
            Main.getLoadingDialog().setStatus(String.format(fmt, rate));
            Main.getLoadingDialog().setProgress(progress);
        }
        in.close();
        return b;
    }

    private void decompress() throws IOException {
        Main.getLoadingDialog().setStatus("Decompressing gamepack");
        ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(data));
        ZipEntry ze;
        while ((ze = zis.getNextEntry()) != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buf = new byte[4096];
            int read;
            while ((read = zis.read(buf)) != -1) {
                baos.write(buf, 0, read);
            }
            resources.put(ze.getName(), baos.toByteArray());
        }
        zis.close();
    }

    public byte[] getResource(String name) {
        return resources.get(name);
    }

}

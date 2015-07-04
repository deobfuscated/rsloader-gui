package rsloader;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class DialogPanel extends JPanel {

	private static final int WIDTH = 250, HEIGHT = 58;
	private String status;
	private double progress;
	private BufferedImage img;

	public DialogPanel() {
		Dimension dim = new Dimension(WIDTH, HEIGHT);
		setPreferredSize(dim);
		setMinimumSize(dim);
		setMaximumSize(dim);
		setSize(dim);
		img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
	}
	
	@Override
	public void update(Graphics g) {
		paint(g);
	}
	
	@Override
	public void paint(Graphics g) {
		Graphics g2 = img.getGraphics();
		g2.setColor(Color.BLACK);
		g2.fillRect(0, 0, 300, 150);
		drawProgress(g2, progress);
		g2.setColor(Color.WHITE);
		drawStatus(g2, status);
		g.drawImage(img, 0, 0, this);
	}

	private void drawStatus(Graphics g, String str) {
		FontMetrics m = g.getFontMetrics();
		int x = WIDTH / 2 - m.stringWidth(str) / 2;
		int y = HEIGHT / 2 + 4;
		Color c = g.getColor();
		g.setColor(Color.BLACK);
		g.drawString(str, x + 1, y + 1);
		g.setColor(c);
		g.drawString(str, x, y);
	}
	
	private void drawProgress(Graphics g, double progress) {
		final int pWidth = WIDTH - 12 * 2, pHeight = HEIGHT - 12 * 2;
		int x = WIDTH / 2 - pWidth / 2;
		int y = HEIGHT / 2 - pHeight / 2;
		g.setColor(new Color(140, 17, 17));
		g.drawRect(x, y, pWidth, pHeight);
		g.fillRect(x + 2, y + 2, (int) (pWidth * progress) - 3, pHeight - 3);
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
		paint(getGraphics());
	}

	public void setProgress(double progress) {
		this.progress = progress;
		paint(getGraphics());
	}
	
}

package rsloader;

import javax.swing.JFrame;

public class DialogFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	private ProgressPanel panel;

	public DialogFrame() {
		setTitle("Starting RSLoader...");
		panel = new ProgressPanel();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setContentPane(panel);
		pack();
		setLocationRelativeTo(null);
	}

	public void setStatus(String str) {
		panel.setStatus(str);
	}

	public void setProgress(double i) {
		panel.setProgress(i);
	}
}

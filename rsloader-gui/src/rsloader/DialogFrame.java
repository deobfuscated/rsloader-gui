package rsloader;

import javax.swing.JFrame;

public class DialogFrame extends JFrame {

	private DialogPanel panel;
	
	public DialogFrame() {
		setTitle("Starting RSLoader...");
		panel = new DialogPanel();
		
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

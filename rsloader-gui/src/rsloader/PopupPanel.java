package rsloader;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;

public class PopupPanel extends JPanel {
	/**
	 * Required by Serializable.
	 */
	private static final long serialVersionUID = 1L;

	private Component owner;
	private Popup popup;
	private Timer hideTimer;
	private JLabel label;
	private Runnable clickAction;

	public PopupPanel(Component owner, int popupDuration) {
		this.owner = owner;

		hideTimer = new Timer(popupDuration, (ActionEvent e) -> {
			popup.hide();
		});
		hideTimer.setRepeats(false);

		label = new JLabel();
		add(label);

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (clickAction != null) {
					clickAction.run();
				}
			}
		});
	}

	public String getText() {
		return label.getText();
	}

	public void setText(String text) {
		label.setText(text);
	}

	public void setClickAction(Runnable clickAction) {
		this.clickAction = clickAction;
	}

	public void hidePopup() {
		if (popup != null) {
			popup.hide();
			hideTimer.stop();
		}
	}

	public void showPopup(int x, int y) {
		hidePopup();

		popup = PopupFactory.getSharedInstance().getPopup(owner, this, x, y);
		popup.show();
		hideTimer.start();
	}
}

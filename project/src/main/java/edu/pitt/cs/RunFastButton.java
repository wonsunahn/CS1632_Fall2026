package edu.pitt.cs;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

public class RunFastButton extends JButton {

	private MainPanel mainPanel;

	/**
	 * Constructor - Adds a listener to the button.
	 * 
	 * @param m the main animation panel where all the action happens
	 */
	@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "This is not a security vulnerability.")
	public RunFastButton(MainPanel m) {
		super("Fast");
		mainPanel = m;
		addActionListener(new RunFastButtonListener());
	}

	class RunFastButtonListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			mainPanel.runFast();
		}
	}

}

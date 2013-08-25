/*
 * ArbeitsrechnungenApp.java
 */

package arbeitsrechnungen;

import arbeitsrechnungen.gui.jframes.StartFenster;

/**
 * The main class of the application.
 */
public class ArbeitsrechnungenApp {

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {

		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new StartFenster().setVisible(true);
			}
		});
    }
}

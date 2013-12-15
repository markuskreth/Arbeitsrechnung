/*
 * ArbeitsrechnungenApp.java
 */

package de.kreth.arbeitsrechnungen;

import de.kreth.arbeitsrechnungen.gui.jframes.StartFenster;

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
			   ArbeitRechnungFactoryProductiv.init();
				new StartFenster().setVisible(true);
			}
		});
    }
}

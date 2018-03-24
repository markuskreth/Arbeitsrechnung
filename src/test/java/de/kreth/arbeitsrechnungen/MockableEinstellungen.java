package de.kreth.arbeitsrechnungen;

public class MockableEinstellungen extends Einstellungen {

	public MockableEinstellungen() {
		instance = this;
	}

	public static void setInstance(Einstellungen instance) {
		Einstellungen.instance = instance;
	}

	public static void setOptions(Options optionen) {
		Einstellungen.setOptions(optionen);
	}
}

package arbeitsabrechnungendataclass;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.kreth.hsqldbcreator.HsqlCreator;

public abstract class Verbindung {

	public enum ConnectionTypes {
		HSQLDB, MYSQL
	}

	protected Logger logger = LoggerFactory.getLogger(getClass());
	private static final String DBTYPE = "dbtype";

	public abstract boolean connected();

	/**
	 * Die Sql-Abfrage gibt den ResultSet zurück.
	 * 
	 * @param sql
	 * @return Ergebnis der Abfrage
	 * @throws SQLException
	 */
	public abstract ResultSet query(CharSequence sql) throws SQLException;

	/**
	 * führt den sqlbefehl aus und liefert bei erfolg true zurück.
	 * 
	 * @param sql
	 * @return befehl ausgeführt oder nicht?
	 */
	public abstract boolean sql(CharSequence sql) throws SQLException;

	public abstract void close();

	/**
	 * stellt eine Verbindung her
	 * 
	 * @param options
	 * @return {@link Verbindung} passend zu den Einstellungen in options
	 */
	public static Verbindung getVerbindung(Properties options) {

		Verbindung verbindung = null;
		if (isOldConnectionData(options)) {
			verbindung = new Verbindung_mysql(options);
		} else if (options.containsKey(DBTYPE)) {

			ConnectionTypes type = ConnectionTypes.valueOf(options.getProperty(DBTYPE));

			switch (type) {
			case HSQLDB:
				HsqlCreator instance = HsqlCreator.getInstance();
				verbindung = new Verbindung_HsqlCreator(instance);
				break;
			case MYSQL:
				verbindung = new Verbindung_mysql(options);
				break;
			default:
				verbindung = null;
				break;

			}
		}

		return verbindung;
	}

	private static boolean isOldConnectionData(Properties options) {
		return options.containsKey("sqlserver") && options.containsKey("datenbank") && options.containsKey("user")
				&& options.containsKey("password");
	}

	public abstract ResultSet getAutoincrement() throws SQLException;

}
package arbeitsabrechnungendataclass;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import de.kreth.hsqldbcreator.HsqlCreator;

public class Verbindung_HsqlCreator extends Verbindung {

	HsqlCreator stm;

	public Verbindung_HsqlCreator(HsqlCreator stm) {
		super();
		this.stm = stm;
		try {
			Statement createStatement = stm.getConnection().createStatement();
			if (DatabaseConfiguration.initDatabase(createStatement, "INTEGER IDENTITY") == false) {
				logger.warn("Tables created");
			}
		} catch (SQLException e) {
			logger.error("Error creating database.", e);
			this.stm = null;
		}
	}

	@Override
	public boolean connected() {
		try {
			return !stm.isClosed();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public ResultSet query(CharSequence sql) throws SQLException {
		logger.debug("executing: " + sql);
		return stm.executeQuery(sql.toString());
	}

	@Override
	public boolean sql(CharSequence sql) throws SQLException {
		logger.debug("executing: " + sql);
		return stm.execute(sql.toString());
	}

	@Override
	public void close() {
		try {
			stm.close();
		} catch (SQLException e) {
			System.err.println(e);
		}
	}

	@Override
	public String toString() {
		try {
			return stm.getConnection().getMetaData().getURL();
		} catch (SQLException e) {
			return stm.toString();
		}
	}

	@Override
	public ResultSet getAutoincrement() throws SQLException {
		return stm.executeQuery("CALL IDENTITY()");
	}
}

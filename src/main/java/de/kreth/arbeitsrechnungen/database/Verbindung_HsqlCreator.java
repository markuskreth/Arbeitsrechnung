package de.kreth.arbeitsrechnungen.database;

import java.sql.*;

import org.hsqldb.jdbc.jdbcDataSource;

public class Verbindung_HsqlCreator extends Verbindung {

   jdbcDataSource stm;
   private Connection conn;

	public Verbindung_HsqlCreator(jdbcDataSource ds) {
		super();
		this.stm = ds;
		try (Connection connection = ds.getConnection()){
			
         Statement createStatement = connection.createStatement();
			if (DatabaseConfiguration.initDatabase(createStatement, "INTEGER IDENTITY") == false) {
				logger.warn("Tables created");
			}
			this.conn = ds.getConnection();
		} catch (SQLException e) {
			logger.error("Error creating database.", e);
			this.stm = null;
		}
	}

	@Override
	public boolean connected() {
		try {
			return !conn.isClosed();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public ResultSet query(CharSequence sql) throws SQLException {
		logger.debug("executing: " + sql);
		
		return conn.createStatement().executeQuery(sql.toString());
	}

	@Override
	public boolean sql(CharSequence sql) throws SQLException {
		logger.debug("executing: " + sql);
      return conn.createStatement().execute(sql.toString());
	}

	@Override
	public void close() {
//		try {
//			conn.close();
//		} catch (SQLException e) {
//			System.err.println(e);
//		}
	}

	@Override
	public String toString() {
		try (Connection connection = stm.getConnection()) {
         return connection.getMetaData().getURL();
		} catch (SQLException e) {
			return stm.toString();
		}
	}

	@Override
	public ResultSet getAutoincrement() throws SQLException {
	   return conn.createStatement().executeQuery("CALL IDENTITY()");
	}

   @Override
   public PreparedStatement prepareStatement(CharSequence sql) throws SQLException {
      return stm.getConnection().prepareStatement(sql.toString());
   }
}

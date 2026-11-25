package io.eddie.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class VectorStoreQueryExecutor implements Connectable, Executable {

    private final String URL;
    private final String USERNAME;
    private final String PASSWORD;

    public VectorStoreQueryExecutor(String URL, String USERNAME, String PASSWORD) {
        this.URL = URL;
        this.USERNAME = USERNAME;
        this.PASSWORD = PASSWORD;
    }

    @Override
    public Connection connect() throws SQLException {
        return DriverManager.getConnection(
                this.URL,
                this.USERNAME,
                this.PASSWORD
        );
    }

    @Override
    public void execute(String sql) {
        try (Connection c = connect(); Statement st = c.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}

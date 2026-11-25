package io.eddie.dao;

import java.sql.SQLException;

public interface Executable {
    public void execute(String sql) throws SQLException;
}

package io.eddie.dao;

import java.sql.Connection;
import java.sql.SQLException;

public interface Connectable {
    Connection connect() throws SQLException;
}

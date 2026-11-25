package io.eddie.dao;

import io.eddie.dto.VectorResult;
import io.eddie.config.constants.VectorStoreOperator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VectorStore extends VectorStoreQueryExecutor{

    private final String table;

    public VectorStore(String url, String username, String password, String table) {
        super(url, username, password);
        this.table = table;
    }

    public void recreate(int dimension) {
        execute("CREATE EXTENSION IF NOT EXISTS vector");
        execute("DROP TABLE IF EXISTS " + table);
        execute("CREATE TABLE " + table + " ("
                + " id BIGSERIAL PRIMARY KEY,"
                + " content TEXT NOT NULL,"
                + " embedding VECTOR(" + dimension + ") NOT NULL)");
    }

    public void insert(String content, double[] embedding) {
        String sql = "INSERT INTO " + table + " (content, embedding) " +
                "VALUES (?, ?::vector)";

        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, content);
            ps.setString(2, toLiteral(embedding));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<VectorResult> search(double[] query, int topK, VectorStoreOperator metric) {
        String sql = "SELECT id, content, embedding " + metric.getOperator() + " ?::vector AS distance"
                + " FROM " + table
                + " ORDER BY distance"
                + " LIMIT ?";

        List<VectorResult> hits = new ArrayList<>();
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, toLiteral(query));
            ps.setInt(2, topK);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    hits.add(new VectorResult(rs.getLong("id"), rs.getString("content"), rs.getDouble("distance")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return hits;
    }

    static String toLiteral(double[] v) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < v.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(v[i]);
        }
        return sb.append(']').toString();
    }

}

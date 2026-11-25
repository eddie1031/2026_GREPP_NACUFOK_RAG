package io.eddie.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpUtil {

    public static final ObjectMapper om = new ObjectMapper();

    public static JsonNode postJson(String url, Map<String, String> headers, Object body) {
        try {

            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(10_000);
            conn.setReadTimeout(120_000);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            for (Map.Entry<String, String> h : headers.entrySet()) {
                conn.setRequestProperty(h.getKey(), h.getValue());
            }

            try (OutputStream out = conn.getOutputStream()) {
                out.write(om.writeValueAsBytes(body));
            }

            int status = conn.getResponseCode();
            InputStream in = status < 400 ? conn.getInputStream() : conn.getErrorStream();

            String text = readAll(in);
            if (status >= 400) {
                throw new RuntimeException("HTTP " + status + " from " + url + "\n" + text);
            }

            return om.readTree(text);
        } catch (IOException e) {
            throw new RuntimeException("요청 실패: " + url + " (" + e.getMessage() + ")", e);
        }
    }

    private static String readAll(InputStream in) throws IOException {
        if (in == null) return "";

        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        byte[] b = new byte[8192];

        int n;
        while ((n = in.read(b)) != -1) {
            buf.write(b, 0, n);
        }

        return new String(buf.toByteArray(), StandardCharsets.UTF_8);
    }

}

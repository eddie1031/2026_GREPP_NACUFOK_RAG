package io.eddie.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class EnvConfiguration {

    private static final Map<String, String> ENV_FILE = loadEnvFile();

    public static String get(String key, String defaultValue) {
        String v = System.getenv(key);

        if (v == null || v.trim().isEmpty()) v = ENV_FILE.get(key);

        return (v == null || v.trim().isEmpty())
                ? defaultValue
                : v.trim()
        ;
    }

    public static String require(String key) {
        String v = get(key, null);

        if (v == null) throw new IllegalStateException(key + " 값이 없습니다. .env 파일을 확인하세요.");

        return v;
    }

    private static Map<String, String> loadEnvFile() {
        Map<String, String> map = new HashMap<>();
        Path path = Paths.get(".env");

        if (!Files.exists(path)) return map;

        try {
            for (String line : Files.readAllLines(path, StandardCharsets.UTF_8)) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#") || !line.contains("=")) continue;
                int i = line.indexOf('=');
                String value = line.substring(i + 1).trim();
                int comment = value.indexOf(" #");
                if (comment >= 0) value = value.substring(0, comment).trim();
                map.put(line.substring(0, i).trim(), value);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return map;
    }

}

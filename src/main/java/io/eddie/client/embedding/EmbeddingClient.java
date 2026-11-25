package io.eddie.client.embedding;

import com.fasterxml.jackson.databind.JsonNode;
import io.eddie.config.EnvConfiguration;
import io.eddie.util.HttpUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public interface EmbeddingClient {

    double[] embed(String target);
    String name();

    static EmbeddingClient create() {
        String provider = EnvConfiguration.get("EMBEDDING_PROVIDER", "ollama").toLowerCase();
        switch (provider) {
            case "ollama":
                return new Ollama();
            case "openai":
                return new OpenAi();
            default:
                throw new IllegalArgumentException();
        }
    }

    class Ollama implements EmbeddingClient {
        private final String url = EnvConfiguration.get("OLLAMA_URL", "http://localhost:11434");
        private final String model = EnvConfiguration.get("OLLAMA_EMBEDDING_MODEL", "bge-m3");

        public double[] embed(String text) {
            Map<String, Object> body = new HashMap<>();
            body.put("model", model);
            body.put("input", text);

            JsonNode res = HttpUtil.postJson(url + "/api/embed", Collections.<String, String>emptyMap(), body);

            return toArray(res.get("embeddings").get(0));
        }

        public String name() {
            return "ollama";
        }

    }

    class OpenAi implements EmbeddingClient {

        private final String apiKey = EnvConfiguration.require("OPENAI_API_KEY");
        private final String model = EnvConfiguration.get("OPENAI_EMBEDDING_MODEL", "text-embedding-3-small");

        public double[] embed(String text) {
            Map<String, Object> body = new HashMap<>();
            body.put("model", model);
            body.put("input", text);

            JsonNode res = HttpUtil.postJson("https://api.openai.com/v1/embeddings",
                    Collections.singletonMap("Authorization", "Bearer " + apiKey), body);

            return toArray(res.get("data").get(0).get("embedding"));

        }

        public String name() {
            return "openai";
        }

    }

    static double[] toArray(JsonNode arr) {
        double[] v = new double[arr.size()];

        for (int i = 0; i < v.length; i++) {
            v[i] = arr.get(i).asDouble();
        }

        return v;
    }

}

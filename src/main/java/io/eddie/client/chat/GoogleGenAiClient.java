package io.eddie.client.chat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.eddie.config.EnvConfiguration;
import io.eddie.util.HttpUtil;

import java.util.Collections;

public class GoogleGenAiClient implements ChatClient {

    private final String apiKey = EnvConfiguration.require("GEMINI_API_KEY");
    private final String model = EnvConfiguration.require("GEMINI_CHAT_MODEL");

    @Override
    public String chat(String systemPrompt, String userPrompt) {
        ObjectNode body = new ObjectMapper().createObjectNode();
        body.putObject("systemInstruction").putArray("parts").addObject().put("text", systemPrompt);

        ObjectNode content = body.putArray("contents").addObject();
        content.put("role", "user");
        content.putArray("parts").addObject().put("text", userPrompt);

        String url = "https://generativelanguage.googleapis.com/v1beta/models/" + model + ":generateContent";
        JsonNode res = HttpUtil.postJson(url, Collections.singletonMap("x-goog-api-key", apiKey), body);

        StringBuilder sb = new StringBuilder();
        for (JsonNode part : res.path("candidates").path(0).path("content").path("parts")) {
            sb.append(part.path("text").asText(""));
        }

        return sb.toString();
    }

    @Override
    public String name() {
        return "gemini/" + model;
    }

}

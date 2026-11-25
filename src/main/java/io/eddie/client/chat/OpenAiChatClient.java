package io.eddie.client.chat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.eddie.config.EnvConfiguration;
import io.eddie.util.HttpUtil;

import java.util.Collections;

public class OpenAiChatClient implements ChatClient {

    private final String apiKey = EnvConfiguration.require("OPENAI_API_KEY");
    private final String model = EnvConfiguration.require("OPENAI_CHAT_MODEL");

    @Override
    public String chat(String systemPrompt, String userPrompt) {
        ObjectNode body = new ObjectMapper().createObjectNode();
        body.put("model", model);

        ArrayNode messages = body.putArray("messages");
        messages.addObject().put("role", "system").put("content", systemPrompt);
        messages.addObject().put("role", "user").put("content", userPrompt);

        JsonNode res = HttpUtil.postJson("https://api.openai.com/v1/chat/completions",
                Collections.singletonMap("Authorization", "Bearer " + apiKey), body);

        return res.get("choices").get(0).get("message").get("content").asText();
    }

    @Override
    public String name() {
        return "openai/" + model;
    }

}

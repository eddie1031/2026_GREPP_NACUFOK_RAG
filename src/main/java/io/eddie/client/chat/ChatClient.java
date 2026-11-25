package io.eddie.client.chat;

import io.eddie.client.Client;

public interface ChatClient extends Client {

    String chat(String systemPrompt, String userPrompt);

}

package io.eddie.a_steps.step5;

import io.eddie.client.chat.ChatClient;
import io.eddie.client.chat.OpenAiChatClient;
import io.eddie.client.embedding.EmbeddingClient;
import io.eddie.client.rag.RagClient;
import io.eddie.dto.VectorResult;

import java.util.List;

public class RagTests {

    public static void main(String[] args) {

        String q = "체크카드를 잃어버렸는데 재발급 수수료와 배송 기간이 어떻게 되나요?";

        ChatClient client = new OpenAiChatClient();
        RagClient ragClient = new RagService(
                EmbeddingClient.create(),
                client
        );

        List<VectorResult> contexts = ragClient.retrieve(q, 3);
        System.out.println("질문: " + q);

        System.out.println("검색 근거 문서 : ");
        for (VectorResult h : contexts) {
            // 검색된 청크마다 유사도 점수와 청크의 첫 줄을 출력
            // 1을 차감해야 눈으로 보기 더 용이(
            System.out.printf("유사도 %.4f | %s%n", 1 - h.getDistance(), h.getContent().split("\n")[0]);
        }

        String answer = ragClient.answer(q, contexts);
        System.out.println("생성된 답변 : " + answer);

    }

}

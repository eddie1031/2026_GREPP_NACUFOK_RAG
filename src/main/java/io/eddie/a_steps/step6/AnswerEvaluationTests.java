package io.eddie.a_steps.step6;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.eddie.a_steps.step5.RagService;
import io.eddie.client.chat.ChatClient;
import io.eddie.client.chat.GoogleGenAiClient;
import io.eddie.client.chat.OpenAiChatClient;
import io.eddie.client.embedding.EmbeddingClient;
import io.eddie.client.rag.RagClient;
import io.eddie.config.constants.RagConstants;
import io.eddie.dto.VectorResult;

import java.io.File;
import java.util.List;

/**
 * 답변 품질을 AI 로 평가하기
 * - data/eval_set.json 의 질문마다 step5 방식으로 답변 생성
 * - 평가용 LLM이 질문/근거/답변/모범답안을 보고 1~5점 채점
 */
public class AnswerEvaluationTests {

    private static final ObjectMapper om = new ObjectMapper();

    public static void main(String[] args) throws Exception {

        ChatClient answerLlm = new OpenAiChatClient();
        ChatClient evaluationLlm = new GoogleGenAiClient();

        RagClient ragService = new RagService(
                EmbeddingClient.create(),
                answerLlm
        );

        JsonNode evalSet = om.readTree(new File("data/eval_set.json"));
        System.out.println("답변 모델: " + answerLlm.name() + " / 평가 모델: " + evaluationLlm.name());

        double total = 0;
        int no = 0;
        for (JsonNode item : evalSet) {
            String question = item.get("question").asText();
            String reference = item.get("reference").asText();

            List<VectorResult> contexts = ragService.retrieve(question, 3);
            String answer = ragService.answer(question, contexts);

            String judgeInput = ragService.buildUserPrompt(question, contexts)
                    + "모범 답안 -- " + reference
                    + "평가할 답변 --" + answer;
            JsonNode score = parseJson(evaluationLlm.chat(RagConstants.EVALUATION_PROMPT, judgeInput));

            int faithfulness = score.path("faithfulness").asInt();
            int relevance = score.path("relevance").asInt();
            int correctness = score.path("correctness").asInt();
            double avg = (faithfulness + relevance + correctness) / 3.0;
            total += avg;
            no++;

            System.out.println("Q" + no);
            System.out.println("질문 : " + question);
            System.out.println("답변 : " + answer.trim());
            System.out.printf ("점수 : 근거충실 %d / 관련성 %d / 정확성 %d %n", faithfulness, relevance, correctness);
            System.out.println("이유 : " + score.path("reason").asText());
        }
        System.out.printf("전체 평균 점수: %.2f / 5.00", total / no);
    }

    /**
     * LLM 이 ```json ... ``` 으로 감싸서 줄 수 있기 때문에 { ~ } 부분만 꺼내 파싱
     **/
    static JsonNode parseJson(String text) throws Exception {
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        return om.readTree(text.substring(start, end + 1));
    }

}

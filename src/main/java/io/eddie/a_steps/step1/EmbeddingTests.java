package io.eddie.a_steps.step1;

import io.eddie.client.embedding.EmbeddingClient;

import java.util.Arrays;

public class EmbeddingTests {

    public static void main(String[] args) {

        String word = "신협중앙회";
        String text = "정기예금을 중도에 해지하면 이자는 어떻게 되나요?";

        EmbeddingClient model = EmbeddingClient.create();

        // 단어 임베딩
        printEmbeddingResult(model, word);

        // 문장 임베딩
        printEmbeddingResult(model, text);

    }

    static void printEmbeddingResult(EmbeddingClient model, String target) {

        double[] wordVector = model.embed(target);

        System.out.println("공급자 : " + model.name());
        System.out.println("입력값 : " + target);
        System.out.println("벡터 차원 : " + wordVector.length);
        System.out.println("벡터 앞 10개 값 : " + Arrays.toString(Arrays.copyOf(wordVector, 10)));

    }

}

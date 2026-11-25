package io.eddie.a_steps.step2;

import io.eddie.client.embedding.EmbeddingClient;
import io.eddie.config.constants.RagConstants;

public class SimilaritySearchTests {

    public static void main(String[] args) {

        String q = "카드를 잃어버렸는데 어떻게 하죠?";

        EmbeddingClient model = EmbeddingClient.create();

        double[] vectorQ = model.embed(q);
        System.out.println("질문 : " + q);

        System.out.println("유사도 측정 결과 ===");
        for ( String s : RagConstants.SENTENCES ) {
            double[] vectorS = model.embed(s);

            System.out.println("문장: " + s);
            System.out.println("q와의 코사인 유사도 : " + VectorMath.cosineSimilarity(vectorQ, vectorS));
            System.out.println("q와의 유클리디안 거리 : " + VectorMath.euclideanDistance(vectorQ, vectorS));
        }

    }

}

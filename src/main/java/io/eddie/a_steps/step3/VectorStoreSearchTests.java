package io.eddie.a_steps.step3;

import io.eddie.client.embedding.EmbeddingClient;
import io.eddie.config.EnvConfiguration;
import io.eddie.config.constants.RagConstants;
import io.eddie.config.constants.VectorStoreOperator;
import io.eddie.dao.VectorStore;
import io.eddie.dto.VectorResult;

import java.util.Arrays;
import java.util.List;

// pgvector 컨테이너가 구동되어야만 합니다.
public class VectorStoreSearchTests {

    public static void main(String[] args) {

        String query = "카드를 잃어버렸어요 어떻게 해야해요?";

        EmbeddingClient model = EmbeddingClient.create();
        VectorStore vectorStore = new VectorStore(
                EnvConfiguration.get("PG_URL", null),
                EnvConfiguration.get("PG_USER", null),
                EnvConfiguration.get("PG_PASSWORD", null),
                RagConstants.SEPARATOR + "step3"
        );

        // 임베딩
        double[] vector = model.embed(RagConstants.SENTENCES[0]);
        // 임베딩 모델 차원에 맞는 테이블을 재생성
        vectorStore.recreate(vector.length);

        for ( String s : RagConstants.SENTENCES ) {
            vectorStore.insert(s, model.embed(s));
        }

        // 거리비교
        double[] queryVector = model.embed(query);
        print("코사인", vectorStore.search(queryVector, 3, VectorStoreOperator.COSINE));
        print("유클리디안",vectorStore.search(queryVector, 3, VectorStoreOperator.EUCLIDEAN));

        System.out.println("질문 벡터");
        System.out.println(Arrays.toString(queryVector));

    }

    static void print(String title, List<VectorResult> results) {
        System.out.println("[" + title + "] Top " + results.size());
        for (VectorResult h : results) {
            System.out.printf("거리 %.4f | %s%n", h.getDistance(), h.getContent());
        }
    }

}

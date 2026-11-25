package io.eddie.a_steps.step4;

import io.eddie.client.embedding.EmbeddingClient;
import io.eddie.config.EnvConfiguration;
import io.eddie.config.constants.RagConstants;
import io.eddie.config.constants.VectorStoreOperator;
import io.eddie.dao.VectorStore;
import io.eddie.dto.VectorResult;
import io.eddie.util.ChunkUtil;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ChunkingTests {

    public static void main(String[] args) throws Exception {
        String doc = new String(Files.readAllBytes(Paths.get("data/bank_qna.md")), StandardCharsets.UTF_8);
        System.out.println("원본 문서 길이: " + doc.length() + "글자");

        // 청킹 전략 비교
        List<String> sizeChunks = ChunkUtil.bySize(doc, 300, 50);
        List<String> headingChunks = ChunkUtil.byHeading(doc, 800);
        System.out.println("1. 고정길이(300자, 겹침 50자) : " + sizeChunks.size() + "개" + "\n");
        System.out.println("2. 제목(##) 단위 : " + headingChunks.size() + "개"+ "\n");
        System.out.println("1의 2번째 청크 예시, 문장이 중간에 잘릴 수 있음 : " + sizeChunks.get(1)+ "\n");
        System.out.println("2의 1번째 청크 예시, 질문/답변 한 세트가 그대로 : " + headingChunks.get(0)+ "\n");

        EmbeddingClient embedder = EmbeddingClient.create();
        VectorStore store = new VectorStore(
                EnvConfiguration.get("PG_URL", null),
                EnvConfiguration.get("PG_USER", null),
                EnvConfiguration.get("PG_PASSWORD", null),
                RagConstants.SEPARATOR + "step4"
        );
        store.recreate(embedder.embed("차원 확인용").length);

        for (String headingChunk : headingChunks) {
            store.insert(headingChunk, embedder.embed(headingChunk));
        }

        // 검색 테스트
        String query = "카드 재발급 수수료가 얼마예요?";
        System.out.println("검색 질문: " + query);
        for (VectorResult h : store.search(embedder.embed(query), 3, VectorStoreOperator.COSINE)) {
            String title = h.getContent().split("\n")[0];
            System.out.printf("유사도 %.4f | %s%n", 1 - h.getDistance(), title);
        }
        // ## Q5. 체크카드를 잃어버렸어요. 어떻게 해야 하나요? 문서에 재발급 수수료 내용이 포함되어있습니다.
    }

}

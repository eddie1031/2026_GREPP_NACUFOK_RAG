package io.eddie.a_steps.step5;

import io.eddie.client.chat.ChatClient;
import io.eddie.client.embedding.EmbeddingClient;
import io.eddie.client.rag.RagClient;
import io.eddie.config.EnvConfiguration;
import io.eddie.config.constants.RagConstants;
import io.eddie.config.constants.VectorStoreOperator;
import io.eddie.dao.VectorStore;
import io.eddie.dto.VectorResult;

import java.util.Collections;
import java.util.List;

public class RagService implements RagClient {

    private final EmbeddingClient embedding;
    private final VectorStore store;
    private final ChatClient llm;

    public RagService(EmbeddingClient embedding, ChatClient llm) {
        this.embedding = embedding;
        this.store = new VectorStore(
                EnvConfiguration.get("PG_URL", null),
                EnvConfiguration.get("PG_USER", null),
                EnvConfiguration.get("PG_PASSWORD", null),
                RagConstants.SEPARATOR + "step4"
        );
        this.llm = llm;
    }

    /**
     * 질문을 임베딩해서 가장 가까운 청크 topK 개
     **/
    @Override
    public List<VectorResult> retrieve(String query, int topK) {
        return store.search(embedding.embed(query), topK, VectorStoreOperator.COSINE);
    }

    /**
     * 문서 검색으로 문맥 확보 후 "새로운 컨텐츠 생성"
     */
    @Override
    public String answer(String query, List<VectorResult> context) {
        return llm.chat(RagConstants.SYSTEM_PROMPT, buildUserPrompt(query, context));
    }
}

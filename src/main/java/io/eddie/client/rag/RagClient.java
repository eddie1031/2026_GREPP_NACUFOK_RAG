package io.eddie.client.rag;

import io.eddie.config.constants.RagConstants;
import io.eddie.dao.VectorStore;
import io.eddie.dto.VectorResult;

import java.util.List;

public interface RagClient {

    List<VectorResult> retrieve(String query, int topK);
    List<VectorResult> answer(String query, List<VectorResult> context);

    default String buildUserPrompt(String query, List<VectorResult> contexts) {
        StringBuilder sb = new StringBuilder("[참고 문서]\n");
        for (VectorResult h : contexts) {
            sb.append("---\n").append(h.getContent()).append("\n");
        }
        sb.append("---\n\n[질문]\n").append(query);
        return sb.toString();
    }

}

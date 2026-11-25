package io.eddie.config.constants;

public abstract class RagConstants {

    public static final String SYSTEM_PROMPT = "당신은 프로그래머스은행 고객상담 AI입니다.\n"
            + "반드시 [참고 문서]에 있는 내용만 근거로 한국어로 답하세요.\n"
            + "금액은 원화(KRW)로 '1,000,000원'처럼 표기하세요.\n"
            + "문서에 근거가 없으면 '제공된 문서에서 확인할 수 없습니다.'라고 답하세요.";

    public static final String EVALUATION_PROMPT = "당신은 은행 상담 답변의 품질을 평가하는 엄격한 평가자입니다.\n"
            + "아래 기준으로 각각 1~5점(정수)을 매기세요.\n"
            + "- faithfulness : 답변이 [참고 문서] 내용에만 근거하는가 (지어낸 내용이 있으면 감점)\n"
            + "- relevance    : 질문에 직접적으로 답하는가\n"
            + "- correctness  : [모범 답안]과 사실(금액, 비율, 기간)이 일치하는가\n"
            + "반드시 아래 JSON 형식으로만 출력하세요. 다른 말은 쓰지 마세요.\n"
            + "{\"faithfulness\": 5, \"relevance\": 5, \"correctness\": 5, \"reason\": \"한 문장 평가 이유\"}";

    public static final String SEPARATOR = "CU_";

}

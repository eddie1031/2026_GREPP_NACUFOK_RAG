package io.eddie.util;

import java.util.ArrayList;
import java.util.List;

public class ChunkUtil {

    /**
     * 오버랩이 적용된 고정길이 청킹
     * 단순하지만 문장/문단 중간이 잘릴 수 있다.
     *
     * @param text {@code String} 청킹 대상 문자열
     * @param size {@code int} 청킹 사이즈
     * @param overlap {@code int} 오버랩 사이즈 예) overlap=50 -> [0~200], [150~350], [300~500] ...
     * @return {@link List<String>} 청킹된 문자열 배열
     */
    public static List<String> bySize(String text, int size, int overlap) {
        List<String> chunks = new ArrayList<>();
        int step = size - overlap;
        for (int start = 0; start < text.length(); start += step) {
            int end = Math.min(start + size, text.length());
            chunks.add(text.substring(start, end).trim());
            if (end == text.length()) break;
        }
        return chunks;
    }

    /**
     * 전략 2) 제목(## ) 단위로 자르기
     * 문서 구조(질문-답변 1세트)를 그대로 살리므로 QnA 문서에 잘 맞는다.
     * maxChars 를 넘는 섹션은 전략 1로 한 번 더 자른다.
     */

    /**
     * 특정한 기준으로 청킹
     * 마크다운 h2(`##`)을 기준으로 자르기
     * 문서 구조(질문-답변 1세트)를 그대로 살리므로 QnA 문서에 잘 맞는다.
     *
     * @param text {@code String} 청킹 대상 문자열
     * @param maxChars {@code int} 청크 하나에 들어갈 수 있는 최대 글자 수
     * @return {@link List<String>} 청킹된 문자열 배열
     */
    public static List<String> byHeading(String text, int maxChars) {
        List<String> chunks = new ArrayList<>();
        for (String section : text.split("(?m)^(?=## )")) {
            section = section.trim();
            if (section.isEmpty() || !section.startsWith("## ")) continue; // 머리말은 제외
            if (section.length() <= maxChars) {
                chunks.add(section);
            } else {
                chunks.addAll(bySize(section, maxChars, maxChars / 5));
            }
        }
        return chunks;
    }
}

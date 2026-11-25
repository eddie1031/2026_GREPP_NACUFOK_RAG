package io.eddie.a_steps.step2;

public class VectorMath {

    /**
     * 코사인 유사도를 구하는 메서드(예시)
     * 두 벡터의 "방향"이 얼마나 같은가? 1에 가까울수록 유사 (-1 ~ 1)
     */
    public static double cosineSimilarity(double[] a, double[] b) {
        double dot = 0, normA = 0, normB = 0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    /**
     * 유클리디안 거리를 구하는 메서드(예시)
     * 두 점 사이의 직선 거리는 얼마인가? 0에 가까울수록 유사
     */
    public static double euclideanDistance(double[] a, double[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            double d = a[i] - b[i];
            sum += d * d;
        }
        return Math.sqrt(sum);
    }


}

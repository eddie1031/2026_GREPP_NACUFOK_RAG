package io.eddie.dto;

public class VectorResult {

    private final long id;
    private final String content;
    private final double distance;

    public VectorResult(long id, String content, double distance) {
        this.id = id;
        this.content = content;
        this.distance = distance;
    }

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public double getDistance() {
        return distance;
    }

}

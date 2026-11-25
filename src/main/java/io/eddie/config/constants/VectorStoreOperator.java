package io.eddie.config.constants;

public enum VectorStoreOperator {

    COSINE("<=>"),
    EUCLIDEAN("<->");

    private final String operator;

    VectorStoreOperator(String operator) {
        this.operator = operator;
    }

    public String getOperator() {
        return operator;
    }

}

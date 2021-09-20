package ru.gateway.domain;

public class IdsRequirement {
    public IdsRequirement(String sequenceId, int requiredIdsCount) {
        this.sequenceId = sequenceId;
        this.requiredIdsCount = requiredIdsCount;
    }

    private final String sequenceId;
    private final int requiredIdsCount;

    public String getSequenceId() {
        return sequenceId;
    }

    public int getRequiredIdsCount() {
        return requiredIdsCount;
    }

    @Override
    public String toString() {
        return "IdsRequirement{" +
                "sequenceId='" + sequenceId + '\'' +
                ", requiredIdsCount=" + requiredIdsCount +
                '}';
    }
}

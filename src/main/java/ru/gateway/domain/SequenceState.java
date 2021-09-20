package ru.gateway.domain;

public class SequenceState {
    public SequenceState(String sequenceId, UsageStatistic hits, UsageStatistic misses) {
        this.sequenceId = sequenceId;
        this.hits = hits;
        this.misses = misses;
    }

    private final String sequenceId;
    private final UsageStatistic hits;
    private final UsageStatistic misses;

    public String getSequenceId() {
        return sequenceId;
    }

    public UsageStatistic getHits() {
        return hits;
    }

    public UsageStatistic getMisses() {
        return misses;
    }

    @Override
    public String toString() {
        return "SequenceState{" +
                "sequenceId='" + sequenceId + '\'' +
                ", hits=" + hits +
                ", misses=" + misses +
                '}';
    }
}

package ru.gateway;

import ru.gateway.domain.SequenceState;

import java.util.Set;

public interface SequenceStateStorage {
    SequenceState getSequenceState(String sequenceId);

    void storeSequenceState(SequenceState sequenceState);

    int getAvailableIdsCount(String sequenceId);

    void addNewAvailableIds(String sequenceId, Set<String> ids);

    Set<String> getNextId(String sequenceId);

}

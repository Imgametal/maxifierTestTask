package ru.gateway.adapters.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import ru.gateway.SequenceStateStorage;
import ru.gateway.domain.SequenceState;
import ru.gateway.domain.UsageStatistic;

import java.util.*;

public class SequenceStateStorageImpl implements SequenceStateStorage {

    private Map<String, SequenceState> sequenceStateMap = getSequenceStateCache();

    private Map<String, Queue<String>> availableIdsMap = getAvailableIdsCache();


    @Override
    public SequenceState getSequenceState(String sequenceId) {
        return sequenceStateMap.computeIfAbsent(sequenceId, s -> getEmptySequenceState(sequenceId));
    }

    private SequenceState getEmptySequenceState(String sequenceId) {

        return new SequenceState(sequenceId, UsageStatistic.createEmpty(), UsageStatistic.createEmpty());
    }

    @Override
    public void storeSequenceState(SequenceState sequenceState) {
        sequenceStateMap.put(sequenceState.getSequenceId(), sequenceState);
    }

    @Override
    public int getAvailableIdsCount(String sequenceId) {

        return availableIdsMap.computeIfAbsent(sequenceId, s -> new LinkedList<>()).size();
    }

    @Override
    public void addNewAvailableIds(String sequenceId, Set<String> ids) {

        availableIdsMap.computeIfAbsent(sequenceId, s -> new LinkedList<>())
                .addAll(ids);
    }

    @Override
    public Set<String> getNextId(String sequenceId) {
        Queue<String> availableIds = availableIdsMap.computeIfAbsent(sequenceId, s -> new LinkedList<>());
        String id = availableIds.poll();
        return id == null ? Collections.emptySet() : Collections.singleton(id);

    }

    private Map<String, Queue<String>> getAvailableIdsCache() {

        Cache<String, Queue<String>> cache = CacheBuilder.newBuilder()
                .softValues()
                .build();
        return cache.asMap();

    }

    private Map<String, SequenceState> getSequenceStateCache() {
        Cache<String, SequenceState> cache = CacheBuilder.newBuilder()
                .softValues()
                .build();
        return cache.asMap();
    }
}

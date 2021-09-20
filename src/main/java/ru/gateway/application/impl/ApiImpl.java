package ru.gateway.application.impl;

import ru.gateway.SequenceStateStorage;
import ru.gateway.application.CacheMaintainer;
import ru.gateway.application.IdIsNotAvailableException;
import ru.gateway.application.api.GatewayApi;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ApiImpl implements GatewayApi {
    public ApiImpl(SequenceStateStorage sequenceStateStorage, CacheMaintainer cacheMaintainer) {
        this.sequenceStateStorage = sequenceStateStorage;
        this.cacheMaintainer = cacheMaintainer;
    }

    private final SequenceStateStorage sequenceStateStorage;
    private final CacheMaintainer cacheMaintainer;

    private final Map<String, Object> locks = new ConcurrentHashMap<>();

    @Override
    public String getId(String sequenceId) {

        synchronized (locks.computeIfAbsent(sequenceId, s -> new Object())) {
            String id;
            supportCacheState(sequenceId);
            final Set<String> idSet = sequenceStateStorage.getNextId(sequenceId);

            if (idSet.isEmpty()) {
                throw new IdIsNotAvailableException("SequenceId: " + sequenceId);
            } else {
                id = idSet.iterator().next();
            }
            locks.remove(sequenceId);
            return id;
        }

    }

    private void supportCacheState(String sequenceId) {
        cacheMaintainer.maintain(sequenceId);
    }
}

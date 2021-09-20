package ru.gateway.application.impl;

import ru.gateway.SequenceStateStorage;
import ru.gateway.application.CacheConfigProvider;
import ru.gateway.application.CacheMaintainer;
import ru.gateway.application.IdsExternalProvider;
import ru.gateway.application.RequirementIdsProvider;
import ru.gateway.domain.IdsRequirement;
import ru.gateway.domain.SequenceState;
import ru.gateway.domain.UsageStatistic;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CacheMaintainerImpl implements CacheMaintainer {
    public CacheMaintainerImpl(SequenceStateStorage sequenceStateStorage, RequirementIdsProvider requirementProvider, IdsExternalProvider idsProvider, CacheConfigProvider configProvider) {
        this.sequenceStateStorage = sequenceStateStorage;
        this.requirementProvider = requirementProvider;
        this.idsProvider = idsProvider;
        this.configProvider = configProvider;
    }

    Logger logger = Logger.getLogger(CacheMaintainerImpl.class.getName());

    private final SequenceStateStorage sequenceStateStorage;
    private final RequirementIdsProvider requirementProvider;
    private final IdsExternalProvider idsProvider;
    private final CacheConfigProvider configProvider;


    @Override
    public void maintain(String sequenceId) {
        final IdsRequirement requirement = defineRequirementIds(sequenceId);

        //todo  можно сделать пул потоков, который будет разгребать приоритезированую очередь таких надобностей
        if (requirement.getRequiredIdsCount() > 0) {
            enrichCache(requirement);
        }

    }

    private IdsRequirement defineRequirementIds(String sequenceId) {
        final SequenceState sequenceState = sequenceStateStorage.getSequenceState(sequenceId);
        int availableIdsCount = sequenceStateStorage.getAvailableIdsCount(sequenceId);
        final SequenceState newSequenceState = recordStatistic(sequenceState, availableIdsCount);

        return requirementProvider.getCurrentRequirement(newSequenceState, availableIdsCount);
    }

    private void enrichCache(IdsRequirement requirement) {
        final Set<String> ids;
        try {
            ids = new HashSet<>(idsProvider.getIds(requirement));
        } catch (RuntimeException e) {
            logger.log(Level.FINE, "Cannot get requirement ids," + requirement + " Cause: ", e);
            return;
        }
        sequenceStateStorage.addNewAvailableIds(requirement.getSequenceId(), ids);
    }

    private SequenceState recordStatistic(SequenceState sequenceState, int availableIdsCount) {
        // todo   можно использовать прокси для сбора статистики
        if (availableIdsCount > 0) {
            return registerCacheHit(sequenceState);
        } else {
            return registerCacheMiss(sequenceState);
        }
    }

    private SequenceState registerCacheMiss(SequenceState sequenceState) {
        int newMissCount = sequenceState.getMisses().getCount() + 1;
        Date date = sequenceState.getMisses().getDiscardDate();
        boolean needDiscardDate = isNeedDiscardDate(date, configProvider.getStoreMissTimeoutSeconds());
        Date actualDate = needDiscardDate ? new Date() : date;
        UsageStatistic misses = new UsageStatistic(newMissCount, actualDate);

        final SequenceState newSequenceState = new SequenceState(sequenceState.getSequenceId(), sequenceState.getHits(), misses);
        sequenceStateStorage.storeSequenceState(newSequenceState);
        return newSequenceState;
    }

    private SequenceState registerCacheHit(SequenceState sequenceState) {
        // TODO: 18/09/2021 Хранить выборку за последние время, а не тупо сбрасывать дату, как для промахов
        int newHintsCount = sequenceState.getHits().getCount() + 1;
        Date date = sequenceState.getHits().getDiscardDate();
        boolean needDiscardDate = isNeedDiscardDate(date, configProvider.getStoreHitsTimeSeconds());
        Date actualDate = needDiscardDate ? new Date() : date;
        UsageStatistic hits = new UsageStatistic(newHintsCount, actualDate);

        final SequenceState newSequenceState = new SequenceState(sequenceState.getSequenceId(), hits, sequenceState.getMisses());
        sequenceStateStorage.storeSequenceState(newSequenceState);
        return newSequenceState;
    }

    private boolean isNeedDiscardDate(Date date, long offset) {
        return date.toInstant().getEpochSecond() + offset < new Date().toInstant().getEpochSecond();
    }
}

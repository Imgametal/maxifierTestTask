package ru.gateway.application.impl;

import ru.gateway.application.CacheConfigProvider;
import ru.gateway.application.RequirementIdsProvider;
import ru.gateway.domain.IdsRequirement;
import ru.gateway.domain.SequenceState;

import java.util.Collections;
import java.util.List;

public class RequirementAnalyzerImpl implements RequirementIdsProvider {


    public RequirementAnalyzerImpl(CacheConfigProvider configProvider) {
        this.configProvider = configProvider;
    }

    private final CacheConfigProvider configProvider;

    @Override
    public IdsRequirement getCurrentRequirement(SequenceState sequenceState, int availableIdsCount) {
        final boolean isNewIdsNeeded = !isIdsAvailable(availableIdsCount) ||
                isMinReservedThresholdOvercome(sequenceState, availableIdsCount);
        if (isNewIdsNeeded) {
            return calcNeededRequirement(sequenceState);
        } else {
            return createNoNeededRequirement(sequenceState.getSequenceId());
        }
    }

    private IdsRequirement calcNeededRequirement(SequenceState sequenceState) {
        int waitingIdsCount = sequenceState.getMisses().getCount();
        int hitsCount = sequenceState.getHits().getCount();
        int neededIdsCount = Collections.max(List.of(waitingIdsCount, hitsCount, configProvider.getStartShiftIdsCount()));
        int requirementIdsCount = (int) (neededIdsCount * configProvider.getFutureReservation());

        return new IdsRequirement(sequenceState.getSequenceId(), requirementIdsCount);

    }

    private IdsRequirement createNoNeededRequirement(String sequenceId) {
        return new IdsRequirement(sequenceId, 0);
    }

    private boolean isIdsAvailable(int availableIds) {
        return availableIds > 0;
    }

    private boolean isMinReservedThresholdOvercome(SequenceState sequenceState, int availableIdsCount) {
        return availableIdsCount < configProvider.getReserveThreshold() * sequenceState.getHits().getCount();
    }
}

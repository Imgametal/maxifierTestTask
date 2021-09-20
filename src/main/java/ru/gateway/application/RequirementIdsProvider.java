package ru.gateway.application;

import ru.gateway.domain.IdsRequirement;
import ru.gateway.domain.SequenceState;

public interface RequirementIdsProvider {


    IdsRequirement getCurrentRequirement(SequenceState sequenceState, int availableIdsCount);
}

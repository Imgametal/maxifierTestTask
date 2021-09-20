package ru.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.AssertionErrors;
import ru.gateway.application.RequirementIdsProvider;
import ru.gateway.domain.IdsRequirement;
import ru.gateway.domain.SequenceState;
import ru.gateway.domain.UsageStatistic;

import java.util.Date;
import java.util.UUID;


@SpringBootTest
public class RequirementProviderTest {

    @Autowired
    private RequirementIdsProvider requirementProvider;


    @Test
    void getNoRequiredIdsData() {
        UsageStatistic hits = new UsageStatistic(Integer.MAX_VALUE, new Date());
        UsageStatistic misses = new UsageStatistic(Integer.MAX_VALUE, new Date());

        String sequenceId = UUID.randomUUID().toString();
        SequenceState sequenceState = new SequenceState(sequenceId, hits, misses);
        IdsRequirement requirement = requirementProvider.getCurrentRequirement(sequenceState, Integer.MAX_VALUE);
        AssertionErrors.assertEquals("seq id: ", sequenceId, requirement.getSequenceId());
        AssertionErrors.assertEquals("seq requirement: ", 0, requirement.getRequiredIdsCount());


    }
}

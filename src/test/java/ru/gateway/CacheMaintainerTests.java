package ru.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import ru.gateway.application.CacheMaintainer;
import ru.gateway.application.IdsExternalProvider;
import ru.gateway.application.RequirementIdsProvider;
import ru.gateway.domain.IdsRequirement;
import ru.gateway.domain.SequenceState;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@SpringBootTest
@Import(IdsProviderTestImpl.class)
public class CacheMaintainerTests {
    @Autowired
    CacheMaintainer cacheMaintainer;

    @SpyBean
    SequenceStateStorage sequenceStateStorage;

    @SpyBean
    IdsExternalProvider idsExternalProvider;
    @SpyBean
    RequirementIdsProvider requirementIdsProvider;


    @Test
    void cacheMissPositiveScenario() {
        String sequenceId = UUID.randomUUID().toString();
        cacheMaintainer.maintain(sequenceId);
        SequenceState sequenceState = sequenceStateStorage.getSequenceState(sequenceId);
        sequenceStateStorage.getAvailableIdsCount(sequenceId);

        assertEquals("registered one cache miss", 1, sequenceState.getMisses().getCount());
        assertEquals("sequence id is set", sequenceId, sequenceState.getSequenceId());


    }

    @Test
    void getIdFromExternalProvider() {
        String sequenceId = UUID.randomUUID().toString();
        cacheMaintainer.maintain(sequenceId);
        sequenceStateStorage.getAvailableIdsCount(sequenceId);
        verify(idsExternalProvider).getIds(any());

    }

    @Test
    void whenIdAlreadyStored() {
        String sequenceId = UUID.randomUUID().toString();
        IdsRequirement requirement = new IdsRequirement(sequenceId, 0);
        doReturn(requirement).
                when(requirementIdsProvider).getCurrentRequirement(any(SequenceState.class), anyInt());
        cacheMaintainer.maintain(sequenceId);
        verify(idsExternalProvider, never()).getIds(any(IdsRequirement.class));

    }

}

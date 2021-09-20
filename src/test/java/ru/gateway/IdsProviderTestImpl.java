package ru.gateway;

import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Primary;
import ru.gateway.application.IdsExternalProvider;
import ru.gateway.domain.IdsRequirement;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@TestComponent
@Primary
public class IdsProviderTestImpl implements IdsExternalProvider {

    Lock lock = new ReentrantLock();

    @Override
    public Set<String> getIds(IdsRequirement requested) {
        if (lock.tryLock()) {
            Set<String> ids = new HashSet<>();
            for (int i = 0; i < requested.getRequiredIdsCount(); i++) {
                ids.add(requested.getSequenceId() + ":" + UUID.randomUUID().toString());
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lock.unlock();
            return ids;
        } else {
            throw new RuntimeException("try again");

        }

    }
}

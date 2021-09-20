package ru.gateway;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import ru.gateway.application.api.GatewayApi;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static org.springframework.test.util.AssertionErrors.assertEquals;


@SpringBootTest
@Import(IdsProviderTestImpl.class)
class GatewayApiTests {
    @Autowired
    GatewayApi gatewayApi;

    Logger logger = LoggerFactory.getLogger(GatewayApiTests.class);


    @Test
    public void smokeTest() {
        gatewayApi.getId("smoke-test-sequence");
    }


    @Test
    void basePositiveScenarioMultiThread() throws InterruptedException {
        int testTimeLimitAdditionalTimeSeconds = 60;
        int totalThreadCount = 50;
        int sequencesCount = 5;
        int requestCountPerSequence = 100;
        ExecutorService executor = Executors.newFixedThreadPool(totalThreadCount * sequencesCount);
        List<Callable<String>> callableList = new ArrayList<>();
        Map<String, String> resultMap = new ConcurrentHashMap<>();

        final String BASE_SEQUENCE_NAME = "test-sequence";
        for (int seqNumber = 0; seqNumber < sequencesCount; seqNumber++) {

            for (int taskNumber = 0; taskNumber < requestCountPerSequence; taskNumber++) {
                int seqTaskNumber = taskNumber;
                int finalSeqNumber = seqNumber;
                callableList.add(() -> {
                    callApiWithReties(resultMap, BASE_SEQUENCE_NAME + finalSeqNumber, seqTaskNumber);
                    return null;
                });
            }
        }


        executor.invokeAll(callableList, sequencesCount + testTimeLimitAdditionalTimeSeconds, TimeUnit.SECONDS);

        int totalIdsReceivedExpect = sequencesCount * requestCountPerSequence;
        assertEquals("ids received", totalIdsReceivedExpect, resultMap.keySet().size());
        int distinctCount = (int) resultMap.values().stream().distinct().count();


        assertEquals("unique ids received", totalIdsReceivedExpect, distinctCount);

        Map<String, String> errors = new HashMap<>();

        resultMap.keySet().forEach(key -> {
            if (!resultMap.get(key).startsWith(key.split(":")[0])) {
                errors.put(key, resultMap.get(key));
            }
        });
        logger.info("consistent errors: " + errors);
        assertEquals("consistent errors count:", 0, errors.size());
        logger.info("total ids:" + totalIdsReceivedExpect);
        resultMap.values().stream().filter(s -> Collections.frequency(resultMap.values(), s) > 1).collect(Collectors.toSet())
                .forEach(s -> logger.info("non-unique: " + s));


    }

    private void callApiWithReties(Map<String, String> resultMap, String sequenceName, int taskNumber) throws InterruptedException {
        final String result;
        int retryTimeout = 200;
        try {
            result = gatewayApi.getId(sequenceName);
        } catch (RuntimeException e) {
            Thread.sleep(retryTimeout);
            callApiWithReties(resultMap, sequenceName, taskNumber);
            return;
        }

        resultMap.put(sequenceName + ":" + taskNumber, result);

    }


}

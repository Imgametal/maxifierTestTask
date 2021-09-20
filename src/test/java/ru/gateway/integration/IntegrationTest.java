package ru.gateway.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Repeat;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.util.UriComponentsBuilder;
import ru.gateway.SpringRunner;
import ru.gateway.application.CacheConfigProvider;
import ru.gateway.application.api.GatewayApi;
import ru.gateway.controller.GatewayControllerImpl;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.springframework.test.util.AssertionErrors.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class IntegrationTest {

    @Autowired
    private GatewayApi gatewayApi;

    @Value("${cache.external.id.provide.base.url}")
    private String baseUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MockMvc mvc;


    @Autowired
    private CacheConfigProvider configProvider;

    private MockRestServiceServer mockServer;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${server.port}")
    private int serverPort;

    @BeforeEach
    void createServer() {
        mockServer = MockRestServiceServer.createServer(restTemplate);

    }



    @Test
    @Repeat(5)
    void positiveWarmUpBaseScenario() throws Exception {
        String sequenceId = UUID.randomUUID().toString();
        int expectedIdsRequestCount = (int) (configProvider.getStartShiftIdsCount() * configProvider.getFutureReservation());
        URI externalEndpoint = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .pathSegment(sequenceId,
                        String.valueOf(expectedIdsRequestCount))
                .build().toUri();

        URI gatewayEndpoint = UriComponentsBuilder.newInstance()
                .path("/v1/getId/")
                .path(sequenceId)
                .build().toUri();

        Set<String> responseIds = new HashSet<>();
        for (int i = 0; i < expectedIdsRequestCount; i++) {
            responseIds.add(UUID.randomUUID().toString());
        }

        mockServer.expect(ExpectedCount.once(),
                requestTo(externalEndpoint))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(responseIds)));

        String obtainedIdFromCache = mvc.perform(post(gatewayEndpoint.toString()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn().getResponse().getContentAsString();

        assertTrue("response list from external server must contain obtained id from gateway"
                , responseIds.contains(obtainedIdFromCache));
        mockServer.verify();
    }
}

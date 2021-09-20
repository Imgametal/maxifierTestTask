package ru.gateway.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.gateway.application.api.GatewayApi;

@RestController
public class GatewayControllerImpl implements GatewayApiController {

    public GatewayControllerImpl(GatewayApi gatewayApi) {
        this.gatewayApi = gatewayApi;
    }

    private final GatewayApi gatewayApi;

    @Override
    @PostMapping(value = "v1/getId/{sequenceId}", consumes = MediaType.ALL_VALUE)

    public String getId(@PathVariable String sequenceId) {

        return gatewayApi.getId(sequenceId);
        //todo сделать обработку ошибок, когда нет свободных id на данный момент, и настоящих вероятных ошибок
    }
}

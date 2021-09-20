package ru.gateway.adapters.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.gateway.application.IdsExternalProvider;
import ru.gateway.domain.IdsRequirement;

import java.net.URI;
import java.util.Set;

public class IdsProviderImpl implements IdsExternalProvider {
    public IdsProviderImpl(
            RestTemplate restTemplate,
            @Value("${cache.external.id.provide.base.url}") String baseUrl) {

        this.baseUrl = baseUrl;
        this.restTemplate = restTemplate;
    }

    private final RestTemplate restTemplate;
    private final String baseUrl;

    @Override
    public Set<String> getIds(IdsRequirement requested) {
        URI endpoint = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .pathSegment(requested.getSequenceId(), String.valueOf(requested.getRequiredIdsCount()))
                .build().toUri();
        return restTemplate.exchange(endpoint, HttpMethod.POST, null, Set.class).getBody();
    }


}

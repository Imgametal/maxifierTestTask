package ru.gateway.application;

import ru.gateway.domain.IdsRequirement;

import java.util.Set;

public interface IdsExternalProvider {
    Set<String> getIds(IdsRequirement requested);
}

package ru.gateway.adapters.impl;

import org.springframework.beans.factory.annotation.Value;
import ru.gateway.application.CacheConfigProvider;

public class ConfigProviderImpl implements CacheConfigProvider {

    @Value("${cache.storeHitsTimeSeconds}")
    private long storeHitsTimeSeconds;

    @Value("${cache.storeMissTimeoutSeconds}")
    private long storeMissTimeoutSeconds;

    @Value("${cache.futureReservation}")
    private double futureReservation;

    @Value("${cache.reserveThreshold}")
    private double reserveThreshold;

    @Value("${cache.startShiftIdsCount}")
    private int startShiftIdsCount;

    @Override
    public long getStoreHitsTimeSeconds() {
        return storeHitsTimeSeconds;
    }

    @Override
    public long getStoreMissTimeoutSeconds() {
        return storeMissTimeoutSeconds;
    }

    @Override
    public double getFutureReservation() {
        return futureReservation;
    }

    @Override
    public int getStartShiftIdsCount() {
        return startShiftIdsCount;
    }

    @Override
    public double getReserveThreshold() {
        return reserveThreshold;
    }
}

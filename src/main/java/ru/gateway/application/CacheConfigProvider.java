package ru.gateway.application;

public interface CacheConfigProvider {

    long getStoreHitsTimeSeconds();
    long getStoreMissTimeoutSeconds();
    double getReserveThreshold();
    double getFutureReservation();
    int getStartShiftIdsCount();
}

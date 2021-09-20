package ru.gateway.application;

public interface CacheMaintainer {
    //todo возможно, стоит пересмотреть дизайн
    void maintain(String sequenceId);
}

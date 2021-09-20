package ru.gateway.domain;

import java.util.Date;

public class UsageStatistic {
    public UsageStatistic(int count, Date discardDate) {
        this.count = count;
        this.discardDate = discardDate;
    }

    private final int count;
    private final Date discardDate;

    public int getCount() {
        return count;
    }

    public Date getDiscardDate() {
        return discardDate;
    }

    public static UsageStatistic createEmpty() {
        return new UsageStatistic(0, new Date());
    }

    @Override
    public String toString() {
        return "UsageStatistic{" +
                "count=" + count +
                ", discardDate=" + discardDate +
                '}';
    }
}

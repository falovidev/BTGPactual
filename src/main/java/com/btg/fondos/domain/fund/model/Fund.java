package com.btg.fondos.domain.fund.model;

import java.math.BigDecimal;

public class Fund {

    private final String fundId;
    private final String name;
    private final BigDecimal minimumAmount;
    private final String category;

    public Fund(String fundId, String name, BigDecimal minimumAmount, String category) {
        this.fundId = fundId;
        this.name = name;
        this.minimumAmount = minimumAmount;
        this.category = category;
    }

    //region Getters

    public String getFundId() { return fundId; }
    public String getName() { return name; }
    public BigDecimal getMinimumAmount() { return minimumAmount; }
    public String getCategory() { return category; }

    //endregion
}

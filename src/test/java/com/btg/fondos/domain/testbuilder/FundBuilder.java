package com.btg.fondos.domain.testbuilder;

import com.btg.fondos.domain.fund.model.Fund;

import java.math.BigDecimal;

public class FundBuilder {

    private String fundId = "1";
    private String name = "FPV_BTG_PACTUAL_RECAUDADORA";
    private BigDecimal minimumAmount = new BigDecimal("75000");
    private String category = "FPV";

    public static FundBuilder aFund() {
        return new FundBuilder();
    }

    public FundBuilder withFundId(String fundId) {
        this.fundId = fundId;
        return this;
    }

    public FundBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public FundBuilder withMinimumAmount(BigDecimal minimumAmount) {
        this.minimumAmount = minimumAmount;
        return this;
    }

    public FundBuilder withMinimumAmount(String minimumAmount) {
        this.minimumAmount = new BigDecimal(minimumAmount);
        return this;
    }

    public FundBuilder withCategory(String category) {
        this.category = category;
        return this;
    }

    public Fund build() {
        return new Fund(fundId, name, minimumAmount, category);
    }
}

package com.btg.fondos.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Fund {
    private String fundId;
    private String name;
    private BigDecimal minimumAmount;
    private String category;
}

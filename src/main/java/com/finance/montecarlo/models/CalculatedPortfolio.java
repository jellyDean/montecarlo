package com.finance.montecarlo.models;

import lombok.Data;

@Data
public class CalculatedPortfolio extends Portfolio {
    private String bestCasePerformance;
    private String worstCasePerformance;
    private String median;
}

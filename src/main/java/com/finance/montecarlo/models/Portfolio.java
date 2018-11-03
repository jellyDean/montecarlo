package com.finance.montecarlo.models;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class Portfolio {

    @NotNull(message = "mean is a required field")
    private float mean;

    @NotNull(message = "standardDeviation is a required field")
    private float standardDeviation;

    @NotNull(message = "portfolioName is a required field")
    @NotEmpty(message = "portfolioName cannot be empty")
    private String portfolioName;

}

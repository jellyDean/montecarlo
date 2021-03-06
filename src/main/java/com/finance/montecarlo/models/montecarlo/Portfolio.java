package com.finance.montecarlo.models.montecarlo;

import lombok.Data;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * Class used for specifying the portfolios to run the simulation on
 * response
 *
 * @author  Dean Hutton
 * @version 1.0
 * @since   2018-11-04
 */
@Data
public class Portfolio {

    @NotNull(message = "mean is a required field")
    @DecimalMin(value = ".001", message = "mean must be greater than .001")
    @DecimalMax(value = "100", message = "mean must be less than 100.00")
    private Double mean;

    @NotNull(message = "standardDeviation is a required field")
    private Double standardDeviation;

    @NotNull(message = "portfolioType is a required field")
    @NotEmpty(message = "portfolioType cannot be empty")
    private String portfolioType;

}

package com.finance.montecarlo.models;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Class used for specifying the request
 *
 * @author  Dean Hutton
 * @version 1.0
 * @since   2018-11-04
 */
@Data
public class MonteCarloRequest {

    @Valid
    @NotNull(message = "portfolios is a required field")
    @NotEmpty(message = "portfolios cannot be empty")
    private List<Portfolio> portfolios;
}

package com.finance.montecarlo.models;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class MonteCarloRequest {

    @Valid
    @NotNull(message = "portfolios is a required field")
    @NotEmpty(message = "portfolios cannot be empty")
    private List<Portfolio> portfolios;
}

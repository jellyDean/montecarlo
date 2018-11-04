package com.finance.montecarlo.models;

import com.finance.montecarlo.configuration.MonteCarloConfiguration;
import lombok.Data;

import java.util.List;

@Data
public class MonteCarloResponse extends MonteCarloConfiguration {
    List<CalculatedPortfolio> portfolios;
}

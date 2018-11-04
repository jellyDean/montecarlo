package com.finance.montecarlo.models;

import com.finance.montecarlo.configuration.MonteCarloConfiguration;
import lombok.Data;

import java.util.List;

/**
 * Class used for specifying the response. Extends configuration so that the simulation params are included in the
 * response
 *
 * @author  Dean Hutton
 * @version 1.0
 * @since   2018-11-04
 */
@Data
public class MonteCarloResponse extends MonteCarloConfiguration {
    List<CalculatedPortfolio> portfolios;
}

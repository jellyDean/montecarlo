package com.finance.montecarlo.services;

import com.finance.montecarlo.models.MonteCarloRequest;
import com.finance.montecarlo.models.MonteCarloResponse;
/**
 * Interface of the service. Can be used for easy mocking
 *
 * @author  Dean Hutton
 * @version 1.0
 * @since   2018-11-04
 */
public interface MonteCarloService {

    MonteCarloResponse runSimulation(MonteCarloRequest monteCarloRequest);

}

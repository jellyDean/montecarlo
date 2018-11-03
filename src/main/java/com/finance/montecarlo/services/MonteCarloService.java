package com.finance.montecarlo.services;

import com.finance.montecarlo.configuration.MonteCarloConfiguration;
import com.finance.montecarlo.models.MonteCarloRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MonteCarloService {

    private final MonteCarloConfiguration monteCarloConfiguration;

    @Autowired
    public MonteCarloService(MonteCarloConfiguration monteCarloConfiguration) {
        this.monteCarloConfiguration = monteCarloConfiguration;
    }

    public boolean runSimulation(MonteCarloRequest portfolios){
        return true;
    }
}

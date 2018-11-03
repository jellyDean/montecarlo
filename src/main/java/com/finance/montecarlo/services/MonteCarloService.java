package com.finance.montecarlo.services;

import com.finance.montecarlo.configuration.MonteCarloConfiguration;
import com.finance.montecarlo.models.MonteCarloRequest;
import com.finance.montecarlo.models.Portfolio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;


@Service
public class MonteCarloService {

    private final MonteCarloConfiguration monteCarloConfiguration;
    private Random random;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public MonteCarloService(MonteCarloConfiguration monteCarloConfiguration) {
        this.monteCarloConfiguration = monteCarloConfiguration;
        this.random = new Random();
    }

    private double calculateRandomNumber(double mean, double standardDeviation){
        // https://stackoverflow.com/questions/6011943/java-normal-distribution
        return random.nextGaussian() * standardDeviation + mean;
    }

    public boolean runSimulation(MonteCarloRequest portfolios){

        // get it working with one portfolio then add more...
        Portfolio portfolio = portfolios.getPortfolios().get(0);

        int simulationSize = monteCarloConfiguration.getSimulationSize();
        int numberOfYears = monteCarloConfiguration.getNumberOfYears();
        double inflationRate = monteCarloConfiguration.getInflationRate();
        double initialInvestmentAmount = monteCarloConfiguration.getInitialInvestmentAmount();

        double random;

        // ending zero represents any extra cash funds added. need to do this 20 times and account for inflation
        // each interation use the previous ending balance to calculate the next one. for now one sample used
        // https://www.youtube.com/watch?v=Q5Fw2IRMjPQ
//        double endingBalance = initialInvestmentAmount * (1 + random) + 0;
        double investmentAmount;
        double investment = initialInvestmentAmount;

        for (int i = 0; i < numberOfYears; i++){
            random = calculateRandomNumber(portfolio.getMean(), portfolio.getStandardDeviation());
            investmentAmount = investment * (1 + random) + 0;
            investment = investmentAmount;
            LOGGER.info(String.valueOf(investment));
        }

        return true;
    }
}

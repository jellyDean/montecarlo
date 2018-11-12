package com.finance.montecarlo.services.implementations;

import com.finance.montecarlo.configuration.MonteCarloConfiguration;
import com.finance.montecarlo.models.montecarlo.CalculatedPortfolio;
import com.finance.montecarlo.models.montecarlo.MonteCarloRequest;
import com.finance.montecarlo.models.montecarlo.MonteCarloResponse;
import com.finance.montecarlo.models.montecarlo.Portfolio;
import com.finance.montecarlo.services.MonteCarloService;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Random;

/**
 * Implementation of the service that contains all the business logic
 *
 * @author  Dean Hutton
 * @version 1.0
 * @since   2018-11-04
 */
@Service
public class MonteCarloServiceImpl implements MonteCarloService {

    private final MonteCarloConfiguration monteCarloConfiguration;
    private Random random;
    private final Logger LOGGER;

    @Autowired
    public MonteCarloServiceImpl(MonteCarloConfiguration monteCarloConfiguration) {
        this.monteCarloConfiguration = monteCarloConfiguration;
        this.random = new Random();
        this.LOGGER = LoggerFactory.getLogger(this.getClass());
    }

    /**
     * Calculates a random number that ensures Gaussian distribution
     *
     * @param mean the mean of the portfolio
     * @param standardDeviation the standard deviation of the portfolio
     * @return Random number
     */
    private double calculateRandomNumber(double mean, double standardDeviation){
        // convert mean and std deviation to percents, important
        return random.nextGaussian() * (standardDeviation / 100) + (mean / 100);
    }

    /**
     * Runs a monte carlo simulation on one or more portfolios
     *
     * @param monteCarloRequest the mean of the portfolio
     * @return ResponseEntity containing the results of the simulation
     */
    @Override
    public MonteCarloResponse runSimulation(MonteCarloRequest monteCarloRequest){

        LOGGER.info("Entering runSimulation");

        // grab all the needed properties from the config
        int simulationSize = monteCarloConfiguration.getSimulationSize();
        int numberOfYears = monteCarloConfiguration.getNumberOfYears();
        double inflationRate = monteCarloConfiguration.getInflationRate();
        double initialInvestmentAmount = monteCarloConfiguration.getInitialInvestmentAmount();

        // initialize properties needed to do calculations
        double randomNumber;

        double[] simulatedInvestments = new double[simulationSize];
        ArrayList<CalculatedPortfolio> calculatedPortfolioArray = new ArrayList<>();
        MonteCarloResponse response = new MonteCarloResponse();

        // used for printing doubles in pretty money format i.e. $10,000
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();

        // iterate through each portfolio in the request
        for (Portfolio portfolio: monteCarloRequest.getPortfolios()) {
            // iterate through the number simulations to be ran
            for (int j = 0; j < simulationSize; j++) {
                double rollingInvestmentAmount = initialInvestmentAmount;
                // calculate the return over a period of time and store it for each simulation
                for (int i = 0; i < numberOfYears; i++) {
                    randomNumber = calculateRandomNumber(portfolio.getMean(), portfolio.getStandardDeviation());
                    rollingInvestmentAmount = rollingInvestmentAmount * (1 + randomNumber) * (1 - inflationRate);
                }
                simulatedInvestments[j] = rollingInvestmentAmount;
            }
            // use a library to get the percentiles
            DescriptiveStatistics descriptiveStatistics = new DescriptiveStatistics(simulatedInvestments);
            CalculatedPortfolio calculatedPortfolio = new CalculatedPortfolio();

            // copy the input params to the output so it can be seen in the response
            BeanUtils.copyProperties(portfolio,calculatedPortfolio);

            // set calculated values
            calculatedPortfolio.setBestCasePerformance(currencyFormatter.format(descriptiveStatistics.getPercentile(90)));
            calculatedPortfolio.setWorstCasePerformance(currencyFormatter.format(descriptiveStatistics.getPercentile(10)));
            calculatedPortfolio.setMedian(currencyFormatter.format(descriptiveStatistics.getPercentile(50)));

            // add the result to the output array
            calculatedPortfolioArray.add(calculatedPortfolio);
        }

        // copy config props to the output
        BeanUtils.copyProperties(monteCarloConfiguration, response);

        // set the response then return to controller
        response.setPortfolios(calculatedPortfolioArray);

        LOGGER.info("Leaving runSimulation");
        return response;
    }

}

package com.finance.montecarlo.services;

import com.finance.montecarlo.configuration.MonteCarloConfiguration;
import com.finance.montecarlo.models.CalculatedPortfolio;
import com.finance.montecarlo.models.MonteCarloRequest;
import com.finance.montecarlo.models.MonteCarloResponse;
import com.finance.montecarlo.models.Portfolio;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Random;


@Service
public class MonteCarloService {

    private final MonteCarloConfiguration monteCarloConfiguration;
    private Random random;
    private final Logger LOGGER;

    @Autowired
    public MonteCarloService(MonteCarloConfiguration monteCarloConfiguration) {
        this.monteCarloConfiguration = monteCarloConfiguration;
        this.random = new Random();
        this.LOGGER = LoggerFactory.getLogger(this.getClass());
    }

    //TODO: docs
    private double calculateRandomNumber(double mean, double standardDeviation){
        return random.nextGaussian() * (standardDeviation / 100) + (mean / 100);
    }

    //TODO: docs
    public MonteCarloResponse runSimulation(MonteCarloRequest monteCarloRequest){

        LOGGER.info("Entering runSimulation");

        int simulationSize = monteCarloConfiguration.getSimulationSize();
        int numberOfYears = monteCarloConfiguration.getNumberOfYears();
        double inflationRate = monteCarloConfiguration.getInflationRate();
        double initialInvestmentAmount = monteCarloConfiguration.getInitialInvestmentAmount();

        double random;

        // https://www.youtube.com/watch?v=Q5Fw2IRMjPQ
        // https://www.programcreek.com/java-api-examples/?api=org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
        // https://stackoverflow.com/questions/13791409/java-format-double-value-as-dollar-amount
        // https://stackoverflow.com/questions/6011943/java-normal-distribution

        double[] simulatedInvestments = new double[simulationSize];
        ArrayList<CalculatedPortfolio> calculatedPortfolioArray = new ArrayList<>();
        MonteCarloResponse response = new MonteCarloResponse();
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();

        for (Portfolio portfolio: monteCarloRequest.getPortfolios()) {
            for (int j = 0; j < simulationSize; j++) {
                double rollingInvestmentAmount = initialInvestmentAmount;
                for (int i = 0; i < numberOfYears; i++) {
                    random = calculateRandomNumber(portfolio.getMean(), portfolio.getStandardDeviation());
                    rollingInvestmentAmount = rollingInvestmentAmount * (1 + random) * (1 - inflationRate);
                }
                simulatedInvestments[j] = rollingInvestmentAmount;
            }
            DescriptiveStatistics descriptiveStatistics = new DescriptiveStatistics(simulatedInvestments);
            CalculatedPortfolio calculatedPortfolio = new CalculatedPortfolio();

            BeanUtils.copyProperties(portfolio,calculatedPortfolio);

            calculatedPortfolio.setBestCasePerformance(currencyFormatter.format(descriptiveStatistics.getPercentile(90)));
            calculatedPortfolio.setWorstCasePerformance(currencyFormatter.format(descriptiveStatistics.getPercentile(10)));
            calculatedPortfolio.setMedian(currencyFormatter.format(descriptiveStatistics.getPercentile(50)));

            calculatedPortfolioArray.add(calculatedPortfolio);
        }

        BeanUtils.copyProperties(monteCarloConfiguration, response);
        response.setPortfolios(calculatedPortfolioArray);

        LOGGER.info("Leaving runSimulation");
        return response;
    }
}

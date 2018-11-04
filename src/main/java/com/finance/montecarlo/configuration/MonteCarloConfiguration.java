package com.finance.montecarlo.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration class for reading application.yml into an object
 *
 * @author  Dean Hutton
 * @version 1.0
 * @since   2018-11-04
 */
@ConfigurationProperties
@Data
@Component
public class MonteCarloConfiguration {
    @Value("${simulation-size}")
    private int simulationSize;

    @Value("${number-of-years}")
    private int numberOfYears;

    @Value("${inflation-rate}")
    private double inflationRate;

    @Value("${initial-investment-amount}")
    private double initialInvestmentAmount;

}

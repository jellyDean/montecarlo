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
    @Value("${monte-carlo.simulation-size}")
    private int simulationSize;

    @Value("${monte-carlo.number-of-years}")
    private int numberOfYears;

    @Value("${monte-carlo.inflation-rate}")
    private double inflationRate;

    @Value("${monte-carlo.initial-investment-amount}")
    private double initialInvestmentAmount;

    @Value("${aws-elasticsearch.host}")
    private String awsElasticSeachHost;

}

package com.finance.montecarlo.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties
@Data
@Component
public class MonteCarloConfiguration {
    @Value("${simulation-size}")
    private int simulationSize;

    @Value("${number-of-years}")
    private int numberOfYears;

    @Value("${inflation-rate}")
    private float inflationRate;

}

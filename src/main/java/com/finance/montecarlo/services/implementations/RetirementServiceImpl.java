package com.finance.montecarlo.services.implementations;

import com.finance.montecarlo.configuration.MonteCarloConfiguration;
import com.finance.montecarlo.models.search.ElasticSearchQueryParameters;
import com.finance.montecarlo.services.RetirementService;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Implementation of the service that contains all the business logic to query aws ES
 *
 * @author  Dean Hutton
 * @version 1.0
 * @since   2018-11-11
 */
@Service
public class RetirementServiceImpl implements RetirementService {

    private final MonteCarloConfiguration monteCarloConfiguration;
    private final Logger LOGGER;

    @Autowired
    public RetirementServiceImpl(MonteCarloConfiguration monteCarloConfiguration) {
        this.monteCarloConfiguration = monteCarloConfiguration;
        this.LOGGER = LoggerFactory.getLogger(this.getClass());
    }

    /**
     * Runs a query on aws elastic search to grab retirement info
     *
     * @param elasticSearchQueryParameters The query params if any
     * @return ResponseEntity containing the results of the query
     */
    @Override
    public ResponseEntity<JSONObject> getRetirementPlan(ElasticSearchQueryParameters elasticSearchQueryParameters){
        LOGGER.info("Entering getRetirementPlan");

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity entity = new HttpEntity(headers);

        ResponseEntity<JSONObject> response = restTemplate.exchange(
                generateQueryString(elasticSearchQueryParameters),
                HttpMethod.GET,
                entity,
                JSONObject.class
        );

        LOGGER.info("Leaving getRetirementPlan");
        return response;
    }

    /**
     * Generates the query string to be sent to aws based on the input params
     *
     * @param elasticSearchQueryParameters The query params if any
     * @return String for making the request to aws elastic search
     */
    private String generateQueryString(ElasticSearchQueryParameters elasticSearchQueryParameters){
        LOGGER.info("Entering generateQueryString");

        StringBuilder elasticSearchQueryString = new StringBuilder();
        elasticSearchQueryString.append(monteCarloConfiguration.getAwsElasticSearchHost());
        elasticSearchQueryString.append("/");
        elasticSearchQueryString.append(monteCarloConfiguration.getAwsElasticSearchIndex());

        if (!StringUtils.isBlank(elasticSearchQueryParameters.getPlanName())) {
            elasticSearchQueryString.append("/_search?q=PLAN_NAME:" + elasticSearchQueryParameters.getPlanName());
        }

        else if (!StringUtils.isBlank(elasticSearchQueryParameters.getSponsorName())) {
            elasticSearchQueryString.append("/_search?q=SPONSOR_DFE_NAME:" + elasticSearchQueryParameters.getSponsorName());

        }

        else if (!StringUtils.isBlank(elasticSearchQueryParameters.getSponsorState())) {
            elasticSearchQueryString.append("/_search?q=SPONS_DFE_MAIL_US_STATE:" + elasticSearchQueryParameters.getSponsorState());
        }
        // If no params are sent return all
        else {
            elasticSearchQueryString.append("/_search");
        }

        LOGGER.info("Leaving generateQueryString");
        return elasticSearchQueryString.toString();

    }

}
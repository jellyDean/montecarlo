package com.finance.montecarlo.services.implementations;

import com.finance.montecarlo.configuration.MonteCarloConfiguration;
import com.finance.montecarlo.models.search.ElasticSearchQueryParameters;
import com.finance.montecarlo.services.RetirementService;
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


@Service
public class RetirementServiceImpl implements RetirementService {

    private final MonteCarloConfiguration monteCarloConfiguration;
    private final Logger LOGGER;

    @Autowired
    public RetirementServiceImpl(MonteCarloConfiguration monteCarloConfiguration) {
        this.monteCarloConfiguration = monteCarloConfiguration;
        this.LOGGER = LoggerFactory.getLogger(this.getClass());
    }

    @Override
    public ResponseEntity<JSONObject> getRetirementPlan(ElasticSearchQueryParameters elasticSearchQueryParameters){


        String url = "https://search-finance-74z6mki36yiw2tlopizwnkrpia.us-east-1.es.amazonaws.com/retirement-plans/_search?q=SPONS_DFE_MAIL_US_STATE:OH";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity entity = new HttpEntity(headers);

        ResponseEntity<JSONObject> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, JSONObject.class);

        return response;
    }

}
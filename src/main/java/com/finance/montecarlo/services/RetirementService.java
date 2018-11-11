package com.finance.montecarlo.services;


import com.finance.montecarlo.models.search.ElasticSearchQueryParameters;
import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;

public interface RetirementService {

    ResponseEntity<JSONObject> getRetirementPlan(ElasticSearchQueryParameters elasticSearchQueryParameters);
}

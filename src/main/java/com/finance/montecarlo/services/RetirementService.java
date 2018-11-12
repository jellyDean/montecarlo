package com.finance.montecarlo.services;


import com.finance.montecarlo.models.search.ElasticSearchQueryParameters;
import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;

/**
 * Interface of the retirement service.
 *
 * @author  Dean Hutton
 * @version 1.0
 * @since   2018-11-11
 */
public interface RetirementService {

    ResponseEntity<JSONObject> getRetirementPlan(ElasticSearchQueryParameters elasticSearchQueryParameters);
}

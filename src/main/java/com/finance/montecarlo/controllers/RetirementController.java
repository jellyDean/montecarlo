package com.finance.montecarlo.controllers;

import com.finance.montecarlo.models.search.ElasticSearchQueryParameters;
import com.finance.montecarlo.services.RetirementService;
import com.finance.montecarlo.validators.ElasticSearchValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller for making requests to run simulations
 *
 * @author  Dean Hutton
 * @version 1.0
 * @since   2018-11-04
 */
@Controller
@RequestMapping("/v1")
public class RetirementController {

    private final Logger LOGGER;
    private final RetirementService retirementService;

    @Autowired
    public RetirementController(RetirementService retirementService) {
        this.retirementService = retirementService;
        this.LOGGER =  LoggerFactory.getLogger(this.getClass());
    }


    //: TODO add docs
    @RequestMapping(method = RequestMethod.GET, value = "/finance/retirement/plans")
    public ResponseEntity createEmployeeRetirementPlan(ElasticSearchQueryParameters searchQueryParameters, BindingResult bindingResult) {
        LOGGER.debug("Entering createEmployeeRetirementPlan method");

        ResponseEntity response;

        try{

            ElasticSearchValidator elasticSearchValidator = new ElasticSearchValidator();
            elasticSearchValidator.validate(searchQueryParameters, bindingResult);

            if (bindingResult.hasErrors()){
                // TODO: Clean up error response
                LOGGER.warn("There has been an error with createEmployeeRetirementPlan form validation");
                response = new ResponseEntity<>(bindingResult.getAllErrors(), HttpStatus.BAD_REQUEST);
            } else {
                // If there are no form validation errors submit the request to spService for processing
                response = new ResponseEntity<>(retirementService.getRetirementPlan(searchQueryParameters).getBody(), HttpStatus.OK);
            }

        }catch(Exception ex){
            LOGGER.error("There has been an exception while running creating an employee retirement plan: ", ex);
            response = new ResponseEntity<>(ex, HttpStatus.BAD_REQUEST);
        }

        LOGGER.debug("Leaving createEmployeeRetirementPlan method");
        return response;

    }

}

package com.finance.montecarlo.controllers;

import com.finance.montecarlo.models.montecarlo.MonteCarloRequest;
import com.finance.montecarlo.services.MonteCarloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

/**
 * Controller for making requests to run simulations
 *
 * @author  Dean Hutton
 * @version 1.0
 * @since   2018-11-04
 */
@Controller
@RequestMapping("/v1")
public class MonteCarloSimulationController {

    private final Logger LOGGER;
    private final MonteCarloService monteCarloService;

    @Autowired
    public MonteCarloSimulationController(MonteCarloService monteCarloService) {
        this.monteCarloService = monteCarloService;
        this.LOGGER =  LoggerFactory.getLogger(this.getClass());
    }

    /**
     * Runs a monte carlo simulation on array of portfolios
     *
     * @param portfolios the portfolios to run the simulation on
     * @return ResponseEntity containing results of the operation
     */
    @RequestMapping(method = RequestMethod.POST, value = "/finance/montecarlo")
    public ResponseEntity runMonteCarloSimulation(@Valid @RequestBody MonteCarloRequest portfolios, BindingResult bindingResult) {
        LOGGER.debug("Entering runMonteCarloSimulation method");

        ResponseEntity response;

        try{
            if (bindingResult.hasErrors()){
                // TODO: Clean up error response
                LOGGER.warn("There has been an error with runMonteCarloSimulation form validation");
                response = new ResponseEntity<>(bindingResult.getAllErrors(), HttpStatus.BAD_REQUEST);
            } else {
                // If there are no form validation errors submit the request to the service layer
                response = new ResponseEntity<>(monteCarloService.runSimulation(portfolios), HttpStatus.OK);
            }

        }catch(Exception ex){
            LOGGER.error("There has been an exception while running a monte carlo simulation: ", ex);
            response = new ResponseEntity<>(ex, HttpStatus.BAD_REQUEST);
        }

        LOGGER.debug("Leaving runMonteCarloSimulation method");
        return response;

    }

}

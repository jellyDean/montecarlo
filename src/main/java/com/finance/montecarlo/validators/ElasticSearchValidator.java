
package com.finance.montecarlo.validators;

import com.finance.montecarlo.models.search.ElasticSearchQueryParameters;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


public class ElasticSearchValidator implements Validator {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Class clazz) {
        return ElasticSearchQueryParameters.class.isAssignableFrom(clazz);
    }


    @Override
    public void validate(Object target, Errors errors){
        ElasticSearchQueryParameters request = (ElasticSearchQueryParameters) target;


        int numberOfRequestParams = 0;

        // Check to see if more than one query param is sent and error out
        if (!StringUtils.isBlank(request.getPlanName())) {
            numberOfRequestParams += 1;
        }

        if (!StringUtils.isBlank(request.getSponsorName())) {
            numberOfRequestParams += 1;
        }

        if (!StringUtils.isBlank(request.getSponsorState())) {
            numberOfRequestParams += 1;
        }

        if(numberOfRequestParams > 1){
            String msg = "Only one query parameter (planName, sponsorState or sponsorName) is allowed in the request";
            LOGGER.warn(msg);
            throw new RuntimeException(msg);
        }

        LOGGER.debug("Leaving CreateNotificationValidator");
    }
}

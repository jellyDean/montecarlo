package com.finance.montecarlo.models.search;

import lombok.Data;

//TODO: Add docs
@Data
public class ElasticSearchQueryParameters {

    private String planName;

    private String sponsorName;

    private String sponsorState;
}

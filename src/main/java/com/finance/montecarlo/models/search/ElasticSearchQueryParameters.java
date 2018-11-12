package com.finance.montecarlo.models.search;

import lombok.Data;

import javax.validation.constraints.Pattern;

//TODO: Add docs
@Data
public class ElasticSearchQueryParameters {

    private String planName;

    private String sponsorName;

    @Pattern(
            regexp="^(AL|AK|AR|AZ|CA|CO|CT|DC|DE|FL|GA|HI|IA|ID|IL|IN|KS|KY|LA|MA|MD|ME|MI|MN|MO|MS|MT|NC|ND|NE|NH|NJ|NM|NV|NY|OH|OK|OR|PA|RI|SC|SD|TN|TX|UT|VA|VT|WA|WI|WV|WY)$",
            message = "SponsorState is not supported. It must be two uppercase characters.")
    private String sponsorState;
}

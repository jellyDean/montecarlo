package com.finance.montecarlo.services;


import com.finance.montecarlo.models.ProfileDocument;

public interface RetirementService {

    boolean createRetirementPlan(ProfileDocument document);
}

package com.finance.montecarlo.models.montecarlo;

import lombok.Data;

/**
 * Class used for writing out the simulation results. Extends portfolio so that the input params are shown in the
 * response
 *
 * @author  Dean Hutton
 * @version 1.0
 * @since   2018-11-04
 */
@Data
public class CalculatedPortfolio extends Portfolio {
    private String bestCasePerformance;
    private String worstCasePerformance;
    private String median;
}

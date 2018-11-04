# Summary
The monte carlo application is a spring boot web service that accepts portfolios to 
run monte carlo simulations on. The input can consist of any number of portfolios. 
The application is set up with a $100,000 initial investment with 10,000 simulations
over a twenty year period. Inflation is accounted for at a rate of 3.5%. The median, bestCase
 and worstCase performance is calculated for each portfolio.

# References
* https://www.youtube.com/watch?v=Q5Fw2IRMjPQ
* https://www.programcreek.com/java-api-examples/?api=org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
* https://stackoverflow.com/questions/13791409/java-format-double-value-as-dollar-amount
* https://stackoverflow.com/questions/6011943/java-normal-distribution

# Usage 
1. Clone the repo `git clone https://github.com/jellyDean/montecarlo.git`
2. Navigate to the repo that was cloned above `cd montecarlo`
3. Package the app with mvn by entering `mvn package` 4 tests will run
4. Run the app from the jar file create in step 3 by entering in `java -jar target/montecarlo-0.0.1-SNAPSHOT.jar`
5. Make a POST request to the app that looks like the below
    * URL http://localhost:8080/v1/finance/montecarlo
    * Body
    ```
    {
    	"portfolios" :[
    		{
    			"mean": 9.4,
    			"standardDeviation": 15.675,
    			"portfolioType": "Agressive"
    		},
    		{
    			"mean": 6.6,
    			"standardDeviation": 6.6,
    			"portfolioType": "Conserve"
    		}
    	]
    }
    ```
6. Observe the response
```
{
    "simulationSize": 10000,
    "numberOfYears": 20,
    "inflationRate": 0.035,
    "initialInvestmentAmount": 100000,
    "portfolios": [
        {
            "mean": 9.4,
            "standardDeviation": 15.675,
            "portfolioType": "Agressive",
            "bestCasePerformance": "$538,563.26",
            "worstCasePerformance": "$100,790.82",
            "median": "$240,388.04"
        },
        {
            "mean": 6.6,
            "standardDeviation": 6.6,
            "portfolioType": "Conserve",
            "bestCasePerformance": "$242,461.66",
            "worstCasePerformance": "$118,880.91",
            "median": "$169,262.38"
        }
    ]
}
```

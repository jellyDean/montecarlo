# Summary
The monte carlo application is a spring boot web service that accepts portfolios to 
run monte carlo simulations on. The input can consist of any number of portfolios. 
The application is set up with a $100,000 initial investment with 10,000 simulations
over a twenty year period. Inflation is accounted for at a rate of 3.5%. The median, bestCase
 and worstCase performance is calculated for each portfolio. The application is also used to retrieve
 retirement plan information from AWS elastic search. The data in AWS must first be messaged and uploaded
 before queries can be ran on it. The tool for the exchange, transform and load was written in Python 3.7 which
 will need to setup on the machine doing the ETL.

# References
* https://www.youtube.com/watch?v=Q5Fw2IRMjPQ
* https://www.programcreek.com/java-api-examples/?api=org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
* https://stackoverflow.com/questions/13791409/java-format-double-value-as-dollar-amount
* https://stackoverflow.com/questions/6011943/java-normal-distribution
* https://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-gsg-create-domain.html
* https://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-gsg-upload-data.html
* https://stackoverflow.com/questions/14365027/python-post-binary-data
* https://stackoverflow.com/questions/16289859/splitting-large-text-file-into-smaller-text-files-by-line-numbers-using-python

# Usage 
## Monte Carlo Simulation
1. Clone the repo `git clone https://github.com/jellyDean/montecarlo.git`
2. Navigate to the repo that was cloned above `cd montecarlo`
3. Package the app with mvn by entering `mvn package`, TODO tests will run
4. Run the app from the jar file created in step 3 by entering in `java -jar target/montecarlo-1.0.0.jar`
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
## Retirement Plan Elastic Search
### Upload Retirement Data to AWS Elastic Search
1. Setup a aws elastic search instance following the steps [here](https://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-gsg.html)
- Make sure to add your IP address in the security policies
- Take note of the domain URL which in this case is `https://search-finance-74z6mki36yiw2tlopizwnkrpia.us-east-1.es.amazonaws.com`
2. Download the required data from the `challenges\aws.pdf` file
3. Unzip it in the `montecarlo\etl` directory. There should be a file called `f_5500_2017_latest.csv`
4. Navigate to the etl directory `cd montecarlo\etl`
5. Open `etl.py` and update the `aws_bulk_load_url` on line 67 to be what was returned in step 1. Make sure that `/_bulk` is at the end thus it
will look like `aws_bulk_load_url = 'https://search-finance-74z6mki36yiw2tlopizwnkrpia.us-east-1.es.amazonaws.com/_bulk'`
6. Confirm that all the required python libraries are install in the virtual environment
7. Run the etl script by entering `python etl.py`. It will take 20 minutes to complete
8. Observe the results from the etl script which are displayed below
```
Starting the ETL process to upload files to AWS
Begin Creating JSON upload file
End Begin Creating JSON upload file. Number of rows written 203082
Begin Chunking
End Chunking. Number of files to upload to aws 41
Begin Upload to AWS
AWS Bulk Upload Response <Response [200]>
Chunk uploaded! Chunk number 1
AWS Bulk Upload Response <Response [200]>
Chunk uploaded! Chunk number 2
........ etc etc ........
Chunk uploaded! Chunk number 40
AWS Bulk Upload Response <Response [200]>
Chunk uploaded! Chunk number 41
End Upload to AWS
ETL Complete Process complete
```
9. Validate the index on AWS. It will look like the `AwsIndexAfterEtlScreenShot.png` in the etl directory of the repo. 
Notice how the counts of 203,082 align both in AWS and from the ETL script output

### Run a Query






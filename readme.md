# Summary
The monte carlo application is a spring boot web service that accepts portfolios to 
run monte carlo simulations on. The input can consist of any number of portfolios. 
The application is set up with a $100,000 initial investment with 10,000 simulations
over a twenty year period. Inflation is accounted for at a rate of 3.5%. The median, bestCase
 and worstCase performance is calculated for each portfolio. The application is also used to retrieve
 retirement plan information from AWS elastic search. The data in AWS must first be massaged and uploaded
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
3. Package the app with mvn by entering `mvn package`, 10 tests will run
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
    * Make sure to add your IP address in the security policies
    * Take note of the domain URL which in this case is `https://search-finance-74z6mki36yiw2tlopizwnkrpia.us-east-1.es.amazonaws.com`
2. Clone the repo `git clone https://github.com/jellyDean/montecarlo.git`
3. Download the required data from the `challenges/aws.pdf` file
4. Unzip it in the `montecarlo/etl` directory. There should be a file called `f_5500_2017_latest.csv`
5. Navigate to the etl directory `cd montecarlo/etl`
6. Open `etl.py` and update the `aws_bulk_load_url` on line 42 to be what was returned in step 1. Make sure that `/_bulk` is at the end thus it
will look like `aws_bulk_load_url = 'https://search-finance-74z6mki36yiw2tlopizwnkrpia.us-east-1.es.amazonaws.com/_bulk'`
7. Confirm that all the required python libraries are install in the virtual environment
8. Run the etl script by entering `python etl.py`. It will take 20 minutes to complete
9. Observe the results from the etl script which are displayed below
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
<<........ etc etc ........>>
Chunk uploaded! Chunk number 40
AWS Bulk Upload Response <Response [200]>
Chunk uploaded! Chunk number 41
End Upload to AWS
ETL Process Complete
```
10. Validate the index on AWS. It will look like the `AwsIndexAfterEtlScreenShot.png` in the etl directory of the repo. 
Notice how the counts of 203,082 align both in AWS and from the ETL script output

### Run a Query
In order for this to work all steps in the `Upload Retirement Data to AWS Elastic Search` section above must be completed
1. Navigate to the repo that was cloned in the previous section above `cd montecarlo`
2. Open `montecarlo/src/main/resources/application.yml` and change the `aws-elasticsearch.host` to be your AWS Elastic
Search instance that was created in the previous section
3. Package the app with mvn by entering `mvn package`, 10 tests will run
4. Run the app from the jar file created in step 3 by entering in `java -jar target/montecarlo-1.0.0.jar`
5. Make a GET request to the app that looks like the below
    * URL http://localhost:8080/v1/finance/retirement/plans
    * Note: The below params are supported in the GET request and can only be used individually and not together
        * planName - http://localhost:8080/v1/finance/retirement/plans?planName=test
        * sponsorName - http://localhost:8080/v1/finance/retirement/plans?sponsorName=apple
        * sponsorState - http://localhost:8080/v1/finance/retirement/plans?sponsorState=OH
6. Observe the response
```
{
    "_shards": {
        "total": 5,
        "successful": 5,
        "skipped": 0,
        "failed": 0
    },
    "hits": {
        "total": 27,
        "max_score": 10.564148,
        "hits": [
            {
                "_index": "retirement-plans",
                "_type": "plan",
                "_id": "20180918160447P030188180295001",
                "_score": 10.564148,
                "_source": {
                    "ACK_ID": "20180918160447P030188180295001",
                    "FORM_PLAN_YEAR_BEGIN_DATE": "2017-01-01",
                    "FORM_TAX_PRD": "2017-12-31",
                    "TYPE_PLAN_ENTITY_CD": "2",
                    "INITIAL_FILING_IND": "0",
                    "AMENDED_IND": "0",
                    "FINAL_FILING_IND": "0",
                    "SHORT_PLAN_YR_IND": "0",
                    "COLLECTIVE_BARGAIN_IND": "0",
                    "F5558_APPLICATION_FILED_IND": "1",
                    "EXT_AUTOMATIC_IND": "0",
                    "DFVC_PROGRAM_IND": "0",
                    "EXT_SPECIAL_IND": "0",
                    "PLAN_NAME": "TEST DEVICES, INC. RETIREMENT PLAN",
                    "SPONS_DFE_PN": "001",
                    "PLAN_EFF_DATE": "1997-01-01",
                    "SPONSOR_DFE_NAME": "TEST DEVICES, INC.",
                    "SPONS_DFE_MAIL_US_ADDRESS1": "571 MAIN STREET",
                    "SPONS_DFE_MAIL_US_CITY": "HUDSON",
                    "SPONS_DFE_MAIL_US_STATE": "MA",
                    "SPONS_DFE_MAIL_US_ZIP": "01749",
                    "SPONS_DFE_EIN": "042613000",
                    "SPONS_DFE_PHONE_NUM": "9785624942",
                    "BUSINESS_CODE": "339900",
                    "LAST_RPT_SPONS_NAME": "TEST DEVICES, INC.",
                    "LAST_RPT_SPONS_EIN": "042613000",
                    "LAST_RPT_PLAN_NUM": "001",
                    "ADMIN_SIGNED_DATE": "2018-09-18T12:04:17-0500",
                    "ADMIN_SIGNED_NAME": "GINA FOURNEIR",
                    "TOT_PARTCP_BOY_CNT": "40",
                    "TOT_ACTIVE_PARTCP_CNT": "37",
                    "RTD_SEP_PARTCP_RCVG_CNT": "1",
                    "RTD_SEP_PARTCP_FUT_CNT": "4",
                    "SUBTL_ACT_RTD_SEP_CNT": "42",
                    "BENEF_RCVG_BNFT_CNT": "0",
                    "TOT_ACT_RTD_SEP_BENEF_CNT": "42",
                    "PARTCP_ACCOUNT_BAL_CNT": "32",
                    "TYPE_PENSION_BNFT_CODE": "2F2G2J2K2S2T2E3D",
                    "FUNDING_INSURANCE_IND": "0",
                    "FUNDING_SEC412_IND": "0",
                    "FUNDING_TRUST_IND": "1",
                    "FUNDING_GEN_ASSET_IND": "0",
                    "BENEFIT_INSURANCE_IND": "0",
                    "BENEFIT_SEC412_IND": "0",
                    "BENEFIT_TRUST_IND": "1",
                    "BENEFIT_GEN_ASSET_IND": "0",
                    "SCH_R_ATTACHED_IND": "1",
                    "SCH_MB_ATTACHED_IND": "0",
                    "SCH_H_ATTACHED_IND": "0",
                    "SCH_I_ATTACHED_IND": "1",
                    "SCH_A_ATTACHED_IND": "0",
                    "SCH_C_ATTACHED_IND": "0",
                    "SCH_D_ATTACHED_IND": "1",
                    "SCH_G_ATTACHED_IND": "0",
                    "FILING_STATUS": "FILING_RECEIVED",
                    "DATE_RECEIVED": "2018-09-18",
                    "VALID_ADMIN_SIGNATURE": "Filed with authorized/valid electronic signature",
                    "ADMIN_NAME_SAME_SPON_IND": "1",
                    "TOT_ACT_PARTCP_BOY_CNT": "31",
                    "LAST_RPT_PLAN_NAME": "TEST DEVICES, INC 401(K) PROFIT SHARING PLAN"
                }
            },
            {<<........ result 2........>>},
            {<<........ result 3 etc etc........>>}
        ]
    },
    "took": 13,
    "timed_out": false
}
```
   




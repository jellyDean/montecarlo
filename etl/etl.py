"""
etl.py
Developer: Dean Hutton
Date: 11/11/18
Summary: This script is used to parse the f_5500_2017_latest.csv file into a large JSON file. The large JSON file
is then broken into chunks less than 10MB and uploaded one by one to aws elastic search.
Pre-Requisites:
https://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-gsg-create-domain.html
https://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-gsg-upload-data.html
https://stackoverflow.com/questions/14365027/python-post-binary-data
https://stackoverflow.com/questions/16289859/splitting-large-text-file-into-smaller-text-files-by-line-numbers-using-python
"""


import os
import requests
import csv
import json

# The field names of the data that is read in. Used later to create JSON objects for writing to file.
field_names = ("ACK_ID","FORM_PLAN_YEAR_BEGIN_DATE","FORM_TAX_PRD","TYPE_PLAN_ENTITY_CD","TYPE_DFE_PLAN_ENTITY_CD",
               "INITIAL_FILING_IND","AMENDED_IND","FINAL_FILING_IND","SHORT_PLAN_YR_IND","COLLECTIVE_BARGAIN_IND",
               "F5558_APPLICATION_FILED_IND","EXT_AUTOMATIC_IND","DFVC_PROGRAM_IND","EXT_SPECIAL_IND","EXT_SPECIAL_TEXT",
               "PLAN_NAME","SPONS_DFE_PN","PLAN_EFF_DATE","SPONSOR_DFE_NAME","SPONS_DFE_DBA_NAME","SPONS_DFE_CARE_OF_NAME",
               "SPONS_DFE_MAIL_US_ADDRESS1","SPONS_DFE_MAIL_US_ADDRESS2","SPONS_DFE_MAIL_US_CITY","SPONS_DFE_MAIL_US_STATE",
               "SPONS_DFE_MAIL_US_ZIP","SPONS_DFE_MAIL_FOREIGN_ADDR1","SPONS_DFE_MAIL_FOREIGN_ADDR2","SPONS_DFE_MAIL_FOREIGN_CITY",
               "SPONS_DFE_MAIL_FORGN_PROV_ST","SPONS_DFE_MAIL_FOREIGN_CNTRY","SPONS_DFE_MAIL_FORGN_POSTAL_CD",
               "SPONS_DFE_LOC_US_ADDRESS1","SPONS_DFE_LOC_US_ADDRESS2","SPONS_DFE_LOC_US_CITY","SPONS_DFE_LOC_US_STATE",
               "SPONS_DFE_LOC_US_ZIP","SPONS_DFE_LOC_FOREIGN_ADDRESS1","SPONS_DFE_LOC_FOREIGN_ADDRESS2",
               "SPONS_DFE_LOC_FOREIGN_CITY","SPONS_DFE_LOC_FORGN_PROV_ST","SPONS_DFE_LOC_FOREIGN_CNTRY",
               "SPONS_DFE_LOC_FORGN_POSTAL_CD","SPONS_DFE_EIN","SPONS_DFE_PHONE_NUM","BUSINESS_CODE","ADMIN_NAME",
               "ADMIN_CARE_OF_NAME","ADMIN_US_ADDRESS1","ADMIN_US_ADDRESS2","ADMIN_US_CITY","ADMIN_US_STATE","ADMIN_US_ZIP",
               "ADMIN_FOREIGN_ADDRESS1","ADMIN_FOREIGN_ADDRESS2","ADMIN_FOREIGN_CITY","ADMIN_FOREIGN_PROV_STATE","ADMIN_FOREIGN_CNTRY",
               "ADMIN_FOREIGN_POSTAL_CD","ADMIN_EIN","ADMIN_PHONE_NUM","LAST_RPT_SPONS_NAME","LAST_RPT_SPONS_EIN","LAST_RPT_PLAN_NUM",
               "ADMIN_SIGNED_DATE","ADMIN_SIGNED_NAME","SPONS_SIGNED_DATE","SPONS_SIGNED_NAME","DFE_SIGNED_DATE","DFE_SIGNED_NAME",
               "TOT_PARTCP_BOY_CNT","TOT_ACTIVE_PARTCP_CNT","RTD_SEP_PARTCP_RCVG_CNT","RTD_SEP_PARTCP_FUT_CNT","SUBTL_ACT_RTD_SEP_CNT",
               "BENEF_RCVG_BNFT_CNT","TOT_ACT_RTD_SEP_BENEF_CNT","PARTCP_ACCOUNT_BAL_CNT","SEP_PARTCP_PARTL_VSTD_CNT","CONTRIB_EMPLRS_CNT",
               "TYPE_PENSION_BNFT_CODE","TYPE_WELFARE_BNFT_CODE","FUNDING_INSURANCE_IND","FUNDING_SEC412_IND","FUNDING_TRUST_IND",
               "FUNDING_GEN_ASSET_IND","BENEFIT_INSURANCE_IND","BENEFIT_SEC412_IND","BENEFIT_TRUST_IND","BENEFIT_GEN_ASSET_IND",
               "SCH_R_ATTACHED_IND","SCH_MB_ATTACHED_IND","SCH_SB_ATTACHED_IND","SCH_H_ATTACHED_IND","SCH_I_ATTACHED_IND",
               "SCH_A_ATTACHED_IND","NUM_SCH_A_ATTACHED_CNT","SCH_C_ATTACHED_IND","SCH_D_ATTACHED_IND","SCH_G_ATTACHED_IND",
               "FILING_STATUS","DATE_RECEIVED","VALID_ADMIN_SIGNATURE","VALID_DFE_SIGNATURE","VALID_SPONSOR_SIGNATURE",
               "ADMIN_PHONE_NUM_FOREIGN","SPONS_DFE_PHONE_NUM_FOREIGN","ADMIN_NAME_SAME_SPON_IND","ADMIN_ADDRESS_SAME_SPON_IND",
               "PREPARER_NAME","PREPARER_FIRM_NAME","PREPARER_US_ADDRESS1","PREPARER_US_ADDRESS2","PREPARER_US_CITY",
               "PREPARER_US_STATE","PREPARER_US_ZIP","PREPARER_FOREIGN_ADDRESS1","PREPARER_FOREIGN_ADDRESS2",
               "PREPARER_FOREIGN_CITY","PREPARER_FOREIGN_PROV_STATE","PREPARER_FOREIGN_CNTRY","PREPARER_FOREIGN_POSTAL_CD",
               "PREPARER_PHONE_NUM","PREPARER_PHONE_NUM_FOREIGN","TOT_ACT_PARTCP_BOY_CNT","SUBJ_M1_FILING_REQ_IND",
               "COMPLIANCE_M1_FILING_REQ_IND","M1_RECEIPT_CONFIRMATION_CODE","ADMIN_MANUAL_SIGNED_DATE",
               "ADMIN_MANUAL_SIGNED_NAME","LAST_RPT_PLAN_NAME","SPONS_MANUAL_SIGNED_DATE","SPONS_MANUAL_SIGNED_NAME",
               "DFE_MANUAL_SIGNED_DATE","DFE_MANUAL_SIGNED_NAME")

# 1
# 2
# git filter-branch --tree-filter 'rm -rf montecarlo/etl/f_5500_2017_latest.csv' HEAD
# The index and type that is going to be created in aws
action_and_meta_data = {
    "index": {
        "_index": "retirement-plans",
        "_type": "plan",
        "_id": "id"
    }
}

# Headers of the request to aws
headers = {
    'Content-type': 'application/json'
}

# The URL of the aws ES instance
aws_bulk_load_url = 'https://search-finance-74z6mki36yiw2tlopizwnkrpia.us-east-1.es.amazonaws.com/_bulk'


def upload_chunks_to_aws(small_file_list):
    """
    Function that reads in a list of files and uploads each one to aws. It then deletes the files.
    :param list small_file_list: List of files to upload
    """
    counter = 0
    for small_filename in small_file_list:
        with open(small_filename) as handle:
            # build the request
            response = requests.put(aws_bulk_load_url, headers=headers, data=handle.read())
            print("AWS Bulk Upload Response", response)
            counter += 1
            print("Chunk uploaded! Chunk number", counter)

        # delete the chunk file
        os.remove(small_filename)


def split_json_data_into_chunks(input_file):
    """
    Function that reads in a large JSON file and splits it into multiple file chunks of 10MB
    :param str input_file: A large file that is going to be split into pieces
    :return: A list of small files the large file was broken in to
    :rtype: list
    """
    small_file_list = []
    lines_per_file = 10000
    smallfile = None
    file_counter = 0
    with open(input_file) as bigfile:
        for lineno, line in enumerate(bigfile):
            if lineno % lines_per_file == 0:
                if smallfile:
                    file_counter += 1
                    smallfile.close()
                small_filename = 'small_etl_{}.json'.format(file_counter)
                small_file_list.append(small_filename)
                smallfile = open(small_filename, "w")
            smallfile.write(line)

        if smallfile:
            smallfile.close()

    return small_file_list


def parse_csv_data_to_json(input_file, output_file):
    """
    Function that reads in large csv file and parses it into a JSON file
    :param str input_file: A large csv file that is going to converted into a JSON file
    :param str output_file: A large JSON file of all the data to be uploaded
    :return: The number of data records to be uploaded
    :rtype: int
    """
    with open(input_file) as f:
        # open the output file for writing
        with open(output_file, 'w') as myfile:

            # read in the csv
            input_content = csv.reader(f, delimiter=',')

            # skip the header
            next(f)
            number_of_records_written = 0
            for x in input_content:
                # make a dictionary of keys and values for json dumping
                dictionary = dict(zip(field_names, x))

                # delete an fields that are empty string to suppress errors while uploading
                cleaned_dict = {k: v for k, v in dictionary.items() if v is not ""}

                # set the id of the index to the ack id
                action_and_meta_data["index"]["_id"] = cleaned_dict.get("ACK_ID")

                # dump the index and data to file
                json.dump(action_and_meta_data, myfile)
                myfile.write('\n')
                json.dump(cleaned_dict, myfile)
                myfile.write('\n')
                number_of_records_written += 1

    return number_of_records_written


def main():

    print("Starting the ETL process to upload files to AWS")

    # get the current working directory
    cwd = os.getcwd()

    # set the location of the input file
    input_file = "%s/f_5500_2017_latest.csv" % cwd

    # set the location of the output file
    output_file = "%s/elastic_search_bulk_output.json" % cwd

    print("\nBegin Creating JSON upload file")
    number_of_records_written = parse_csv_data_to_json(input_file, output_file)
    print("\nEnd Begin Creating JSON upload file. Number of rows written", number_of_records_written)

    print("\nBegin Chunking")
    small_file_list = split_json_data_into_chunks(output_file)
    print("\nEnd Chunking. Number of files to upload to aws", len(small_file_list))

    print("\nBegin Upload to AWS")
    upload_chunks_to_aws(small_file_list)
    print("\nEnd Upload to AWS")


if __name__ == "__main__":
    # execute only if run as a script
    main()
    print("\nETL Complete Process complete")

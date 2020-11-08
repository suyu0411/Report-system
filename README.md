Since the *.application files contain the secretKey/accessKey will trigger some security issues of AWS, I just add them to .gitignore file


Main improvement/changes:
1.  In ExcelServiceImpl class, make the excel not only store at local but also in aws S3 Bucket.
2.	In ReportServiceImpl class, change the method getFileBodyByReqId() to make it able to get Excel file from S3 Bucket not local.
3.	Change the hardcode filename to the variable ReqId + filetype
4.	In ReportServiceImpl class change sendDirectRequests method to parallel processing use threadPool and Future.
5.	Write a test case to check whether the reports successfully generate or not.
6.	For configuration files, set my own S3 bucket and secretKey/accessKey to make the message Queue work

# REST API 


## 2 Table of contents
<!-- TOC depthFrom:1 depthTo:3 withLinks:1 updateOnSave:1 orderedList:0 -->

- [General Overview ](#1-general-overview)
	- [Microsoft REST API Guidelines Working Group](#microsoft-rest-api-guidelines-working-group)
- [Microsoft REST API Guidelines](#microsoft-rest-api-guidelines)
	- [1 Abstract](#1-abstract)
	- [2 Table of contents](#2-table-of-contents)
	- [3 Introduction](#3-introduction)
		- [3.1 Recommended reading](#31-recommended-reading)
	- [4    Interpreting the guidelines](#4-interpreting-the-guidelines)
		- [4.1    Application of the guidelines](#41-application-of-the-guidelines)
		- [4.2    Guidelines for existing services and versioning of services](#42-guidelines-for-existing-services-and-versioning-of-services)
		- [4.3    Requirements language](#43-requirements-language)
		- [4.4    License](#44-license)

##1 General Overview
This documentation is the result of the ongoing conversation in this ticket:
https://taskman.eionet.europa.eu/issues/29005

**Synchronous QA for a single file**
----
  
  Wrapper around XQueryService.runQAScript

* **URL**

  /webapi/qajobs

* **Method:**

  `POST`
  
* **Content-Type:** application/json

*  **URL Params**

   none

* **Data Params**
  ```json  
    {
        "source_url":"http://www.example.com",
        "script_id":"42" 
    }
  ```
    


* **Success Response:**

   **Code:** 200 OK <br />
   **Content:** 
  
   ```json
     {
        "feedbackStatus": "ERROR",
        "feedbackMessage": "Some message",
        "feedbackContentType": "text/html;charset=UTF-8",
        "feedbackContent": "<div>...</div>" 
     }
    ```
 
* **Error Response:**

   **Code:** 400 Bad Request <br />
   **Reason:** missing or malformed source_url <br/>
    **Content:** 
    ```json
    {
     "httpStatusCode": 400,
     "errorMessage"  : "Parameter source_url cannot be null" 
    }
    ```

* **Error Response:**
  
   **Code:** 400 Bad Request <br />
   **Reason:** missing or malformed script_id <br/>
    **Content:** 
    ```json
    {
     "httpStatusCode": 400,
     "errorMessage"  : "Parameter script_id cannot be null" 
    }
    ```
    
* **Error Response:**
      
   **Code:** 500 Internal Server Error <br />
   **Reason:** xQuery Service Exception <br/>
    **Content:** 
    ```json
    {
     "httpStatusCode": 500,
     "errorMessage"  : "xQuery Service Exception" 
    }
    ```
 
 **Asynchronous QA for a single file**
----
  the Backend mechanism is not implemented yet in Xquery Service so we return a temporary error code and message.

* **URL**

  /webapi/asynctasks/qajobs

* **Method:**

  `POST`
  
* **Content-Type:** application/json

*  **URL Params**

   none

* **Data Params**
  ```json  
    {
        "source_url":"http://www.example.com",
        "script_id":"42" 
    }
  ```
    


* **Success Response:**

   **Code:** 200 OK <br />
   **Content:** 
  
   ```json
     {
        "jobId":1234
     }
    ```
 
* **Error Response:**

   **Code:** 400 Bad Request <br />
   **Reason:** missing or malformed source_url <br/>
    **Content:** 
    ```json
    {
     "httpStatusCode": 400,
     "errorMessage"  : "Parameter source_url cannot be null" 
    }
    ```

* **Error Response:**
  
   **Code:** 400 Bad Request <br />
   **Reason:** missing or malformed script_id <br/>
    **Content:** 
    ```json
    {
     "httpStatusCode": 400,
     "errorMessage"  : "Parameter script_id cannot be null" 
    }
    ```
    
* **Error Response:**
      
   **Code:** 500 Internal Server Error <br />
   **Reason:** xQuery Service Exception <br/>
    **Content:** 
    ```json
    {
     "httpStatusCode": 500,
     "errorMessage"  : "xQuery Service Exception" 
    }
    ```
 

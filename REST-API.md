# REST API 


##  Table of contents
<!-- TOC depthFrom:1 depthTo:3 withLinks:1 updateOnSave:1 orderedList:0 -->

- [1 General Overview ](#1-general-overview)
- [2 Common Error Results ](#2-common-error-results)
   - [2.1 generic Error Response ](#21-generic-error-response)
   - [2.2 Error Response for not implemented methods ](#22-error-response-for-not-implemented-methods)

- [3 QA Service Endpoints ](#3-qa-service-endpoints)
   - [3.1 Synchronous QA for a single file](#31-synchronous-qa-for-a-single-file)
      - [3.1.1 Synchronous QA for a single file with schema validation](#311-synchronous-qa-for-a-single-file-with-schema-validation)
   
   - [3.2 Asynchronous QA for a single file](#32-asynchronous-qa-for-a-single-file)
   - [3.3 Asynchronous QA for an Envelope](#33-asynchronous-qa-for-an-envelope)
   - [3.4 Get QA result of a Job Status](#34-get-qa-result-of-a-job-status)
   - [3.5 Get list of QA queries for  a schema ](#35-get-list-of-qa-queries-for-a-schema)
   - [3.6 Get list of QA scripts for  a schema ](#36-get-list-of-qa-scripts-for-a-schema)
- [4 Security ](#4-security)

</br>
</br>
</br>
##1 General Overview
This documentation is the result of the ongoing conversation in this ticket:
https://taskman.eionet.europa.eu/issues/29005 regarding the REST API of the xmlconv application.


## 2 Common Error Results


### 2.1 Generic Error Response


  ```json
    {
     "httpStatusCode": 1234,
     "errorMessage"  : "error message" 
    }
   ```
### 2.2 Error Response for not implemented methods


  ```json
    {
     "errorMessage": "asynchronous QA for a Single file is not supported yet",
     "httpStatusCode": "501"
    }
   ```
    
## 3 QA Service Endpoints 


### 3.1 Synchronous QA for a single file


  Wrapper around XQueryService.runQAScript

* **URL**

  /restapi/qajobs

* **Method:**

  `POST`
  
* **Content-Type:** application/json

*  **URL Params**

   none

* **Data Params**
  ```json  
    {
        "sourceUrl":"http://www.example.com",
        "scriptId":"42" 
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
   **Reason:** missing or malformed sourceUrl <br/>
    **Content:** 
    ```json
    {
     "httpStatusCode": 400,
     "errorMessage"  : "Parameter sourceUrl cannot be null" 
    }
    ```

* **Error Response:**
  
   **Code:** 400 Bad Request <br />
   **Reason:** missing or malformed scriptId <br/>
    **Content:** 
    ```json
    {
     "httpStatusCode": 400,
     "errorMessage"  : "Parameter scriptId cannot be null" 
    }
    ```
    
* **Error Response:**
      
   **Code:** 500 Internal Server Error <br />
   **Reason:** qA Service Exception <br/>
    **Content:** 
    ```json
    {
     "httpStatusCode": 500,
     "errorMessage"  : "QA Service Exception" 
    }
    ```

--
### 3.1.1 Synchronous QA for a single file with schema validation
  
  The endpoint is the same as [Synchronous QA for a single file](#31-synchronous-qa-for-a-single-file)
   except that we pass the parameter `scriptId` with value `-1` in order to enable schema validation.
   
* **Example of Data Params(Http Request Body)**


  ```json
    {
     "sourceUrl": "www.example.com",
     "scriptId"  : "-1" 
    }
   ```
* **Success Response:**
  
  See: [Synchronous QA for a single file](#31-synchronous-qa-for-a-single-file)

* **Error Response:**
  
  See: [Synchronous QA for a single file](#31-synchronous-qa-for-a-single-file)

--
### 3.2 Asynchronous QA for a single file


 
 the Backend mechanism is not implemented yet in Xquery Service so we return a temporary error code and message.

* **URL**

  /restapi/asynctasks/qajobs

* **Method:**

  `POST`
  
* **Content-Type:** application/json

*  **URL Params**

   none

* **Data Params**
  ```json  
    {
        "sourceUrl":"http://www.example.com",
        "scriptId":"42" 
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
   **Reason:** missing or malformed sourceUrl <br/>
    **Content:** 
    ```json
    {
     "httpStatusCode": 400,
     "errorMessage"  : "Parameter sourceUrl cannot be null" 
    }
    ```

* **Error Response:**
  
   **Code:** 400 Bad Request <br />
   **Reason:** missing or malformed scriptId <br/>
    **Content:** 
    ```json
    {
     "httpStatusCode": 400,
     "errorMessage"  : "Parameter scriptId cannot be null" 
    }
    ```
    
* **Error Response:**
      
   **Code:** 500 Internal Server Error <br />
   **Reason:** QA Service Exception <br/>
    **Content:** 
    ```json
    {
     "httpStatusCode": 500,
     "errorMessage"  : "QA Service Exception" 
    }
    ```
 
-- 
### 3.3 Asynchronous QA for an Envelope
 

* **URL**

  /restapi/asynctasks/qajobs/batch

* **Method:**

  `POST`
  
* **Content-Type:** application/json

*  **URL Params**

   none

* **Data Params**
  ```json  
    {
    "envelopeUrl":"http://cdrtest.eionet.europa.eu/gr/envelope1234"
    }
  ```
    


* **Success Response:**

   **Code:** 200 OK <br />
   **Content:** 
  
   ```json
    "jobs": [
        {
            "id": 123,
            "fileUrl": "http://some.file.url.1" 
        }, {
            "id": 456,
            "fileUrl": "http://some.file.url.2" 
        }
    ]
    ```
 
* **Error Response:**

   **Code:** 400 Bad Request <br />
   **Reason:** missing or malformed envelopeUrl <br/>
    **Content:** 
    ```json
    {
     "httpStatusCode": 400,
     "errorMessage"  : "Parameter envelopeUrl cannot be null" 
    }
    ```


    
* **Error Response:**
      
   **Code:** 500 Internal Server Error <br />
   **Reason:** QA Service Exception <br/>
    **Content:** 
    ```json
    {
     "httpStatusCode": 500,
     "errorMessage"  : "QA Service Exception" 
    }
    ```



-- 
### 3.4 Get QA result of a Job Status
 

* **URL**

  /restapi/asynctasks/qajobs/[jobid]

* **Method:**

  `GET`
  
* **Content-Type:** none

*  **URL Params**

   [id]

* **Data Params**
  
   none
    

* **Success Response:**

   **Code:** 200 OK <br />
   **Content:** 
  
   ```json
    {
     "executionStatus": "completed",
     "feedbackStatus": "ERROR",
     "feedbackMessage": "Some message",
     "feedbackContentType": "text/html",
     "feedbackContent": "<div>...</div>" 
    }
    ```
 
    
* **Error Response:**
      
   **Code:** 500 Internal Server Error <br />
   **Reason:** QA Service Exception <br/>
    **Content:** 
    ```json
    {
     "httpStatusCode": 500,
     "errorMessage"  : "QA Service Exception" 
    }
    ```


-- 
### 3.5 Get list of QA queries for a schema

 
* **URL**

  /restapi/queries

* **Method:**

  `GET`
  
* **Content-Type:** none

*  **URL Params**

   schema=[schema]

* **Data Params**
  
   none
    

* **Success Response:**

   **Code:** 200 OK <br />
   **Content:** 
  
   ```json
    [
  {
    "schema_id": "1234",
    "content_type_id": "HTML",
    "content_type_out": "text/html;charset=UTF-8",
    "query_id": "-1",
    "short_name": "XML Schema Validation",
    "type": "",
    "query": "http://url.xsd",
    "description": "",
    "upper_limit": "200",
    "xml_schema": "http://url.xsd"
  },
  {
    "schema_id": "123",
    "content_type_id": "HTML",
    "content_type_out": "text/html;charset=UTF-8",
    "query_id": "321",
    "short_name": "name ",
    "is_active": "1",
    "type": "xquery",
    "query": "url.xquery",
    "script_type": "xquery 1.0",
    "description": "some description",
    "upper_limit": "10",
    "xml_schema": "url.xsd"
  }
]
    ```
    
    -- 
### 3.6 Get list of QA scripts for a schema

 
* **URL**

  /restapi/qascripts

* **Method:**

  `GET`
  
* **Content-Type:** none

*  **URL Params**

   schema=[schema]

* **Data Params**
  
   none
    

* **Success Response:**

   **Code:** 200 OK <br />
   **Content:** 
  
   ```json
   [
  [
    "-1",
    "XML Schema Validation",
    "",
    "200"
  ],
  [
    "834",
    "Article version ",
    "01 Jan 1970 02:00",
    "10"
  ]
]
    ```
 

## 4 Security
 We have implemented a way to secure the end points utilizing JWT tokens and Spring Security Framework.
 Below you can find implementation details  as well as a secured endpoint which can be used for testing the mechanism.<br>
 **Important Note:** This is a proof of concept  to get us started regarding how to secure the endpoints.
 
 
### 4.1 Required Claims of the JWT token
####  Standard JWT Claims 
   **iss:**The issuer of the token.<br>
   **subject:** Subject- Identifier (or, name) of the user this token represents<br>
   **exp:** The expiration date of the token. Should be later than current date.<br>
   **aud:** The audience - Intended recipient of this token

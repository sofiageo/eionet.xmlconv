# REST API 


##  Table of contents
<!-- TOC depthFrom:1 depthTo:3 withLinks:1 updateOnSave:1 orderedList:0 -->

- [General Overview ](#general-overview)
- [Common Error Results ](#common-error-results)
   - [generic-error-response ](#generic-error-response)

- [QA Service Endpoints ](#qa-service-endpoints)
   - [Synchronous QA for a single file](#synchronous-qa-for-a-single-file)
   - [Asynchronous QA for a single file](#asynchronous-qa-for-a-single-file)

</br>
</br>
</br>
## General Overview
This documentation is the result of the ongoing conversation in this ticket:
https://taskman.eionet.europa.eu/issues/29005 regarding the REST API of the xmlconv application.


# Common Error Results


**Generic Error Response**
----

 * ```json
    {
     "httpStatusCode": 1234,
     "errorMessage"  : "error message" 
    }
   ```
    
# QA Service Endpoints 


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
 

package com.ccieurope.ads.test.customer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.ccieurope.ads.test.HttpStatus;
import com.ccieurope.ads.test.RESTApiTest;
import com.jayway.jsonpath.JsonModel;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;

public class SimpleLocalCustomerTest extends CustomerTest
{
  private static int invocations = 0;

  /**
   * 
   * @throws AssertionError
   */

  public void run() throws AssertionError
  {
    invocations++;

    // Create new empty customer locally.
    Map<String, Serializable> customerMap = new HashMap<String, Serializable>();
    
	Response customerResponse = doPost(customerMap, true, this.getSessionId(), HttpStatus.CREATED);
	
	//Retrieve the sessionId from the response. This sessionId will be used by all subsequent requests made by this test.
    this.setSessionId(customerResponse.getSessionId());
			
	JsonModel customerModel = JsonModel.model(customerResponse.asString());

    int id = Integer.parseInt((String) customerModel.get("customerId"));
    
    System.out.println("Customer created with Customer ID: " + id + " by: " + getClass().getSimpleName() + " using Session ID " + this.getSessionId());

    // Check that the customer has't been created in the "global" storage...

    // doGet(id, false, HttpStatus.NOT_FOUND);

    // ...but has been created in the local.

    customerResponse = doGet(id, true, this.getSessionId(), HttpStatus.OK);
    
    customerModel = JsonModel.model(customerResponse.asString());

    // Update the local customer with a name.

    customerModel.opsForObject().put("companyName", "Test " + RESTApiTest.TEST_ID);

    customerResponse = doPut(id, (Map<String, Serializable>) customerModel.map().to(Map.class), true, this.getSessionId(), HttpStatus.OK);

    // Finally delete the local customer.

    doDelete(id, true, this.getSessionId(), HttpStatus.OK);
    
    // This thread is done
    if(this.getLatch() != null)
    {
    	this.getLatch().countDown();
    }
  }
  
}

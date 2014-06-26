package com.ccieurope.ads.test.customer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.ccieurope.ads.test.HttpStatus;
import com.ccieurope.ads.test.RESTApiTest;
import com.jayway.restassured.response.Response;

public class SimplePersistentCustomerTest extends CustomerTest
{
  private static int invocations = 0;

  /**
   * 
   * @throws AssertionError
   */

  public void run() throws AssertionError
  {
    invocations++;

    Map<String, Serializable> customer = new HashMap<String, Serializable>();

    // Create the (empty) customer in the database, but do expect an error due
    // to a missing name.

    // doPost(customer, false, HttpStatus.CONFLICT);

    // Give the customer a name and try again, excepting a successful creation.

    customer.put("companyName", getClass().getSimpleName() + " "
        + RESTApiTest.TEST_ID);

    Response customerResponse = doPost(customer, true, this.getSessionId(), HttpStatus.CREATED);
    
	//Retrieve the sessionId from the response. This sessionId will be used by all subsequent requests made by this test.
    this.setSessionId(customerResponse.getSessionId());
    
    customer = (Map<String, Serializable>) customerResponse.as(Map.class);
    int id = Integer.parseInt((String) customer.get("customerId"));
    
    System.out.println("Customer created with Customer ID: " + id + " by: " + getClass().getSimpleName() + " using Session ID " + this.getSessionId());

    // Add a main address and update in the database.

    Map<String, Serializable> address = new HashMap<String, Serializable>();

    address.put("extraCity", "Buckingham Palace");
    address.put("zipCode", "SW1A 1AA");
    address.put("city", "London");
    address.put("countryCode", "UK");

    customer.put("mainAddress", (Serializable) address);

    customerResponse = doPut(id, customer, true, this.getSessionId(), HttpStatus.OK);
    customer = (Map<String, Serializable>) customerResponse.as(Map.class);

    // Add a main telephone and update in the database.

    Map<String, Serializable> phone = new HashMap<String, Serializable>();

    phone.put("number", "123 456 789");

    customer.put("mainTelephone", (Serializable) phone);

    customerResponse = doPut(id, customer, false, this.getSessionId(), HttpStatus.OK);
    
    // This thread is done
    if(this.getLatch() != null)
    {
    	this.getLatch().countDown();
    }
  }
}

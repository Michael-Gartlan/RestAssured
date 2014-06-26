package com.ccieurope.ads.test.account;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.ccieurope.ads.test.HttpStatus;
import com.ccieurope.ads.test.RESTApiTest;
import com.ccieurope.ads.test.customer.CustomerTest;
import com.jayway.restassured.response.Response;

public class SimplePersistentAccountTest extends AccountTest
{
  private static int invocations = 0;

  /**
   * 
   * @throws AssertionError
   */

  public void run() throws AssertionError
  {
    invocations++;

    Map<String, Serializable> account = new HashMap<String, Serializable>();

    // Create the (empty) account in the database, but do expect an error due
    // to a missing owner.

   // doPost(account, false, HttpStatus.CONFLICT);

    // Create a customer in the database to use as account owner...

    Response customerResponse = CustomerTest.create(getClass().getSimpleName() + " " + RESTApiTest.TEST_ID, false, this.getSessionId());
    
    //Retrieve the sessionId from the response. This sessionId will be used by all subsequent requests made by this test.
    this.setSessionId(customerResponse.getSessionId());
    
    Map<String, Serializable> customer = (Map<String, Serializable>) customerResponse.as(Map.class);

    // Set the account's owner and try again, excepting a successful creation.

    Map<String, Serializable> owner = new HashMap<String, Serializable>();

    owner.put("customerId", (String) customer.get("customerId"));

    account.put("owner", (Serializable) owner);

    Response accountResponse = doPost(account, false, this.getSessionId(), HttpStatus.CREATED);
    account = (Map<String, Serializable>) accountResponse.as(Map.class);

    int id = Integer.parseInt((String) account.get("accountId"));
    
    System.out.println("Account created with Account ID: " + id + " by: " + getClass().getSimpleName() + " using Session ID " + this.getSessionId());
    
 // This thread is done
    if(this.getLatch() != null)
    {
    	this.getLatch().countDown();
    }
  }
}

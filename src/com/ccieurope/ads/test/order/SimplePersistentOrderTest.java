package com.ccieurope.ads.test.order;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.ccieurope.ads.test.HttpStatus;
import com.ccieurope.ads.test.RESTApiTest;
import com.ccieurope.ads.test.account.AccountTest;
import com.jayway.restassured.response.Response;

public class SimplePersistentOrderTest extends OrderTest
{
  private static int invocations = 0;

  /**
   * 
   * @throws AssertionError
   */

  public void run() throws AssertionError
  {
    invocations++;

    Map<String, Serializable> order = new HashMap<String, Serializable>();

    // Create the (empty) order in the database.

    Response orderResponse = doPost(order, false, this.getSessionId(), HttpStatus.CREATED);
    order = (Map<String, Serializable>) orderResponse.as(Map.class);
    
	//Retrieve the sessionId from the response. This sessionId will be used by all subsequent requests made by this test.
    this.setSessionId(orderResponse.getSessionId());

    // Create an account (with a customer) in the database to use for the
    // order...

     Response accountResponse = AccountTest.create(getClass().getSimpleName() + " " + RESTApiTest.TEST_ID, false, this.getSessionId());

     Map<String, Serializable> account = (Map<String, Serializable>) accountResponse.as(Map.class);
    // Create another order in the database, booked to the account created
    // above.

    Map<String, Serializable> ownerAccount = new HashMap<String, Serializable>();

    ownerAccount.put("accountId", (String) account.get("accountId"));

    order.put("account", (Serializable) ownerAccount);
    order.put("advertiserCustomer", account.get("owner"));
    order.put("bookingSource", "PluginOE");
    
    int id = Integer.parseInt((String) order.get("orderId"));
    
    System.out.println("Order created with Order ID: " + id + " by: " + getClass().getSimpleName() + " using Session ID " + this.getSessionId());

    orderResponse = doPut(id, order, false, this.getSessionId(), HttpStatus.OK);
    
 // This thread is done
    if(this.getLatch() != null)
    {
    	this.getLatch().countDown();
    }
  }
}

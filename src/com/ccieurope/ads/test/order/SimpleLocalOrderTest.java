package com.ccieurope.ads.test.order;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.ccieurope.ads.test.HttpStatus;
import com.ccieurope.ads.test.RESTApiTest;
import com.jayway.jsonpath.JsonModel;
import com.jayway.restassured.response.Response;

public class SimpleLocalOrderTest extends OrderTest
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

    // Create new empty order locally.

    Response orderResponse = doPost(order, true, this.getSessionId(), HttpStatus.CREATED);
    order = (Map<String, Serializable>) orderResponse.as(Map.class);
    
	//Retrieve the sessionId from the response. This sessionId will be used by all subsequent requests made by this test.
    this.setSessionId(orderResponse.getSessionId());

    int id = Integer.parseInt((String) order.get("orderId"));
    
    System.out.println("Order created with Order ID: " + id + " by: " + getClass().getSimpleName() + " using Session ID " + this.getSessionId());

    // Check that the order has't been created in the "global" storage...

    // doGet(id, false, HttpStatus.NOT_FOUND);

    // ...but has been created in the local.

    orderResponse = doGet(id, true, this.getSessionId(), HttpStatus.OK);

    // Update the local order with a caller name.
    
    JsonModel orderModel = JsonModel.model(orderResponse.asString());

    orderModel.opsForObject().put("callerName", "Test " + RESTApiTest.TEST_ID);

    orderResponse = doPut(id, orderModel.map().to(Map.class), true, this.getSessionId(), HttpStatus.OK);

    // Finally delete the local customer.

    doDelete(id, true, this.getSessionId(), HttpStatus.OK);
    
 // This thread is done
    if(this.getLatch() != null)
    {
    	this.getLatch().countDown();
    }
  }
}

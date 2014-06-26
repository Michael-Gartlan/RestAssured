package com.ccieurope.ads.test.customer;

import java.util.concurrent.CountDownLatch;

import com.ccieurope.ads.test.HttpStatus;
import com.ccieurope.ads.test.RESTApiTest;
import com.ccieurope.ads.test.Test;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;

public class CustomerLoadTest extends Test implements Runnable 
{

  class SingleTest extends Thread
  {
	  private CountDownLatch latch;
	  
    public SingleTest(CountDownLatch latch) {
		this.latch = latch;
	}

	public void run()
    {
    	System.out.println("Starting thread " + this.getName());
    	String sessionId;
    	String id;
    	Response customerResponse = createLocalCustomer();
    	id = customerResponse.body().jsonPath().get("customerId");
    	sessionId = customerResponse.getSessionId();
    	System.out.println("Created customer ID " + id + ", using session ID " + sessionId + ", on thread " + this.getName() );
    	getLocalCustomer(id, sessionId, HttpStatus.OK);
    	// getCustomer(id, sessionId, HttpStatus.NOT_FOUND);

    	deleteLocalCustomer(id, sessionId, HttpStatus.OK);
    	
    	RESTApiTest.requestCount++;
    	
    	latch.countDown();
    	
    }
  }

  /**
   * 
   * @throws AssertionError
   */

  public void run() throws AssertionError
  {
    SingleTest[] t = new SingleTest[10];

    CountDownLatch latch = new CountDownLatch(t.length);

    for (int i = 0; i < t.length; i++)
    {
      t[i] = new SingleTest(latch);

      t[i].start();
    }

    try
    {
      latch.await();
    }
    catch (InterruptedException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * 
   * @throws AssertionError
   */

  private static Response createLocalCustomer() throws AssertionError
  {
    System.out.print(".");

    /* @formatter:off */

    Response r = RestAssured.given()
        .header("authorization", "bearer " + RESTApiTest.aToken)
        .header("x-cci-profile", RESTApiTest.X_CCI_PROFILE)
        .header("x-cci-group", RESTApiTest.X_CCI_GROUP)
        .header("content-type", "application/json")
        .body("{}")
        .sessionId("")
      .expect()
        .statusCode(201)
      .when()
        .post(RESTApiTest.REST_SERVER_BASE_URL + "/customers/local");

    /* @formatter:on */

    return r;
  }

  /**
   * 
   * @throws AssertionError
   */

  private static void getCustomer(String id, String sessionId, int expectedStatus)
      throws AssertionError
  {
    System.out.print(".");

    /* @formatter:off */

    RestAssured.given()
        .header("authorization", "bearer " + RESTApiTest.aToken)
        .header("x-cci-profile", RESTApiTest.X_CCI_PROFILE)
        .header("x-cci-group", RESTApiTest.X_CCI_GROUP)
        .pathParam("id", id)
        .sessionId(sessionId)
      .expect()
        .statusCode(expectedStatus)
      .when()
        .get(RESTApiTest.REST_SERVER_BASE_URL + "/customers/{id}");

    /* @formatter:on */
  }

  /**
   * 
   * @throws AssertionError
   */

  private static void getLocalCustomer(String id, String sessionId, int expectedStatus)
      throws AssertionError
  {
    System.out.print(".");

    /* @formatter:off */

    RestAssured.given()
        .header("authorization", "bearer " + RESTApiTest.aToken)
        .header("x-cci-profile", RESTApiTest.X_CCI_PROFILE)
        .header("x-cci-group", RESTApiTest.X_CCI_GROUP)
        .pathParam("id", id)
        .sessionId(sessionId)
      .expect()
        .statusCode(expectedStatus)
      .when()
        .get(RESTApiTest.REST_SERVER_BASE_URL + "/customers/local/{id}");
        
    /* @formatter:on */
  }

  /**
   * 
   * @throws AssertionError
   */

  private static void deleteLocalCustomer(String id, String sessionId, int expectedStatus)
      throws AssertionError
  {
    System.out.print(".");

    /* @formatter:off */

    RestAssured.given()
        .header("authorization", "bearer " + RESTApiTest.aToken)
        .header("x-cci-profile", RESTApiTest.X_CCI_PROFILE)
        .header("x-cci-group", RESTApiTest.X_CCI_GROUP)
        .pathParam("id", id)
        .sessionId(sessionId)
      .expect()
        .statusCode(expectedStatus)
      .when()
        .delete(RESTApiTest.REST_SERVER_BASE_URL + "/customers/local/{id}");
        
    /* @formatter:on */
  }
}

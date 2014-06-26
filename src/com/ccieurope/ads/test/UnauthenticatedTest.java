package com.ccieurope.ads.test;

import com.jayway.restassured.RestAssured;

/**
 * 
 * @author Peter Berg (PBE)
 * 
 */

public class UnauthenticatedTest extends Test
{
  private static int invocations = 0;

  /**
   * 
   * @throws AssertionError
   */

  public void run() throws AssertionError
  {
    invocations++;

    System.out.print(".");

    RestAssured.get(RESTApiTest.REST_SERVER_BASE_URL + "/users/me").then()
        .assertThat().statusCode(401);

    RESTApiTest.requestCount++;
    
 // This thread is done
    if(this.getLatch() != null)
    {
    	this.getLatch().countDown();
    }
  }

}

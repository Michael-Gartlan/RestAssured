package com.ccieurope.ads.test.account;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.ccieurope.ads.test.HttpStatus;
import com.ccieurope.ads.test.RESTApiTest;
import com.ccieurope.ads.test.Test;
import com.ccieurope.ads.test.customer.CustomerTest;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;

/**
 * 
 * @author Peter Berg (PBE)
 * 
 */

public abstract class AccountTest extends Test
{
  /**
   * 
   * @param name
   * @param local
   * @return
   */

  public static Response create(String name, boolean local, String sessionId)
  {
    // Create a customer in the database to use as account owner...

	Response customerResponse = CustomerTest.create(name, local, sessionId);
	
	Map<String, Serializable> customer = new HashMap<String, Serializable>();
	  
	customer = (Map<String, Serializable>) customerResponse.as(Map.class);

    // Set the account's owner and try again, excepting a successful creation.

    Map<String, Serializable> owner = new HashMap<String, Serializable>();

    owner.put("customerId", (String) customer.get("customerId"));

    // Create a customer in the database to use as account owner...

    Map<String, Serializable> account = new HashMap<String, Serializable>();

    account.put("owner", (Serializable) owner);

    // Do the actual customer creation.

    return doPost(account, false, sessionId, HttpStatus.CREATED);
  }

  /**
   * 
   * @param id
   * @param local
   * @return
   */

  public static Response get(int id, String sessionId, boolean local)
  {
    return doGet(id, local, sessionId, HttpStatus.OK);
  }

  /**
   * 
   * @return
   * @throws AssertionError
   */

  protected static Response doPost(
      Map<String, Serializable> account, boolean local, String sessionId, int expectedStatus)
      throws AssertionError
  {
    System.out.print(".");

    /* @formatter:off */

    Response r = RestAssured.given()
        .header("authorization", "bearer " + RESTApiTest.aToken)
        .header("x-cci-profile", RESTApiTest.X_CCI_PROFILE)
        .header("x-cci-group", RESTApiTest.X_CCI_GROUP)
        .header("content-type", "application/json")
        .body(account)
        .sessionId(sessionId)
      .expect()
        .statusCode(expectedStatus)
      .when()
        .post(RESTApiTest.REST_SERVER_BASE_URL + "/accounts" + (local ? "/local": ""));

    /* @formatter:on */

    RESTApiTest.requestCount++;

    if (expectedStatus == HttpStatus.CREATED)
      return r;

    return null;
  }

  /**
   * 
   * @throws AssertionError
   */

  protected static Response doGet(int id, boolean local, String sessionId, int expectedStatus) throws AssertionError
  {
    System.out.print(".");

    /* @formatter:off */

    Response r = RestAssured.given()
        .header("authorization", "bearer " + RESTApiTest.aToken)
        .header("x-cci-profile", RESTApiTest.X_CCI_PROFILE)
        .header("x-cci-group", RESTApiTest.X_CCI_GROUP)
        .pathParam("id", id)
        .sessionId(sessionId)
      .expect()
        .statusCode(expectedStatus)
      .when()
        .get(RESTApiTest.REST_SERVER_BASE_URL + "/accounts/"+ (local? "local/" : "") +"{id}");
        
    /* @formatter:on */

    RESTApiTest.requestCount++;

    if (expectedStatus == HttpStatus.OK)
      return r;

    return null;
  }

  /**
   * 
   * @throws AssertionError
   */

  protected static Response doPut(int id, Map<String, Serializable> account, 
		  boolean local, String sessionId, int expectedStatus)
      throws AssertionError
  {
    System.out.print(".");

    /* @formatter:off */

    Response r = RestAssured.given()
        .header("authorization", "bearer " + RESTApiTest.aToken)
        .header("x-cci-profile", RESTApiTest.X_CCI_PROFILE)
        .header("x-cci-group", RESTApiTest.X_CCI_GROUP)
        .header("content-type","application/json")
        .pathParam("id", id)
        .body(account)
        .sessionId(sessionId)
      .expect()
        .statusCode(expectedStatus)
      .when()
        .put(RESTApiTest.REST_SERVER_BASE_URL + "/accounts/"+ (local? "local/" : "") +"{id}");
        
    /* @formatter:on */

    RESTApiTest.requestCount++;

    if (expectedStatus == HttpStatus.OK)
      return r;

    return null;
  }

  /**
   * 
   * @throws AssertionError
   */

  protected static void doDelete(int id, boolean local, String sessionId, int expectedStatus)
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
      .delete(RESTApiTest.REST_SERVER_BASE_URL + "/accounts/"+ (local? "local/" : "") +"{id}");
        
    /* @formatter:on */

    RESTApiTest.requestCount++;
  }

}

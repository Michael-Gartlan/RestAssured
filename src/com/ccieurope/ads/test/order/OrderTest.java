package com.ccieurope.ads.test.order;

import java.io.Serializable;
import java.util.Map;

import com.ccieurope.ads.test.HttpStatus;
import com.ccieurope.ads.test.RESTApiTest;
import com.ccieurope.ads.test.Test;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;

/**
 * 
 * @author Peter Berg (PBE)
 * 
 */

public abstract class OrderTest extends Test
{

  /**
   * 
   * @return
   * @throws AssertionError
   */

  protected static Response doPost(Map<String, Serializable> order, boolean local, String sessionId, int expectedStatus)
      throws AssertionError
  {
    System.out.print(".");

    /* @formatter:off */

    Response r = RestAssured.given()
        .header("authorization", "bearer " + RESTApiTest.aToken)
        .header("x-cci-profile", RESTApiTest.X_CCI_PROFILE)
        .header("x-cci-group", RESTApiTest.X_CCI_GROUP)
        .header("content-type", "application/json")
        .body(order)
        .sessionId(sessionId)
      .expect()
        .statusCode(expectedStatus)
      .when()
        .post(RESTApiTest.REST_SERVER_BASE_URL + "/orders" + (local ? "/local": ""));

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
        .get(RESTApiTest.REST_SERVER_BASE_URL + "/orders/"+ (local? "local/" : "") +"{id}");
        
    /* @formatter:on */

    RESTApiTest.requestCount++;

    if (expectedStatus == HttpStatus.OK)
      return r;

    return null;
  }

  protected static Response doPut(int id, Map<String, Serializable> order, boolean local, String sessionId, int expectedStatus)
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
        .body(order)
        .sessionId(sessionId)
      .expect()
        .statusCode(expectedStatus)
      .when()
        .put(RESTApiTest.REST_SERVER_BASE_URL + "/orders/"+ (local? "local/" : "") +"{id}");
        
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
      .delete(RESTApiTest.REST_SERVER_BASE_URL + "/orders/"+ (local? "local/" : "") +"{id}");
        
    /* @formatter:on */

    RESTApiTest.requestCount++;
  }

}

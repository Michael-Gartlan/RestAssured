package com.ccieurope.ads.test.customer;

import java.io.Serializable;
import java.util.HashMap;
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

public abstract class CustomerTest extends Test
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

    Map<String, Serializable> customer = new HashMap<String, Serializable>();

    // ...with a name...

    customer.put("companyName", name);

    // ...an address...

    Map<String, Serializable> address = new HashMap<String, Serializable>();

    address.put("extraCity", "Buckingham Palace");
    address.put("zipCode", "SW1A 1AA");
    address.put("city", "London");
    address.put("countryCode", "UK");

    customer.put("mainAddress", (Serializable) address);

    // ...and a phone number.

    Map<String, Serializable> phone = new HashMap<String, Serializable>();

    phone.put("number", "123 456 789");

    customer.put("mainTelephone", (Serializable) phone);

    // Do the actual customer creation.

    return CustomerTest.doPost(customer, local, sessionId, HttpStatus.CREATED);
  }

  /**
   * 
   * @return
   * @throws AssertionError
   */

  protected static Response doPost(Map<String, Serializable> customerMap, boolean local, String sessionId, int expectedStatus)
      throws AssertionError
  {
    System.out.print(".");

    /* @formatter:off */

    Response r = RestAssured.given()
        .header("authorization", "bearer " + RESTApiTest.aToken)
        .header("x-cci-profile", RESTApiTest.X_CCI_PROFILE)
        .header("x-cci-group", RESTApiTest.X_CCI_GROUP)
        .header("content-type", "application/json")
        .body(customerMap)
        .sessionId(sessionId)
      .expect()
        .statusCode(expectedStatus)
      .when()
        .post(RESTApiTest.REST_SERVER_BASE_URL + "/customers" + (local ? "/local": ""));

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
        .get(RESTApiTest.REST_SERVER_BASE_URL + "/customers/"+ (local? "local/" : "") +"{id}");
        
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

  protected static Response doPut(int id, Map<String, Serializable> customerMap, boolean local, String sessionId, int expectedStatus)
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
        .body(customerMap)
        .sessionId(sessionId)
      .expect()
        .statusCode(expectedStatus)
      .when()
        .put(RESTApiTest.REST_SERVER_BASE_URL + "/customers/"+ (local? "local/" : "") +"{id}");
        
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
      .delete(RESTApiTest.REST_SERVER_BASE_URL + "/customers/"+ (local? "local/" : "") +"{id}");
        
    /* @formatter:on */

    RESTApiTest.requestCount++;
  }

}

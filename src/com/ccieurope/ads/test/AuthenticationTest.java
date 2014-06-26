package com.ccieurope.ads.test;

import java.util.List;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.config.RestAssuredConfig;
import com.jayway.restassured.config.SessionConfig;
import com.jayway.restassured.response.Response;

/**
 * 
 * @author pbe
 * 
 */

public class AuthenticationTest extends Test
{

  private static int invocations = 0;

  /**
   * 
   * @throws AssertionError
   */

  public void run() throws AssertionError
  {
    invocations++;

    oauth2passwordFlow();
    oauth2refreshFlow();
    getMe();
    getProfiles();
    getGroups();
    
 // This thread is done
    if(this.getLatch() != null)
    {
    	this.getLatch().countDown();
    }

  }

  /**
   * 
   * @throws AssertionError
   */

  private static void oauth2passwordFlow() throws AssertionError
  {
    System.out.print(".");

    /* @formatter:off */
    
    // The default session cookie in RestAssured is JSESSIONID. The AdDesk rest services uses the cookie sessionid to store the SessionId. So we change the default session id name. 
    RestAssured.config = RestAssuredConfig.config().sessionConfig(new SessionConfig().sessionIdName("sessionid"));
    
    // Always log when a test fails validation
    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    
    Response r = RestAssured.given()
        .param("grant_type", "password")
        .param("client_id", RESTApiTest.props.get("ClientId"))
        .param("username", RESTApiTest.props.get("Username"))
        .param("password", RESTApiTest.props.get("Password"))
      .expect()
        .statusCode(200)
      .when()
        .get(RESTApiTest.AUTH_SERVER_BASE_URL + "/oauth/token");

    /* @formatter:on */

    RESTApiTest.requestCount++;

    RESTApiTest.aToken = r.path("access_token");
    RESTApiTest.rToken = r.path("refresh_token");
    
    // Set a session id that will be supplied with all subsequent requests.
    RestAssured.sessionId = r.sessionId();
  }

  /**
   * 
   * @throws AssertionError
   */

  private static void oauth2refreshFlow() throws AssertionError
  {
    System.out.print(".");

    /* @formatter:off */

    Response r = RestAssured.given()
        .param("grant_type", "refresh_token")
        .param("client_id", RESTApiTest.props.get("ClientId"))
        .param("refresh_token", RESTApiTest.rToken)
      .expect()
        .statusCode(200)
      .when()
        .get(RESTApiTest.AUTH_SERVER_BASE_URL + "/oauth/token");

    /* @formatter:on */

    RESTApiTest.requestCount++;

    RESTApiTest.aToken = r.path("access_token");
    RESTApiTest.rToken = r.path("refresh_token");
  }

  /**
   * 
   * @throws AssertionError
   */

  private static void getMe() throws AssertionError
  {
    System.out.print(".");

    /* @formatter:off */
    
    Response r = RestAssured.given()
        .header("authorization", "bearer " + RESTApiTest.aToken)
      .expect()
        .statusCode(200)
      .when()
        .get(RESTApiTest.REST_SERVER_BASE_URL + "/users/me");
    
    /* @formatter:on */

    RESTApiTest.requestCount++;
  }

  /**
   * 
   * @throws AssertionError
   */

  private static void getProfiles() throws AssertionError
  {
    System.out.print(".");

    /* @formatter:off */
    
    Response r = RestAssured.given()
        .header("authorization", "bearer " + RESTApiTest.aToken)
      .expect()
        .statusCode(200)
      .when()
        .get(RESTApiTest.REST_SERVER_BASE_URL + "/setup/user/profiles");
    
    /* @formatter:on */

    RESTApiTest.requestCount++;

    List<String> profileNames = r.body().jsonPath().get("name");

    RESTApiTest.X_CCI_PROFILE = profileNames.get(0);
  }

  /**
   * 
   * @throws AssertionError
   */

  private static void getGroups() throws AssertionError
  {
    System.out.print(".");

    /* @formatter:off */

    Response r = RestAssured.given()
        .header("authorization", "bearer " + RESTApiTest.aToken)
      .expect()
        .statusCode(200)
      .when()
        .get(RESTApiTest.REST_SERVER_BASE_URL + "/setup/user/groups");

    /* @formatter:on */

    RESTApiTest.requestCount++;

    List<String> groupNames = r.body().jsonPath().get("name");

    RESTApiTest.X_CCI_GROUP = groupNames.get(0);
  }
}

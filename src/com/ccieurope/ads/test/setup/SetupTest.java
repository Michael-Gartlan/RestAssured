package com.ccieurope.ads.test.setup;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
public class SetupTest extends Test
{

  private static int invocations = 0;

  /**
   * 
   * @throws AssertionError
   */

  public void run() throws AssertionError
  {
    invocations++;

    Map<String, String[]> objectEntities = new HashMap<String, String[]>();

    objectEntities.put("system", new String[] { "countries", "states",
        "languages", "businessunits", "salesterritories" });

    objectEntities.put("customer", new String[] { "categories", "statuses",
        "businessstatuses", "creditstatuses", "ratetypes", "collectionmethods",
        "promptpaymentdiscounts", "commissioncodes" });

    objectEntities.put("account", new String[] { "profiles", "statuses",
        "collectionmethods", "receivableledgers" });

    objectEntities.put("order", new String[] { "statuses" });

    objectEntities.put("price", new String[] { "taxtables", "taxgroups",
        "taxtypes" });

    /*
     * objectEntities.put("contract", new String[] { "categories", "statuses"
     * });
     */

    objectEntities.put("billing", new String[] { "reasons", "cycles" });

    for (Entry<String, String[]> e : objectEntities.entrySet())
    {
      for (String entity : e.getValue())
        getJdbcBased(e.getKey(), entity);
    }

    getPackages();
    
 // This thread is done
    if(this.getLatch() != null)
    {
    	this.getLatch().countDown();
    }
  }

  /**
   * Test JDBC based setup. Just get a list of all countries in the database.
   * 
   * @throws AssertionError
   */

  private static void getJdbcBased(String object, String entity)
      throws AssertionError
  {
    System.out.print(".");

    /* @formatter:off */
    
    RestAssured.given()
        .header("authorization", "bearer " + RESTApiTest.aToken)
        .header("x-cci-profile", RESTApiTest.X_CCI_PROFILE)
        .header("x-cci-group", RESTApiTest.X_CCI_GROUP)
        .pathParam("object", object)
        .pathParam("entity", entity)
      .expect()
        .statusCode(new RESTApiTest.OkOrEmpty())
      .when().
        get(RESTApiTest.REST_SERVER_BASE_URL + "/setup/{object}/{entity}");

    RESTApiTest.requestCount++;
    
    /* @formatter:on */

  }

  /**
   * Test ProductService based setup. Just get a list of all packages available
   * for the web apps.
   * 
   * @return
   * 
   * @throws AssertionError
   */

  public static List<Map<String, Serializable>> getPackages()
      throws AssertionError
  {
    System.out.print(".");

    /* @formatter:off */

    Response r = RestAssured.given()
        .header("authorization", "bearer " + RESTApiTest.aToken)
        .header("x-cci-profile", RESTApiTest.X_CCI_PROFILE)
        .header("x-cci-group", RESTApiTest.X_CCI_GROUP)
      .expect()
        .statusCode(HttpStatus.OK)
      .when()
        .get(RESTApiTest.REST_SERVER_BASE_URL + "/setup/product/packages");

    /* @formatter:on */

    RESTApiTest.requestCount++;

    return r.as(List.class);
  }
}

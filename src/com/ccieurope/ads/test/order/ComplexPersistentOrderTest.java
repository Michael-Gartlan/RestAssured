package com.ccieurope.ads.test.order;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ccieurope.ads.test.HttpStatus;
import com.ccieurope.ads.test.RESTApiTest;
import com.ccieurope.ads.test.account.AccountTest;
import com.ccieurope.ads.test.setup.SetupTest;
import com.jayway.restassured.response.Response;

public class ComplexPersistentOrderTest extends OrderTest
{
  private static int invocations = 0;

  /**
   * 
   * @throws AssertionError
   */

  public void run() throws AssertionError
  {
    invocations++;

    // Create an account (with a customer) in the database to use for the
    // order...

    Response accountResponse = AccountTest.create(getClass().getSimpleName() + " " + RESTApiTest.TEST_ID, false, this.getSessionId());
    
	//Retrieve the sessionId from the response. This sessionId will be used by all subsequent requests made by this test.
    this.setSessionId(accountResponse.getSessionId());
    
    Map<String, Serializable> account = (Map<String, Serializable>) accountResponse.as(Map.class);

    // Get list of all available packages...

    List<Map<String, Serializable>> allPackages = SetupTest.getPackages();

    List<Map<String, Serializable>> packageItems = new ArrayList<Map<String, Serializable>>();

    // ...and select the one specified in test.properties.

    for (Map<String, Serializable> p : allPackages)
      if (p.get("code").equals(RESTApiTest.props.get("PackageCode")))
      {
        // "Translate" the JSON format returned by the getPackage API call to
        // the one used by the order service.

        p.put("packageCode", p.get("code"));
        p.put("packageName", p.get("name"));

        p.remove("code");
        p.remove("name");
        p.remove("desc");

        packageItems.add(p);

        break;
      }

    if (packageItems.isEmpty())
      throw new AssertionError("Package '"
          + RESTApiTest.props.get("PackageCode") + "' not found.");

    Response orderResponse = createOrder(account, packageItems, true, this.getSessionId());
    Map<String, Serializable> order = (Map<String, Serializable>) orderResponse.as(Map.class);

    order.put("callerName", (Serializable) RESTApiTest.props.get("CallerName"));
    
    int id = Integer.parseInt((String) order.get("orderId"));
    
    System.out.println("Order created with Order ID: " + id + " by: " + getClass().getSimpleName() + " using Session ID " + this.getSessionId());

    orderResponse = doPut(id, order, false, this.getSessionId(), HttpStatus.OK);
    // Check that the order is found in the database ...
    doGet(id, false, this.getSessionId(), HttpStatus.OK);
    
 // This thread is done
    if(this.getLatch() != null)
    {
    	this.getLatch().countDown();
    }
  }

  /**
   * 
   * @param account
   * @param packageItem
   * @param local
   * @return
   */

  private static Response createOrder(
      Map<String, Serializable> account,
      List<Map<String, Serializable>> packageItem, boolean local, String sessionId)
  {
    // Create an order booked to the given account an package(s).

    Map<String, Serializable> order = new HashMap<String, Serializable>();

    Map<String, Serializable> ownerAccount = new HashMap<String, Serializable>();

    ownerAccount.put("accountId", (String) account.get("accountId"));

    order.put("account", (Serializable) ownerAccount);
    order.put("advertiserCustomer", (Serializable) account.get("owner"));
    order.put("bookingSource", "PluginOE");

    Map<String, Serializable> packageItems = new HashMap<String, Serializable>();

    packageItems.put("packageItem", (Serializable) packageItem);

    order.put("packageItems", (Serializable) packageItems);

    return doPost(order, local, sessionId, HttpStatus.CREATED);
  }
}

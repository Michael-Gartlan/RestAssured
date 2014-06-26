package com.ccieurope.ads.test.account;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.ccieurope.ads.test.HttpStatus;
import com.ccieurope.ads.test.RESTApiTest;
import com.jayway.restassured.response.Response;

public class SimpleLocalAccountTest extends AccountTest
{
  private static int invocations = 0;

  /**
   * 
   * @throws AssertionError
   */

  public void run() throws AssertionError
  {
    invocations++;
    
    Map<String, Serializable> account = new HashMap<String, Serializable>();

    // Create new empty account locally.

    Response accountResponse = doPost(account, true, this.getSessionId(), HttpStatus.CREATED);
    
    //Retrieve the sessionId from the response. This sessionId will be used by all subsequent requests made by this test.
    this.setSessionId(accountResponse.getSessionId());
    
    account = (Map<String, Serializable>) accountResponse.as(Map.class);

    int id = Integer.parseInt((String) account.get("accountId"));
    
    System.out.println("Account created with Account ID: " + id + " by: " + getClass().getSimpleName() + " using Session ID " + this.getSessionId());

    // Check that the account has't been created in the "global" storage...

    // doGet(id, false, HttpStatus.NOT_FOUND);

    // ...but has been created in the local.

    accountResponse = doGet(id, true, this.getSessionId(), HttpStatus.OK);
    account = (Map<String, Serializable>) accountResponse.as(Map.class);

    // Update the local account with a name.

    account.put("name", "Test " + RESTApiTest.TEST_ID);

    accountResponse = doPut(id, account, true, this.getSessionId(), HttpStatus.OK);
    account = (Map<String, Serializable>) accountResponse.as(Map.class);

    // Finally delete the local account.

    doDelete(id, true, this.getSessionId(), HttpStatus.OK);
    
 // This thread is done
    if(this.getLatch() != null)
    {
    	this.getLatch().countDown();
    }
  }
}

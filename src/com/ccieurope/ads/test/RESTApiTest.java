package com.ccieurope.ads.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import com.ccieurope.ads.test.account.SimpleLocalAccountTest;
import com.ccieurope.ads.test.account.SimplePersistentAccountTest;
import com.ccieurope.ads.test.customer.CustomerLoadTest;
import com.ccieurope.ads.test.customer.SimpleLocalCustomerTest;
import com.ccieurope.ads.test.customer.SimplePersistentCustomerTest;
import com.ccieurope.ads.test.order.ComplexPersistentOrderTest;
import com.ccieurope.ads.test.order.DMGorderTest2;
import com.ccieurope.ads.test.order.SimpleLocalOrderTest;
import com.ccieurope.ads.test.order.SimplePersistentOrderTest;
import com.ccieurope.ads.test.setup.SetupTest;
import com.jayway.restassured.RestAssured;

/**
 * 
 * @author Peter Berg (PBE)
 * 
 */

public class RESTApiTest
{

  /**
   * 
   * @author Peter Berg (PBE)
   * 
   */

  public static class OkOrEmpty extends BaseMatcher<Integer>
  {

    /**
     * 
     */

    public boolean matches(Object arg0)
    {
      return ((Integer) arg0) == 200 || ((Integer) arg0) == 204;
    }

    /**
     * 
     */

    public void describeTo(Description arg0)
    {
    }
  }

  public static final Properties props        = new Properties();

  public static String           aToken;
  public static String           rToken;

  public static String           X_CCI_GROUP;
  public static String           X_CCI_PROFILE;

  public static String           REST_SERVER_BASE_URL;
  public static String           AUTH_SERVER_BASE_URL;

  public static int              requestCount = 0;
  
  static {
	  System.setProperty("https.proxyHost", "dmg-proxy-vip");
	  System.setProperty("https.proxyPort", "80");	  
  }

  public static final String     TEST_ID      = Long.toHexString(new Date()
                                                  .getTime());

  private static final Test ini_tests[] = {new UnauthenticatedTest(), new AuthenticationTest(), new SetupTest()};
	  
	 /** new UnauthenticatedTest(), new AuthenticationTest(), new SetupTest()}; **/
  
  private static final Test all_tests[] = {new SimpleLocalCustomerTest(), new SimpleLocalAccountTest(), new SimpleLocalOrderTest(), new SimplePersistentCustomerTest(), 
	  new SimplePersistentAccountTest(), new SimplePersistentOrderTest(), new ComplexPersistentOrderTest(), new DMGorderTest2()};
   /** new SimpleLocalCustomerTest(), new SimpleLocalAccountTest(), new SimpleLocalOrderTest(), new SimplePersistentCustomerTest(), 
	  new SimplePersistentAccountTest(), new SimplePersistentOrderTest(), new ComplexPersistentOrderTest(), new DMGorderTest2()};
	  **/
	  

  /**
   * 
   */

  static
  {
    InputStream input = null;

    System.out.println("Initializing test.");

    try
    {
      input = new FileInputStream("test.properties");

      // Load the properties file

      props.load(input);

      REST_SERVER_BASE_URL = props.getProperty("REST_SERVER_BASE_URL",
          "http://localhost/api");
      AUTH_SERVER_BASE_URL = props.getProperty("AUTH_SERVER_BASE_URL",
          "http://localhost/api/authentication");

      X_CCI_GROUP = props.getProperty("X_CCI_GROUP", "Systems Support");
      X_CCI_PROFILE = props.getProperty("X_CCI_PROFILE", "System Administrator");

    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    finally
    {
      if (input != null)
      {
        try
        {
          input.close();
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
      }
    }

    System.out
        .println("Using " + AUTH_SERVER_BASE_URL + " for authentication.");
    System.out.println("Using " + REST_SERVER_BASE_URL
        + " for REST API testing.");

    System.out.println("Initialization done.");

  }

  /**
   * 
   * @param args
   */

  public static void main(String[] args)
  {

    System.out.println("Running tests (" + TEST_ID + ").");

    int failure = 0;
    int success = 0;

    int testNo = 0;

    long globalStart = new Date().getTime();

    // Run initial tests
    for (Runnable test : ini_tests)
    {
      int countBefore = requestCount;
      
      // use the sessionId retrieved in AuthenticationTest.oauth2passwordFlow()
      ((Test) test).setSessionId(RestAssured.sessionId);

      try
      {
        System.out.print("Running test " + ++testNo + " ("
            + test.getClass().getSimpleName() + ") ");

        long start = new Date().getTime();

  	  	
        test.run();

        long end = new Date().getTime();

        System.out.println(" success - total: " + (end - start)
            + "mS, average: " + (end - start) / (requestCount - countBefore)
            + "mS");
        success++;
      }
      catch (AssertionError e)
      {
        System.out.println(" failure!");
        System.out.println(e.getMessage());
        failure++;
      }
      catch (Exception e)
      {
        System.out.println(e);
        failure++;
      }
    }
    
    CountDownLatch latch = new CountDownLatch(all_tests.length);
    
    // Run the rest of the tests in parallel 
    for (Runnable test : all_tests)
    {
      int countBefore = requestCount;
      
      // set an empty sessionId to get a new session per thread
      ((Test) test).setSessionId("");

      try
      {
        System.out.print("Running test " + ++testNo + " ("
            + test.getClass().getSimpleName() + ") ");

        long start = new Date().getTime();

  	  	((Test) test).setLatch(latch);
        Thread t = new Thread (test, test.getClass().getSimpleName());
  		System.out.println("Starting Thread " + test.getClass().getSimpleName());
  		t.start();
  	 	
      //  long end = new Date().getTime();

      /** System.out.println(" success - total: " + (end - start)
            + "mS, average: " + (end - start) / (requestCount - countBefore)
            + "mS");
            **/
      //  success++;
      }
      catch (AssertionError e)
      {
        System.out.println(" failure!");
        System.out.println(e.getMessage());
        failure++;
      }
      catch (Exception e)
      {
        System.out.println(e);
        failure++;
      }
    }
    try
    {
      latch.await();
    }
    catch (InterruptedException e)
    {
    	System.out.println(e);
        failure++;
    }

    long globalEnd = new Date().getTime();

    System.out.println("Total: " + (globalEnd - globalStart) + "mS, average: "
        + (globalEnd - globalStart) / requestCount + "ms");
       
    System.out.println("Success: " + success);
    System.out.println("Failure: " + failure);
    System.out.println("No of requests: " + requestCount);
  }
}

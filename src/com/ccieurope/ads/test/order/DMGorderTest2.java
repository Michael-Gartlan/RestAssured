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
import com.jayway.jsonpath.JsonModel;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;

public class DMGorderTest2 extends OrderTest {

	private static int invocations = 0;

	/**
	 * 
	 * @throws AssertionError
	 */

	public void run() throws AssertionError {
		invocations++;

		// Get existing account ID: 200235

		Response accountResponse = AccountTest.get(200235, this.getSessionId(), false);
		Map<String, Serializable> account = (Map<String, Serializable>) accountResponse.as(Map.class);
		
		//Retrieve the sessionId from the response. This sessionId will be used by all subsequent requests made by this test.
	    this.setSessionId(accountResponse.getSessionId());

		// Get list of all available packages...

		List<Map<String, Serializable>> allPackages = SetupTest.getPackages();

		List<Map<String, Serializable>> packageItems = new ArrayList<Map<String, Serializable>>();

		// ...and select the one specified in test.properties.

		for (Map<String, Serializable> p : allPackages)
			if (p.get("code").equals(RESTApiTest.props.get("PackageCode"))) {
				// "Translate" the JSON format returned by the getPackage API
				// call to
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
			throw new AssertionError("Package '" + RESTApiTest.props.get("PackageCode") + "' not found.");

		Response orderResponse = createOrder(account, packageItems, true, this.getSessionId());
		
		JsonModel orderModel = JsonModel.model(orderResponse.asString());

		int id = Integer.parseInt((String) orderModel.get("orderId"));
		
		System.out.println("Order created with Order ID: " + id + " by: " + getClass().getSimpleName() + " using Session ID " + this.getSessionId());
		
		Map<String, Object> salesTerritory = new HashMap<String, Object>();
		salesTerritory.put("territoryName", "2QO");
		
		Map<String, Object> primarySalesRep = new HashMap<String, Object>();
        primarySalesRep.put("id", new Integer(291339296));
        
        Map<String, Object> industryCode = new HashMap<String, Object>();
        industryCode.put("code", "02.01.00");
        
		orderModel.opsForObject()
				.put("callerName", RESTApiTest.props.get("CallerName"))
				.put("purchaseOrderNo", "1")
				.put("saveState", "RELEASED")
				.put("salesTerritory", salesTerritory)
				.put("primarySalesRep", primarySalesRep)
				.put("industryCode", industryCode)
				;
		
		orderModel.opsForObject("packageItems.packageItem[0].schedules.schedule[0]")
        		.put("dateList", "15/8/2014,28/11/2014")
                .put("mediaCode", "ROP")
                .remove("mediaName")
                .put("publicationCode", "DMD")
                .remove("publicationName")
                .put("sectionCode", "ENTSDMD")
                .remove("sectionName")
                .put("subSectionCode", "BOOKDMD")
                .remove("subSectionName")
                .put("titleCode", "DMAIL")
                .remove("titleName")
                .put("titleCategoryCode", "ANL")
                .remove("titleCategoryName")
                .put("combinedConcept", "01. National MD")
                ;
        
        Map<String, Object> newNewspaperModule = new HashMap<String, Object>();
        newNewspaperModule.put("moduleCode", "10x2");

        orderModel.opsForObject("materials.material[0]")
        		.put("newspaperModule", newNewspaperModule);
		    
        orderResponse = doPut(id, orderModel.map().to(Map.class), true, this.getSessionId(), HttpStatus.OK);
        orderModel = JsonModel.model(orderResponse.asString());
        
        // Set insertion level PO Numbers
        orderModel.opsForObject("packageItems.packageItem[0].schedules.schedule[0].orderItems.orderItem[0]")
			.put("purchaseOrderNo", "2");
        orderModel.opsForObject("packageItems.packageItem[0].schedules.schedule[0].orderItems.orderItem[1]")
			.put("purchaseOrderNo", "3");
        
       List<Map> propertysets = orderModel.map("materials.material[0].matPropertySets.matPropertySet").toListOf(Map.class);
       for(int i=0; i<propertysets.size()-1; i++) {
            if (orderModel.get("materials.material[0].matPropertySets.matPropertySet[" + i + "].name").equals("DM Display Nat Copy Splits")) {
            	orderModel.opsForObject("materials.material[0].matPropertySets.matPropertySet[" + i + "].matPropertyValues.matPropertyValue[0]")
            		.put("propertyTextValue", "Nat ex Scot\\Scot Only");
            }
       }
        
		orderResponse = doPut(id, orderModel.map().to(Map.class), false, this.getSessionId(), HttpStatus.OK);
		
		// Check that the order is found in the database ...
        orderResponse = doGet(id, true, this.getSessionId(), HttpStatus.OK);
		

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
			List<Map<String, Serializable>> packageItem, boolean local, String sessionId) {
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

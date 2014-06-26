package com.ccieurope.ads.test;

import java.util.concurrent.CountDownLatch;

/**
 * 
 * @author Peter Berg (PBE)
 * 
 */

public abstract class Test implements Runnable
{
  /**
   * 
   */

private CountDownLatch latch;
private String SessionId = null;
	
  public void run()
  {
	  
  }

public CountDownLatch getLatch() {
	return latch;
}

public void setLatch(CountDownLatch latch) {
	this.latch = latch;
}

public String getSessionId() {
	return SessionId;
}

public void setSessionId(String sessionId) {
	SessionId = sessionId;
} 
  
  
}

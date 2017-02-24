package com.ec.consul;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	 Timer timer = new Timer(); 
    	 timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				System.out.println("test");
				Map<String, Boolean> blacklist = new HashMap<String, Boolean>();
				blacklist.put("a", true);
				System.out.println(blacklist.get("a"));
				System.out.println(blacklist.get("b"));
				
				if(blacklist.get("b")!=null){
					System.out.println("xxxxx");
				}
				
				if(blacklist.get("a")!=null){
					System.out.println("aaaaaaaa");
				}
			}
		}, 1000, 1000);
    }
}

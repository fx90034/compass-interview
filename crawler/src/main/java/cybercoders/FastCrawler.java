package cybercoders;

import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import org.apache.commons.lang3.StringUtils;

import cybercoders.parser.HtmlParser;

public class FastCrawler {
	
	private static ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	
	private static Set linkVisited = (Set)ConcurrentHashMap.newKeySet();
	private static Queue<String> linkToVisit = new ConcurrentLinkedQueue<String>(); // for breadth first
	
	private static String baseUrl = "https://httpbin.org";
	
	private static volatile int numRequested = 0;
	private static volatile int numSuccess = 0;
	private static volatile int numFailed = 0;
	
	private void crawl() {
		
		try {
			// 1. Read in the list of links
			List<String> links = Crawler.readLinks();
			if(links == null)
				throw new Exception("No link data is available.");
			
			// 2. Crawl the links
			for(String link:links) {
				Callable<String> crawler = new ClawerThread(link);
				
				FutureTask<String> task = new FutureTask<String>(crawler);
	            executor.execute(task);
	            
	            while(!task.isDone())
	            	Thread.sleep(10);
	        }		
// System.out.println(linkToVisit.size());
			while(linkToVisit.size() != 0) {
				String link = (String)linkToVisit.remove();
				
				Callable<String> crawler = new ClawerThread(link);
				FutureTask<String> task = new FutureTask<String>(crawler);
	            executor.execute(task);
	            
	            while(!task.isDone())
	            	Thread.sleep(10);
			}

	        executor.shutdown();
	        
	        while (!executor.isShutdown())
	        	Thread.sleep(1000);

	        System.out.println("Finished all threads");
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}

	
	public static void main(String[] args) {
		
		long start = System.currentTimeMillis();
		
		FastCrawler crawler = new FastCrawler();
		crawler.crawl();
		
		System.out.println();
		
		System.out.println("total number of http requests:" + numRequested);
		System.out.println("total number of successful requests:" + numSuccess);
		System.out.println("total number of failed requests:" + numFailed);
		
		long end = System.currentTimeMillis();
		System.out.println();
		System.out.println("It takes " + (end-start)/1000 + " seconds.");
	}
	
	static class ClawerThread implements Callable<String> {
		
		private String url;
		
		ClawerThread(String url) {
			this.url = url;
		}
		
		@Override
		public String call() throws Exception {
			if(linkVisited.contains(url))
				return null;
			
			linkVisited.add(url);
			numRequested ++;
// System.out.println("linkVisited = " + linkVisited.size());
			try {
				HttpClient client = new HttpClient(url);
				
				String response = client.callGet();
				
				if(client.statusCode == 200) {
					numSuccess ++;
					
					if(StringUtils.isNotBlank(response)) {
						HtmlParser htmlParser = new HtmlParser();
						List<String> links = htmlParser.parse(response);
						if(links != null) {
							for(String link:links) {
								String subLink = baseUrl+link;
								if(linkVisited.contains(subLink))
									continue;
								
								linkToVisit.add(subLink);
							}
						}
					}
				} // 200
				
				else {
					numFailed ++;
System.err.println(response);
System.out.println("numFailed = " + numFailed);
				}
			} catch(Exception e) {
				numFailed ++;
				System.err.println(e.getMessage());
System.out.println("numFailed = " + numFailed);
			}
			
			return "";
		} // call()
	} // class ClawerThread
	
} // class FastCrawler

package cybercoders;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import cybercoders.parser.HtmlParser;
import cybercoders.parser.JsonParser;

public class Crawler {
	
	private static final String linkLocation = "https://raw.githubusercontent.com/OnAssignment/compass-interview/master/data.json";
	private static Set<String> linkVisited = new HashSet<String>();
//	private static Set<String> linkToVisit = new HashSet<String>(); // for breadth first
	
	private static String baseUrl = "https://httpbin.org";
	
	private static int numRequested = 0;
	private static int numSuccess = 0;
	private static int numFailed = 0;
	
	static List<String> readLinks() throws Exception {
		
		List<String> links = null;
		
		try {
			HttpClient client = new HttpClient(linkLocation);
			String linkJson = client.callGet();
// System.out.println(linkJson);
			if(StringUtils.isBlank(linkJson))
				return null;
			
			JsonParser parser = new JsonParser();
			links = parser.parse(linkJson);
		} catch(Exception e) {
			System.err.println(e.getMessage());
			throw e;
		}
		
		return links;
	}
	
	private void crawl(String url) throws Exception {
		
		HttpClient client = new HttpClient(url);
		if(linkVisited.contains(url))
			return;
		
		numRequested ++;
		
		linkVisited.add(url);
// System.out.println("linkVisited = " + linkVisited.size());
		try {
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
							try {
								crawl(subLink); // depth first: calling crawl() recursively
							} catch(Exception ee) {
								System.err.println(ee.getMessage());
							}
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
	}

	
	public static void main(String[] args) {
		
		long start = System.currentTimeMillis();
		
		Crawler crawler = new Crawler();
		
		try {
			// 1. Read in the list of links
			List<String> links = crawler.readLinks();
			if(links == null)
				throw new Exception("No link data is available.");
			
			// 2. Crawl the links
			for(String link:links) {
				try {
System.out.println(link);
					crawler.crawl(link);
				} catch(Exception ee) {
					System.err.println(ee.getMessage());
				}
			}
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
		
		System.out.println();
		System.out.println("total number of http requests:" + numRequested);
		System.out.println("total number of successful requests:" + numSuccess);
		System.out.println("total number of failed requests:" + numFailed);
		
		long end = System.currentTimeMillis();
		System.out.println();
		System.out.println("It takes " + (end-start)/1000 + " seconds.");
	}
	
}

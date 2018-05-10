package cybercoders.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class HtmlParser {
	
	private static final String startTag = "<a href='";
	private static final String endTag = "'>";
	
	public List<String> parse(String html) throws Exception {
		
		if(StringUtils.isBlank(html))
			return null;
		
		List<String> links = new ArrayList<String>();
		
		int beginIndex, endIndex;
		
		for(;;) {
			beginIndex = html.indexOf(startTag);
			if(beginIndex == -1)
				break;
			endIndex = html.indexOf(endTag);
			if(endIndex == -1)
				break;
			if(beginIndex >= endIndex)
				break;
			
			String uri = html.substring(beginIndex+9, endIndex);
			html = html.substring(endIndex+2);
// System.out.println(uri);
			if(StringUtils.isNotBlank(uri))
				links.add(uri);
		}
		
		return links;
	}

}

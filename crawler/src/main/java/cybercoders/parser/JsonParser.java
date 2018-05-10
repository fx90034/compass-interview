package cybercoders.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class JsonParser {
	
	public List<String> parse(String jsonLinks) throws Exception {
		
		if(StringUtils.isBlank(jsonLinks))
			return null;
		
		List<String> links = new ArrayList<String>();
		
		JSONObject input = JSONObject.fromObject(jsonLinks);
		JSONArray jsonArray = input.getJSONArray("links");
		
		for(int i=0; i<jsonArray.size(); i++) {
			links.add((String)jsonArray.get(i));
		}
		
		return links;
	}

}

package cybercoders;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;

public class HttpClient {
	
	private static final String lineSeparator = System.getProperty("line.separator");
	private static String TLS = "TLSv1.2";
	
	private URL url;
	int statusCode;
	String reasonPhrase;
	
	public HttpClient(String inputUrl) throws Exception {
		
		if(StringUtils.isBlank(inputUrl))
			throw new IllegalStateException("baseUrl must be provided.");
		
		try {
			url = new URL(inputUrl.trim());
//			System.out.println(url.toString());
		} catch(MalformedURLException e1) {
			System.err.println(e1);
			throw e1;
		}
	}
	
    private static TrustManager[] trustAllCertificates() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        } };
        
        return trustAllCerts;
    }
	
    public String callGet() throws Exception {

        HttpURLConnection conn = null;
        InputStream input = null;
        InputStream error = null;
        BufferedReader bufferedReader = null;
        StringBuilder path = new StringBuilder();
        StringBuilder response = new StringBuilder();
        
        try {
            conn = getConnection();
            
            conn.setRequestMethod("GET");
            
            // Get response code
            statusCode = conn.getResponseCode();
            
            // Get Response message
            reasonPhrase = conn.getResponseMessage();
                
            input = conn.getInputStream();

            bufferedReader = new BufferedReader(new InputStreamReader(input));

            String line = null;
            while ((line = bufferedReader.readLine()) != null)
                response.append(line).append(lineSeparator);
        } catch(Exception e) {
//            e.printStackTrace();
            statusCode = conn.getResponseCode();
            reasonPhrase = conn.getResponseMessage();
            System.err.println("Response code is: " + statusCode);
            System.err.println("Response message is: " + reasonPhrase);

            String errorMsg = "";
            
            try {
                error = conn.getErrorStream();

                if(bufferedReader != null)
                    bufferedReader.close();
                
                bufferedReader = new BufferedReader(new InputStreamReader(error));
                
                String line = null;
                StringBuilder sb = new StringBuilder();
                while ((line = bufferedReader.readLine()) != null)
                    sb.append(line);
                
                errorMsg = sb.toString();
            } catch(Exception eee) {
            	if(StringUtils.isNotBlank(eee.getMessage()))
            		System.err.println(eee.getMessage());
            }
            
            String message = e.getMessage();
            if(StringUtils.isNotEmpty(message))
                message = message + lineSeparator + errorMsg;
            else
                message = errorMsg;
            
            if(StringUtils.isEmpty(message))
                message = "";
            
            Exception ee = new Exception(message);
//            ee.setStackTrace(ee.getStackTrace());
            
//            System.err.println(ee.getMessage());
            throw ee;
        } finally {
            try {
                if(input != null)
                    input.close();
                if(error != null)
                    error.close();
                if(bufferedReader != null)
                    bufferedReader.close();
                if(conn != null)
                    conn.disconnect();
            } catch(Exception ee) {}
        }
        
        return response.toString();
    }
    
    private HttpURLConnection getConnection() throws Exception {
        
        HttpURLConnection conn = null;

        String protocol = url.getProtocol();
        if(StringUtils.isNotBlank(protocol) && protocol.equalsIgnoreCase("https")) {
            TrustManager[] trustAllCerts = trustAllCertificates();
            
            SSLContext sc = SSLContext.getInstance(TLS);
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            
            HttpsURLConnection.setDefaultHostnameVerifier(new javax.net.ssl.HostnameVerifier() {
                public boolean verify(String hostname, javax.net.ssl.SSLSession sslSession) {
                    return true;
                }
            });
            
            conn = (HttpsURLConnection)url.openConnection();
        } // https
        
        else
            conn = (HttpURLConnection) url.openConnection();

        conn.setConnectTimeout(30000);
        
        return conn;
    }
    
}
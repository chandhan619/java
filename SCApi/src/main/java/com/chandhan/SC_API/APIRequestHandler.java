package main.java.com.chandhan.SC_API;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.oauth.client.OAuthClientFilter;
import com.sun.jersey.oauth.signature.OAuthParameters;
import com.sun.jersey.oauth.signature.OAuthSecrets;
public class APIRequestHandler {


	  
	  String url;
	  String key;
	  String secret;
	  boolean debug;
	  
	  ClientConfig clientConfig;
	  Client sCclient;
	  WebResource resource;
	  OAuthParameters params;
	  OAuthSecrets secrets;
	  ClientResponse clientResponse;
	  String result= "";
	  int statusCode;
	  
	  public APIRequestHandler(String url,String key,String secret, boolean debug,boolean setProxy)
	  {
		  super();
		  this.url =url;
		  this.key = key;
		  this.secret = secret;
		  this.debug = debug;
		  
		  if(setProxy)
		  {
			  System.setProperty("https.proxyHost", "ukproxyoak.bip.uk.fid.intl.com");
			  System.setProperty("https.proxyPost", "8000");
		  }
		  
		  clientConfig = new DefaultClientConfig();
		  clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
		  
		  sCclient = Client.create(clientConfig);
		  resource = sCclient.resource(url);
		  params = new OAuthParameters().signatureMethod(null).consumerKey(key).timestamp().nonce().version();
		  secrets =  new OAuthSecrets().consumerSecret(secret);
		  resource.addFilter(new OAuthClientFilter(sCclient.getProviders(),params,secrets));
		  
		  if(debug)
		  {
			  resource.addFilter(new LoggingFilter());
		  }
	  }
	  
	  public String getRequest() {
		  clientResponse = resource.type(MediaType.APPLICATION_JSON).get(ClientResponse.class);
		  statusCode = clientResponse.getStatus();
		  System.out.println("Response Status : "+statusCode);
		  result = clientResponse.getEntity(String.class);
		  System.out.println("Response : "+result);
		  return result;
		  
	  }

	  public String postRequest(String body) {
		  clientResponse = resource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class,body);
		  statusCode = clientResponse.getStatus();
		  System.out.println("Response Status : "+statusCode);
		  result = clientResponse.getEntity(String.class);
		  System.out.println("Response : "+result);
		  return result;
		  
	  }
}

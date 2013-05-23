/*

Copyright 2011 LinkedIn Corporation

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package com.linkedin.sample;

import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.LinkedInApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import java.io.*;
import java.util.HashMap;

public class Main {

	private static String API_KEY = "stf2a13jbtjh";
	private static String API_SECRET = "WGIB0CopE3xGGcKT";

	public static void main(Token token,String title,String post) {

		/*
		 * we need a OAuthService to handle authentication and the subsequent
		 * calls. Since we are going to use the REST APIs we need to generate a
		 * request token as the first step in the call. Once we get an access
		 * toke we can continue to use that until the API key changes or auth is
		 * revoked. Therefore, to make this sample easier to re-use we serialize
		 * the AuthHandler (which stores the access token) to disk and then
		 * reuse it.
		 * 
		 * When you first run this code please insure that you fill in the
		 * API_KEY and API_SECRET above with your own credentials and if there
		 * is a service.dat file in the code please delete it.
		 */

		// The Access Token is used in all Data calls to the APIs - it basically
		// says our application has been given access
		// to the approved information in LinkedIn
		Token accessToken = token;

		// Using the Scribe library we enter the information needed to begin the
		// chain of Oauth2 calls.
		OAuthService service = new ServiceBuilder().provider(LinkedInApi.class)
				.apiKey(API_KEY).apiSecret(API_SECRET).build();

		

	
		System.out.println();
		System.out.println("********A basic user profile call********");
		// The ~ means yourself - so this should return the basic default
		// information for your profile in XML format
		// https://developer.linkedin.com/documents/profile-api
		String url = "http://api.linkedin.com/v1/people/~";
		OAuthRequest request = new OAuthRequest(Verb.GET, url);
		service.signRequest(accessToken, request);
		Response response = request.send();
		System.out.println(response.getBody());
		System.out.println();
		System.out.println();

		System.out.println("********Get the profile in JSON********");
		// This basic call profile in JSON format
		// You can read more about JSON here http://json.org
		url = "http://api.linkedin.com/v1/people/~";
		request = new OAuthRequest(Verb.GET, url);
		request.addHeader("x-li-format", "json");
		service.signRequest(accessToken, request);
		response = request.send();
		System.out.println(response.getBody());
		System.out.println();
		System.out.println();

		System.out.println("ur title ====="+title);
		System.out.println("ur post ====="+post);
		String RequestXML = "<?xml version='1.0' encoding='UTF-8'?>"
				+ "<share>"
				+ "<comment>"+title+"</comment>"
				+ "<content>"
				+ " <title>"+post+"</title>   "
				+ " <submitted-url>http://www.encros.com</submitted-url>      "
				+ " <submitted-image-url>http://lnkd.in/Vjc5ec</submitted-image-url>  "
				+ " </content>" + " <visibility>" + "  <code>anyone</code>"
				+ " </visibility>" + "</share>";

		System.out.println("********Write to the  share - using XML********");
		// This basic shares some basic information on the users activity stream
		// https://developer.linkedin.com/documents/share-api
		url = "http://api.linkedin.com/v1/people/~/shares";
		request = new OAuthRequest(Verb.POST, url);
		request.addHeader("Content-Type", "text/xml");
		// Make an XML document
		
		request.addPayload(RequestXML);
		service.signRequest(accessToken, request);
		response = request.send();
		// there is no body just a header
		System.out.println(response.getBody());
		System.out.println(response.getHeaders().toString());
		System.out.println();
		System.out.println();

	}

	private static void logDiagnostics(OAuthRequest request, Response response) {
		System.out
				.println("\n\n[********************LinkedIn API Diagnostics**************************]\n");
		System.out.println("Key: |-> " + API_KEY + " <-|");
		System.out.println("\n|-> [******Sent*****] <-|");
		System.out.println("Headers: |-> " + request.getHeaders().toString()
				+ " <-|");
		System.out.println("URL: |-> " + request.getUrl() + " <-|");
		System.out.println("Query Params: |-> "
				+ request.getQueryStringParams().toString() + " <-|");
		System.out.println("Body Contents: |-> " + request.getBodyContents()
				+ " <-|");
		System.out.println("\n|-> [*****Received*****] <-|");
		System.out.println("Headers: |-> " + response.getHeaders().toString()
				+ " <-|");
		System.out.println("Body: |-> " + response.getBody() + " <-|");
		System.out
				.println("\n[******************End LinkedIn API Diagnostics************************]\n\n");
	}

}

package com.encros.SocialSharing;

import java.util.EnumSet;
import java.util.List;

import org.scribe.model.Token;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.encros.SocialSharing.R;
import com.google.code.linkedinapi.client.LinkedInApiClient;
import com.google.code.linkedinapi.client.LinkedInApiClientException;
import com.google.code.linkedinapi.client.LinkedInApiClientFactory;
import com.google.code.linkedinapi.client.enumeration.NetworkUpdateType;
import com.google.code.linkedinapi.client.oauth.LinkedInAccessToken;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthService;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthServiceFactory;
import com.google.code.linkedinapi.client.oauth.LinkedInRequestToken;
import com.google.code.linkedinapi.schema.Network;
import com.google.code.linkedinapi.schema.Person;
import com.google.code.linkedinapi.schema.Update;
import com.google.code.linkedinapi.schema.UpdateComment;
import com.google.code.linkedinapi.schema.Updates;
import com.linkedin.sample.Main;

public class LITestActivity extends Activity {

	// /change keysssssssssssssssssssssssssssss!!!!!!!!!!
	Button btnpost;
	EditText txttitle, txtpost;
	static final String CONSUMER_KEY = "stf2a13jbtjh";
	static final String CONSUMER_SECRET = "WGIB0CopE3xGGcKT";

	static final String APP_NAME = "LITest";
	static final String OAUTH_CALLBACK_SCHEME = "x-oauthflow-linkedin";
	static final String OAUTH_CALLBACK_HOST = "litestcalback";
	static final String OAUTH_CALLBACK_URL = String.format("%s://%s",
			OAUTH_CALLBACK_SCHEME, OAUTH_CALLBACK_HOST);
	static final String OAUTH_QUERY_TOKEN = "oauth_token";
	static final String OAUTH_QUERY_VERIFIER = "oauth_verifier";
	static final String OAUTH_QUERY_PROBLEM = "oauth_problem";

	final LinkedInOAuthService oAuthService = LinkedInOAuthServiceFactory
			.getInstance().createLinkedInOAuthService(CONSUMER_KEY,
					CONSUMER_SECRET);
	final LinkedInApiClientFactory factory = LinkedInApiClientFactory
			.newInstance(CONSUMER_KEY, CONSUMER_SECRET);

	static final String OAUTH_PREF = "LIKEDIN_OAUTH";
	static final String PREF_TOKEN = "token";
	static final String PREF_TOKENSECRET = "tokenSecret";
	static final String PREF_REQTOKENSECRET = "requestTokenSecret";

	TextView tv = null;
	
	String titledata,postdata,token,tokenSecret;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		tv = new TextView(this);
		// setContentView(tv);
		setContentView(R.layout.linkedinmain);
		txttitle = (EditText) findViewById(R.id.txttitle);
		txtpost = (EditText) findViewById(R.id.txtpost);
		txttitle.setImeActionLabel("", EditorInfo.IME_ACTION_NEXT);
		txtpost.setImeActionLabel("", EditorInfo.IME_ACTION_NEXT);
		btnpost = (Button) findViewById(R.id.btnpost);
		
	
		
		btnpost.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(txttitle.getText().toString().equals("") || txtpost.getText().toString().equals(""))
				{
					showeror();
				}
				
					
				if(txttitle.getText().toString().equals("") && txtpost.getText().toString().equals(""))
				{
					showeror();
				}
				else
				{
				if (token == null || tokenSecret == null) {

					startAutheniticate();
				} else {
					showCurrentUser(new LinkedInAccessToken(token, tokenSecret));
					Main.main(new Token(token, tokenSecret), titledata,
							postdata);
				}
				}
			}

			private void showeror() {
				// TODO Auto-generated method stub
				Drawable err_indiactor = getResources().getDrawable(
						R.drawable.error_icon);
			 txttitle.setCompoundDrawablesWithIntrinsicBounds(null, null,
						err_indiactor, null);
				txttitle.setError("Please enter some thing!!!");
				 txtpost.setCompoundDrawablesWithIntrinsicBounds(null, null,
							err_indiactor, null);
					txtpost.setError("Please enter some thing!!!");
				
				
			}
		});

	}

	void startAutheniticate() {
		final LinkedInRequestToken liToken = oAuthService
				.getOAuthRequestToken(OAUTH_CALLBACK_URL);

		final String uri = liToken.getAuthorizationUrl();
		getSharedPreferences(OAUTH_PREF, MODE_PRIVATE).edit()
				.putString(PREF_REQTOKENSECRET, liToken.getTokenSecret())
				.commit();

		
		
		
		Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
		startActivity(i);

		Toast.makeText(this, "start Autheniticating..", Toast.LENGTH_LONG)
				.show();
		//
	}

	void finishAuthenticate(final Uri uri) {
		if (uri != null && uri.getScheme().equals(OAUTH_CALLBACK_SCHEME)) {
			final String problem = uri.getQueryParameter(OAUTH_QUERY_PROBLEM);
			if (problem == null) {
				final SharedPreferences pref = getSharedPreferences(OAUTH_PREF,
						MODE_PRIVATE);
				final LinkedInAccessToken accessToken = oAuthService
						.getOAuthAccessToken(
								new LinkedInRequestToken(uri
										.getQueryParameter(OAUTH_QUERY_TOKEN),
										pref.getString(PREF_REQTOKENSECRET,
												null)),
								uri.getQueryParameter(OAUTH_QUERY_VERIFIER));
				pref.edit()
						.putString(PREF_TOKEN, accessToken.getToken())
						.putString(PREF_TOKENSECRET,
								accessToken.getTokenSecret())
						.remove(PREF_REQTOKENSECRET).commit();
				showCurrentUser(accessToken);
				
				
				Main.main(new Token(token, tokenSecret), titledata,
						postdata);

			} else {
				Toast.makeText(this,
						"Appliaction down due OAuth problem: " + problem,
						Toast.LENGTH_LONG).show();
				finish();
			}

		}
	}

	void clearTokens() {
		getSharedPreferences(OAUTH_PREF, MODE_PRIVATE).edit()
				.remove(PREF_TOKEN).remove(PREF_TOKENSECRET)
				.remove(PREF_REQTOKENSECRET).commit();
	}

	void showCurrentUser(final LinkedInAccessToken accessToken) {
		
		final SharedPreferences pref = getSharedPreferences(OAUTH_PREF,
				MODE_PRIVATE);
		 token = pref.getString(PREF_TOKEN, null);
		 tokenSecret = pref.getString(PREF_TOKENSECRET,
				null);
		Toast.makeText(LITestActivity.this, "token value---" + token,
				Toast.LENGTH_LONG).show();
		 titledata = txttitle.getText().toString();
		 postdata = txtpost.getText().toString();
		
		final LinkedInApiClient client = factory
				.createLinkedInApiClient(accessToken);
		try {
			final Person p = client.getProfileForCurrentUser();
			Toast.makeText(getApplicationContext(),
					p.getFirstName() + " Logged in"+"Your post send on Linked In..", Toast.LENGTH_LONG).show();
			// /////////////////////////////////////////////////////////
			// here you can do client API calls ...
			// client.postComment(arg0, arg1);
			// client.updateCurrentStatus(arg0);
			// or any other API call (this sample only check for current user
			// and shows it in TextView)

			Network network = client.getNetworkUpdates(EnumSet
					.of(NetworkUpdateType.STATUS_UPDATE));

			System.out.println("Total updates fetched:"
					+ network.getUpdates().getTotal());
			for (Update update : network.getUpdates().getUpdateList()) {
				System.out.println("-------------------------------");
				System.out.println(update.getUpdateKey()
						+ ":"
						+ update.getUpdateContent().getPerson().getFirstName()
						+ " "
						+ update.getUpdateContent().getPerson().getLastName()
						+ "->"
						+ update.getUpdateContent().getPerson()
								.getCurrentStatus());
				if (update.getUpdateComments() != null) {
					System.out.println("Total comments fetched:"
							+ update.getUpdateComments().getTotal());
					for (UpdateComment comment : update.getUpdateComments()
							.getUpdateCommentList()) {
						System.out.println(comment.getPerson().getFirstName()
								+ " " + comment.getPerson().getLastName()
								+ "->" + comment.getComment());
					}
				}
			}

			// ///////////////////

			// System.out.println("********Write to the  share - using XML********");
			// //This basic shares some basic information on the users activity
			// stream
			// //https://developer.linkedin.com/documents/share-api
			// url = "http://api.linkedin.com/v1/people/~/shares";
			// request = new OAuthRequest(Verb.POST, url);
			// request.addHeader("Content-Type", "text/xml");
			// //Make an XML document
			// Document doc = DocumentHelper.createDocument();
			// Element share = doc.addElement("share");
			// share.addElement("comment").addText("Guess who is testing the LinkedIn REST APIs");
			// Element content = share.addElement("content");
			// content.addElement("title").addText("A title for your share");
			// content.addElement("submitted-url").addText("http://developer.linkedin.com");
			// share.addElement("visibility").addElement("code").addText("anyone");
			// request.addPayload(doc.asXML());
			// service.signRequest(accessToken, request);
			// response = request.send();
			// //there is no body just a header
			// System.out.println(response.getBody());
			// System.out.println(response.getHeaders().toString());
			// System.out.println();System.out.println();

			// /////////////////////////////////////////////////////////
			tv.setText(p.getLastName() + ", " + p.getFirstName());
		} catch (LinkedInApiClientException ex) {
			clearTokens();

			Toast.makeText(
					this,
					"Appliaction down due LinkedInApiClientException: "
							+ ex.getMessage()
							+ " Authokens cleared - try run application again.",
					Toast.LENGTH_LONG).show();

			finish();
		}

	}

	@Override
	protected void onNewIntent(Intent intent) {
		finishAuthenticate(intent.getData());
	}
}

// import android.app.Activity;
// import android.content.Intent;
// import android.net.Uri;
// import android.os.Bundle;
// import android.widget.TextView;
//
// import com.google.code.linkedinapi.client.LinkedInApiClient;
// import com.google.code.linkedinapi.client.LinkedInApiClientFactory;
// import com.google.code.linkedinapi.client.oauth.LinkedInAccessToken;
// import com.google.code.linkedinapi.client.oauth.LinkedInOAuthService;
// import com.google.code.linkedinapi.client.oauth.LinkedInOAuthServiceFactory;
// import com.google.code.linkedinapi.client.oauth.LinkedInRequestToken;
// import com.google.code.linkedinapi.schema.Person;
// import com.google.code.linkedinapi.schema.VisibilityType;
//
// public class LITestActivity extends Activity {
//
// // /change keysssssssssssssssssssssssssssss !!!!!!!!!!
//
// public static final String CONSUMER_KEY = "stf2a13jbtjh";
// public static final String CONSUMER_SECRET = "WGIB0CopE3xGGcKT";
//
// public static final String APP_NAME = "LITest";
// public static final String OAUTH_CALLBACK_SCHEME = "x-oauthflow-linkedin";
// public static final String OAUTH_CALLBACK_HOST = "litestcalback";
// public static final String OAUTH_CALLBACK_URL = OAUTH_CALLBACK_SCHEME
// + "://" + OAUTH_CALLBACK_HOST;
//
// final LinkedInOAuthService oAuthService = LinkedInOAuthServiceFactory
// .getInstance().createLinkedInOAuthService(CONSUMER_KEY,
// CONSUMER_SECRET);
// final LinkedInApiClientFactory factory = LinkedInApiClientFactory
// .newInstance(CONSUMER_KEY, CONSUMER_SECRET);
// LinkedInRequestToken liToken;
// LinkedInApiClient client;
//
// TextView tv = null;
//
// @Override
// public void onCreate(Bundle savedInstanceState) {
// super.onCreate(savedInstanceState);
// setContentView(R.layout.main);
// tv = (TextView) findViewById(R.id.tv);
//
// liToken = oAuthService.getOAuthRequestToken(OAUTH_CALLBACK_URL);
// Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(liToken
// .getAuthorizationUrl()));
// startActivity(i);
// }
//
// @Override
// protected void onNewIntent(Intent intent) {
// String verifier = intent.getData().getQueryParameter("oauth_verifier");
//
// LinkedInAccessToken accessToken = oAuthService.getOAuthAccessToken(
// liToken, verifier);
// client = factory.createLinkedInApiClient(accessToken);
// client.postNetworkUpdate("LinkedIn Android app test");
//
// //client.addPostComment("1111", "comment2");
//
// client.postShare("1111", "comment2", "comment2", "comment2",
// VisibilityType.ALL_MEMBERS);
//
// Person p = client.getProfileForCurrentUser();
// tv.setText(p.getLastName() + ", " + p.getFirstName());
//
// }
// }
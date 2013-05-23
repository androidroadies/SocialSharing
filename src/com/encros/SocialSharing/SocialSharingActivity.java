package com.encros.SocialSharing;

import java.util.EnumSet;




import com.encros.facebook.Example;
import com.encros.twitter.TestConnect;
import com.encros.twitter.TestPost;
import com.encros.twitter.TwitterApp;
import com.encros.twitter.TwitterApp.TwDialogListener;
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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class SocialSharingActivity extends Activity {

	Button sharing;
	ProgressDialog progDialog;
	private TwitterApp mTwitter;
	private static final String twitter_consumer_key = "mpuWRi6Pfpg4Ard030d5w";
	private static final String twitter_secret_key = "MIo5wVfwLL77Qc2ub4BEGAafvKpQvq5IyWHn7YCRU";

	
	
//	static final String CONSUMER_KEY = "stf2a13jbtjh";
//	static final String CONSUMER_SECRET = "WGIB0CopE3xGGcKT";
//
//	static final String APP_NAME = "SocialSharing";
//	static final String OAUTH_CALLBACK_SCHEME = "x-oauthflow-linkedin";
//	static final String OAUTH_CALLBACK_HOST = "litestcalback";
//	static final String OAUTH_CALLBACK_URL = String.format("%s://%s",
//			OAUTH_CALLBACK_SCHEME, OAUTH_CALLBACK_HOST);
//	static final String OAUTH_QUERY_TOKEN = "oauth_token";
//	static final String OAUTH_QUERY_VERIFIER = "oauth_verifier";
//	static final String OAUTH_QUERY_PROBLEM = "oauth_problem";
//
//	final LinkedInOAuthService oAuthService = LinkedInOAuthServiceFactory
//			.getInstance().createLinkedInOAuthService(CONSUMER_KEY,
//					CONSUMER_SECRET);
//	final LinkedInApiClientFactory factory = LinkedInApiClientFactory
//			.newInstance(CONSUMER_KEY, CONSUMER_SECRET);
//
//	static final String OAUTH_PREF = "LIKEDIN_OAUTH";
//	static final String PREF_TOKEN = "token";
//	static final String PREF_TOKENSECRET = "tokenSecret";
//	static final String PREF_REQTOKENSECRET = "requestTokenSecret";

	TextView tv = null;
	Dialog myDialog;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		sharing = (Button) findViewById(R.id.btn_sharing);
		sharing.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				// Intent i = new Intent(SocialSharingActivity.this, .class);
				// startActivity(i);
				//
				myDialog = new Dialog(SocialSharingActivity.this);
				myDialog.setContentView(R.layout.custom);
				myDialog.setTitle("Share to..");
				myDialog.setCancelable(true);

				Button facebookbtn = (Button) myDialog
						.findViewById(R.id.btn_facebook);
				facebookbtn.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub

						Intent fbintent = new Intent(
								SocialSharingActivity.this, Example.class);
						startActivity(fbintent);
						myDialog.dismiss();
					}
				});
				Button twitterbtn = (Button) myDialog
						.findViewById(R.id.btn_tweeter);
				twitterbtn.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						onTwitterClick();
						myDialog.dismiss();
						mTwitter.setListener(mTwLoginDialogListener);

						if (mTwitter.hasAccessToken()) {

							String username = mTwitter.getUsername();
							username = (username.equals("")) ? "Unknown"
									: username;

							Toast.makeText(getApplicationContext(),
									"  Twitter (" + username + ")",
									Toast.LENGTH_LONG).show();

						}
						// Intent twIntent=new
						// Intent(SocialSharingActivity.this,TestConnect.class);
						// startActivity(twIntent);
					}
				});

				Button linkedinbtn = (Button) myDialog
						.findViewById(R.id.btn_linkedin);
				
				
				linkedinbtn.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						
					Intent i=new Intent(SocialSharingActivity.this,LITestActivity.class);
					startActivity(i);
						
						myDialog.dismiss();
					}
				});

				myDialog.show();

				//

			}
		});
		
		
//
		
	}

	private void onTwitterClick() {
		mTwitter = new TwitterApp(this, twitter_consumer_key,
				twitter_secret_key);
		if (mTwitter.hasAccessToken()) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);

			builder.setMessage("Delete current Twitter connection?")
					.setCancelable(false)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									mTwitter.resetAccessToken();

								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
									Intent i = new Intent(
											SocialSharingActivity.this,
											TestPost.class);
									startActivity(i);
								}
							});
			final AlertDialog alert = builder.create();

			alert.show();
		} else {

			mTwitter.authorize();
		}
	}

	private final TwDialogListener mTwLoginDialogListener = new TwDialogListener() {
		@Override
		public void onComplete(String value) {
			String username = mTwitter.getUsername();
			username = (username.equals("")) ? "No Name" : username;

			// mTwitterBtn.setText("  Twitter  (" + username + ")");
			// mTwitterBtn.setChecked(true);
			// mTwitterBtn.setTextColor(Color.WHITE);
			// goBtn.setVisibility(View.VISIBLE);

			Intent i = new Intent(SocialSharingActivity.this, TestPost.class);
			startActivity(i);
			Toast.makeText(SocialSharingActivity.this,
					"Connected to Twitter as " + username, Toast.LENGTH_LONG)
					.show();
		}

		@Override
		public void onError(String value) {

			Toast.makeText(SocialSharingActivity.this,
					"Twitter connection failed", Toast.LENGTH_LONG).show();
		}
	};

//	void startAutheniticate() {
//		final LinkedInRequestToken liToken = oAuthService
//				.getOAuthRequestToken(OAUTH_CALLBACK_URL);
//
//		final String uri = liToken.getAuthorizationUrl();
//		getSharedPreferences(OAUTH_PREF, MODE_PRIVATE).edit()
//				.putString(PREF_REQTOKENSECRET, liToken.getTokenSecret())
//				.commit();
//
//		Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
//		startActivity(i);
//
//		Toast.makeText(this, "startAutheniticate END", Toast.LENGTH_LONG)
//				.show();
//	}


//	void finishAuthenticate(final Uri uri) {
//		if (uri != null && uri.getScheme().equals(OAUTH_CALLBACK_SCHEME)) {
//			final String problem = uri.getQueryParameter(OAUTH_QUERY_PROBLEM);
//			if (problem == null) {
//				final SharedPreferences pref = getSharedPreferences(OAUTH_PREF,
//						MODE_PRIVATE);
//				final LinkedInAccessToken accessToken = oAuthService
//						.getOAuthAccessToken(
//								new LinkedInRequestToken(uri
//										.getQueryParameter(OAUTH_QUERY_TOKEN),
//										pref.getString(PREF_REQTOKENSECRET,
//												null)),
//								uri.getQueryParameter(OAUTH_QUERY_VERIFIER));
//				pref.edit()
//						.putString(PREF_TOKEN, accessToken.getToken())
//						.putString(PREF_TOKENSECRET,
//								accessToken.getTokenSecret())
//						.remove(PREF_REQTOKENSECRET).commit();
//				showCurrentUser(accessToken);
//
//			} else {
//				Toast.makeText(this,
//						"Appliaction down due OAuth problem: " + problem,
//						Toast.LENGTH_LONG).show();
//				finish();
//			}
//
//		}
//	}
//	void clearTokens() {
//		getSharedPreferences(OAUTH_PREF, MODE_PRIVATE).edit()
//				.remove(PREF_TOKEN).remove(PREF_TOKENSECRET)
//				.remove(PREF_REQTOKENSECRET).commit();
//	}
//
//
//	void showCurrentUser(final LinkedInAccessToken accessToken) {
//		final LinkedInApiClient client = factory
//				.createLinkedInApiClient(accessToken);
//		try {
//			final Person p = client.getProfileForCurrentUser();
//			Toast.makeText(getApplicationContext(),
//					p.getFirstName() + " Logged in", Toast.LENGTH_LONG).show();
//			// /////////////////////////////////////////////////////////
//			// here you can do client API calls ...
//			// client.postComment(arg0, arg1);
//			// client.updateCurrentStatus(arg0);
//			// or any other API call (this sample only check for current user
//			// and shows it in TextView)
//
//			Network network = client.getNetworkUpdates(EnumSet
//					.of(NetworkUpdateType.STATUS_UPDATE));
//
//			System.out.println("Total updates fetched:"
//					+ network.getUpdates().getTotal());
//			for (Update update : network.getUpdates().getUpdateList()) {
//				System.out.println("-------------------------------");
//				System.out.println(update.getUpdateKey()
//						+ ":"
//						+ update.getUpdateContent().getPerson().getFirstName()
//						+ " "
//						+ update.getUpdateContent().getPerson().getLastName()
//						+ "->"
//						+ update.getUpdateContent().getPerson()
//								.getCurrentStatus());
//				if (update.getUpdateComments() != null) {
//					System.out.println("Total comments fetched:"
//							+ update.getUpdateComments().getTotal());
//					for (UpdateComment comment : update.getUpdateComments()
//							.getUpdateCommentList()) {
//						System.out.println(comment.getPerson().getFirstName()
//								+ " " + comment.getPerson().getLastName()
//								+ "->" + comment.getComment());
//					}
//				}
//			}
//
//			// ///////////////////
//
//			// System.out.println("********Write to the  share - using XML********");
//			// //This basic shares some basic information on the users activity
//			// stream
//			// //https://developer.linkedin.com/documents/share-api
//			// url = "http://api.linkedin.com/v1/people/~/shares";
//			// request = new OAuthRequest(Verb.POST, url);
//			// request.addHeader("Content-Type", "text/xml");
//			// //Make an XML document
//			// Document doc = DocumentHelper.createDocument();
//			// Element share = doc.addElement("share");
//			// share.addElement("comment").addText("Guess who is testing the LinkedIn REST APIs");
//			// Element content = share.addElement("content");
//			// content.addElement("title").addText("A title for your share");
//			// content.addElement("submitted-url").addText("http://developer.linkedin.com");
//			// share.addElement("visibility").addElement("code").addText("anyone");
//			// request.addPayload(doc.asXML());
//			// service.signRequest(accessToken, request);
//			// response = request.send();
//			// //there is no body just a header
//			// System.out.println(response.getBody());
//			// System.out.println(response.getHeaders().toString());
//			// System.out.println();System.out.println();
//
//			// /////////////////////////////////////////////////////////
//			tv.setText(p.getLastName() + ", " + p.getFirstName());
//		} catch (LinkedInApiClientException ex) {
//			clearTokens();
//
//			Toast.makeText(
//					this,
//					"Appliaction down due LinkedInApiClientException: "
//							+ ex.getMessage()
//							+ " Authokens cleared - try run application again.",
//					Toast.LENGTH_LONG).show();
//
//			finish();
//		}
//
//	}

	
//	@Override
//	protected void onNewIntent(Intent intent) {
//		finishAuthenticate(intent.getData());
//	}

}
/**
 * This example shows how to post status to Twitter.
 * 
 * @author Lorensius W. L. T <lorenz@londatiga.net>
 * 
 * http://www.londatiga.net
 */

package com.encros.twitter;


import com.encros.SocialSharing.R;
import com.encros.twitter.TwitterApp.TwDialogListener;


import android.app.Activity;
import android.graphics.drawable.Drawable;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.view.View;
import android.view.View.OnClickListener;

import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Button;

public class TestPost extends Activity {
	private TwitterApp mTwitter;
	private CheckBox mTwitterBtn;
	private String username = "";
	private boolean postToTwitter = false;
	
	private static final String twitter_consumer_key = "mpuWRi6Pfpg4Ard030d5w";
	private static final String twitter_secret_key = "MIo5wVfwLL77Qc2ub4BEGAafvKpQvq5IyWHn7YCRU";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.post);
		
		Button postBtn 				= (Button) findViewById(R.id.button1);
		final EditText reviewEdit   = (EditText) findViewById(R.id.revieew);
		
		postBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String review = reviewEdit.getText().toString();
				
				if(reviewEdit.getText().toString().equals(""))
				{
					showeror();
				}
				else
				{
				if (review.equals("")) return;
				
				postReview(review);
				
				if (postToTwitter) postToTwitter(review);
			}
			}

			private void showeror() {
				// TODO Auto-generated method stub
				Drawable err_indiactor = getResources().getDrawable(
						R.drawable.error_icon);
			 reviewEdit.setCompoundDrawablesWithIntrinsicBounds(null, null,
						err_indiactor, null);
				reviewEdit.setError("Please enter some thing!!!");
			
			}
		});

		mTwitter = new TwitterApp(this, twitter_consumer_key,twitter_secret_key);
		
		mTwitter.setListener(mTwLoginDialogListener);

		mTwitterBtn	= (CheckBox) findViewById(R.id.twitterCheck);

		mTwitterBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mTwitter.hasAccessToken()) {
					postToTwitter = mTwitterBtn.isChecked();
				} else {
					mTwitterBtn.setChecked(false);
					mTwitter.authorize();
				}
			}
		});
		
		if (mTwitter.hasAccessToken()) {
			username 	= mTwitter.getUsername();
			username	= (username.equals("")) ? "No Name" : username;
			
			mTwitterBtn.setText("  Twitter  (" + username + ")");
		}
	}
	
	private void postReview(String review) {
		//post to server
		
		Toast.makeText(this, "Review posted", Toast.LENGTH_SHORT).show();
	}
	
	private void postToTwitter(final String review) {
		new Thread() {
			@Override
			public void run() {
				int what = 0;
				
				try {
					mTwitter.updateStatus(review);
				} catch (Exception e) {
					what = 1;
				}
				
				mHandler.sendMessage(mHandler.obtainMessage(what));
			}
		}.start();
	}
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			String text = (msg.what == 0) ? "Posted to Twitter" : "Post to Twitter failed";
			
			Toast.makeText(TestPost.this, text, Toast.LENGTH_SHORT).show();
		}
	};
	private final TwDialogListener mTwLoginDialogListener = new TwDialogListener() {
		@Override
		public void onComplete(String value) {
			username 	= mTwitter.getUsername();
			username	= (username.equals("")) ? "No Name" : username;
		
			mTwitterBtn.setText("  Twitter  (" + username + ")");
			mTwitterBtn.setChecked(true);
			
			postToTwitter = true;
			
			Toast.makeText(TestPost.this, "Connected to Twitter as " + username, Toast.LENGTH_LONG).show();
		}
		
		@Override
		public void onError(String value) {
			mTwitterBtn.setChecked(false);
			
			Toast.makeText(TestPost.this, "Twitter connection failed", Toast.LENGTH_LONG).show();
		}
	};
}
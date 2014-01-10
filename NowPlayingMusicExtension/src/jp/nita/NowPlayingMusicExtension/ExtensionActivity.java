/*
 * Copyright (C) 2011 Sony Ericsson Mobile Communications AB.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package jp.nita.NowPlayingMusicExtension;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import jp.nita.NowPlayingMusicExtension.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.format.Time;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.*;
import com.facebook.android.Facebook.*;

@SuppressWarnings("deprecation")
public class ExtensionActivity extends Activity implements OnClickListener {
	final static Handler handler = new Handler();
	
	private static final String ApiKey = "508033875922009";

	private static final String PREF_KEY = "NowPlayingMusicExtension";  
	private static final String KEY_TEXT_1 = "templete";
	private static final String KEY_TEXT_2 = "templete2";
	private static final String KEY_TEXT_3 = "templete3";
	private static final String KEY_TEXT_4 = "templete4";
	private static final String KEY_TEXT_QUIT = "quitAfterSharing";
	private static final String KEY_TEXT_URL = "shareAsUrl";

	private static final int PICKUP_SEND_TO_APP = 1;

	private Facebook facebook = null;
	private AsyncFacebookRunner asyncFbRunner = null;
	
	Cursor trackCursor;

	Uri trackUri;
	String uri;

	String title;
	String artist;
	String album;
	String duration;
	String composer;
	String year;
	String trackno;
	String mime;

	String template1;
	String template2;
	String template3;
	String template4;
	boolean quitAfterSharing;
	boolean shareAsUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		facebook = new Facebook(ApiKey);
		asyncFbRunner = new AsyncFacebookRunner(facebook);

		Intent intent = getIntent();

		// Retrieve the URI from the intent, this is a URI to a MediaStore audio
		// file
		trackUri = intent.getData();

		// Use it to query the media provider
		initForm();

		findViewById(R.id.tweet).setOnClickListener(this);
		findViewById(R.id.apply).setOnClickListener(this);
		findViewById(R.id.share).setOnClickListener(this);
		findViewById(R.id.cancel).setOnClickListener(this);
		findViewById(R.id.information).setOnClickListener(this);
		findViewById(R.id.settings).setOnClickListener(this);
		findViewById(R.id.facebook).setOnClickListener(this);

		applyDefault();
	}

	@Override
	public void onResume(){
		super.onResume();
		initForm();
	}

	public void initForm(){
		trackCursor = getContentResolver().query(
				trackUri,
				new String[] {
						MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST,
						MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.DURATION,
						MediaStore.Audio.Media.COMPOSER, MediaStore.Audio.Media.YEAR,
						MediaStore.Audio.Media.TRACK, MediaStore.Audio.Media.MIME_TYPE,
						MediaStore.Audio.Media.DATA
				}, null, null, null);

		if (trackCursor != null) {
			try {
				if (trackCursor.moveToFirst()) {

					// And retrieve the wanted information
					SharedPreferences pref=getSharedPreferences(PREF_KEY,Activity.MODE_PRIVATE);

					template1=pref.getString(KEY_TEXT_1,getString(R.string.content_default));
					template2=pref.getString(KEY_TEXT_2,getString(R.string.content_default_2));
					template3=pref.getString(KEY_TEXT_3,getString(R.string.content_default_3));
					template4=pref.getString(KEY_TEXT_4,getString(R.string.content_default_4));
					quitAfterSharing=pref.getBoolean(KEY_TEXT_QUIT,false);
					shareAsUrl=pref.getBoolean(KEY_TEXT_URL,true);

					Time time = new Time();
					time.set(trackCursor.getLong(trackCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
					duration=time.format("%M:%S");

					try{
						title=trackCursor.getString(trackCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
					}finally{
						if(title==null) title="";
					}
					try{
						artist=trackCursor.getString(trackCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
					}finally{
						if(artist==null) artist="";
					}
					try{
						album=trackCursor.getString(trackCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
					}finally{
						if(album==null) album="";
					}
					try{
						uri=trackCursor.getString(trackCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
					}finally{
						if(uri==null) uri="";
					}
					try{
						composer=trackCursor.getString(trackCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.COMPOSER));
					}finally{
						if(composer==null) composer="";
					}
					try{
						year=trackCursor.getString(trackCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR));
					}finally{
						if(year==null) year="";
					}
					try{
						trackno=trackCursor.getString(trackCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK));
					}finally{
						if(trackno==null) trackno="";
						else{
							int src;
							try{
								src=Integer.parseInt(trackno);
							}catch(Exception e){
								src=0;
							}
							int disc=src/1000;
							int trk=src%1000;
							if(disc>0) trackno=""+trk+" ("+getString(R.string.info_disc)+" "+disc+")";
							else trackno=""+trk;
						}
					}
					try{
						mime=trackCursor.getString(trackCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE));
					}finally{
						if(mime==null) mime="";
					}
				}
			} finally {
				trackCursor.close();
			}
		}
	}
	
	public void applyDefault(){
		String tweetContent;
		SharedPreferences pref=getSharedPreferences(PREF_KEY,Activity.MODE_PRIVATE);
		tweetContent=pref.getString(KEY_TEXT_1,getString(R.string.content_default));
		tweetContent=applyTemplate(tweetContent);
		((TextView)findViewById(R.id.textField)).setText(tweetContent);
	}

	public String applyTemplate(String param){
		param=param.replace("$t",title);
		param=param.replace("$a",artist);
		param=param.replace("$l",album);
		param=param.replace("$d",duration);
		//param=param.replace("$c",composer);
		param=param.replace("$y",year);
		return param;
	}

	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
		((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public void onClick(View arg0) {
		((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		if(arg0==(View)findViewById(R.id.tweet)){
			try {
				String msg=((TextView)findViewById(R.id.textField)).getText().toString();
				if(shareAsUrl&&(msg.indexOf("http://")==0||msg.indexOf("https://")==0)){
					Uri uri=Uri.parse(msg);
					Intent intent = new Intent(Intent.ACTION_VIEW,uri);
					startActivityForResult(intent,PICKUP_SEND_TO_APP);
				}else{
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_SEND);
					intent.setType("text/plain");
					intent.putExtra(Intent.EXTRA_TEXT, ((TextView)findViewById(R.id.textField)).getText().toString());
					startActivityForResult(intent,PICKUP_SEND_TO_APP);
				}
			} catch (Exception e) {
				Log.d("ExampleExtensionActivity", "Error");
				e.printStackTrace();
			}
		}else if(arg0==(View)findViewById(R.id.apply)){
			CharSequence list[]=new String[5];
			SharedPreferences pref=getSharedPreferences(PREF_KEY,Activity.MODE_PRIVATE);
			template1=pref.getString(KEY_TEXT_1,getString(R.string.content_default));
			template2=pref.getString(KEY_TEXT_2,getString(R.string.content_default_2));
			template3=pref.getString(KEY_TEXT_3,getString(R.string.content_default_3));
			template4=pref.getString(KEY_TEXT_4,getString(R.string.content_default_4));
			list[0]="1: "+applyTemplate(template1);
			list[1]="2: "+applyTemplate(template2);
			list[2]="3: "+applyTemplate(template3);
			list[3]="4: "+applyTemplate(template4);
			list[4]=getString(R.string.edit_template);
			new AlertDialog.Builder(ExtensionActivity.this)
			.setTitle(getString(R.string.apply_template))
			.setItems(list,new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					switch(arg1){
					case 0:
						((TextView)findViewById(R.id.textField)).setText(applyTemplate(template1));
						break;
					case 1:
						((TextView)findViewById(R.id.textField)).setText(applyTemplate(template2));
						break;
					case 2:
						((TextView)findViewById(R.id.textField)).setText(applyTemplate(template3));
						break;
					case 3:
						((TextView)findViewById(R.id.textField)).setText(applyTemplate(template4));
						break;
					case 4:
						Intent intent=new Intent(ExtensionActivity.this,PreferencesActivity.class);
						intent.setAction(Intent.ACTION_VIEW);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
						break;
					}
				}
			}).show();
		}else if(arg0==(View)findViewById(R.id.share)){
			try {
				Uri trackUri = getIntent().getData();
				String ext=uri.substring(uri.lastIndexOf(".")+1);
				String type=MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_SEND);
				intent.setType(type);
				intent.putExtra(Intent.EXTRA_STREAM, trackUri);
				startActivityForResult(intent,PICKUP_SEND_TO_APP);
			} catch (Exception e) {
				Log.d("ExampleExtensionActivity", "Error");
				e.printStackTrace();
			}
		}else if(arg0==(View)findViewById(R.id.cancel)){
			finish();
		}else if(arg0==(View)findViewById(R.id.information)){
			Intent intent=new Intent(this,InformationActivity.class);
			intent.setAction(Intent.ACTION_VIEW);
			intent.putExtra("title",title);
			intent.putExtra("artist",artist);
			intent.putExtra("album",album);
			intent.putExtra("duration",duration);
			intent.putExtra("year",year);
			intent.putExtra("trackno",trackno);
			intent.putExtra("composer",composer);
			intent.putExtra("mime",mime);
			intent.putExtra("uri",uri);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}else if(arg0==(View)findViewById(R.id.settings)){
			Intent intent=new Intent(this,PreferencesActivity.class);
			intent.setAction(Intent.ACTION_VIEW);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}else if(arg0==(View)findViewById(R.id.facebook)){
			
			facebook.authorize(ExtensionActivity.this
                    , new String[] {"publish_stream"}
                    , new DialogListener(){
				@Override
                public void onComplete(Bundle values) {
					String postStr = ((TextView)findViewById(R.id.textField)).getText().toString();
					
                    Bundle params = new Bundle();
                    params.putString("message",postStr);
                        asyncFbRunner.request("me/feed",params,"POST", new PostRequestListener(), null);
                }

                @Override
                public void onFacebookError(FacebookError e) {
                	showToast(ExtensionActivity.this,getString(R.string.posting_failed));
                }

                @Override
                public void onError(DialogError e) {
                	showToast(ExtensionActivity.this,getString(R.string.posting_failed));
                }

				@Override
				public void onCancel() {
					showToast(ExtensionActivity.this,getString(R.string.posting_cancelled));
				}
			});
		}
	}
	
	public class PostRequestListener implements AsyncFacebookRunner.RequestListener{
	    @Override
	    public void onFacebookError(FacebookError e, Object state) {
	    	showToast(ExtensionActivity.this,getString(R.string.posting_failed));
	    }
	 
	    @Override
	    public void onComplete(String response, Object state) {
	    	showToast(ExtensionActivity.this,getString(R.string.posting_completed));
	    	if(quitAfterSharing) ExtensionActivity.this.finish();
	    }

		@Override
		public void onIOException(IOException e, Object state) {
			showToast(ExtensionActivity.this,getString(R.string.posting_failed));
		}

		@Override
		public void onFileNotFoundException(FileNotFoundException e,
				Object state) {
			showToast(ExtensionActivity.this,getString(R.string.posting_failed));
		}

		@Override
		public void onMalformedURLException(MalformedURLException e,
				Object state) {
			showToast(ExtensionActivity.this,getString(R.string.posting_failed));
		}
	}

	public void showAlert(){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle(getString(R.string.error));
		alertDialogBuilder.setMessage(getString(R.string.error_message));
		alertDialogBuilder.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	//メニューのアイテムが選択された際に起動される。
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		if(item.getItemId()==R.id.edit_template){
			Intent intent=new Intent(this,PreferencesActivity.class);
			intent.setAction(Intent.ACTION_VIEW);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
		if(item.getItemId()==R.id.information){
			
			Intent intent=new Intent(this,InformationActivity.class);
			intent.setAction(Intent.ACTION_VIEW);
			intent.putExtra("title",title);
			intent.putExtra("artist",artist);
			intent.putExtra("album",album);
			intent.putExtra("duration",duration);
			intent.putExtra("year",year);
			intent.putExtra("trackno",trackno);
			intent.putExtra("composer",composer);
			intent.putExtra("mime",mime);
			intent.putExtra("uri",uri);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode,resultCode,data);
		if(requestCode==PICKUP_SEND_TO_APP){
			if(quitAfterSharing) finish();	
		}else{
			facebook.authorizeCallback(requestCode, resultCode, data);
		}
	}
	
	public static void showToast(final Context context,final String title){
		new Thread(new Runnable(){
			@Override
			public void run() {
				handler.post(new Runnable() {
					public void run() {
						Toast.makeText(context, title, Toast.LENGTH_LONG).show();
					}
				});
			}
		}).start();
	}

}

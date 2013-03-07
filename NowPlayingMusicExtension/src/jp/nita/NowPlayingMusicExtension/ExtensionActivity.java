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

import jp.nita.NowPlayingMusicExtension.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.Time;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.MimeTypeMap;
import android.widget.TextView;

public class ExtensionActivity extends Activity implements OnClickListener {
	private static final String PREF_KEY = "NowPlayingMusicExtension";  
	private static final String KEY_TEXT_1 = "templete";
	private static final String KEY_TEXT_2 = "templete2";
	private static final String KEY_TEXT_3 = "templete3";
	private static final String KEY_TEXT_QUIT = "quitAfterSharing"; 
	
	Uri trackUri;
	String uri;
	
	String title;
	String artist;
	String album;
	String duration;
	String composer;
	String year;
	
	String template1;
	String template2;
	String template3;
	boolean quitAfterSharing;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

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

	}
	
	@Override
	public void onResume(){
		super.onResume();
		initForm();
	}

	public void initForm(){
		Cursor trackCursor = getContentResolver().query(
				trackUri,
				new String[] {
						MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST,
						MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.DURATION,
						MediaStore.Audio.Media.COMPOSER, MediaStore.Audio.Media.YEAR,
						MediaStore.Audio.Media.DATA
				}, null, null, null);

		if (trackCursor != null) {
			try {
				if (trackCursor.moveToFirst()) {

					// And retrieve the wanted information
					String tweetContent;
					SharedPreferences pref=getSharedPreferences(PREF_KEY,Activity.MODE_PRIVATE);
					
					template1=pref.getString(KEY_TEXT_1,getString(R.string.content_default));
					template2=pref.getString(KEY_TEXT_2,getString(R.string.content_default_2));
					template3=pref.getString(KEY_TEXT_3,getString(R.string.content_default_3));
					quitAfterSharing=pref.getBoolean(KEY_TEXT_QUIT,false);
					
					tweetContent=pref.getString(KEY_TEXT_1,getString(R.string.content_default));

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

					tweetContent=applyTemplate(tweetContent);
					((TextView)findViewById(R.id.textField)).setText(tweetContent);

				}
			} finally {
				trackCursor.close();
			}
		}
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
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public void onClick(View arg0) {
		if(arg0==(View)findViewById(R.id.tweet)){
			try {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_SEND);
				intent.setType("text/plain");
				intent.putExtra(Intent.EXTRA_TEXT, ((TextView)findViewById(R.id.textField)).getText().toString());
				startActivity(intent);
				if(quitAfterSharing) finish();
			} catch (Exception e) {
				Log.d("ExampleExtensionActivity", "Error");
				e.printStackTrace();
			}
		}else if(arg0==(View)findViewById(R.id.apply)){
			CharSequence list[]=new String[4];
			SharedPreferences pref=getSharedPreferences(PREF_KEY,Activity.MODE_PRIVATE);
			template1=pref.getString(KEY_TEXT_1,getString(R.string.content_default));
			template2=pref.getString(KEY_TEXT_2,getString(R.string.content_default_2));
			template3=pref.getString(KEY_TEXT_3,getString(R.string.content_default_3));
			list[0]="1: "+applyTemplate(template1);
			list[1]="2: "+applyTemplate(template2);
			list[2]="3: "+applyTemplate(template3);
			list[3]=getString(R.string.edit_template);
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
						Intent intent=new Intent(ExtensionActivity.this,PreferencesActivity.class);
						intent.setAction(Intent.ACTION_VIEW);
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
				startActivity(intent);
				if(quitAfterSharing) finish();
			} catch (Exception e) {
				Log.d("ExampleExtensionActivity", "Error");
				e.printStackTrace();
			}
		}else if(arg0==(View)findViewById(R.id.cancel)){
			finish();
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
		switch (item.getItemId()) {
		case R.id.edit_template:
			Intent intent=new Intent(this,PreferencesActivity.class);
			intent.setAction(Intent.ACTION_VIEW);
			startActivity(intent);
		}
		return true;
	}

}

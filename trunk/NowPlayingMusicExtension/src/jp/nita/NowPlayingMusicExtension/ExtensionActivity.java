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
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.MimeTypeMap;
import android.widget.TextView;

public class ExtensionActivity extends Activity implements OnClickListener {
	private static final String PREF_KEY = "NowPlayingMusicExtension";  
	private static final String KEY_TEXT = "templete";  
	
	Uri trackUri;
	String uri;

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
						MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.DATA
				}, null, null, null);

		if (trackCursor != null) {
			try {
				if (trackCursor.moveToFirst()) {

					// And retrieve the wanted information
					String tweetContent;
					SharedPreferences pref=getSharedPreferences(PREF_KEY,Activity.MODE_PRIVATE);
					tweetContent=pref.getString(KEY_TEXT,getString(R.string.content_default));

					tweetContent=tweetContent.replace("$t",trackCursor.getString(trackCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));
					tweetContent=tweetContent.replace("$a",trackCursor.getString(trackCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
					tweetContent=tweetContent.replace("$l",trackCursor.getString(trackCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)));
					uri=trackCursor.getString(trackCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));

					((TextView)findViewById(R.id.textField)).setText(tweetContent);

				}
			} finally {
				trackCursor.close();
			}
		}
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
			} catch (Exception e) {
				Log.d("ExampleExtensionActivity", "Error");
				e.printStackTrace();
			}
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

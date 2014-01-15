package jp.nita.NowPlayingMusicExtension;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.ClipboardManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class InformationActivity extends Activity implements OnClickListener {
	
	boolean quitAfterSharing;
	
	public static final int PICKUP_SEARCH = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_information);
		
		findViewById(R.id.copy).setOnClickListener(this);
		findViewById(R.id.ok).setOnClickListener(this);
		
		String title=getIntent().getExtras().getString("title");
		if(title==null||title=="") title=getString(R.string.info_not_available);
		((TextView)findViewById(R.id.title)).setText(title);
		
		String artist=getIntent().getExtras().getString("artist");
		if(artist==null||artist=="") artist=getString(R.string.info_not_available);
		((TextView)findViewById(R.id.artist)).setText(artist);
		
		String album=getIntent().getExtras().getString("album");
		if(album==null||album=="") album=getString(R.string.info_not_available);
		((TextView)findViewById(R.id.album)).setText(album);
		
		String duration=getIntent().getExtras().getString("duration");
		if(duration==null||duration=="") duration=getString(R.string.info_not_available);
		((TextView)findViewById(R.id.duration)).setText(duration);
		
		String year=getIntent().getExtras().getString("year");
		if(year==null||year=="") year=getString(R.string.info_not_available);
		((TextView)findViewById(R.id.year)).setText(year);
		
		String composer=getIntent().getExtras().getString("composer");
		if(composer==null||composer=="") composer=getString(R.string.info_not_available);
		((TextView)findViewById(R.id.composer)).setText(composer);
		
		String trackno=getIntent().getExtras().getString("trackno");
		if(trackno==null||trackno=="") trackno=getString(R.string.info_not_available);
		((TextView)findViewById(R.id.trackno)).setText(trackno);
		
		String mime=getIntent().getExtras().getString("mime");
		if(mime==null||mime=="") mime=getString(R.string.info_not_available);
		((TextView)findViewById(R.id.mediatype)).setText(mime);
		
		String uri=getIntent().getExtras().getString("uri");
		if(uri==null||uri=="") uri=getString(R.string.info_not_available);
		((TextView)findViewById(R.id.location)).setText(uri);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_information, menu);
		return true;
	}
	
	public boolean copy(){
		CharSequence list[]=new String[3];
		list[0]=""+getIntent().getExtras().getString("title");
		list[1]=""+getIntent().getExtras().getString("artist");
		list[2]=""+getIntent().getExtras().getString("album");
		new AlertDialog.Builder(InformationActivity.this)
		.setTitle(getString(R.string.copy))
		.setItems(list,new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				ClipboardManager clipboard=(ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
				String str="";
				switch(arg1){
				case 0:
					str=getIntent().getExtras().getString("title");
					break;
				case 1:
					str=getIntent().getExtras().getString("artist");
					break;
				case 2:
					str=getIntent().getExtras().getString("album");
					break;
				}
				clipboard.setText(str);
				ExtensionActivity.showToast(InformationActivity.this, ""+getString(R.string.copied)+" "+str);
			}
		}).show();
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId()==R.id.menu_copy){
			copy();
		}
		/*
		if(item.getItemId()==R.id.menu_search){
			SharedPreferences pref=getSharedPreferences(ExtensionActivity.PREF_KEY,Activity.MODE_PRIVATE);
			quitAfterSharing=pref.getBoolean(ExtensionActivity.KEY_TEXT_QUIT,false);
			
			CharSequence list[]=new String[3];
			list[0]=""+getIntent().getExtras().getString("title");
			list[1]=""+getIntent().getExtras().getString("artist");
			list[2]=""+getIntent().getExtras().getString("album");
			new AlertDialog.Builder(InformationActivity.this)
			.setTitle(getString(R.string.search))
			.setItems(list,new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					String str="";
					switch(arg1){
					case 0:
						str=getIntent().getExtras().getString("title");
						break;
					case 1:
						str=getIntent().getExtras().getString("artist");
						break;
					case 2:
						str=getIntent().getExtras().getString("album");
						break;
					}
					Intent intent=new Intent(Intent.ACTION_WEB_SEARCH);
					intent.putExtra(SearchManager.QUERY, str);
					startActivityForResult(intent,PICKUP_SEARCH);
				}
			}).show();
			
		}
		*/
		return true;
	}
	
	@Override
	public void onClick(View arg0) {
		if(arg0==(View)findViewById(R.id.ok)){
			finish();
		}
		if(arg0==(View)findViewById(R.id.copy)){
			copy();
		}
	}

}

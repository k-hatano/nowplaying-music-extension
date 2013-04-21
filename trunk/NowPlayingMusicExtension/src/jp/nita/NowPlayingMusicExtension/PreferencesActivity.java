package jp.nita.NowPlayingMusicExtension;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

public class PreferencesActivity extends Activity implements OnClickListener {
	private static final String PREF_KEY = "NowPlayingMusicExtension";  
	private static final String KEY_TEXT_1 = "templete";  
	private static final String KEY_TEXT_2 = "templete2";  
	private static final String KEY_TEXT_3 = "templete3";  
	private static final String KEY_TEXT_QUIT = "quitAfterSharing";  
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preferences);
		
		initForm();
		findViewById(R.id.revert).setOnClickListener(this);
		findViewById(R.id.ok).setOnClickListener(this);
	}
	
	@Override
	public void onPause(){
		super.onPause();
		savePrefs();
	}
	
	@Override
	public void onResume(){
		super.onResume();
		initForm();
	}
	
	public void savePrefs(){
		SharedPreferences pref=getSharedPreferences(PREF_KEY,Activity.MODE_PRIVATE);
		Editor editor=pref.edit();
		editor.putString(KEY_TEXT_1,((EditText)findViewById(R.id.templete1)).getText().toString());
		editor.putString(KEY_TEXT_2,((EditText)findViewById(R.id.templete2)).getText().toString());
		editor.putString(KEY_TEXT_3,((EditText)findViewById(R.id.templete3)).getText().toString());
		editor.putBoolean(KEY_TEXT_QUIT,((CheckBox)findViewById(R.id.quit_after_sharing)).isChecked());
		editor.commit();
	}
	
	public void initForm(){
		String temp1,temp2,temp3;
		boolean quitAfterSharing;
		SharedPreferences pref=getSharedPreferences(PREF_KEY,Activity.MODE_PRIVATE);
		temp1=pref.getString(KEY_TEXT_1,getString(R.string.content_default));
		temp2=pref.getString(KEY_TEXT_2,getString(R.string.content_default_2));
		temp3=pref.getString(KEY_TEXT_3,getString(R.string.content_default_3));
		quitAfterSharing=pref.getBoolean(KEY_TEXT_QUIT,false);
		
		((EditText)findViewById(R.id.templete1)).setText(temp1);
		((EditText)findViewById(R.id.templete2)).setText(temp2);
		((EditText)findViewById(R.id.templete3)).setText(temp3);
		
		((CheckBox)findViewById(R.id.quit_after_sharing)).setChecked(quitAfterSharing);
	}

	@Override
	public void onClick(View v) {
		((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		if(v==(View)findViewById(R.id.ok)){
			savePrefs();
			finish();
		}else if(v==(View)findViewById(R.id.revert)){
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
	        alertDialogBuilder.setTitle(getString(R.string.revert));
	        alertDialogBuilder.setMessage(getString(R.string.confirm_revert));
	        alertDialogBuilder.setPositiveButton(getString(R.string.ok),
	                new DialogInterface.OnClickListener() {
	                    @Override
	                    public void onClick(DialogInterface dialog, int which) {
	                    	((EditText)findViewById(R.id.templete1)).setText(getString(R.string.content_default));
	            			((EditText)findViewById(R.id.templete2)).setText(getString(R.string.content_default_2));
	            			((EditText)findViewById(R.id.templete3)).setText(getString(R.string.content_default_3));
	            			((CheckBox)findViewById(R.id.quit_after_sharing)).setChecked(false);
	                    }
	                });
	        alertDialogBuilder.setNegativeButton(getString(R.string.cancel),
	                new DialogInterface.OnClickListener() {
	                    @Override
	                    public void onClick(DialogInterface dialog, int which) {
	                    }
	                });
	        alertDialogBuilder.setCancelable(true);
	        AlertDialog alertDialog = alertDialogBuilder.create();
	        alertDialog.show();
		}
	}

}

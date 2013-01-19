package jp.nita.NowPlayingMusicExtension;

import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class PreferencesActivity extends Activity implements OnClickListener {
	private static final String PREF_KEY = "NowPlayingMusicExtension";  
	private static final String KEY_TEXT = "templete";  
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preferences);
		
		initForm();
		findViewById(R.id.ok).setOnClickListener(this);
	}
	
	@Override
	public void onResume(){
		super.onResume();
		initForm();
	}
	
	public void initForm(){
		String temp;
		SharedPreferences pref=getSharedPreferences(PREF_KEY,Activity.MODE_PRIVATE);
		temp=pref.getString(KEY_TEXT,getString(R.string.content_default));
		
		((EditText)findViewById(R.id.templete)).setText(temp);
	}

	@Override
	public void onClick(View v) {
		if(v==(View)findViewById(R.id.ok)){
			SharedPreferences pref=getSharedPreferences(PREF_KEY,Activity.MODE_PRIVATE);
			Editor editor=pref.edit();
			editor.putString(KEY_TEXT,((EditText)findViewById(R.id.templete)).getText().toString());
			editor.commit();
			finish();
		}
	}

}

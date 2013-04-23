package jp.nita.NowPlayingMusicExtension;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

public class CommonUtils {
	public static void showToast(final Context context,final String title){
		final Handler handler = new Handler();
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

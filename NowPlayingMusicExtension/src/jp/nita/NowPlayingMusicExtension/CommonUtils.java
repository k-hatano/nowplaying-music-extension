package jp.nita.NowPlayingMusicExtension;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class CommonUtils {
	public static void showDialog(Context context,String title,String message){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setTitle(title);
		alertDialogBuilder.setMessage(message);
		alertDialogBuilder.setPositiveButton(context.getString(R.string.ok),
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

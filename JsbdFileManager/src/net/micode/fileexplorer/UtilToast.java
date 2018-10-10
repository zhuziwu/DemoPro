package net.micode.fileexplorer;

import android.content.Context;
import android.widget.Toast;

public class UtilToast {
	private static Toast mToast;
	public static  void showToast(Context mContext, String text) {
		if (mToast != null) {
			mToast.setText(text);
			mToast.setDuration(Toast.LENGTH_SHORT);
		} else
			mToast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);

		mToast.show();
	}

	public static void showToast(Context mContext, int resId) {
		showToast(mContext, mContext.getResources().getString(resId));
	}

}

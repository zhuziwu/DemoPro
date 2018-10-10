package net.micode.fileexplorer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class UsbState extends BroadcastReceiver {
	public static int p = 0;
	public static int p1 = 0;
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_MEDIA_EJECT)) {
			String xx = intent.getDataString();
			String datapath = xx.replace("file://", "");
			Log.d("FileExplorer", "out-Upath  = " + datapath);
			if (datapath.equals(GlobalConsts.USB_PATH2)) {
				p = 2;
			}
			if (datapath.equals(GlobalConsts.USB_PATH3)) {
				p1 = 2;
			}
		}
		if (intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED)) {
			String xx = intent.getDataString();
			String datapath = xx.replace("file://", "");
			Log.d("FileExplorer", "in-Upath  = " + datapath);
			if (datapath.equals(GlobalConsts.USB_PATH2)) {
				p = 1;
			}
			if (datapath.equals(GlobalConsts.USB_PATH3)) {
				p1 = 1; 
			}
		}
	}
}

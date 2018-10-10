package Dialog;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import net.micode.fileexplorer.R;

public class MyEventAddAlertDialog2 {
	private Context context;
	private Dialog dialog;
	private LinearLayout lLayout_bg;
	private TextView txt_title;
	private LinearLayout dialog_Group;
	private View view = null;

	public MyEventAddAlertDialog2(Context context) {
		this.context = context;
	}

	public MyEventAddAlertDialog2 builder(int resource) {
		view = LayoutInflater.from(context).inflate(resource, null);
		lLayout_bg = (LinearLayout) view.findViewById(R.id.lLayout_bg);
		txt_title = (TextView) view.findViewById(R.id.txt_title);
		dialog_Group = (LinearLayout) view.findViewById(R.id.dialog_addmemo_Group);
		if (dialog == null) {
			dialog = new Dialog(context, R.style.AlertDialogStyle);
		}
		dialog.setCancelable(true);
		dialog.setContentView(view);
		lLayout_bg.setLayoutParams(new FrameLayout.LayoutParams(684, 317));
		return this;
	}

	public MyEventAddAlertDialog2 setSizeWind(int dwidth, int dheight) {
		this.lLayout_bg.setLayoutParams(new FrameLayout.LayoutParams(dwidth, dheight));
		return this;
	}

	public MyEventAddAlertDialog2 setTitle(String title) {
		if ("".equals(title)) {
			txt_title.setText("");
		} else {
			txt_title.setText(title);
		}
		return this;
	}

	public MyEventAddAlertDialog2 setView(View view) {
		// showLayout = true;
		if (view == null) {
			// showLayout = false;
		} else {
			dialog_Group.addView(view, 600, 220);
		}
		return this;
	}

	public void dismiss() {
		if(dialog != null){
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
		}

	}

	public void show() {
		if (dialog.isShowing()) {
			dialog.dismiss();
		} else {
			dialog.show();
		}

	}
}

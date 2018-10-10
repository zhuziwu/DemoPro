package Dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import net.micode.fileexplorer.R;

public class MyEventAddAlertDialog {
	private Context context;
	private Dialog dialog;
	private LinearLayout lLayout_bg;
	private TextView txt_title;
	private LinearLayout dialog_Group;
	private TextView btn_neg;
	private TextView btn_pos;
	private boolean showTitle = false;
	private boolean showMsg = false;
	private boolean showEditText = false;
	private boolean showLayout = false;
	private boolean showPosBtn = false;
	private boolean showNegBtn = false;
	private View view = null;

	public interface OnFinishListener {
		boolean onFinish(String text);
	}

	public MyEventAddAlertDialog(Context context) {
		this.context = context;
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		windowManager.getDefaultDisplay();
	}

	public MyEventAddAlertDialog builder(int resource) {
		view = LayoutInflater.from(context).inflate(resource, null);
		lLayout_bg = (LinearLayout) view.findViewById(R.id.lLayout_bg);
		txt_title = (TextView) view.findViewById(R.id.txt_title);
		dialog_Group = (LinearLayout) view.findViewById(R.id.dialog_addmemo_Group);
		dialog_Group.setVisibility(View.GONE);
		btn_neg = (TextView) view.findViewById(R.id.btn_neg);
		btn_neg.setVisibility(View.VISIBLE);
		btn_pos = (TextView) view.findViewById(R.id.btn_pos);
		btn_pos.setVisibility(View.VISIBLE);
		dialog = new Dialog(context, R.style.AlertDialogStyle);
		dialog.setCanceledOnTouchOutside(true); 
		dialog.setContentView(view);
		lLayout_bg.setLayoutParams(new FrameLayout.LayoutParams(697, 332));
		return this;
	}

	public MyEventAddAlertDialog setSizeWind(int dwidth, int dheight) {
		this.lLayout_bg.setLayoutParams(new FrameLayout.LayoutParams(dwidth, dheight));
		return this;
	}

	public MyEventAddAlertDialog setTitle(String title) {
		showTitle = true;
		if ("".equals(title)) {
			txt_title.setText("����");
		} else {
			txt_title.setText(title);
		}
		return this;
	}

	public MyEventAddAlertDialog setTitleDate(String dateStr) {
		// TextView titleDate = (TextView)view.findViewById(R.id.eventtoday);
		// titleDate.setText(dateStr);
		return this;
	}

	public MyEventAddAlertDialog setEditText(String msg) {
		showEditText = true;
		if ("".equals(msg)) {
			// edittxt_result.setHint("����");
		} else {
			// edittxt_result.setText(msg);
		}
		return this;
	}

	public String getResult() {
		return "";
	}

	public MyEventAddAlertDialog setMsg(String msg) {
		showMsg = true;
		if ("".equals(msg)) {
		} else {
		}
		return this;
	}

	public MyEventAddAlertDialog setView(View view) {
		showLayout = true;
		if (view == null) {
			showLayout = false;
		} else {
			dialog_Group.addView(view, android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		return this;
	}

	public MyEventAddAlertDialog setCancelable(boolean cancel) {
		dialog.setCancelable(cancel);
		return this;
	}

	public MyEventAddAlertDialog setPositiveButton(String text, final OnClickListener listener) {
		showPosBtn = true;
		if ("".equals(text)) {
			btn_pos.setText("ȷ��");
		} else {
			btn_pos.setText(text);
		}
		btn_pos.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				listener.onClick(v);
				// dialog.dismiss();
			}
		});
		return this;
	}

	public MyEventAddAlertDialog setNegativeButton(String text, final OnClickListener listener) {
		showNegBtn = true;
		if ("".equals(text)) {
			btn_neg.setText("ȡ��");
		} else {
			btn_neg.setText(text);
		}
		btn_neg.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				listener.onClick(v);
				// dialog.dismiss();
				missMydialog();
			}
		});
		return this;
	}

	public void missMydialog() {
		dialog.dismiss();
	}

	private void setLayout() {
		if (!showTitle && !showMsg) {
			// txt_title.setText("��ʾ");
			// txt_title.setVisibility(View.VISIBLE);
		}

		if (showTitle) {
			// txt_title.setVisibility(View.VISIBLE);
		}

		if (showEditText) {
			// edittxt_result.setVisibility(View.VISIBLE);
		}

		if (showMsg) {
			// txt_msg.setVisibility(View.VISIBLE);
		}

		if (showLayout) {
			dialog_Group.setVisibility(View.VISIBLE);
			// dialog_marBottom.setVisibility(View.GONE);
		}

		if (!showPosBtn && !showNegBtn) {
			// btn_pos.setText("ȷ��");
			// btn_pos.setVisibility(View.VISIBLE);
			// btn_pos.setBackgroundResource(R.drawable.alertdialog_single_selector);
			// btn_pos.setOnClickListener(new OnClickListener() {
			// @Override
			// public void onClick(View v) {
			// dialog.dismiss();
			// }
			// });
		}

		if (showPosBtn && showNegBtn) {
			// btn_pos.setVisibility(View.VISIBLE);
			// btn_pos.setBackgroundResource(R.drawable.alertdialog_right_selector);
			// btn_neg.setVisibility(View.VISIBLE);
			// btn_neg.setBackgroundResource(R.drawable.alertdialog_left_selector);
			// img_line.setVisibility(View.VISIBLE);
		}

		if (showPosBtn && !showNegBtn) {
			// btn_pos.setVisibility(View.VISIBLE);
			// btn_pos.setBackgroundResource(R.drawable.alertdialog_single_selector);
		}

		if (!showPosBtn && showNegBtn) {
			// btn_neg.setVisibility(View.VISIBLE);
			// btn_neg.setBackgroundResource(R.drawable.alertdialog_single_selector);
		}
	}

	public void show() {
		setLayout();
		dialog.show();
	}
}

package net.micode.fileexplorer;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FileListItem extends LinearLayout {
	private static Context mContext;
	private static FileViewInteractionHub mFileViewInteractionHub;
	private FileInfo lFileInfo;
//	public FileListItem(Context context) {
//		super(context);
//		mContext = context;
//	}
    public FileListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

	public final void bind(FileInfo file, FileViewInteractionHub f, boolean isShowDelete,
			boolean isShowXq) {
		mFileViewInteractionHub = f;
		lFileInfo = file;
		FrameLayout ccc = (FrameLayout) findViewById(R.id.file_checkbox_area);
		ccc.setVisibility(isShowDelete ? View.VISIBLE : View.GONE);
		ImageView gantanhao = (ImageView) findViewById(R.id.gantanhao);
		ImageView lFileImage = (ImageView) findViewById(R.id.file_image);
		gantanhao.setVisibility(isShowXq ? View.VISIBLE : View.GONE);
		gantanhao.findViewById(R.id.gantanhao).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mFileViewInteractionHub.onOperationInfo(lFileInfo);
			}
		});
		if (mFileViewInteractionHub.isMoveState()) {
			lFileInfo.Selected = mFileViewInteractionHub.isFileSelected(lFileInfo.filePath);
		}
		ImageView checkbox = (ImageView) findViewById(R.id.file_checkbox);
			checkbox.setImageResource(lFileInfo.Selected ? R.drawable.file_select : R.drawable.file_noselect);
			checkbox.setTag(lFileInfo);
			ccc.setOnClickListener(checkClick);
			setSelected(lFileInfo.Selected);

		Log.d("hhjj", "lFileInfo.    = " + lFileInfo.filePath);
		if (lFileInfo.filePath.equals(GlobalConsts.SDCARD_PATH) || lFileInfo.filePath.equals(GlobalConsts.USB_PATH3)
				|| lFileInfo.filePath.equals(GlobalConsts.USB_PATH2)) {
			if (lFileInfo.filePath.equals(GlobalConsts.SDCARD_PATH)) {
			setText(this, R.id.file_name, getResources().getString(R.string.NBCC));
			}
			if (lFileInfo.filePath.equals(GlobalConsts.USB_PATH3)) {
			setText(this, R.id.file_name, "USB2");
			}
			if (lFileInfo.filePath.equals(GlobalConsts.USB_PATH2)) {
			setText(this, R.id.file_name, "USB1");
			}
			lFileImage.setImageResource(R.drawable.icon_folder);
			return;
		} else {
			setText(this, R.id.file_name, lFileInfo.fileName);
		}

		if (lFileInfo.IsDir) {
			lFileImage.setImageResource(R.drawable.icon_folder);
		} else {
			FileIconHelper.setIcon(lFileInfo, lFileImage);
		}

	}
	

	private boolean setText(View view, int id, String text) {
		TextView textView = (TextView) view.findViewById(id);
		if (textView == null)
			return false;
		textView.setText(text);
		return true;
	}


	
	//点击子项选中文件。
	private OnClickListener checkClick = new OnClickListener() {
		public void onClick(View v) {
			ImageView img = (ImageView) v.findViewById(R.id.file_checkbox);
			assert (img != null && img.getTag() != null);
			FileInfo tag = (FileInfo) img.getTag();
			tag.Selected = !tag.Selected;
			if (mFileViewInteractionHub.onCheckItem(tag, v)) {
				img.setImageResource(tag.Selected ? R.drawable.file_select : R.drawable.file_noselect);
			} else {
				tag.Selected = !tag.Selected;
			}
		}
	};

}

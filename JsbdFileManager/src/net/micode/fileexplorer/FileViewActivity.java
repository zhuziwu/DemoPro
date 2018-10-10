package net.micode.fileexplorer;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import net.micode.fileexplorer.FileSortHelper.SortMethod;

@SuppressLint({ "Override", "NewApi", "HandlerLeak" })
public class FileViewActivity extends Activity implements IFileInteractionListener, OnClickListener {
	private SdcardStateChanageReceiver sdcardStateReceiver;
	private String TAG = "FileExplorer";
	private String TAG2 = "LLV";
	private ListView mFileListView;
	private ImageButton ib_sort;
	private ImageButton ibrefresh;
	private ImageButton ib_newfolder;
	private Button bt_selectall;
	private Button bt_rename;
	private Button bt_del;
	private Button bt_cancel_tohome;
	private Button bt_copy;
	private Button bt_cut;
	private TextView device_name;
	private RelativeLayout buttom_tools;
	private LinearLayout buttom_home;
	private LinearLayout upOnLevel;
	private RelativeLayout ll_fz;
	private Button bt_paste;
	private Button bt_cancel;
	private EditText ed;
	private String edString;
	private ImageButton file_icon;
	private TextView bt_lastpath;
	private View emptyView;
	private boolean isShowxq = false;
	private int pop = 111111;
	private FileViewInteractionHub mFileViewInteractionHub;
	private FileIconHelper mFileIconHelper;
	private ArrayList<FileInfo> mFileNameList;
	private Activity mActivity;
	private FileListAdapter mAdapter;
	private boolean selectall = false;
	private Drawable drawableTop;
	private boolean NBCC = true;
	private File[] listFiles = null;
	private String SDpath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题栏
		setContentView(R.layout.file_explorer_list);
		mActivity = this;
		mFileIconHelper = new FileIconHelper();
		mFileViewInteractionHub = new FileViewInteractionHub(this);
		ib_sort = (ImageButton) findViewById(R.id.bt_sort);
		ibrefresh = (ImageButton) findViewById(R.id.btrefresh);
		ib_newfolder = (ImageButton) findViewById(R.id.bt_newfolder);
		bt_rename = (Button) findViewById(R.id.bt_rename);
		bt_del = (Button) findViewById(R.id.bt_del);
		bt_cancel_tohome = (Button) findViewById(R.id.bt_cancel_tohome);
		bt_selectall = (Button) findViewById(R.id.bt_selectall);
		bt_copy = (Button) findViewById(R.id.bt_copy);
		bt_cut = (Button) findViewById(R.id.bt_cut);
		buttom_tools = (RelativeLayout) findViewById(R.id.buttom_tools);
		upOnLevel = (LinearLayout) findViewById(R.id.upOnLevel);
		buttom_home = (LinearLayout) findViewById(R.id.buttom_home);
		device_name = (TextView) findViewById(R.id.device_name);
		ll_fz = (RelativeLayout) findViewById(R.id.buttom_toolsaction);
		bt_paste = (Button) findViewById(R.id.bt_paste);
		bt_cancel = (Button) findViewById(R.id.bt_cancel);
		file_icon = (ImageButton) findViewById(R.id.file_icon);
		bt_lastpath = (TextView) findViewById(R.id.bt_lastpath);
		emptyView = findViewById(R.id.empty_view);
		mFileListView = (ListView) findViewById(R.id.file_path_list);
		ed = (EditText) findViewById(R.id.search_filter);

		bt_paste.setOnClickListener(this);
		bt_cancel.setOnClickListener(this);
		ib_sort.setOnClickListener(this);
		ibrefresh.setOnClickListener(this);
		ib_newfolder.setOnClickListener(this);
		bt_rename.setOnClickListener(this);
		bt_del.setOnClickListener(this);
		bt_cancel_tohome.setOnClickListener(this);
		bt_selectall.setOnClickListener(this);
		bt_copy.setOnClickListener(this);
		bt_cut.setOnClickListener(this);
		upOnLevel.setOnClickListener(this);
		ed.setOnEditorActionListener(new OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					edString = ed.getText().toString();
					if (!edString.equals("")) {
						mFileViewInteractionHub.refreshQueryList(edString);
					} else {
						mFileViewInteractionHub.refreshFileList();
					}
					ed.setText("");
					disInput(ed);
				}
				return false;
			}
		});
		ed.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ed.setCursorVisible(true);
			}
		});
		File file1 = new File(GlobalConsts.USB_PATH3);
		File file = new File(GlobalConsts.USB_PATH2);
		if (file1.exists() && (file1.canExecute())) {
			GlobalConsts.USBSTATE3 = true;
			if (UsbState.p1 == 2) {
				GlobalConsts.USBSTATE3 = false;
			}
		}
		if (file.exists() && (file.canExecute())) {
			GlobalConsts.USBSTATE2 = true;
			if (UsbState.p == 2) {
				GlobalConsts.USBSTATE2 = false;
			}
		} else {
			Log.d("USBSTATE2", " USBSTATE2 = " + GlobalConsts.USBSTATE2);
		}
		mFileViewInteractionHub.setRootPath(GlobalConsts.MAIN_USB_PATH);
		mFileNameList = new ArrayList<FileInfo>();
		mAdapter = new FileListAdapter(mActivity, R.layout.file_browse_item, mFileNameList, mFileViewInteractionHub);
		mFileListView.setAdapter(mAdapter);
		mFileViewInteractionHub.refreshFileList();
		regReceiverSDcard();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mFileNameList = null;
		unregisterReceiver(sdcardStateReceiver);
	}

	public boolean onBack() {
		return mFileViewInteractionHub.onBackPressed();
	}

	public boolean onRefreshFileList(String path, final FileSortHelper sort) {
		Log.i("USBSTATE2", "path" + path);
		ed.setCursorVisible(false);
		File file = new File(path);
		if (!file.exists() || file == null) {
			return false;
		}
		if (path.equals(GlobalConsts.MAIN_USB_PATH)) {
			listFiles = null;
			listFiles = file.listFiles(new FileFilter() {
				public boolean accept(File pathname) {
					Log.d("USBSTATE2", " pathname = " + pathname);
					if (NBCC) {
						if (pathname.getAbsolutePath().equals(GlobalConsts.SDCARD_PATH)) {
							return true;
						}
					} else {
						if (pathname.getAbsolutePath().equals(GlobalConsts.USB_PATH3)) {
							if (GlobalConsts.USBSTATE3) {
								return true;
							} else {
								return false;
							}
						}
						if (pathname.getAbsolutePath().equals(GlobalConsts.USB_PATH2)) {
							if (GlobalConsts.USBSTATE2) {
								return true;
							} else {
								return false;
							}
						}
					}
					return false;
				}
			});
			Log.d(TAG, "走路径  =  " + path);
		} else {
			Log.d(TAG, "路径能不能读" + file.canRead());
			listFiles = null;
			if (file.isDirectory()) {
				listFiles = file.listFiles(new FileFilter() {
					@Override
					public boolean accept(File pathname) {
						if (pathname.getAbsolutePath().equals(GlobalConsts.XUNFEI)
								|| pathname.getAbsolutePath().equals(GlobalConsts.Android)
								|| pathname.getName().substring(0, 1).equals(".") || pathname.isHidden()
								|| !pathname.canRead()) {
							return false;
						}
						return true;
					}
				});
			}
		}
		
		mFileNameList.clear();
		if (listFiles == null || listFiles.length == 0) {
			Log.d(TAG, "listFiles == null");
			mAdapter.notifyDataSetChanged();
			showEmptyView(true);
			return true;
		}
		Log.d(TAG2, "listFiles数量   = " + listFiles.length);

		if (listFiles != null) {
			for (File child : listFiles) {
				FileInfo lFileInfo = Util.GetFileInfo(child);
				mFileNameList.add(lFileInfo);
				showEmptyView(mFileNameList.size() == 0);
			}
			File dd = new File(GlobalConsts.USB_PATH2);
			if (dd.exists()) {
				FileInfo xx = Util.GetFileInfo(dd);
				mFileNameList.add(xx);
			} else {
				UtilToast.showToast(this, " ______!!!!!!______");
			}
			listFiles = null;
		}
		refreshjiemian(sort);
		drawableTop = getResources().getDrawable(R.drawable.tools_check_selectnone);
		bt_selectall.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop, null, null);
		bt_selectall.setTextColor(getResources().getColor(R.color.colorwhite));
		selectall = false;

		if (!mFileViewInteractionHub.isRoot()) {
			isShowxq = true;
			mAdapter.setIsShowXq(isShowxq);
			ed.setEnabled(true);
			bt_paste.setEnabled(true);
			bt_cancel.setEnabled(true);
			mFileListView.setOnItemLongClickListener(new OnItemLongClickListener() {
				public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
					
					show3or6(parent,buttom_tools.getVisibility());
					return true;
					
				}

			});
			file_icon.setBackground(getResources().getDrawable(R.drawable.back_p));
			bt_lastpath.setTextColor(Color.parseColor("#FFFFFF"));
			upOnLevel.setClickable(true);
			ib_newfolder.setClickable(true);

			SDpath = mFileViewInteractionHub.getCurrentPath();
			Log.i("zhUSB", "SDpath" + SDpath);
			if (SDpath.equals(GlobalConsts.USB_PATH3)) {
				device_name.setText(R.string.USB2);
			} else if (SDpath.equals(GlobalConsts.SDCARD_PATH)) {
				Log.i("SDpath--------------", "SDpath" + SDpath);
				device_name.setText(R.string.NBCC);
			} else if (SDpath.equals(GlobalConsts.USB_PATH2)) {
				device_name.setText(R.string.USB1);
			}
		} else {
			isShowxq = false;
			mAdapter.setIsShowXq(isShowxq);
			ed.setEnabled(false);
			mFileListView.setOnItemLongClickListener(null);
			file_icon.setBackground(getResources().getDrawable(R.drawable.back_n));
			bt_lastpath.setTextColor(Color.parseColor("#909090"));
			device_name.setText(R.string.app_name);
			upOnLevel.setClickable(false);
			ib_newfolder.setClickable(false);
		}
		return true;
	}
	
	private void show3or6(View view,int x){
		if(x == 0){
			buttom_tools.setVisibility(View.GONE);
			ll_fz.setVisibility(View.GONE);
			buttom_home.setVisibility(View.VISIBLE);
			mAdapter.setIsShowDelete(false);
			mFileViewInteractionHub.clearSelection();
			selectall = false;
			mFileViewInteractionHub.onOperationButtonCancel();
		}else {
			buttom_tools.setVisibility(View.VISIBLE);
			buttom_home.setVisibility(View.GONE);
			mAdapter.setIsShowDelete(true);
			clik(view);
		}
	}
	
	
	//长按list选中的子项
	public void clik(View v) {
		ImageView img = (ImageView) v.findViewById(R.id.file_checkbox);
		if(img != null && img.getTag() != null){
			FileInfo tag = (FileInfo) img.getTag();
			tag.Selected = !tag.Selected;
			if (mFileViewInteractionHub.onCheckItem(tag, v)) {
				img.setImageResource(tag.Selected ? R.drawable.file_select : R.drawable.file_noselect);
			} else {
				tag.Selected = !tag.Selected;
			}
		}
	}

	// 设置列表正在播放位置

	private void regReceiverSDcard() {
		sdcardStateReceiver = new SdcardStateChanageReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_MEDIA_REMOVED);// 如果SDCard未安装,并通过USB大容量存储共享返回
		filter.addAction(Intent.ACTION_MEDIA_MOUNTED);// 表明sd对象是存在并具有读/写权限
		filter.addAction(Intent.ACTION_MEDIA_EJECT); // 物理的拔出 SDCARD
		filter.addDataScheme("file");
		mActivity.registerReceiver(sdcardStateReceiver, filter);
	}

	private class SdcardStateChanageReceiver extends BroadcastReceiver {

		public void onReceive(Context context, Intent intent) {
			String xx = intent.getDataString();
			String datapath = xx.replace("file://", "");
			Log.d(TAG, "U-P  = " + datapath);
			
			if (intent.getAction().equals(Intent.ACTION_MEDIA_REMOVED) || intent.getAction().equals(Intent.ACTION_MEDIA_EJECT)) {
				Log.d(TAG, "U-out  = " + datapath);
				if (datapath.equals(GlobalConsts.USB_PATH2) || datapath.equals(GlobalConsts.YUSB_PATH2)) {
					GlobalConsts.USBSTATE2 = false;
				}
				if (datapath.equals(GlobalConsts.USB_PATH3) || datapath.equals(GlobalConsts.YUSB_PATH3)) {
					GlobalConsts.USBSTATE3 = false;
				}
				mFileViewInteractionHub.setRootPath(GlobalConsts.MAIN_USB_PATH);

			}
			if (intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED)) {
				Log.d(TAG, "U-in  = " + datapath);
				if (datapath.equals(GlobalConsts.USB_PATH2) || datapath.equals(GlobalConsts.YUSB_PATH2)) {
					GlobalConsts.USBSTATE2 = true;
				}
				if (datapath.equals(GlobalConsts.USB_PATH3) || datapath.equals(GlobalConsts.YUSB_PATH3)) {
					GlobalConsts.USBSTATE3 = true;
				}
				showmListView(true);
				showEmptyView(false);
				file_icon.setBackground(getResources().getDrawable(R.drawable.back_p));
				bt_lastpath.setTextColor(Color.parseColor("#FFFFFF"));
				upOnLevel.setClickable(true);
			}
			mFileViewInteractionHub.refreshFileList();
		}

	}

	private void showmListView(boolean show) {
		if (mFileListView != null) {
			mFileListView.setVisibility(show ? View.VISIBLE : View.GONE);
		}
	}

	private void showEmptyView(boolean show) {
		if (emptyView != null)
			emptyView.setVisibility(show ? View.VISIBLE : View.GONE);
	}

	public View getViewById(int id) {
		return findViewById(id);
	}

	public Context getContext() {
		return mActivity;
	}

	public void onDataChanged() {
		runOnUiThread(new Runnable() {
			public void run() {
				mAdapter.notifyDataSetChanged();
			}
		});
	}

	public void refresh() {
		mFileViewInteractionHub.refreshFileList();
	}

	public void moveToFile(ArrayList<FileInfo> files) {
		mFileViewInteractionHub.moveFileFrom(files);
	}

	public interface SelectFilesCallback {
		void selected(ArrayList<FileInfo> files);
	}

	public void startSelectFiles(SelectFilesCallback callback) {
		mFileViewInteractionHub.startSelectFiles(callback);
	}

	public FileIconHelper getFileIconHelper() {
		return mFileIconHelper;
	}

	public FileInfo getItem(int pos) {
		if (pos < 0 || pos > mFileNameList.size() - 1)
			return null;

		return mFileNameList.get(pos);
	}

	@SuppressWarnings("unchecked")
	public void sortCurrentList(FileSortHelper sort) {
		Collections.sort(mFileNameList, sort.getComparator());
		onDataChanged();
	}

	public ArrayList<FileInfo> getAllFiles() {
		return mFileNameList;
	}

	public void addSingleFile(FileInfo file) {
		mFileNameList.add(file);
		onDataChanged();
	}

	public int getItemCount() {
		return mFileNameList.size();
	}

	public void onClick(View v) {
		switch (v.getId()) {
		/* 排序 */
		case R.id.bt_sort:
			popwindow(v);
			break;
		/* 更新 */
		case R.id.btrefresh:
			mFileViewInteractionHub.refreshFileList();
			break;
		/* 新建 */
		case R.id.bt_newfolder:
			mFileViewInteractionHub.onOperationCreateFolder();
			break;
		/* 重命名 */
		case R.id.bt_rename:
			if (mFileViewInteractionHub.getSelectedFileList().size() >= 2) {
				UtilToast.showToast(mActivity, R.string.toast_onlyone);
			} else if (mFileViewInteractionHub.getSelectedFileList().size() < 1) {
				UtilToast.showToast(mActivity, R.string.toast_zuishao_one);
			} else {
				mFileViewInteractionHub.onOperationRename();
			}
			break;
		/* 删除 */
		case R.id.bt_del:
			mFileViewInteractionHub.onOperationDelete();
			break;
		/* 取消 */
		case R.id.bt_cancel_tohome:
				buttom_tools.setVisibility(View.GONE);
				ll_fz.setVisibility(View.GONE);
				buttom_home.setVisibility(View.VISIBLE);
				mFileViewInteractionHub.clearSelection();
				mAdapter.setIsShowDelete(false);
				drawableTop = getResources().getDrawable(R.drawable.tools_check_selectnone);
				bt_selectall.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop, null, null);
				bt_selectall.setTextColor(getResources().getColor(R.color.colorwhite));
				selectall = false;
//			}

			break;
		/* 全选 */
		case R.id.bt_selectall:
			if (!selectall) {
				mFileViewInteractionHub.onOperationSelectAll();
				drawableTop = getResources().getDrawable(R.drawable.tools_check_selectall);
				bt_selectall.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop, null, null);
				bt_selectall.setTextColor(getResources().getColor(R.color.colortextpress));
				selectall = true;
			} else {
				mFileViewInteractionHub.clearSelection();
				drawableTop = getResources().getDrawable(R.drawable.tools_check_selectnone);
				bt_selectall.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop, null, null);
				bt_selectall.setTextColor(getResources().getColor(R.color.colorwhite));
				selectall = false;
			}
			break;
		/* 复制 */
		case R.id.bt_copy:
			if (mFileViewInteractionHub.getSelectedFileList().size() < 1) {
				UtilToast.showToast(mActivity, R.string.toast_zuishao_one);
			} else {
				ll_fz.setVisibility(View.VISIBLE);
				buttom_home.setVisibility(View.GONE);
				buttom_tools.setVisibility(View.GONE);
				mAdapter.setIsShowDelete(false);
				mFileViewInteractionHub.onOperationCopy();
			}

			break;
		/* 剪切 */
		case R.id.bt_cut:
			if (mFileViewInteractionHub.getSelectedFileList().size() < 1) {
				UtilToast.showToast(mActivity, R.string.toast_zuishao_one);
			} else {
				ll_fz.setVisibility(View.VISIBLE);
				buttom_home.setVisibility(View.GONE);
				buttom_tools.setVisibility(View.GONE);
				mAdapter.setIsShowDelete(false);
				mFileViewInteractionHub.onOperationMove();
			}
			break;
		case R.id.upOnLevel:
			if (ll_fz.getVisibility() == 0) {
				onBack();
			} else {
				if (buttom_tools.getVisibility() == 0) {
					buttom_tools.setVisibility(View.GONE);
					ll_fz.setVisibility(View.GONE);
					buttom_home.setVisibility(View.VISIBLE);
					mAdapter.setIsShowDelete(false);
					device_name.setText(R.string.app_name);
					mFileViewInteractionHub.clearSelection();
					drawableTop = getResources().getDrawable(R.drawable.tools_check_selectnone);
					bt_selectall.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop, null, null);
					bt_selectall.setTextColor(getResources().getColor(R.color.colorwhite));
					selectall = false;
				} else {
					onBack();
				}
			}
			break;
		/* 粘贴 */
		case R.id.bt_paste:
			if (mFileViewInteractionHub.getCurrentPath().equals(GlobalConsts.MAIN_USB_PATH)) {
				UtilToast.showToast(mActivity, R.string.root_faile);
			} else {
				mFileViewInteractionHub.onOperationButtonConfirm();
			}
			break;
		case R.id.bt_cancel:
			mAdapter.setIsShowDelete(false);
			ll_fz.setVisibility(View.GONE);
			buttom_home.setVisibility(View.VISIBLE);
			buttom_tools.setVisibility(View.GONE);
			mFileViewInteractionHub.onOperationButtonCancel();
			break;
		}
	}

	private void popwindow(View v) {
		// 一个自定义的布局，作为显示的内容
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		View contentView = LayoutInflater.from(mActivity).inflate(R.layout.popupwindow_tpypesort, null);
		final PopupWindow popupWindow = new PopupWindow(contentView, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setOutsideTouchable(true);
		popupWindow.setFocusable(true);
		popupWindow.showAtLocation(v, Gravity.BOTTOM | Gravity.LEFT, 0, 0);
		RadioGroup rg = (RadioGroup) contentView.findViewById(R.id.radiogroup_sorttype);
		final RadioButton radio_sortbyname;
		final RadioButton radio_sortbytime;
		final RadioButton radio_sortbysize;
		final RadioButton radio_sortbyfiletype;
		radio_sortbyname = (RadioButton) contentView.findViewById(R.id.radio_sortbyname);
		radio_sortbytime = (RadioButton) contentView.findViewById(R.id.radio_sortbytime);
		radio_sortbysize = (RadioButton) contentView.findViewById(R.id.radio_sortbysize);
		radio_sortbyfiletype = (RadioButton) contentView.findViewById(R.id.radio_sortbyfiletype);
		switch (pop) {
		case 111111:
			pop = 111111;
			radio_sortbyname.setChecked(true);
			radio_sortbytime.setChecked(false);
			radio_sortbysize.setChecked(false);
			radio_sortbyfiletype.setChecked(false);
			mFileViewInteractionHub.onSortChanged(SortMethod.name);
			break;
		case 222222:
			pop = 222222;
			radio_sortbytime.setChecked(true);
			radio_sortbyname.setChecked(false);
			radio_sortbysize.setChecked(false);
			radio_sortbyfiletype.setChecked(false);
			mFileViewInteractionHub.onSortChanged(SortMethod.date);
			break;
		case 333333:
			pop = 333333;
			radio_sortbysize.setChecked(false);
			radio_sortbytime.setChecked(false);
			radio_sortbyname.setChecked(false);
			radio_sortbyfiletype.setChecked(true);
			mFileViewInteractionHub.onSortChanged(SortMethod.size);
			break;
		case 444444:
			pop = 444444;
			radio_sortbyfiletype.setChecked(false);
			radio_sortbysize.setChecked(true);
			radio_sortbytime.setChecked(false);
			radio_sortbyname.setChecked(false);
			mFileViewInteractionHub.onSortChanged(SortMethod.type);
			break;
		}
		rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.radio_sortbyname:
					pop = 111111;
					radio_sortbyname.setChecked(true);
					radio_sortbytime.setChecked(false);
					radio_sortbysize.setChecked(false);
					radio_sortbyfiletype.setChecked(false);
					mFileViewInteractionHub.onSortChanged(SortMethod.name);
					break;
				case R.id.radio_sortbytime:
					pop = 222222;
					radio_sortbytime.setChecked(true);
					radio_sortbyname.setChecked(false);
					radio_sortbysize.setChecked(false);
					radio_sortbyfiletype.setChecked(false);
					mFileViewInteractionHub.onSortChanged(SortMethod.date);
					break;
				case R.id.radio_sortbyfiletype:
					pop = 333333;
					radio_sortbysize.setChecked(false);
					radio_sortbytime.setChecked(false);
					radio_sortbyname.setChecked(false);
					radio_sortbyfiletype.setChecked(true);
					mFileViewInteractionHub.onSortChanged(SortMethod.size);
					break;
				case R.id.radio_sortbysize:
					pop = 444444;
					radio_sortbyfiletype.setChecked(false);
					radio_sortbysize.setChecked(true);
					radio_sortbytime.setChecked(false);
					radio_sortbyname.setChecked(false);
					mFileViewInteractionHub.onSortChanged(SortMethod.type);
					break;
				}
			}
		});
		popupWindow.showAsDropDown(v);
	}

	protected boolean disInput(EditText folderNameEditText) {
		if (folderNameEditText == null) {
			return false;
		}
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		return imm.hideSoftInputFromWindow(folderNameEditText.getWindowToken(), 0);
	}

	public boolean onRefreshQueryList(String path, FileSortHelper sort, String keyword) {
		ed.setCursorVisible(false);
		File file = new File(path);
		if (!file.exists() || !file.isDirectory()) {
			return false;
		}
		searchFile(file, sort, keyword); // 调用查询方法
		refreshjiemian(sort);
		return false;
	}

	public void refreshjiemian(FileSortHelper sort) {
		sortCurrentList(sort);
		mFileListView.post(new Runnable() {
			public void run() {
//				mFileListView.setSelection(pos); // 选中
			}
		});
	}

	public boolean searchFile(File folder, FileSortHelper msort, final String keyWord) {// 递归查找包含关键字的文件

		File[] subFolders = folder.listFiles(new FileFilter() {

			public boolean accept(File pathname) {
				if (pathname.getAbsolutePath().equals(GlobalConsts.XUNFEI)
						|| pathname.getAbsolutePath().equals(GlobalConsts.Android)
						|| pathname.getName().substring(0, 1).equals(".") || pathname.isHidden()
						|| !pathname.canRead()) {
					return false;
				}
				if (pathname.getName().toLowerCase().contains(keyWord.toLowerCase())) {
					return true;
				}
				return false;
			}
		});
		ArrayList<FileInfo> fileList = mFileNameList;
		fileList.clear();
		for (File child : subFolders) {
			if (mFileViewInteractionHub.isMoveState() && mFileViewInteractionHub.isFileSelected(child.getPath()))
				continue;
			FileInfo lFileInfo = Util.GetFileInfo(child);
			if (lFileInfo != null) {
				fileList.add(lFileInfo);
			}
		}
		showEmptyView(fileList.size() == 0);// 如果没有文件，将隐藏的“没有文件图标提示”显示出来。
		return false;
	}

	/********************************* 查询方法e **********************************************/
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			if (ll_fz.getVisibility() == 0) {
				if (!mFileViewInteractionHub.isRoot()) {
					onBack();
					Log.d("ccc", "返回的值是" + onBack());
				} else {
					ll_fz.setVisibility(View.GONE);
					buttom_home.setVisibility(View.VISIBLE);
					buttom_tools.setVisibility(View.GONE);
					mAdapter.setIsShowDelete(false);
					mFileViewInteractionHub.onOperationButtonCancel();
				}
				return true;
			}
			if (buttom_tools.getVisibility() == 0) {
				buttom_tools.setVisibility(View.GONE);
				ll_fz.setVisibility(View.GONE);
				buttom_home.setVisibility(View.VISIBLE);
				mAdapter.setIsShowDelete(false);
				mFileViewInteractionHub.clearSelection();
				drawableTop = getResources().getDrawable(R.drawable.tools_check_selectnone);
				bt_selectall.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop, null, null);
				bt_selectall.setTextColor(getResources().getColor(R.color.colorwhite));
				selectall = false;
				return true;
			}
			if (mFileViewInteractionHub.onOperationUpLevel()) {
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mFileViewInteractionHub.dismiss();
	}

	public void refreshll() {
		mFileViewInteractionHub.refreshFileList();
		buttom_tools.setVisibility(View.GONE);
		ll_fz.setVisibility(View.GONE);
		buttom_home.setVisibility(View.VISIBLE);
		mAdapter.setIsShowDelete(false);
		selectall = false;
	}
}

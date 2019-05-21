//#if def{lang} == cn
/*
 * 官网地站:http://www.mob.com
 * 技术支持QQ: 4006852216
 * 官方微信:ShareSDK   （如果发布新版本的话，我们将会第一时间通过微信将版本更新内容推送给您。如果使用过程中有任何问题，也可以通过微信与我们取得联系，我们将会在24小时内给予回复）
 * 
 * Copyright (c) 2013年 mob.com. All rights reserved.
 */
//#elif def{lang} == en
/*
 * Offical Website:http://www.mob.com
 * Support QQ: 4006852216
 * Offical Wechat Account:ShareSDK   (We will inform you our updated news at the first time by Wechat, if we release a new version. 
 * If you get any problem, you can also contact us with Wechat, we will reply you within 24 hours.)
 * 
 * Copyright (c) 2013 mob.com. All rights reserved.
 */
//#endif

package cn.sharesdk.onekeyshare.themes.classic;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.mob.MobSDK;
import com.mob.tools.gui.AsyncImageView;
import com.mob.tools.utils.DeviceHelper;
import com.mob.tools.utils.ReflectHelper;
import com.mob.tools.utils.ResHelper;

import java.util.ArrayList;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeySharePage;
import cn.sharesdk.onekeyshare.OnekeyShareThemeImpl;
import cn.sharesdk.onekeyshare.themes.classic.land.FriendListPageLand;
import cn.sharesdk.onekeyshare.themes.classic.port.FriendListPagePort;

public class EditPage extends OnekeySharePage implements OnClickListener, TextWatcher, Runnable {
	private OnekeyShareThemeImpl impl;
	protected Platform platform;
	protected ShareParams sp;
	
	protected LinearLayout llPage;
	protected RelativeLayout rlTitle;
	protected ScrollView svContent;
	protected EditText etContent;
	protected TextView tvCancel;
	protected TextView tvShare;
	protected RelativeLayout rlThumb;
	//#if def{lang} == cn
	/** 异步加载图片的控件 */
	//#elif def{lang} == en
	/** the view of loading picture asynchronously */
	//#endif
	protected AsyncImageView aivThumb;
	protected XView xvRemove;
	protected LinearLayout llBottom;
	protected TextView tvAt;
	protected TextView tvTextCouter;
	
	protected Bitmap thumb;
	protected int maxBodyHeight;
	
	public EditPage(OnekeyShareThemeImpl impl) {
		super(impl);
		this.impl = impl;
	}
	
	public void setPlatform(Platform platform) {
		this.platform = platform;
	}
	
	public void setShareParams(ShareParams sp) {
		this.sp = sp;
	}

	@Override
	protected int onSetTheme(int resid, boolean atLaunch) {
		if (isDialogMode()) {
			activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
			if (Build.VERSION.SDK_INT >= 11) {
				try {
					ReflectHelper.invokeInstanceMethod(activity, "setFinishOnTouchOutside", false);
				} catch (Throwable e) {}
			}
			return android.R.style.Theme_Dialog;
		} else {
			activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
					| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		}
		return super.onSetTheme(resid, atLaunch);
	}
	
	public void onCreate() {
		activity.getWindow().setBackgroundDrawable(new ColorDrawable(0xfff3f3f3));
	}
	
	//#if def{lang} == cn
	/** 取消分享时，执行的方法 */
	//#elif def{lang} == en
	/** the method of cacel share */
	//#endif
	private void cancelAndFinish() {
		//#if def{lang} == cn
		// 分享失败的统计
		//#elif def{lang} == en
		// a statistics of cancel sharing
		//#endif
		ShareSDK.logDemoEvent(5, platform);
		finish();
	}
	
	//#if def{lang} == cn
	/** 执行分享时的方法 */
	//#elif def{lang} == en
	/** the method of share */
	//#endif
	private void shareAndFinish() {
		int resId = ResHelper.getStringRes(activity, "ssdk_oks_sharing");
		if (resId > 0) {
			Toast.makeText(activity, resId, Toast.LENGTH_SHORT).show();
		}
		
		if (isDisableSSO()) {
			platform.SSOSetting(true);
		}
		platform.setPlatformActionListener(getCallback());
		platform.share(sp);
		impl.callback = null;
		finish();
	}
	
	//#if def{lang} == cn
	/** 编辑界面，显示的图片 */
	//#elif def{lang} == en
	/** show the big picture in editpage */
	//#endif
	private void showThumb(Bitmap pic) {
		PicViewerPage page = new PicViewerPage(impl);
		page.setImageBitmap(pic);
		page.show(activity, null);
	}

	private void removeThumb() {
		sp.setImageArray(null);
		sp.setImageData(null);
		sp.setImagePath(null);
		sp.setImageUrl(null);
	}
	
	//#if def{lang} == cn
	/** @ 好友时，展示的好友列表 */
	//#elif def{lang} == en
	/** show the friend list when click the ‘@’ in edit page */
	//#endif
	private void showFriendList() {
		FriendListPage page;
		int orientation = activity.getResources().getConfiguration().orientation;
		if (orientation == Configuration.ORIENTATION_PORTRAIT) {
			page = new FriendListPagePort(impl);
		} else {
			page = new FriendListPageLand(impl);
		}
		page.setPlatform(platform);
		page.showForResult(MobSDK.getContext(), null, this);
	}
	
	public void onResult(HashMap<String, Object> data) {
		String atText = getJoinSelectedUser(data);
		if(!TextUtils.isEmpty(atText)) {
			etContent.append(atText);
		}
	}
	
	private String getJoinSelectedUser(HashMap<String, Object> data) {
		if (data != null && data.containsKey("selected")) {
			@SuppressWarnings("unchecked")
			ArrayList<String> selected = (ArrayList<String>) data.get("selected");
			String platform = ((Platform)data.get("platform")).getName();
			if("FacebookMessenger".equals(platform)) {
				return null;
			}
			StringBuilder sb = new StringBuilder();
			for (String sel : selected) {
				sb.append('@').append(sel).append(' ');
			}
			return sb.toString();
		}
		return null;
	}
	
	protected boolean isShowAtUserLayout(String platformName) {
		return "SinaWeibo".equals(platformName) 
				|| "TencentWeibo".equals(platformName)
				|| "Facebook".equals(platformName) 
				|| "Twitter".equals(platformName);
	}
	
	public void onClick(View v) {
		if (v.equals(tvCancel)) {
			cancelAndFinish();
		} else if (v.equals(tvShare)) {
			sp.setText(etContent.getText().toString().trim());
			shareAndFinish();
		} else if (v.equals(aivThumb)) {
			showThumb(thumb);
		} else if (v.equals(xvRemove)) {
			maxBodyHeight = 0;
			rlThumb.setVisibility(View.GONE);
			llPage.measure(0, 0);
			onTextChanged(etContent.getText(), 0, 0, 0);
			removeThumb();
		} else if (v.equals(tvAt)) {
			showFriendList();
		}
	}
	
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		tvTextCouter.setText(String.valueOf(s.length()));
	
		if (maxBodyHeight == 0) {
			maxBodyHeight = llPage.getHeight() - rlTitle.getHeight() - llBottom.getHeight();
		}
		
		if (maxBodyHeight > 0) {
			svContent.post(this);
		}
	}
	
	//#if def{lang} == cn
	/** 动态适配编辑界面的高度 */
	//#elif def{lang} == en
	/** calculate the height of edit view */
	//#endif
	public void run() {
		int height = svContent.getChildAt(0).getHeight();
		RelativeLayout.LayoutParams lp = ResHelper.forceCast(svContent.getLayoutParams());
		if (height > maxBodyHeight && lp.height != maxBodyHeight) {
			lp.height = maxBodyHeight;
			svContent.setLayoutParams(lp);
		} else if (height < maxBodyHeight && lp.height == maxBodyHeight) {
			lp.height = LayoutParams.WRAP_CONTENT;
			svContent.setLayoutParams(lp);
		}
	}
	
	public void afterTextChanged(Editable s) {
		
	}
	
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		
	}
	
	public void onPause() {
		DeviceHelper.getInstance(activity).hideSoftInput(getContentView());
		super.onPause();
	}
	
}
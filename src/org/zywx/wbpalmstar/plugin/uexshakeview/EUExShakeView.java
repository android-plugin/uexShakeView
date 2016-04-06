package org.zywx.wbpalmstar.plugin.uexshakeview;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout.LayoutParams;

import org.json.JSONException;
import org.json.JSONObject;
import org.zywx.wbpalmstar.engine.EBrowserView;
import org.zywx.wbpalmstar.engine.universalex.EUExBase;

import java.io.Serializable;

@SuppressWarnings({ "deprecation", "serial" })
public class EUExShakeView extends EUExBase implements Serializable {
	
	static final String onShake = "uexShakeView.onShake";
	private float density;
	private LocalActivityManager mgr;

	public EUExShakeView(Context context, EBrowserView eBrowserView) {
		super(context, eBrowserView);
        if (mgr == null) {
            mgr = new LocalActivityManager((Activity) mContext, true);
            mgr.dispatchCreate(null);
        }
		density = mContext.getResources().getDisplayMetrics().density;
	}
	
	public void open(String[] params) {
		sendMessageWithType(EUExShakeUtils.SHAKEVIEW_MSG_CODE_OPEN, params);
	}
	
	public void close(String[] params) {
		sendMessageWithType(EUExShakeUtils.SHAKEVIEW_MSG_CODE_CLOSE, params);
	}

	private void sendMessageWithType(int msgType, String[] params) {
		if(mHandler == null) {
			return;
		}
		Message msg = Message.obtain();
		msg.what = msgType;
		msg.obj = this;
		Bundle bundle = new Bundle();
		bundle.putStringArray(EUExShakeUtils.SHAKEVIEW_KEY_CODE_FUNCTION, params);
		msg.setData(bundle);
		mHandler.sendMessage(msg);
	}
	
	@Override
	public void onHandleMessage(Message msg) {
		if(msg.what == EUExShakeUtils.SHAKEVIEW_MSG_CODE_OPEN) {
			handleOpen(msg);
		}else {
			handleMessageInShake(msg);
		}
	}

	private void handleMessageInShake(Message msg) {
		String[] params = msg.getData().getStringArray(EUExShakeUtils.SHAKEVIEW_KEY_CODE_FUNCTION);
		String activityId = EUExShakeView.this.hashCode() + EUExShakeUtils.SHAKEVIEW_KEY_CODE_ACTIVITYID;
		Activity activity = mgr.getActivity(activityId);
		if(activity != null && activity instanceof EShakeViewBaseActivity) {
			EShakeViewBaseActivity eShakeViewBaseActivity = (EShakeViewBaseActivity) activity;
			switch (msg.what) {
			case EUExShakeUtils.SHAKEVIEW_MSG_CODE_CLOSE:
				handleClose(params, eShakeViewBaseActivity);
				break;
			}
		}
	}

	private void handleClose(String[] params,
			EShakeViewBaseActivity eShakeViewBaseActivity) {
		View decorView = eShakeViewBaseActivity.getWindow().getDecorView();
		removeViewFromCurrentWindow(decorView);
		String activityId = EUExShakeView.this.hashCode() + EUExShakeUtils.SHAKEVIEW_KEY_CODE_ACTIVITYID;
		mgr.destroyActivity(activityId, true);
	}

	private void handleOpen(Message msg) {
		String[] params = msg.getData().getStringArray(EUExShakeUtils.SHAKEVIEW_KEY_CODE_FUNCTION);
		try {
			JSONObject object = new JSONObject(params[0]);
			final int x = Integer.parseInt(object.getString(EUExShakeUtils.SHAKEVIEW_PARAMS_JSON_KEY_X));
			final int y = Integer.parseInt(object.getString(EUExShakeUtils.SHAKEVIEW_PARAMS_JSON_KEY_Y));
			final int w = Integer.parseInt(object.getString(EUExShakeUtils.SHAKEVIEW_PARAMS_JSON_KEY_W));
			final int h = Integer.parseInt(object.getString(EUExShakeUtils.SHAKEVIEW_PARAMS_JSON_KEY_H));
			Intent intent = new Intent(mContext, EShakeViewBaseActivity.class);
			intent.putExtra(EUExShakeUtils.SHAKEVIEW_KEY_CODE_OBJ, this);
			String activityId = EUExShakeView.this.hashCode() + EUExShakeUtils.SHAKEVIEW_KEY_CODE_ACTIVITYID;
			EShakeViewBaseActivity activity = (EShakeViewBaseActivity) mgr.getActivity(activityId);
			if(activity != null) {
				return;
			}
			Window window = mgr.startActivity(activityId, intent);
			View decorView = window.getDecorView();

			LayoutParams param = new LayoutParams(w, h);
			param.leftMargin = x;
			param.topMargin = y;
			addViewToCurrentWindow(decorView, param);

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void callBack(String str) {
		onCallback(str);
	}
	
	@Override
	protected boolean clean() {
		close(null);
		return true;
	}
	
}

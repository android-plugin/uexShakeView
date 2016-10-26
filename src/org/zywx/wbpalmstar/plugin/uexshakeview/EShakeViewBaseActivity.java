package org.zywx.wbpalmstar.plugin.uexshakeview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import org.zywx.wbpalmstar.base.BUtility;
import org.zywx.wbpalmstar.engine.universalex.EUExUtil;
import org.zywx.wbpalmstar.plugin.uexshakeview.ShakeListener.OnShakeListener;

import java.io.IOException;
import java.io.InputStream;

public class EShakeViewBaseActivity extends Activity implements OnShakeListener, AnimationListener {

	static final String ONSHAKE = "uexShakeView.onShake";
	static final String SCRIPT_HEADER = "javascript:";

	private ImageView up, down;
	private ImageView ivu, ivd;
	private ShakeListener shakeListener;
	private MediaPlayer player, player2;
	private Vibrator vibrator;
	private EUExShakeView euexShakeView;

    private String backgroundImagePath;
    private String upImagePath;
    private String downImagePath;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        backgroundImagePath = getIntent().getStringExtra(EUExShakeUtils.SHAKEVIEW_PARAMS_JSON_KEY_BACKGROUD_IMAGE_PATH);
        upImagePath = getIntent().getStringExtra(EUExShakeUtils.SHAKEVIEW_PARAMS_JSON_KEY_UP_IMAGE_PATH);
        downImagePath = getIntent().getStringExtra(EUExShakeUtils.SHAKEVIEW_PARAMS_JSON_KEY_DOWN_IMAGE_PATH);

		LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
		View view = layoutInflater.inflate(EUExUtil.getResLayoutID("plugin_uexshakeview_main"), null);
		setContentView(view);
		initView(view);
		euexShakeView = (EUExShakeView) getIntent().getSerializableExtra(EUExShakeUtils.SHAKEVIEW_KEY_CODE_OBJ);
		vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
		player = MediaPlayer.create(getApplicationContext(), EUExUtil.getResRawID("shake_sound_male"));
		player2 = MediaPlayer.create(getApplicationContext(), EUExUtil.getResRawID("shake_match"));
		shakeListener = new ShakeListener(getApplicationContext());
		shakeListener.setOnShakeListener(this);
	}

	private void startUserAnimation() {

		AnimationSet animUp = new AnimationSet(true);
		AnimationSet animDown = new AnimationSet(true);
		animUp.setAnimationListener(this);

		TranslateAnimation animUp1 = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, -0.8f);
		animUp1.setDuration(300);
		TranslateAnimation animUp2 = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF,	+0.8f);
		animUp2.setDuration(300);
		animUp2.setStartOffset(700);
		animUp.addAnimation(animUp1);
		animUp.addAnimation(animUp2);
		up.startAnimation(animUp);

		TranslateAnimation animDown1 = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, +0.8f);
		animDown1.setDuration(300);
		TranslateAnimation animDown2 = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, -0.8f);
		animDown2.setDuration(300);
		animDown2.setStartOffset(700);
		animDown.addAnimation(animDown1);
		animDown.addAnimation(animDown2);
		down.startAnimation(animDown);

	}

	private void initView(View view) {
		up = (ImageView) view.findViewById(EUExUtil.getResIdID("shakeImgUp"));
		down = (ImageView) view.findViewById(EUExUtil.getResIdID("shakeImgDown"));
        ImageView bg = (ImageView) view.findViewById(EUExUtil.getResIdID("iv_bg"));

        up.setImageBitmap(getLocalImage(this,upImagePath));
        down.setImageBitmap(getLocalImage(this, downImagePath));
        bg.setImageBitmap(getLocalImage(this, backgroundImagePath));
	}

    public static Bitmap getLocalImage(Context ctx, String imgUrl) {
        if (imgUrl == null || imgUrl.length() == 0) {
            return null;
        }

        Bitmap bitmap = null;
        InputStream is = null;
        try {
            if (imgUrl.startsWith(BUtility.F_Widget_RES_path)) {
                try {
                    is = ctx.getAssets().open(imgUrl);
                    if (is != null) {
                        bitmap = BitmapFactory.decodeStream(is);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (imgUrl.startsWith("/")) {
                bitmap = BitmapFactory.decodeFile(imgUrl);
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }

	private void showImage() {
		ivu.setVisibility(View.VISIBLE);
		ivd.setVisibility(View.VISIBLE);
	}

	private void dismissImage() {
		ivu.setVisibility(View.INVISIBLE);
		ivd.setVisibility(View.INVISIBLE);
	}



	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(shakeListener != null) {
			shakeListener.start();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if(shakeListener != null) {
			shakeListener.stop();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(shakeListener != null) {
			shakeListener.stop();
		}
		if(vibrator != null) {
			vibrator.cancel();
		}
		if(player != null) {
			player.release();
		}
		if(player2 != null) {
			player2.release();
		}
	}

	@Override
	public void onShake() {
		startUserAnimation();
		player.start();
		vibrator.vibrate(1000);
	}

	@Override
	public void onAnimationStart(Animation animation) {
		//showImage();
		shakeListener.stop();
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		//dismissImage();
		shakeListener.start();
		euexShakeView.callBack(SCRIPT_HEADER + "if(" + ONSHAKE + "){"+ ONSHAKE + "();}");
		player2.start();
	}

	@Override
	public void onAnimationRepeat(Animation animation) {

	}
}

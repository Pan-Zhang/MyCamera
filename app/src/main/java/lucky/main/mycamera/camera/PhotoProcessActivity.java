package lucky.main.mycamera.camera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;


import java.io.File;
import java.io.IOException;

import lucky.main.mycamera.R;


public class PhotoProcessActivity extends Activity implements View.OnClickListener {

	private ImageView photoImageView;
	private String path = "";
	private TextView actionTextView;
	Bitmap bitmap = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(savedInstanceState != null){
			path = savedInstanceState.getString("path");
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.photo_activity);
		Intent intent = getIntent();
		if(intent.hasExtra(CameraActivity.CAMERA_PATH_VALUE1)){
			path = intent.getStringExtra(CameraActivity.CAMERA_PATH_VALUE1);
		}
		if(TextUtils.isEmpty(path)){
			finish();
			return;
		}
		initView();
		initData();
	}

	private void initData() {
		try {
			bitmap = getImage(path);
			//bitmap = loadBitmap(path, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		photoImageView.setImageBitmap(bitmap);
		actionTextView.setOnClickListener(this);
	}

	private void initView() {
		photoImageView = (ImageView) findViewById(R.id.photo_imageview);
		actionTextView = (TextView) findViewById(R.id.photo_process_action);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("path", path);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		bitmap.recycle();
		bitmap = null;
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	/**
	 * 从给定路径加载图片
	 */
	public static Bitmap loadBitmap(String imgpath) {
		return BitmapFactory.decodeFile(imgpath);
	}


	/**
	 * 从给定的路径加载图片，并指定是否自动旋转方向
	 */
	public static Bitmap loadBitmap(String imgpath, boolean adjustOritation) throws OutOfMemoryError {
		if (!adjustOritation) {
			return loadBitmap(imgpath);
		} else {
			Bitmap bm = loadBitmap(imgpath);
			int digree = 0;
			ExifInterface exif = null;
			try {
				exif = new ExifInterface(imgpath);
			} catch (IOException e) {
				e.printStackTrace();
				exif = null;
			}
			if (exif != null) {
				// 读取图片中相机方向信息
				int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
						ExifInterface.ORIENTATION_UNDEFINED);
				// 计算旋转角度
				switch (ori) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					digree = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					digree = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					digree = 270;
					break;
				default:
					digree = 0;
					break;
				}
			}
			if (digree != 0) {
				// 旋转图片
				Matrix m = new Matrix();
				m.postRotate(digree);
				bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
						bm.getHeight(), m, true);
			}
			return bm;
		}
	}


	private Bitmap getImage(String srcPath) throws OutOfMemoryError {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		//开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);//此时返回bm为空

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		//现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 800f;//这里设置高度为800f
		float ww = 480f;//这里设置宽度为480f
		//缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;//be=1表示不缩放
		if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;//设置缩放比例
		//重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		return bitmap;//压缩好比例大小后再进行质量压缩
	}

	private void refreshGallery(String file) {
		Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		mediaScanIntent.setData(Uri.fromFile(new File(file)));
		sendBroadcast(mediaScanIntent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			Intent intent = new Intent();
			intent.putExtra(CameraActivity.CAMERA_PATH_VALUE2, path);
			setResult(0, intent);
			finish();
			break;

		case R.id.photo_process_action:
			refreshGallery(path);
			Intent intentOk = new Intent();
			intentOk.putExtra(CameraActivity.CAMERA_PATH_VALUE2, path);
			setResult(RESULT_OK, intentOk);
			finish();
			break;
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getRepeatCount() == 0) {
			Intent intent = new Intent();
			intent.putExtra(CameraActivity.CAMERA_PATH_VALUE2, path);
			setResult(0, intent);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}

package com.jayyyR.gifjam;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CameraActivity extends Activity {

	private Camera mCamera;
	private CameraPreview mPreview;
	MediaRecorder mMediaRecorder;
	public int screenWidth;
	public int screenHeight;
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	private boolean isRecording = false;
	public Activity thisActive;
	String fileLoc;
	String user;
	String profileId;
	boolean forProf;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		//		WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_camera);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			user = extras.getString("user");
			profileId = extras.getString("profileId");
			forProf = extras.getBoolean("forProf");
		}


		//get display size to set the camera preview
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		screenWidth = size.x;
		screenHeight = size.y;
		thisActive = this;
		FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);


		//preview.setLayoutParams(new LinearLayout.LayoutParams(width,width));
		////
		// Create an instance of Camera
		mCamera = getCameraInstance();
		Size mPreviewSize = null;
		List<Size> mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
		if (mSupportedPreviewSizes != null) {
			mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, screenWidth, screenHeight);
		}

		Camera.Parameters parameters = mCamera.getParameters();
		parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);

		//block off correct size
		Log.v("height", "height of preview: " + mPreviewSize.height);
		Log.v("height", "height of preview: " + mPreviewSize.width);
		int pixelsToBlock = screenHeight-screenWidth;
		Log.v("height", "pixelstoBlock: " + pixelsToBlock);
		View whiteBox = (View) findViewById(R.id.whitebox);
		RelativeLayout.LayoutParams boxParams = (android.widget.RelativeLayout.LayoutParams) whiteBox.getLayoutParams();
		boxParams.height = pixelsToBlock;
		whiteBox.setLayoutParams(boxParams);

		mCamera.setParameters(parameters);
		mCamera.startPreview();
		mCamera.setDisplayOrientation(90);


		// Create our Preview view and set it as the content of our activity.
		mPreview = new CameraPreview(this, mCamera);
		final TextView countdown = (TextView) findViewById(R.id.textCounter);
		final Handler handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				if(msg.what==1){
					Log.v("recorded", "counter " + msg.arg1);
					countdown.setText(Integer.toString(5-msg.arg1));
					msg.what=0;
				}
				else if(msg.what==2){
					if(!forProf){
						Intent intent = new Intent(thisActive, SendVideo.class);
						intent.putExtra("file", fileLoc);
						intent.putExtra("user", user);
						intent.putExtra("profileId", profileId);
						startActivity(intent);
						msg.what=0;
					}
					else{


					}
				}
				super.handleMessage(msg);
			}
		};

		preview.addView(mPreview);
		final Button captureButton = (Button) findViewById(R.id.button_capture);

		final Thread camCount = new Thread(new Runnable(){
			@Override
			public void run() {
				try {
					int counter = 0;
					while(isRecording == true){

						if (counter >= 5){
							Log.v("recording", "breaking out");
							// stop recording and release camera
							mMediaRecorder.stop();  // stop the recording
							releaseMediaRecorder(); // release the MediaRecorder object
							mCamera.lock();         // take camera access back from MediaRecorder
							Log.v("recording", "before settting capture button");
							// inform the user that recording has stopped
							isRecording = false;

							Log.v("recording", "after setting isrecording");
							//captureButton.setText("Capture");
							Log.v("recording", "after settting capture button");

							Message msg = handler.obtainMessage();
							msg.what = 2; //break out
							handler.sendMessage(msg);


						}
						Log.v("recording", "yes");
						counter+=1;
						Thread.sleep(1000);
						Message msg = handler.obtainMessage();
						msg.what = (1);
						msg.arg1 = counter;
						handler.sendMessage(msg);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});






		// Add a listener to the Capture button

		captureButton.setOnTouchListener(
				new View.OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						switch (event.getAction() & MotionEvent.ACTION_MASK) {

						case MotionEvent.ACTION_DOWN:
							v.setPressed(true);
							Log.v("touch", "touching button");
							isRecording = true;
							// initialize video camera
							if (prepareVideoRecorder()) {
								// Camera is available and unlocked, MediaRecorder is prepared,
								// now you can start recording
								mMediaRecorder.start();

								// inform the user that recording has started
								setCaptureButtonText("Stop");
								//isRecording = true;
							} else {
								// prepare didn't work, release the camera
								releaseMediaRecorder();
								// inform user
							}

							//count for 5 then break
							camCount.start();
							break;
						case MotionEvent.ACTION_UP:
							Log.v("touch", "let go of button");
							Log.v("recording", "is Recording: " + isRecording);
							if (isRecording == true){
								v.setPressed(false);

								// stop recording and release camera
								mMediaRecorder.stop();  // stop the recording
								releaseMediaRecorder(); // release the MediaRecorder object
								mCamera.lock();         // take camera access back from MediaRecorder

								// inform the user that recording has stopped
								setCaptureButtonText("Capture");
								isRecording = false;
							}
							if(!forProf){
								Intent intent = new Intent(thisActive, SendVideo.class);
								intent.putExtra("file", fileLoc);
								intent.putExtra("user", user);
								intent.putExtra("profileId", profileId);
								startActivity(intent);}
							else{

							}
							break;
						case MotionEvent.ACTION_OUTSIDE:

							// Stop action ...
							break;
						case MotionEvent.ACTION_POINTER_DOWN:
							break;
						case MotionEvent.ACTION_POINTER_UP:
							break;
						case MotionEvent.ACTION_MOVE:
							break;
						}

						return true;
					}
				}
				/*new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (isRecording) {
							// stop recording and release camera
							mMediaRecorder.stop();  // stop the recording
							releaseMediaRecorder(); // release the MediaRecorder object
							mCamera.lock();         // take camera access back from MediaRecorder

							// inform the user that recording has stopped
							setCaptureButtonText("Capture");
							isRecording = false;
						} else {
							// initialize video camera
							if (prepareVideoRecorder()) {
								// Camera is available and unlocked, MediaRecorder is prepared,
								// now you can start recording
								mMediaRecorder.start();

								// inform the user that recording has started
								setCaptureButtonText("Stop");
								isRecording = true;
							} else {
								// prepare didn't work, release the camera
								releaseMediaRecorder();
								// inform user
							}
						}
					}
				}*/
				);
	}

	private void setCaptureButtonText(String text){
		Button captureButton = (Button) findViewById(R.id.button_capture);
		captureButton.setText(text);
	}



	private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
		final double ASPECT_TOLERANCE = 0.1;
		double targetRatio=(double)h / w;

		if (sizes == null) return null;

		Camera.Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;

		int targetHeight = h;

		for (Camera.Size size : sizes) {
			double ratio = (double) size.width / size.height;
			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
			if (Math.abs(size.height - targetHeight) < minDiff) {
				optimalSize = size;
				minDiff = Math.abs(size.height - targetHeight);
			}
		}

		if (optimalSize == null) {
			minDiff = Double.MAX_VALUE;
			for (Camera.Size size : sizes) {
				if (Math.abs(size.height - targetHeight) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			}
		}
		return optimalSize;
	}

	private boolean prepareVideoRecorder(){
		if(mCamera == null)
			Log.v("null", "mcamera was null");
		mMediaRecorder = new MediaRecorder();

		// store the quality profile required
		CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);

		// Step 1: Unlock and set camera to MediaRecorder
		mCamera.unlock();
		mMediaRecorder.setCamera(mCamera);

		// Step 2: Set sources
		//mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
		mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

		// Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
		mMediaRecorder.setOutputFormat(profile.fileFormat);
		mMediaRecorder.setVideoEncoder(profile.videoCodec);
		mMediaRecorder.setVideoEncodingBitRate(profile.videoBitRate);
		mMediaRecorder.setVideoFrameRate(profile.videoFrameRate);
		mMediaRecorder.setVideoSize(profile.videoFrameWidth, profile.videoFrameHeight);


		// Step 4: Set output file
		fileLoc = getOutputMediaFile(MEDIA_TYPE_VIDEO).toString();
		Log.v("fileloc", fileLoc);
		mMediaRecorder.setOutputFile(fileLoc);


		//mMediaRecorder.setVideoSize(screenWidth, screenWidth);

		// Step 5: Set the preview output
		//mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());
		mMediaRecorder.setOrientationHint(90);
		// Step 6: Prepare configured MediaRecorder
		try {
			mMediaRecorder.prepare();
		} catch (IllegalStateException e) {
			Log.d("help", "IllegalStateException preparing MediaRecorder: " + e.getMessage());
			releaseMediaRecorder();
			return false;
		} catch (IOException e) {
			Log.d("help", "IOException preparing MediaRecorder: " + e.getMessage());
			releaseMediaRecorder();
			return false;
		}
		return true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		releaseMediaRecorder();       // if you are using MediaRecorder, release it first
		releaseCamera();              // release the camera immediately on pause event
	}

	private void releaseMediaRecorder(){
		if (mMediaRecorder != null) {
			mMediaRecorder.reset();   // clear recorder configuration
			mMediaRecorder.release(); // release the recorder object
			mMediaRecorder = null;
			mCamera.lock();           // lock camera for later use
		}
	}

	private void releaseCamera(){
		if (mCamera != null){
			mCamera.release();        // release the camera for other applications
			mCamera = null;
		}
	}


	/** Create a file Uri for saving an image or video */
	private static Uri getOutputMediaFileUri(int type){
		return Uri.fromFile(getOutputMediaFile(type));
	}

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type){
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES), "GifJam");
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (! mediaStorageDir.exists()){
			if (! mediaStorageDir.mkdirs()){
				Log.d("MyCameraApp", "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE){
			mediaFile = new File(mediaStorageDir.getPath() + File.separator +
					"IMG_"+ timeStamp + ".jpg");
		} else if(type == MEDIA_TYPE_VIDEO) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator +
					"VID_"+ timeStamp + ".mp4");
		} else {
			return null;
		}

		return mediaFile;
	}

	/** Check if this device has a camera */
	private boolean checkCameraHardware(Context context) {
		if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
			// this device has a camera
			return true;
		} else {
			// no camera on this device
			return false;
		}
	}

	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance(){
		Camera c = null;
		try {
			c = Camera.open(); // attempt to get a Camera instance
		}
		catch (Exception e){
			// Camera is not available (in use or does not exist)
		}
		return c; // returns null if camera is unavailable
	}
}

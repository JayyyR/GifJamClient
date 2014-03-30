package com.jayyyR.gifjam;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class CameraActivity extends Activity {

	private Camera mCamera;
	private CameraPreview mPreview;
	MediaRecorder mMediaRecorder;
	public int screenWidth;
	public int screenHeight;
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	private boolean isRecording = false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		//		WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_camera);
		

		//get display size to set the camera preview
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		screenWidth = size.x;
		screenHeight = size.y;

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
		mCamera.setParameters(parameters);
		mCamera.startPreview();
		mCamera.setDisplayOrientation(90);


		// Create our Preview view and set it as the content of our activity.
		mPreview = new CameraPreview(this, mCamera);

		preview.addView(mPreview);



		// Add a listener to the Capture button
		Button captureButton = (Button) findViewById(R.id.button_capture);
		captureButton.setOnClickListener(
				new View.OnClickListener() {
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
				}
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

		// Step 1: Unlock and set camera to MediaRecorder
		mCamera.unlock();
		mMediaRecorder.setCamera(mCamera);

		// Step 2: Set sources
		mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
		mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

		// Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
		mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

		// Step 4: Set output file
		String fileLoc = getOutputMediaFile(MEDIA_TYPE_VIDEO).toString();
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
				Environment.DIRECTORY_PICTURES), "MyCameraApp");
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

package com.example.gmahn.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class CameraPreviewView extends SurfaceView {
    private static final String TAG = "CameraPreviewView";

    private SurfaceHolder surfaceHolder;
    private Camera camera;

    private MediaRecorder mediaRecorder;

    public CameraPreviewView(Context context, AttributeSet attrs) {
        super(context, attrs);
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(surfaceHolderCallback);
        mediaRecorder = new MediaRecorder();
    }

    private SurfaceHolder.Callback surfaceHolderCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
            try {
                camera.setPreviewDisplay(holder);
                camera.setDisplayOrientation(90);
                camera.startPreview();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            camera.release();
            camera = null;
        }
    };

    private void capture(Camera.PictureCallback handler) {
        if (camera != null) {
            camera.takePicture(null, null, handler);
        }
    }

    public void captureCamera(Activity activity) {
        if (camera != null) {
            capture((data, camera) -> {
                try {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    String outUriStr = MediaStore.Images.Media.insertImage
                            (activity.getContentResolver(), bitmap, "캡쳐했습니다.", "카메라를 통해 캡쳐했습니다.");

                    if (outUriStr == null) {
                        Log.d(TAG, "이미지 삽입에 실패했습니다.");
                        return;
                    }
                    else {
                        Uri outUri = Uri.parse(outUriStr);

                        activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, outUri));
                    }
                    camera.startPreview();
                }
                catch (Exception e) {
                    Log.e(TAG, "캡쳐에 실패했습니다.", e);
                }
            });
        }
    }

    public void recordVideo(boolean recoding, Activity activity) {
        if(recoding) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            camera.lock();
        }
        else {
            activity.runOnUiThread(() -> {
                    try {
                        mediaRecorder = new MediaRecorder();
                        camera.unlock();
                        mediaRecorder.setCamera(camera);
                        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
                        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

                        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);

                        // 촬영 각도 90도로
                        mediaRecorder.setOrientationHint(90);
                        mediaRecorder.setOutputFile(Environment.getExternalStorageDirectory().getPath() + "/" +  Environment.DIRECTORY_MOVIES + "/test2.mp4");
                        Log.d("경로", Environment.getExternalStorageDirectory().getPath() + "/" +  Environment.DIRECTORY_MOVIES + "/test2.mp4");

                        // 미리보기 화면 셋팅
                        mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());
                        Log.d(TAG, "여기왔다..");
                        mediaRecorder.prepare();
                        mediaRecorder.start();

                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                });
        }
    }
}

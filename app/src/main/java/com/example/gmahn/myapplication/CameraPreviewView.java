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
            // 셔터후
            // Raw 이미지 생성후
            // JPE 이미지 생성후
            camera.takePicture(null, null, handler);
        }
    }

    public void captureCamera(Activity activity) {
        if (camera != null) {
            capture(new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    //data: 그림 데이터의 바이트 배열
                    //camera: 카메라 서비스 객체

                    try {
                        //사진 데이터를 비트맵 객체로 저장
                        //바이트 형태로 되어있는 이미지를 bitmap으로 만들 때 사용
                        //data: 압축된 이미지 데이터의 바이트 계열
                        //offset: 디코더가 해석을 개시하는 위치의 imageData에의 오프셋
                        //length: 오프셋에서 시작하여 구문 분석 할 바이트 수
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                        //비트맵 이미지를 이용해 앨범에 저장
                        //내용제공자를 통해서 앨범에 저장
                        String outUriStr = MediaStore.Images.Media.insertImage
                                (activity.getContentResolver(), bitmap, "Captured!", "Captured Image using Camera.");


                        if (outUriStr == null) {
                            Log.d("SampleCapture", "Image insert failed.");
                            return;
                        } else {
                            Uri outUri = Uri.parse(outUriStr);

                            //사진파일이 생성되어도 파일 관리자에 반영이 되지않음
                            //미디어 라이브러리에 파일이 추가되지 않아서 발생하는 경우임
                            //안드로이드를 재부팅하거나 sd카드를 다시 장착하면 미디어 스캔을 실행해 파일 처리의 결과가 반영됨
                            //수동으로 안드로이드 미디어 스캔을 해야함
                            activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, outUri));
                        }
                        // 다시 미리보기 화면 보여줌
                        camera.startPreview();

                    } catch (Exception e) {
                        Log.e("SampleCapture", "Failed to insert image.", e);
                    }

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
                        mediaRecorder.setOutputFile(Environment.getExternalStorageDirectory().getPath() + Environment.DIRECTORY_MOVIES + "/test.mp4");
                        Log.d("경로", Environment.getRootDirectory().getPath() + Environment.DIRECTORY_MOVIES + "/test.mp4");

                        // 미리보기 화면 셋팅
                        mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());
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

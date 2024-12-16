package fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;

import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Collections;


import do_an.tkll.an_iot_app.MQTTHelper;
import do_an.tkll.an_iot_app.R;
import do_an.tkll.an_iot_app.secretKey;

public class FragmentCamera extends Fragment {

    private static final String TAG = "FragmentCamera";
    private static final int CAMERA_REQUEST_CODE = 100;

    private LinearLayout cameraInfoLayout; // Thẻ LinearLayout để hiển thị thông tin
    private MQTTHelper mqttHelper;

    private TextureView textureView;
    private CameraDevice cameraDevice;
    private CameraCaptureSession cameraCaptureSession;


    public FragmentCamera() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentCamera.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentCamera newInstance(String param1, String param2) {
        FragmentCamera fragment = new FragmentCamera();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        textureView = view.findViewById(R.id.textureView);
        cameraInfoLayout = view.findViewById(R.id.cameraInfo);

        initMQTTInBackground();

        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
                openCamera();
            }

            @Override
            public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
            }

            @Override
            public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
                closeCamera();
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {
            }
        });
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
            return;
        }

        CameraManager cameraManager = (CameraManager) requireContext().getSystemService(Context.CAMERA_SERVICE);
        try {
            String cameraId = cameraManager.getCameraIdList()[0]; // Sử dụng camera đầu tiên
            cameraManager.openCamera(cameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    cameraDevice = camera;
                    startCameraPreview();
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice camera) {
                    camera.close();
                }

                @Override
                public void onError(@NonNull CameraDevice camera, int error) {
                    camera.close();
                    cameraDevice = null;
                }
            }, null);
        } catch (CameraAccessException e) {
            Log.e(TAG, "Camera access error: " + e.getMessage());
        }
    }

    private void startCameraPreview() {
        try {
            SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();
            if (surfaceTexture == null) return;

            surfaceTexture.setDefaultBufferSize(textureView.getWidth(), textureView.getHeight());
            Surface previewSurface = new Surface(surfaceTexture);

            CaptureRequest.Builder captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(previewSurface);

            cameraDevice.createCaptureSession(Collections.singletonList(previewSurface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    cameraCaptureSession = session;
                    try {
                        CaptureRequest captureRequest = captureRequestBuilder.build();
                        cameraCaptureSession.setRepeatingRequest(captureRequest, null, null);
                    } catch (CameraAccessException e) {
                        Log.e(TAG, "Error starting camera preview: " + e.getMessage());
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    Toast.makeText(getContext(), "Failed to configure camera preview", Toast.LENGTH_SHORT).show();
                }
            }, null);
        } catch (CameraAccessException e) {
            Log.e(TAG, "Error setting up camera preview: " + e.getMessage());
        }
    }
    private void closeCamera() {
        if (cameraCaptureSession != null) {
            cameraCaptureSession.close();
            cameraCaptureSession = null;
        }
        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }
    }


    public String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy  hh:mm:ssa  "); // Định dạng thời gian
        return sdf.format(new Date()); // Lấy thời gian hiện tại và định dạng
    }

    private void displayMessage(String message) {
//        cameraInfoLayout.removeAllViews();
        String timestamp = getCurrentTimestamp();
        message = timestamp + " " + message;

        TextView textView = new TextView(requireContext());
        textView.setText(message);
        textView.setTextSize(12);
        textView.setTextColor(getResources().getColor(R.color.black));
        textView.setPadding(16, 16, 16, 10);

        requireActivity().runOnUiThread(() -> {
            cameraInfoLayout.addView(textView);
            cameraInfoLayout.post(() -> {
                View parent = (View) cameraInfoLayout.getParent();
                if (parent instanceof ScrollView) {
                    ((ScrollView) parent).fullScroll(View.FOCUS_DOWN);
                }
            });
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mqttHelper == null) {
            mqttHelper = new MQTTHelper(requireContext());
            mqttHelper.setCallback(new MqttCallbackExtended() {
                @Override
                public void connectComplete(boolean reconnect, String serverURI) {
                    Log.d(TAG, "Reconnected to MQTT server: " + serverURI);
                }

                @Override
                public void connectionLost(Throwable cause) {
                    Log.e(TAG, "MQTT connection lost: " + cause.getMessage());
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    if (topic.equals(secretKey.MQTT_AI)) {
                        String receivedData = new String(message.getPayload());
                        displayMessage(receivedData);
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    Log.d(TAG, "MQTT delivery complete");
                }
            });
        }
    }

    private void initMQTTInBackground() {
        new Thread(() -> {
            mqttHelper = new MQTTHelper(requireContext());
            mqttHelper.setCallback(new MqttCallbackExtended() {
                @Override
                public void connectComplete(boolean reconnect, String serverURI) {
                    Log.d(TAG, "Connected to MQTT server in background: " + serverURI);
                }

                @Override
                public void connectionLost(Throwable cause) {
                    Log.e(TAG, "MQTT connection lost in background: " + cause.getMessage());
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    if (topic.equals(secretKey.MQTT_AI)) {
                        displayMessage(new String(message.getPayload()));
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    Log.d(TAG, "MQTT delivery complete in background");
                }
            });
        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        closeCamera();
    }

}
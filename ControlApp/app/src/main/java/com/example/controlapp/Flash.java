package com.example.controlapp;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;

import java.util.Timer;
import java.util.TimerTask;

/* H klash auth, periexei oles tis aparaithtes sunarthseis gia ton xeirismo
 * tou flash ths kameras, ths suskeuhs. Oi parakatw methodoi, merimnoun kai
  * gia thn periptwsh opou h suskeuh de diathetei katholou flashlight. */

public class Flash {

    private Context context;
    private CameraManager camManager;
    private String cameraId;
    private boolean flashExists;

    public Flash(Context context) {
        this.context = context;
        camManager = (CameraManager) this.context.getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraId = camManager.getCameraIdList()[0];
        } catch (CameraAccessException e){
            e.printStackTrace();
        }
        CameraCharacteristics chars = null;
        try {
            chars = camManager.getCameraCharacteristics(cameraId);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        flashExists = chars.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
    }

    /* Energopoihsh tou flashlight */
    public void openFlash(int seconds) {
        if(flashExists) {
            try {
                camManager.setTorchMode(cameraId, true);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    closeFlash();
                }
            };
            Timer timer = new Timer();
            timer.schedule(task, 1000 * seconds);
        }
    }

    /* Apenergopoihsh tou flashlight */
    public void closeFlash() {
        if(flashExists) {
            try {
                camManager.setTorchMode(cameraId, false);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
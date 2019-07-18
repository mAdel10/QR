package com.toastdemoapp.qrdemoapp.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.toastdemoapp.qrdemoapp.R;
import com.toastdemoapp.qrdemoapp.managers.UserManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ScanActivity extends BaseActivity {

    @BindView(R.id.scan_button)
    Button scanButton;
    @BindView(R.id.user_logout_btn)
    Button userLogoutBtn;

    public ScanActivity() {
        super(R.layout.activity_scan, false);
    }

    @Override
    protected void doOnCreate(Bundle bundle) {
        ButterKnife.bind(this);

        int cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ScanActivity.this,
                    new String[]{Manifest.permission.CAMERA}, 1);
        }
    }


    @OnClick({R.id.scan_button, R.id.user_logout_btn})
    public void onViewClicked(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.scan_button:
                intent = new Intent(this,QrScanActivity.class);
                startActivity(intent);

                break;
            case R.id.user_logout_btn:
                UserManager.getInstance().logout();
                intent = new Intent(this , LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(ScanActivity.this, "Permission denied to open you Camera.", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
}

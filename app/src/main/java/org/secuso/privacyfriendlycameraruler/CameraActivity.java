package org.secuso.privacyfriendlycameraruler;

import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by roberts on 12.12.16.
 */

public class CameraActivity extends BaseActivity {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageButton cameraButton;
    private ImageButton galleryButton;
    private ImageView pictureView;
    Uri uri;

    private static final String TAG = "Touch";

    // These matrices will be used to move and zoom image
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    // We can be in one of these 3 states
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    // Remember some things for zooming
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        mSharedPreferences.edit().putString("lastMode", "camera").commit();

        cameraButton = (ImageButton) findViewById(R.id.from_camera_button);
        galleryButton = (ImageButton) findViewById(R.id.from_gallery_button);
        pictureView = (ImageView) findViewById(R.id.pictureView);

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, false);
                startActivityForResult(cameraIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });

        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        overridePendingTransition(0, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE || requestCode == PICK_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK) {
                startImageFragment(data.getData());
            } else {
                if (resultCode != RESULT_CANCELED) {
                    if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
                        Toast.makeText(this, R.string.camera_crash, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, R.string.gallery_crash, Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }

    public void startImageFragment(Uri uri) {
        cameraButton.setVisibility(GONE);
        cameraButton.setClickable(false);
        galleryButton.setVisibility(GONE);
        galleryButton.setClickable(false);
        pictureView.setImageURI(uri);
        pictureView.setVisibility(VISIBLE);
    }

    @Override
    protected int getNavigationDrawerID() {
        return R.id.nav_camera;
    }
}
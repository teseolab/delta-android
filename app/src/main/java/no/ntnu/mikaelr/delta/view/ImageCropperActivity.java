package no.ntnu.mikaelr.delta.view;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.theartofdev.edmodo.cropper.CropImageView;
import no.ntnu.mikaelr.delta.R;
import no.ntnu.mikaelr.delta.util.Constants;
import no.ntnu.mikaelr.delta.util.ImageHandler;

import java.io.File;

public class ImageCropperActivity extends AppCompatActivity implements CropImageView.OnSetImageUriCompleteListener {


    private Uri avatarUri;
    private CropImageView cropImageView;
    private boolean imageIsLoaded = false;
    private Bitmap croppedImage;
    private File croppedImageFile;
    private final int REQUEST_OPEN_CAMERA = 0;
    private final int REQUEST_OPEN_GALLERY = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getLayoutInflater().inflate(R.layout.activity_image_cropper, null);
        cropImageView = (CropImageView) view.findViewById(R.id.crop_image_view);
        cropImageView.setCropShape(CropImageView.CropShape.OVAL);
        cropImageView.setAspectRatio(1, 1);
        cropImageView.setFixedAspectRatio(true);
        cropImageView.setOnSetImageUriCompleteListener(this);
        setContentView(view);

        ToolbarUtil.initializeToolbar(this, R.drawable.ic_close_white_24dp, "Beskjær bildet");

        int requestCode = getIntent().getIntExtra("requestCode", -1);

        if (requestCode == Constants.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_OPEN_CAMERA);
            } else {
                openCameraIntent();
            }
        } else if (requestCode == Constants.CHOOSE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_OPEN_GALLERY);
            } else {
                openGalleryIntent();
            }
        }
    }

    private void openCameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        avatarUri = ImageHandler.getOutputImageFileUri();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, avatarUri);
        startActivityForResult(intent, 0);
    }

    private void openGalleryIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_image_cropper, menu);
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        // Disables and makes the done button gray when the image is not yet loaded into the view
        MenuItem doneButton = menu.findItem(R.id.action_cropping_done);
        Drawable doneIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_done_white_24dp, null);

        if (!imageIsLoaded && doneIcon != null)
            doneIcon.mutate().setColorFilter(ContextCompat.getColor(this, R.color.white_trans_30), PorterDuff.Mode.SRC_IN);

        doneButton.setEnabled(imageIsLoaded);
        doneButton.setIcon(doneIcon);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_cropping_done) {
            croppedImage = cropImageView.getCroppedImage();
            saveCroppedImageToFile();
            finishCropperActivity();
        }
        else {
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_OPEN_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCameraIntent();
                } else {
                    Toast.makeText(this, "Delta does not have permission to save the image", Toast.LENGTH_LONG).show();
                }
            }
            case REQUEST_OPEN_GALLERY: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGalleryIntent();
                } else {
                    Toast.makeText(this, "Delta does not have permission to save the image", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void finishCropperActivity() {
        Intent intent = new Intent();
        intent.putExtra("avatarUri", Uri.fromFile(croppedImageFile));
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void saveCroppedImageToFile() {
        croppedImageFile = ImageHandler.getOutputImageFile();
        ImageHandler.writeBitmapToFile(croppedImage, croppedImageFile);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            boolean isCamera = true;
            if (data != null && data.getData() != null) {
                String action = data.getAction();
                isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
            }

            Uri outputUri = isCamera ? avatarUri : data.getData();
            cropImageView.setImageUriAsync(outputUri);

        } else {
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
    }

    @Override
    public void onSetImageUriComplete(CropImageView cropImageView, Uri uri, Exception e) {
        imageIsLoaded = true;
        supportInvalidateOptionsMenu();
    }
}

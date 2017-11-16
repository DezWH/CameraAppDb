package com.dez.cameraapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.sip.SipSession;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * Created by Dez on 11/15/2017.
 */



public class Take_Picture_Activity extends Activity

{
    // Logging tag

    private static final String TAG = "Camera Main Activity";
    // UI components

    Button mTakePictureButton;
    ImageView mCameraPicture;

    // To identify the which permission request is returning a result
    private static final int REQUEST_SAVE_IMAGE_PERMISSION_REQUEST_CODE = 1001;

    // To identify that the camera is returning a result
    private static final int TAKE_PICTURE_REQUEST_CODE = 0;

    // For file storage, where is the current image stored?
    private String mImagePath;

    private int Id;
    EditText pictureName;
    EditText searchPicture;
    Button addPictureButton;

    // The image to be displayed in the app
    private Bitmap mImage;

    // Used in the instance state Bundle, to preserve image when device is rotated
    private static final String IMAGE_FILEPATH_KEY = "image filepath key";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_picture);

        //Create database manager

        pictureName = (EditText)findViewById(R.id.add_new_picture_name_et);


        if (savedInstanceState != null) {
            mImagePath = savedInstanceState.getString(IMAGE_FILEPATH_KEY, null);

        }

// Add Listener for save button



        mCameraPicture = findViewById(R.id.camera_picture);
        mTakePictureButton = findViewById(R.id.take_picture_button);
        mTakePictureButton.setOnClickListener(new View.OnClickListener() {



            @Override
            public void onClick(View view) {
                takePhoto();

            }

        });

    }


    private void takePhoto() {


        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);



        // Check to see if there is a camera on this device.

        if (pictureIntent.resolveActivity(getPackageManager()) == null) {
            Toast.makeText(Take_Picture_Activity.this, "Your device does not have a camera", Toast.LENGTH_LONG).show();

        }


        else {

            // Create a unique filename for the image

            String imageFilename = "simple_camera_app_" + new Date().getTime();  //Create a unique filename with a timestamp

            File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);  // directory to store temp file
            File imageFile = null;

            Uri imageFileUri = null;



            try {

                // Create a temporary file with this name and path

                imageFile = File.createTempFile(imageFilename, ".jpg", storageDirectory);
                mImagePath = imageFile.getAbsolutePath();   // Save path in global variable

                // Create an URI from the path; the Intent will send this to the camera. A URI defines a location and how to access it

                // For example content://com.dez.aapp/my_images/simple_camera_app_15054908234543141945190112.jpg

                imageFileUri = FileProvider.getUriForFile(com.dez.cameraapp.Take_Picture_Activity.this, "com.dez.cameraapp", imageFile);
                Log.i(TAG, "image file URI  " + imageFileUri);



            } catch (IOException ioe) {
                Log.e(TAG, "Error creating file for photo storage", ioe);
                return;   // Will be unable to continue if unable to access storage

            }


            //So if creating the temporary file worked, should have a value for imageFileUri. Include this URI as an extra
            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);



            //And then request the camera is launched
            startActivityForResult(pictureIntent, TAKE_PICTURE_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {



        Log.d(TAG, "On Activity Result");
        if (resultCode == RESULT_OK && requestCode == TAKE_PICTURE_REQUEST_CODE) {
            saveImageToMediaStore();
        }
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.d(TAG, "onWindowFocusChanged");



        if (hasFocus && mImagePath != null) {
            scaleBitmap();
            mCameraPicture.setImageBitmap(mImage);
        }

    }

    private void saveImageToMediaStore() {
        //Add image to device's MediaStore - this makes the image accessible to the
        //gallery app, and any other apps that can read from the MediaStore
        //Do we have permission to write to storage?
        // Marshmallow and before, we just need to request permission in AndroidManifest,
        // and this check will return true if we've done so. The user will be notified that this app uses the file system when they first install it.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            MediaStore.Images.Media.insertImage(getContentResolver(), mImage, "SimpleCameraApp", "Photo taken by SimpleCameraApp");

        }

        // WRITE_EXTERNAL_STORAGE is a dangerous permission. So for Nougat and above, need to request permission in the mainfest

        // AND we will need to ask the user for permission when the app runs.

        else {

            //This request opens a dialog box for the user to accept the permission request.

            // When the user clicks ok or cancel, the onRequestPermission method (below) is called with the results

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_SAVE_IMAGE_PERMISSION_REQUEST_CODE);

        }

    }


    // This is the callback for requestPermissions for adding image to media store

    @Override

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        if (requestCode == REQUEST_SAVE_IMAGE_PERMISSION_REQUEST_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                //Now should be able to save

                MediaStore.Images.Media.insertImage(getContentResolver(), mImage, "SimpleCameraApp", "Photo taken by SimpleCameraApp");

            } else {

                Log.w(TAG, "Permission to WRITE_EXERNAL_STORAGE was NOT granted.");

                Toast.makeText(this, "The images taken will NOT saved to the gallery", Toast.LENGTH_SHORT).show();

            }

        }

    }


    //Handle rotation. Save the image pathname.

    // Bitmap objects are parcelable so can be put in a bundle, but the bitmap might be huge and take up more

    // memory that the app is allocated. So, put the file path and re-decode the bitmap on rotation.

    @Override

    public void onSaveInstanceState(Bundle outBundle) {

        outBundle.putString(IMAGE_FILEPATH_KEY, mImagePath);

    }
    // Scale picture taken to fit into the ImageView

    private void scaleBitmap() {


        //Step 1: what size is the ImageView?

        int imageViewHeight = mCameraPicture.getHeight();

        int imageViewWidth = mCameraPicture.getWidth();



        // If height or width are zero, there's no point doing this. Return.

        if (imageViewHeight == 0 || imageViewWidth == 0) {

            Log.w(TAG, "The image view size is zero. Unable to scale.");

            return;

        }



        //Step 2: decode file to find out how large the image is.



        // BitmapFactory is used to create bitmaps from pixels in a file.

        // Many options and settings, so use a BitmapFactory.Options object to store our desired settings.

        // Set the inJustDecodeBounds flag to true,

        // which means just the *information about* the picture is decoded and stored in bOptions

        // Not all of the pixels have to be read and stored in this process.

        // When we've done this, we can query bOptions to find out the original picture's height and width.



        BitmapFactory.Options bOptions = new BitmapFactory.Options();

        bOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(mImagePath, bOptions);

        int pictureHeight = bOptions.outHeight;
        int pictureWidth = bOptions.outWidth;


        //Step 3. Can use the original size and target size to calculate scale factor

        int scaleFactor = Math.min(pictureHeight / imageViewHeight, pictureWidth / imageViewWidth);



        //Step 4. Decode the image file into a new Bitmap, scaled to fit the ImageView

        bOptions.inJustDecodeBounds = false;   //setting this to false will actually decode the file to a Bitmap
        bOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(mImagePath, bOptions);
        mImage = bitmap;

    }

}



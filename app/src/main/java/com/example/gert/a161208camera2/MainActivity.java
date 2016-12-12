package com.example.gert.a161208camera2;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.widget.Toast.makeText;
/**
 * Gert Gous, 8 Des 2016
 * 161208Camera2
 * A simple program to take a photo, stor the photo, and display on screen.
 * This program explores two issues 1) FileProvider and 2) Basic Camera operation
 * For the FileProvider to work, you need to add the following:
 * 1) XML file with the path names allowed in res>xml>filepaths.xml
 * 2) AndroidManfest.xml needs various <uses-permission and <uses-features
 * 2.1) These must be inside the <application></>
 * 3) AndroidManifest needs <provider to provide for FileProvider
 * 3.1 The <provider></> provided must be inside of the <application> section within the Manifest
 * Check:
 * http://stackoverflow.com/questions/21931169/fileprovider-throws-exception-on-geturiforfile
 * https://github.com/commonsguy/cw-omnibus/tree/master/ContentProvider
 */
public class MainActivity extends AppCompatActivity {

    //Declare Variables
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    String mCurrentPhotoPath;               // unique name created for each photo taken


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     *
      * @param view This is random description to test effect
     */
    public void onClickButton(View view){
    dispatchTakePictureIntent();
    }

    public void onClickDisplayButton(View view){

        Toast toast = makeText(getApplicationContext(),mCurrentPhotoPath,Toast.LENGTH_SHORT);toast.show();

        ImageView mImageView = (ImageView)findViewById(R.id.image_View);
        mImageView.setImageBitmap(BitmapFactory.decodeFile(mCurrentPhotoPath));
        //ImageView jpgView = (ImageView)findViewById(R.id.image_View);
        //Bitmap bm = BitmapFactory.decodeFile(mCurrentPhotoPath);
        //jpgView.setImageBitmap(bm);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            Context context = getApplicationContext();
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Toast toast = makeText(context,photoFile.toString(),Toast.LENGTH_SHORT);toast.show();
                mCurrentPhotoPath = photoFile.toString();  //store for later reference
                Uri photoURI = FileProvider.getUriForFile(
                        getApplicationContext(),
                        "com.example.gert.a161208camera2.fileprovider",     // this has to match exactly the android:authorities provider in the Android Manifest
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);      //this is where the intent is dispatched
                /** Code fine to this point*/

            }
        }
    }

    private File createImageFile() throws IOException { //Creates a unique file name linked to date and time, stores Image
        String sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date());  // Create an image file name using date and time
        String imageFileName = "JPEG_" + sdf;  // Concatenate the file name
        //Create a File
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        Context context = getApplicationContext();
        return image;
    } //END createImageFile()

} // END CLASS


/**
 * Problems and issues experienced:
 * 1. Geting null object error is line 69 - where the getUriForFile() is called;
 *   > Maybe it is the second parameter: the reference to the path?
 *   -> Study getUriForFile() method in deph
 *          = in Manifest has to specify the Activity from which you will create the URI (in this case MainActivity)
 *    Solution: In the Manifest file, the <provider> HAS to be inside the  <application></> body of the file!!
 *
 * 2. Camera Intent fails, cannot invoke camera startActivityForResult()
 *   -> moved the permissions to inside the <application></>
 *   This resolved the problem
 *
 * 3. Camera Error - cannot connect to the camera
 *  -> Read up on the issue
 */





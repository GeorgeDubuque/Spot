package com.georgedubuque.spot

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v4.content.PermissionChecker
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import com.georgedubuque.spot.R.drawable.spot
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_add_spot.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/*
purpose:    allows user to add/edit/delete a spot from the map, currently does not add the spot
            /deletes it if the user clicks the back button when editing or adding.
 */

class AddSpot : AppCompatActivity(){

    var oldName = ""
    val DELETE = 4
    private var viewBitmap: Bitmap? = null
    private var pictureUri: Uri? = null
    private var img_path = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_spot)

        val adapter = ArrayAdapter.createFromResource(this,R.array.spot_types,
                R.layout.spinner)
        adapter.setDropDownViewResource(R.layout.spinner)
        spot_type.adapter = adapter

        button_edit.setOnClickListener{ editSpot() }
        button_delete.setOnClickListener { deleteSpot() }
        button_photo.setOnClickListener { takePhoto() }

        if(intent.getSerializableExtra("spot") != null){
            val spot = intent.getSerializableExtra("spot") as Spot
            spot_name.setText(spot.name)
            spot_rating.rating = spot.rating
            if(spot.img.isNotEmpty()){

                img_path = spot.img
                spot_img.setImageDrawable(Drawable.createFromPath(spot.img))
            }
            Log.d("numStars: ", spot.rating.toString())
            spot_type.setSelection(spot.typeNum)
            oldName = spot.name
        }

        checkReceivedIntent()
    }

    private fun checkReceivedIntent() {
        val imageReceivedIntent = intent
        val intentAction = imageReceivedIntent.action
        val intentType = imageReceivedIntent.type

        if (Intent.ACTION_SEND == intentAction && intentType != null) {
            if (intentType.startsWith(MIME_TYPE_IMAGE)) {
                selectedPhotoPath = imageReceivedIntent.getParcelableExtra(Intent.EXTRA_STREAM)
                setImageView()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.help_button) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Help:")
            builder.setMessage("This is where you can edit your spot. Eventually you will be able" +
                    "to add a photo as well. There is a place holder image for now. Swipe right " +
                    "to view menu.")
            builder.show()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun askPermissions() {
        @PermissionChecker.PermissionResult val permissionCheck
                = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE)
        } else {
            saveImageToGallery(viewBitmap)
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray) {
        if (requestCode == MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted, yay!
                saveImageToGallery(viewBitmap)
            } else {
                Toaster.show(this, R.string.permissions_please)
            }
        }
    }

    private fun saveImageToGallery(memeBitmap: Bitmap?) {
            // save bitmap to file
            memeBitmap?.let { it ->
                val imageFile =
                        File(
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                                it.toString() + FILE_SUFFIX_JPG
                        )
                img_path = imageFile.absolutePath
                Log.d("abs image_path",img_path)
                Log.d("con image path",imageFile.canonicalPath)
                Log.d("image path",imageFile.path)

                try {
                    // create output stream, compress image and write to file, flush and close outputstream
                    val fos = FileOutputStream(imageFile)
                    it.compress(Bitmap.CompressFormat.JPEG, 85, fos)
                    fos.flush()
                    fos.close()
                } catch (e: IOException) {
                    Toaster.show(this, R.string.save_image_failed)
                }

                Toaster.show(this, R.string.save_image_succeeded)
            }

    }

    fun takePhoto(){

        // declares and Intent object
        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // return the file path

        val imagePath = File(filesDir, "images")
        val newFile = File(imagePath, "default_image.jpg")

        if (newFile.exists()) {
            newFile.delete()
        } else {
            newFile.parentFile.mkdirs()
        }

        selectedPhotoPath =
                FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID
                        + ".fileprovider", newFile)

        // an intent can be as explicit or as implicit: ACTION_IMAGE_CAPTURE is a perfect example
        // of an Implicit Intent, and an implicit intent let Android developers give users
        // the power of choice.
        captureIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, selectedPhotoPath)
        val clip =
                ClipData.newUri(contentResolver, "A photo", selectedPhotoPath)
        captureIntent.clipData = clip
        captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

        startActivityForResult(captureIntent, REQUEST_IMAGE_CAPTURE)
    }

    fun setImageView(){

// the following in Kotlin doc call it "returns and jumps":
        // https://kotlinlang.org/docs/reference/returns.html
        val photoPath: Uri = selectedPhotoPath ?: return
        spot_img.post {
            /*
            BitmapResizer: a helper class bundled with the starter project to make sure the Bitmap
                           retrieve from the camera is scaled to the correct size for your device’s
                           screen. Although the device can scale the image for us, resizing it in
                           this way is more memory efficient.
             */
            viewBitmap = BitmapResizer.shrinkBitmap(
                    this,
                    photoPath,
                    spot_img.width,
                    spot_img.height
            )
            spot_img.setImageBitmap(viewBitmap)
        }

    }

    fun deleteSpot(){

        val lat = intent.getDoubleExtra("lat",0.0)
        val lng = intent.getDoubleExtra("lng",0.0)
        val latLng = LatLng(lat,lng)
        var spotName = spot_name.text.toString()
        var spotRating = 0f
        var spotType = 5


        if(spot_name.text.isBlank()){

            spotName = "Spot Name"
        }

        if(spot_type.isSelected){

            spotType = spot_type.selectedItemPosition
        }

        if(spot_rating.isSelected){

            spotRating = spot_rating.rating
        }

        val spot = Spot(spotName,spot_type.selectedItem.toString(),
                spotType ,spotRating, img_path, lat, lng)

        val intent = Intent()
        intent.putExtra("spot",spot)
        intent.putExtra("oldName",oldName)
        setResult(DELETE,intent)
        finish()
    }

    fun editSpot(){

        val lat = intent.getDoubleExtra("lat",0.0)
        val lng = intent.getDoubleExtra("lng",0.0)
        val latLng = LatLng(lat,lng)
        askPermissions()
        var spotName = spot_name.text.toString()
        var spotRating = spot_rating.rating
        var spotType = spot_type.selectedItemPosition


        if(spot_name.text.isBlank()){

            spotName = "Spot Name"
        }

        val spot = Spot(spotName,spot_type.selectedItem.toString(),
                spotType ,spotRating, img_path, lat, lng)

        val intent = Intent()
        intent.putExtra("oldName",oldName)
        intent.putExtra("spot",spot)
        setResult(RESULT_OK,intent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK){

            Log.d("function returned","take photo")
            setImageView()
        }
    }

    companion object {
        private const val MIME_TYPE_IMAGE = "image/"

        // this will be used to identify the take picture intent
        private const val take_photo_code = 1

        // set flag for the action wither of not the picture is taken
        private var pictureTaken: Boolean = false

        // initialize the path to save the image
        private var selectedPhotoPath: Uri? = null

        private val REQUEST_IMAGE_CAPTURE = 1
        private const val FILE_SUFFIX_JPG = ".jpg"

        // widely used sans-serif typeface: Helvetica
        private const val HELVETICA_FONT = "Helvetica"
        private const val MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 42

    }


}

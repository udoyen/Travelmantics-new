package com.connect.systems.ng.travelmantics_new

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_deal.*
import java.lang.Exception

class DealActivity : AppCompatActivity() {

    companion object {
        const val PICTURE_RESULT: Int = 42
    }

    // get the database reference
    private lateinit var firebaseDatabase: FirebaseDatabase
    private var databaseReference: DatabaseReference? = null
    var deal: TravelDeal? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deal)

        FirebaseUtil.openFbReference("traveldeals")
        firebaseDatabase = FirebaseUtil.firebaseDatabase!!
        databaseReference = FirebaseUtil.databaseReference

        // get the passed extra
        var extra: TravelDeal? = intent.getParcelableExtra("Deal")
        //
//        var d : TravelDeal? = extra
        // if null we came from the insert deal
        if (extra == null) {
            extra = TravelDeal()
        }
        this.deal = extra

        txtDescription.setText(deal!!.description)
        txtPrice.setText(deal!!.price)
        txtTitle.setText(deal!!.title)
        showImage(deal!!.imageUrl!!)

        btnImage.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            // set the intent type
            intent.type = "image/jpeg"
            // accept only local content
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            startActivityForResult(Intent.createChooser(intent, "Insert Picture"), PICTURE_RESULT)
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == PICTURE_RESULT) {
            if (data != null) {

                val imageUri: Uri = data.data!!
                val ref: StorageReference = FirebaseUtil.storageRef!!.child(imageUri.lastPathSegment!!)
                val uploadTask: UploadTask = ref.putFile(imageUri)
                uploadTask.addOnSuccessListener { taskSnapshot ->
                    ref.downloadUrl.addOnSuccessListener { uri ->
                        val url: String = uri.toString()
                        val pictureName: String = taskSnapshot.storage.name
                        deal!!.imageName = pictureName
                        deal!!.imageUrl = url
                        Log.d("Url: ", url)
                        Log.d("Name: ", pictureName)
                        showImage(url)

                    }.addOnFailureListener { exception : Exception ->
                        Log.d("UPLOAD", "Error: " + exception.message)
                        Toast.makeText(this, "File Upload Error!", Toast.LENGTH_LONG).show()
                    }
                }

            } else {
                Log.d("IMAGE", resultCode.toString())
                Toast.makeText(this, "Upload failure", Toast.LENGTH_LONG).show()
            }

        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.save_menu, menu)
        val saveMenu: MenuItem = menu!!.findItem(R.id.save_menu)
        val deleteMenu: MenuItem = menu.findItem(R.id.delete_menu)
        if (FirebaseUtil.isAdmin!!) {
            saveMenu.isVisible = true
            deleteMenu.isVisible = true
            enabledEditText(true)
            btnImage.isEnabled = true
        } else {
            saveMenu.isVisible = false
            deleteMenu.isVisible = false
            enabledEditText(false)
            btnImage.isEnabled = false
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save_menu -> {
                saveDeal()
                Toast.makeText(applicationContext, "Deal Saved", Toast.LENGTH_LONG).show()
                // use to reset the content of
                // the edit text after it has
                // sent to the database
                clean()
                backToList()
            }
            R.id.delete_menu -> {
                deleteDeal()
                Toast.makeText(this, "Deal Deleted", Toast.LENGTH_LONG).show()
                backToList()
            }
            else -> super.onOptionsItemSelected(item)

        }

        return true
    }

    private fun clean() {
        txtTitle.text = null
        txtDescription.text = null
        txtPrice.text = null

        txtTitle.requestFocus()

    }

    private fun saveDeal() {
        deal!!.title = txtTitle.text.toString().trim()
        deal!!.description = txtDescription.text.toString().trim()
        deal!!.price = txtPrice.text.toString().trim()

        if (deal!!.id == "") {
            // save data into firebase
            databaseReference!!.push().setValue(deal)
                .addOnSuccessListener {
                    Toast.makeText(this, "Success", Toast.LENGTH_LONG).show()

                }
                .addOnFailureListener { ex: Exception ->
                    Log.d("TAG", ex.toString())

                }
        } else {
            databaseReference!!.child(deal!!.id.toString()).setValue(deal)
        }


    }

    private fun deleteDeal() {
        if (deal == null) {
            Toast.makeText(this, "Please save the deal before deleting", Toast.LENGTH_SHORT).show()
            return
        }
        databaseReference!!.child(deal!!.id.toString()).removeValue()

        if (deal!!.imageName != "" && !deal!!.imageName!!.isEmpty()) {
            val picRef: StorageReference = FirebaseUtil.storageRef!!.child(deal!!.imageName!!)
            picRef.delete().addOnSuccessListener {
                Log.d("Delete Image", "Image Successfully Deleted")
            }.addOnFailureListener { p0 ->
                Log.d("Delete Image Error", p0.message!!)
            }
        }
    }

    private fun backToList() {
        val intent = Intent(this, ListActivity::class.java)
        startActivity(intent)
    }

    private fun enabledEditText(isEnabled: Boolean) {
        txtTitle.isEnabled = isEnabled
        txtPrice.isEnabled = isEnabled
        txtDescription.isEnabled = isEnabled
    }

    private fun showImage(url: String) {
        if (!url.isEmpty()) {
            val width = Resources.getSystem().displayMetrics.widthPixels
            val picasso = Picasso.get()
            picasso.load(url)
                .resize(width, width * 2 / 3)
                .centerCrop()
                .into(image)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}

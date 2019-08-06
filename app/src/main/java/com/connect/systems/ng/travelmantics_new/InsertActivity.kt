package com.connect.systems.ng.travelmantics_new


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_deal.*
import java.lang.Exception

class InsertActivity : AppCompatActivity() {

    // get the database reference
    private var firebaseDatabase: FirebaseDatabase? = null
    private var databaseReference: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deal)

        FirebaseUtil.openFbReference("traveldeals")
        firebaseDatabase = FirebaseUtil.firebaseDatabase
        databaseReference = FirebaseUtil.databaseReference


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.save_menu, menu)

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
        val title = txtTitle.text.toString().trim()
        val description = txtDescription.text.toString().trim()
        val price = txtPrice.text.toString().trim()

        val travelDeal = TravelDeal(title, description, price, "")

        // save data into firebase
        databaseReference!!.push().setValue(travelDeal)
            .addOnSuccessListener {
                Toast.makeText(this, "Success", Toast.LENGTH_LONG).show()

            }
            .addOnFailureListener { ex : Exception ->
                Log.d("TAG", ex.toString())

            }

    }
}

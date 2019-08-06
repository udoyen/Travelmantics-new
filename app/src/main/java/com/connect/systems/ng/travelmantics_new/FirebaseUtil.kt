package com.connect.systems.ng.travelmantics_new

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.*
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlin.collections.ArrayList

/**
 * Class can't be instantiated
 */
@SuppressLint("Registered")
class FirebaseUtil private constructor(){

    companion object {
        private const val RC_SIGN_IN: Int = 200
        var firebaseDatabase: FirebaseDatabase? = null
        var databaseReference: DatabaseReference? = null
        var deals: ArrayList<TravelDeal>? = null
        private var firebaseUtil: FirebaseUtil? = null
        var firebaseAuth: FirebaseAuth? = null
        var isAdmin: Boolean? = null
        var childEventListener: ChildEventListener? = null
        var storage : FirebaseStorage? = null
        var storageRef : StorageReference? = null


        fun openFbReference(ref: String) {
            if (firebaseUtil == null) {
                firebaseUtil = FirebaseUtil()
                firebaseDatabase = FirebaseDatabase.getInstance()
                firebaseAuth = getInstance()

            }
            deals = ArrayList()
            databaseReference = firebaseDatabase!!.reference.child(ref)
            connectStorage()
        }

        fun attachListener(callerActivity: ListActivity) {
            // TODO change this to lamda expression
            firebaseAuth!!.addAuthStateListener(object : AuthStateListener {
                override fun onAuthStateChanged(firebaseAuth: FirebaseAuth) {
                    if (firebaseAuth.currentUser == null) {
                        signin(callerActivity)

                    } else {
                        val userId: String = firebaseAuth.uid.toString()
                        checkAdmin(userId, callerActivity)
                    }
                    Toast.makeText(callerActivity.baseContext, "Welcome Back", Toast.LENGTH_LONG).show()

                }
            })
        }

        private fun checkAdmin(userId: String, activity: ListActivity) {

            isAdmin = false

            val ref: DatabaseReference = firebaseDatabase!!.reference
                .child("administrators")
                .child(userId)

            childEventListener = ref.addChildEventListener(object : ChildEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    Log.d("Cancelled", "FirebaseUtil cancel")
                }

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                    isAdmin = true
                    activity.showMenu(activity)

                }

                override fun onChildRemoved(p0: DataSnapshot) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            })
        }

        fun detachListener(callerActivity: ListActivity) {
            firebaseAuth!!.removeAuthStateListener(object : AuthStateListener {
                override fun onAuthStateChanged(p0: FirebaseAuth) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            })
        }

        fun signin(callerActivity: Activity) {

            callerActivity.startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
//                    .setIsSmartLockEnabled(false)
                    .setAvailableProviders(
                        listOf(
                            AuthUI.IdpConfig.GoogleBuilder().build(),
                            AuthUI.IdpConfig.EmailBuilder().build()
                        )
                    )
                    .build(),
                RC_SIGN_IN
            )
        }

        fun connectStorage() {
            storage = FirebaseStorage.getInstance()
            storageRef = storage!!.reference.child("deals_pictures")
        }


    }

}
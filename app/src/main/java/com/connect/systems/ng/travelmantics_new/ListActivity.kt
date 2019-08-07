package com.connect.systems.ng.travelmantics_new

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_list.*
import com.firebase.ui.auth.AuthUI


class ListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.list_activity_menu, menu)
        val insertMenu : MenuItem = menu!!.findItem(R.id.insert_menu)
        insertMenu.isVisible = FirebaseUtil.isAdmin == true
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.insert_menu -> {
                val intent = Intent(this, DealActivity::class.java)
                startActivity(intent)
            }
            R.id.logout_menu -> {
                AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener {
                        FirebaseUtil.attachListener(this@ListActivity)
                        // user is now signed out
//                        startActivity(Intent(this@ListActivity, SignInActivity::class.java))
                        finish()
                    }
                FirebaseUtil.detachListener(this@ListActivity)
            }
        }
        return true
    }

    override fun onPause() {
        super.onPause()
        FirebaseUtil.detachListener(this@ListActivity)
    }

    override fun onResume() {
        super.onResume()
        rvDeals.adapter = DealAdapter()
        rvDeals.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        FirebaseUtil.attachListener(this@ListActivity)
    }

    fun showMenu(activity: ListActivity) {

        activity.invalidateOptionsMenu()
    }




}

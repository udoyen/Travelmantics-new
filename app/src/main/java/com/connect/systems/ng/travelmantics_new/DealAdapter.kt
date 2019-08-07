package com.connect.systems.ng.travelmantics_new

import android.content.Context
import com.connect.systems.ng.travelmantics_new.FirebaseUtil.Companion.deals

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class DealAdapter : RecyclerView.Adapter<DealAdapter.DealViewHolder>() {

    private var deals : ArrayList<TravelDeal> = ArrayList()
    private var firebaseDatabase : FirebaseDatabase? = null
    private var databaseReference : DatabaseReference? = null
    private var childEventListener : ChildEventListener? = null
    private val className : String = DealAdapter::class.java.name

    init {

        FirebaseUtil.openFbReference("traveldeals")
        firebaseDatabase = FirebaseUtil.firebaseDatabase
        databaseReference = FirebaseUtil.databaseReference
        deals = FirebaseUtil.deals!!
//        context!!.applicationContext
        childEventListener = databaseReference!!.addChildEventListener(object : ChildEventListener{
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                Log.d("CHANGE", "A new file was added")
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                val td : TravelDeal? = dataSnapshot.getValue(TravelDeal::class.java)
                Log.d("Deal: ", td!!.title as String)
                // set the td object to the firebase
                // snapshot
                td.id = dataSnapshot.key
                // add to the ArrayList
                deals.add(td)
                notifyItemInserted(deals.size - 1)

            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                val td : TravelDeal? = dataSnapshot.getValue(TravelDeal::class.java)
                Log.d("Deal: ", td!!.title as String)
                // set the td object to the firebase snapshot
                td.id = dataSnapshot.key
                // add to the ArrayList
                deals.remove(td)
                notifyItemInserted(deals.size - 1)
            }

            override fun onCancelled(dbError: DatabaseError) {
                Log.d("SignInError", "Database connection cancelled: $className " + dbError.message)
//                Toast.makeText(context!!.applicationContext, dbError.message, Toast.LENGTH_LONG).show()
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                Log.d("MOVED", "The file was moved: $className")
            }
        })
    }

    /**
     * Called when recycler view
     * needs a new view holder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DealViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.rv_row, parent, false)

        return DealViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return deals.size
    }

    /**
     * Called to display the data
     */
    override fun onBindViewHolder(holder: DealViewHolder, position: Int) {
        val deal : TravelDeal = deals[position]
        holder.bind(deal)
    }

    class DealViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), OnClickListener {

        init {
            itemView.setOnClickListener {
                onClick(it)
            }
        }

        override fun onClick(itemView: View?) {
            val position : Int = adapterPosition
            Log.d("Click", position.toString())
            val selectedDeal : TravelDeal = deals!![position]
            val intent = Intent(itemView!!.context, DealActivity::class.java)
            intent.putExtra("Deal", selectedDeal)
            itemView.context.startActivity(intent)

        }

        val tvTitle = itemView.findViewById<TextView>(R.id.tvTitle)
        val tvDescription = itemView.findViewById<TextView>(R.id.tvDescription)
        val tvPrice = itemView.findViewById<TextView>(R.id.tvPrice)
        val imageDeal = itemView.findViewById<ImageView>(R.id.imageDeal)

        fun bind(deal : TravelDeal) {
            tvTitle.text = deal.title
            tvDescription.text = deal.description
            tvPrice.text = deal.price
            showImage(deal.imageUrl!!)


        }

        private fun showImage(url : String) {
            if (!url.isEmpty()) {
                val picasso = Picasso.get()
                picasso.load(url)
                    .resize(160, 160)
                    .centerCrop()
                    .into(imageDeal)
            }
        }
    }



}
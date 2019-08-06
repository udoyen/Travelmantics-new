package com.connect.systems.ng.travelmantics_new



import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class DealAdapter : RecyclerView.Adapter<DealAdapter.DealViewHolder>() {

    private var deals : ArrayList<TravelDeal> = ArrayList()
    private var firebaseDatabase : FirebaseDatabase? = null
    private var databaseReference : DatabaseReference? = null
    private var childEventListener : ChildEventListener? = null

    init {
        FirebaseUtil.openFbReference("traveldeals")
        firebaseDatabase = FirebaseUtil.firebaseDatabase
        databaseReference = FirebaseUtil.databaseReference
        deals = FirebaseUtil.deals!!
        childEventListener = databaseReference!!.addChildEventListener(object : ChildEventListener{
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                val td = dataSnapshot.getValue(TravelDeal::class.java)
                Log.d("Deal: ", td!!.title as String)
                // set the td object to the firebase
                // snapshot
                td.id = dataSnapshot.key
                // add to the ArrayList
                deals.add(td)
                notifyItemInserted(deals.size - 1)

//                tvDeals.text = getString(R.string.deal_title, tvDeals.text, td!!.title)
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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

    class DealViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle = itemView.findViewById<TextView>(R.id.tvTitle)

        fun bind(deal : TravelDeal) {
            tvTitle.text = deal.title
        }
    }



}
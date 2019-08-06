package com.connect.systems.ng.travelmantics_new

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class TravelDeal(
    var id : String? = "",
    var title: String? = "",
    var description: String? = "",
    var price: String? = "",
    var imageUrl: String? = "",
    var imageName: String? = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeString(price)
        parcel.writeString(imageUrl)
        parcel.writeString(imageName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TravelDeal> {
        override fun createFromParcel(parcel: Parcel): TravelDeal {
            return TravelDeal(parcel)
        }

        override fun newArray(size: Int): Array<TravelDeal?> {
            return arrayOfNulls(size)
        }
    }
}
package com.example.myapplication.classes

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

class ListAttendance(val student_name: String, val time: String) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
                parcel.readString()!!)


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(student_name)
        parcel.writeString(time)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ListAttendance> {
        override fun createFromParcel(parcel: Parcel): ListAttendance {
            return ListAttendance(parcel)
        }

        override fun newArray(size: Int): Array<ListAttendance?> {
            return arrayOfNulls(size)
        }
    }

}
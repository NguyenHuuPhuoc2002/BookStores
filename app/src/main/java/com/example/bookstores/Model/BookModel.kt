package com.example.bookstores.Model

import android.os.Parcel
import android.os.Parcelable

class BookModel(
    var bid:String? = null,
    val btitle: String? = null,
    val bimg: String? = null,
    val bauthor: String? = null,
    val bnxb: String? = null,
    val bnumpages: String? = null,
    val bkindOfSach: String? = null,
    val bprice: Double = 0.0,
    val bdetail: String? = null,
): Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        (parcel.readDouble() ?: "") as Double,
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(bid)
        parcel.writeString(btitle)
        parcel.writeString(bimg)
        parcel.writeString(bauthor)
        parcel.writeString(bnxb)
        parcel.writeString(bnumpages)
        parcel.writeString(bkindOfSach)
        parcel.writeDouble(bprice)
        parcel.writeString(bdetail)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BookModel> {
        override fun createFromParcel(parcel: Parcel): BookModel {
            return BookModel(parcel)
        }

        override fun newArray(size: Int): Array<BookModel?> {
            return arrayOfNulls(size)
        }
    }
}
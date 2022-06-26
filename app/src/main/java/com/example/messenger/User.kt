package com.example.messenger

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(val username:String,val login:String, val profileImageUrl: String): Parcelable {
    constructor() : this("","","")
}
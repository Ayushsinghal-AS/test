package com.example.lksk

import com.google.firebase.database.IgnoreExtraProperties



@IgnoreExtraProperties
class User {
    var name: String? = null
    var email: String? = null

    // Default constructor required for calls to
// DataSnapshot.getValue(User.class)
    constructor() {}

    constructor(name: String?, email: String?) {
        this.name = name
        this.email = email
    }
}
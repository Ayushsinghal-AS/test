package com.example.lksk

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*


class MainActivity : AppCompatActivity() {
    private var txtDetails: TextView? = null
    private var inputName: EditText? = null
    private var inputEmail: EditText? = null
    private var btnSave: Button? = null
    private var mFirebaseDatabase: DatabaseReference? = null
    private var mFirebaseInstance: FirebaseDatabase? = null
    private var userId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Displaying toolbar icon
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setIcon(R.mipmap.ic_launcher)
        txtDetails = findViewById<View>(R.id.txt_user) as TextView
        inputName = findViewById<View>(R.id.name) as EditText
        inputEmail = findViewById<View>(R.id.email) as EditText
        btnSave = findViewById<View>(R.id.btn_save) as Button
        mFirebaseInstance = FirebaseDatabase.getInstance()
        // get reference to 'users' node
        mFirebaseDatabase = mFirebaseInstance?.getReference("users")
        // store app title to 'app_title' node
        mFirebaseInstance?.getReference("app_title")?.setValue("Realtime Database")
        // app_title change listener
        mFirebaseInstance?.getReference("app_title")
            ?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    Log.e(TAG, "App title updated")
                    val appTitle =
                        dataSnapshot.getValue(String::class.java)
                    // update toolbar title
                    supportActionBar!!.setTitle(appTitle)
                }

                override fun onCancelled(error: DatabaseError) { // Failed to read value
                    Log.e(
                        TAG,
                        "Failed to read app title value.",
                        error.toException()
                    )
                }
            })
        // Save / update the user
        btnSave!!.setOnClickListener {
            val name = inputName!!.text.toString()
            val email = inputEmail!!.text.toString()
            // Check for already existed userId
            if (TextUtils.isEmpty(userId)) {
                createUser(name, email)
            } else {
                updateUser(name, email)
            }
        }
        toggleButton()
    }

    // Changing button text
    private fun toggleButton() {
        if (TextUtils.isEmpty(userId)) {
            btnSave!!.text = "Save"
        } else {
            btnSave!!.text = "Update"
        }
    }

    /**
     * Creating new user node under 'users'
     */
    private fun createUser(name: String, email: String) { // TODO
// In real apps this userId should be fetched
// by implementing firebase auth
        if (TextUtils.isEmpty(userId)) {
            userId = mFirebaseDatabase!!.push().key
        }
        val user = User(name, email)
        userId?.let { mFirebaseDatabase!!.child(it).setValue(user) }
        addUserChangeListener()
    }

    /**
     * User data change listener
     */
    private fun addUserChangeListener() { // User data change listener
        userId?.let {
            mFirebaseDatabase!!.child(it).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user = dataSnapshot.getValue(
                        User::class.java
                    )
                    // Check for null
                    if (user == null) {
                        Log.e(TAG, "User data is null!")
                        return
                    }
                    Log.e(
                        TAG,
                        "User data is changed!" + user.name + ", " + user.email
                    )
                    // Display newly updated name and email
                    txtDetails!!.text = user.name + ", " + user.email
                    // clear edit text
                    inputEmail!!.setText("")
                    inputName!!.setText("")
                    toggleButton()
                }

                override fun onCancelled(error: DatabaseError) { // Failed to read value
                    Log.e(
                        TAG,
                        "Failed to read user",
                        error.toException()
                    )
                }
            })
        }
    }

    private fun updateUser(
        name: String,
        email: String
    ) { // updating the user via child nodes
        if (!TextUtils.isEmpty(name)) userId?.let { mFirebaseDatabase!!.child(it).child("name").setValue(name) }
        if (!TextUtils.isEmpty(email)) userId?.let {
            mFirebaseDatabase!!.child(it).child("email").setValue(
                email
            )
        }
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}

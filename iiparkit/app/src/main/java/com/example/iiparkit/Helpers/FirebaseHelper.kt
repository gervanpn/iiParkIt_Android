package com.example.iiparkit.Helpers

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

object FirebaseHelper {

    private  var auth: FirebaseAuth
    var currentUser : FirebaseUser
// ...
// Initialize Firebase Auth
    init {
        auth = Firebase.auth
        currentUser = auth.currentUser!!
    }
}
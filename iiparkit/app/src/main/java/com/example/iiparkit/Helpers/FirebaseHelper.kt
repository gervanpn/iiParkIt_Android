package com.example.iiparkit.Helpers

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.example.iiparkit.MainActivity

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.UserProfileChangeRequest

@SuppressLint("StaticFieldLeak")
object FirebaseHelper {

    private  var auth: FirebaseAuth
    //private var db : Firebase
    private var context : Context
    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()
    var currentUser : FirebaseUser

    @SuppressLint("StaticFieldLeak")
    val docData = hashMapOf(
        "stringExample" to "Hello world!",
        "booleanExample" to true,
        "numberExample" to 3.14159265,
        "dateExample" to Timestamp.now(),
        "listExample" to arrayListOf(1, 2, 3)
        //"nullExample" to null
    )
// ...
// Initialize Firebase Auth
    init {
        auth = Firebase.auth
        currentUser = auth.currentUser!!
        context = MainActivity().applicationContext
    }


    fun updateDisplayName(displayName : String, imageUrl : String){
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(displayName)
            .setPhotoUri(Uri.parse(imageUrl))
            .build()

        currentUser.updateProfile(profileUpdates)
            .addOnCompleteListener(OnCompleteListener<Void?> { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "User profile updated.")
                }
            })
    }
    fun signIn(email : String, password : String){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(context as Activity) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    Toast.makeText(context, "Authentication Successful.",
                        Toast.LENGTH_SHORT).show()
                    val user = auth.currentUser
                    //updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(context, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    //updateUI(null)
                }
            }
    }

    fun signUp(email : String, password : String){
        auth
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(context as Activity) { task ->
                //mProgressBar!!.hide()

                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(context as Activity, "Account Creation Successful.",
                        Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "createUserWithEmail:success")

                    val userId = auth.currentUser!!.uid

                    //Verify Email
                    verifyEmail();

                    //update user profile information
                    //val currentUserDb = db!!.child(userId)
                    //currentUserDb.child("firstName").setValue(firstName)
                    //currentUserDb.child("lastName").setValue(lastName)

                    //updateUserInfoAndUI()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(context as Activity, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun verifyEmail(){
        val mUser = auth.currentUser;
        mUser!!.sendEmailVerification()
            .addOnCompleteListener(context as Activity) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context,
                        "Verification email sent to " + mUser.getEmail(),
                        Toast.LENGTH_SHORT).show()
                } else {
                    Log.e(TAG, "sendEmailVerification", task.exception)
                    Toast.makeText(context,
                        "Failed to send verification email.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun signOut(){
        auth.signOut()
    }

    fun readDBList(collection : String, searchField : String, searchFor : Any){
        db.collection(collection)
            .whereEqualTo(searchField, searchFor)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }
    fun readDB(collection : String, document : String) : String {
        val docRef = db.collection(collection).document(document)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }

        return ""
    }

    fun writeDB(collection : String, document : String, data : String){
        db.collection(collection).document(document)
            .set(docData)
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
    }
}
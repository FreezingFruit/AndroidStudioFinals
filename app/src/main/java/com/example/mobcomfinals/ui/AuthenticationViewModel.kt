package com.example.mobcomfinals.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mobcomfinals.model.OwnedPropertyModel
import com.example.mobcomfinals.model.ProfileModel
import com.example.mobcomfinals.model.PropertyModel
import com.example.mobcomfinals.states.StorageStates
import com.example.mobcomfinals.viewmodel.NODE_OWNEDPROPERTY
import com.example.mobcomfinals.viewmodel.NODE_USER
import com.example.mobcomfinals.viewmodel.NODE_USERINFORMATION
import com.example.mobcomfinals.viewmodel.NODE_USER_IMAGES
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class AuthenticationViewModel:ViewModel() {
    private val database = FirebaseDatabase.getInstance()
    private val auth = Firebase.auth
    private val ref = Firebase.database.reference
    private var state = MutableLiveData<StorageStates>()
    private var states = MutableLiveData<AuthenticationStates>()
    private val refUser = database.getReference(NODE_USER)
    private var fb_storage = FirebaseStorage.getInstance().getReference(NODE_USER_IMAGES)


    private lateinit var gso: GoogleSignInOptions

    fun getStates(): LiveData<AuthenticationStates> = states

    fun isSignedIn() {
        states.value = AuthenticationStates.IsSignedIn(auth.currentUser != null)
    }

    fun signUp(email : String, password : String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if(it.isSuccessful) states.value = AuthenticationStates.SignedUp
            else states.value = AuthenticationStates.Error

        }.addOnFailureListener {
            states.value = AuthenticationStates.Error
        }
    }

    fun signIn(email : String, password : String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if(it.isSuccessful) states.value = AuthenticationStates.SignedIn
            else states.value = AuthenticationStates.Error

        }.addOnFailureListener {
            states.value = AuthenticationStates.Error
        }
    }

    fun getUserProfile() {
        /*val user = auth.currentUser
        user?.let {
            var uid = it.uid
            var name = it.displayName
            var email = it.email
            var photoUrl = it.photoUrl
        }
        states.value = AuthenticationStates.Default(user)*/

        val objectListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val users = snapshot.getValue<ProfileModel>()
                states.value = AuthenticationStates.Default(users)
            }

            override fun onCancelled(error: DatabaseError) {
                states.value = AuthenticationStates.Error
            }

        }
        ref.child(NODE_USER).child(auth.currentUser?.uid!!).child(NODE_USERINFORMATION).addValueEventListener(objectListener)
    }

    fun updateUserProfile(newName : String) {
        val userUpdates = userProfileChangeRequest {
            displayName = newName
        }

        auth.currentUser?.updateProfile(userUpdates)?.addOnCompleteListener {
            if(it.isSuccessful) states.value = AuthenticationStates.ProfileUpdated
            else states.value = AuthenticationStates.Error

        }?.addOnFailureListener {
            states.value = AuthenticationStates.Error
        }
    }

    private fun updateUserEmail(newEmail : String) {
        auth.currentUser?.updateEmail(newEmail)?.addOnCompleteListener {
            if(it.isSuccessful)states.value = AuthenticationStates.EmailUpdated
            else states.value = AuthenticationStates.Error

        }?.addOnFailureListener {
            states.value = AuthenticationStates.Error
        }
    }

    private fun updateUserPassword(newPassword : String) {
        auth.currentUser?.updatePassword(newPassword)?.addOnCompleteListener {
            if(it.isSuccessful) states.value = AuthenticationStates.PasswordUpdated
            else states.value = AuthenticationStates.Error

        }?.addOnFailureListener {
            states.value = AuthenticationStates.Error
        }
    }

    fun sendVerificationEmail() {
        auth.currentUser?.sendEmailVerification()?.addOnCompleteListener {
            if(it.isSuccessful) states.value = AuthenticationStates.VerificationEmailSent
            else states.value = AuthenticationStates.Error

        }?.addOnFailureListener {
            states.value = AuthenticationStates.Error
        }
    }

    fun sendPasswordResetEmail(email : String) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener {
            if(it.isSuccessful) states.value = AuthenticationStates.PasswordResetEmailSent
            else states.value = AuthenticationStates.Error

        }?.addOnFailureListener {
            states.value = AuthenticationStates.Error
        }
    }

    fun logOut() {
        auth.signOut()
        states.value = AuthenticationStates.LogOut
    }

    fun deleteUser() {
        auth.currentUser?.delete()?.addOnCompleteListener {
            if(it.isSuccessful) states.value = AuthenticationStates.UserDeleted
            else states.value = AuthenticationStates.Error

        }?.addOnFailureListener {
            states.value = AuthenticationStates.Error
        }
    }

    fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)

        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) states.value = AuthenticationStates.SignedIn
            else states.value = AuthenticationStates.Error
        }.addOnFailureListener {

        }
    }


    fun createUserRecord (img : ByteArray,email : String, contactNumber : String , username : String){
        val userRef = fb_storage.child("${auth.currentUser?.uid}.jpg")
        userRef.putBytes(img).addOnSuccessListener {
            userRef.downloadUrl.addOnSuccessListener {
                val user = ProfileModel(null, it.toString() ,email, contactNumber, username)
                refUser.child(auth.currentUser?.uid.toString()).child(NODE_USERINFORMATION).setValue(user)

            }
        }

    }


    fun getUserUid(): String?{
        return auth.currentUser?.uid
    }

    fun getUserEmail(): String?{
        return auth.currentUser?.email
    }

    fun getUserNum(): String?{
        return auth.currentUser?.phoneNumber
    }








}
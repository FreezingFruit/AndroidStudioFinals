package com.example.mobcomfinals.ui

import android.app.Activity
import android.content.Intent

import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.mobcomfinals.MainActivity
import com.example.mobcomfinals.R
import com.example.mobcomfinals.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding
    private lateinit var viewModel : AuthenticationViewModel //VIEWMODEL
    private lateinit var gso : GoogleSignInOptions
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private var auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = AuthenticationViewModel()
        viewModel.getStates().observe(this@LoginActivity){
            renderUi(it)
        }

        createRequest()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail().build()

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso)

        with(binding) {
            btnSignin.setOnClickListener {
                viewModel.signIn(
                    binding.tieEmail.text.toString(),
                    binding.tiePassword.text.toString()
                )
            }

            btnGoogle.setOnClickListener {
                mGoogleSignInClient.signOut()
                val signInIntent = mGoogleSignInClient.signInIntent

                startActivityForResult(signInIntent, 2)
            }



            tvSignup.apply {
                text = addClickableLink("Don't have an account yet? Sign up", "Sign up"){
                    SignUp.launch(this@LoginActivity)
                }
                movementMethod = LinkMovementMethod.getInstance()
            }
        }

    }

    private fun renderUi(states : AuthenticationStates) {
        when(states) {
            is AuthenticationStates.IsSignedIn -> {
                if(states.isSignedIn) {
                    MainActivity.launch(this@LoginActivity)
                    finish()
                }
            }
            AuthenticationStates.SignedIn -> {
                MainActivity.launch(this@LoginActivity)
                finish()
            }
            AuthenticationStates.Error -> {}
            else -> {}
        }
    }

    private fun createRequest() {
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(com.firebase.ui.auth.R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 2) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val exception = task.exception

            try {
                val account = task.getResult(ApiException::class.java)!!
                viewModel.firebaseAuthWithGoogle(account)

            } catch (e: ApiException) {
                //TODO
            }

        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.isSignedIn()
    }

    private fun addClickableLink(fullText: String, linkText: String, callback: () -> Unit): SpannableString {
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                callback.invoke()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true
                ds.typeface = Typeface.DEFAULT_BOLD
            }

        }

        val startIndex = fullText.indexOf(linkText, 0, true)

        return SpannableString(fullText).apply {
            setSpan(clickableSpan, startIndex, startIndex + linkText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    private fun signInGoogle(){
        val signInIntent = mGoogleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        if(result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResults(task)
        }
    }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if(task.isSuccessful){
            val account: GoogleSignInAccount? = task.result
            if(account != null){
                updateUi(account)
            }
        }else{
            Toast.makeText(this,task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUi(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if(it.isSuccessful){
                val intent : Intent = Intent(this, MainActivity::class.java)
                intent.putExtra("email", account.email)
                startActivity(intent)
            }else{
                Toast.makeText(this,it.exception.toString(), Toast.LENGTH_SHORT).show()

            }
        }
    }

    companion object {
        fun launch(activity : Activity) = activity.startActivity(Intent(activity, LoginActivity::class.java))
=======
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    companion object {
        fun launch(activity : Activity) = activity.startActivity(Intent(activity, MainActivity::class.java))
>>>>>>> origin/main
    }
}
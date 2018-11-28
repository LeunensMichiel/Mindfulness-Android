package com.hogent.mindfulness.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.hogent.mindfulness.MainActivity
import com.hogent.mindfulness.R

/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity(), LoginFragment.LoginFragmentCallBack, RegisterFragment.LoginFragmentCallBack {

    lateinit var loginFragment : LoginFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Log.i("wtf", "MOTHERFUCKER")
        loginFragment = LoginFragment()

        supportFragmentManager.beginTransaction()
            .add(R.id.login_container, loginFragment)
            .commit()
    }

    override fun onStart() {
        super.onStart()

        if (!checkedIfLoggedIn()) {
            sendToMain()
        }
    }

    private fun checkedIfLoggedIn(): Boolean {
        val token = getSharedPreferences(getString(R.string.sharedPreferenceUserDetailsKey), Context.MODE_PRIVATE)
            .getString(getString(R.string.authTokenKey), null)
        return token == null
    }

    private fun sendToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    //This function replqces the register fragment back with the login fragment
    override fun onClickGoBackToLogin() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.login_container, loginFragment)
            .commit()    }

    /**
     * This function replaces the login fragment with the register fragment
     */
    override fun onclickRegister() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.login_container, RegisterFragment())
            .commit()
    }




}

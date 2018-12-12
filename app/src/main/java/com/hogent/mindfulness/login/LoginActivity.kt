package com.hogent.mindfulness.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.hogent.mindfulness.MainActivity
import com.hogent.mindfulness.R

/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity(), LoginFragment.LoginFragmentCallBack, RegisterFragment.LoginFragmentCallBack {

    lateinit var loginFragment: LoginFragment
    lateinit var registerFragment: RegisterFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        registerFragment = RegisterFragment()
        loginFragment = LoginFragment()

//        if (savedInstanceState != null) {
//            //Restore the fragment's instance
//            registerFragment = getSupportFragmentManager().getFragment(savedInstanceState, "registerFragment") as RegisterFragment
//            supportFragmentManager.beginTransaction()
//                .add(R.id.login_container, registerFragment)
//                .commit()
//        }
//        else {
        if (intent.getIntExtra("register", 0) == 0)
            supportFragmentManager.beginTransaction()
                .add(R.id.login_container, loginFragment)
                .addToBackStack( "login" )
                .commit()
//        }
    }

    override fun onResume() {
        super.onResume()
        if (intent.hasExtra("register"))
            supportFragmentManager.beginTransaction()
                .replace(R.id.login_container, registerFragment)
                .commit()
        else
            supportFragmentManager.beginTransaction()
                .replace(R.id.login_container, loginFragment)
                .commit()
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

    //This function replaces the register fragment back with the login fragment
    override fun onClickGoBackToLogin() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.login_container, loginFragment)
            .commit()
    }

    /**
     * This function replaces the login fragment with the register fragment
     */
    override fun onclickRegister() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.login_container, registerFragment)
            .commit()
    }

//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        getSupportFragmentManager().putFragment(outState, "registerFragment", registerFragment);
//    }
}

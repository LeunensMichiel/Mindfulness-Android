package com.hogent.mindfulness.login

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.hogent.mindfulness.R

/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity(), LoginFragment.LoginFragmentCallBack {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginFragment = LoginFragment()

        supportFragmentManager.beginTransaction()
            .add(R.id.login_container, loginFragment)
            .commit()
    }

    /**
     * This function replaces the login fragment with the register fragment
     */
    override fun onclickRegister() {

        supportFragmentManager.beginTransaction()
            .replace(R.id.login_container, RegisterFragment())
            .commit()
    }
}

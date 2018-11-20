package com.hogent.mindfulness.login

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.hogent.mindfulness.R

/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity(), LoginFragment.LoginFragmentCallBack, RegisterFragment.LoginFragmentCallBack {

    lateinit var loginFragment : LoginFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginFragment = LoginFragment()

        supportFragmentManager.beginTransaction()
            .add(R.id.login_container, loginFragment)
            .commit()
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

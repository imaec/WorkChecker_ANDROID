package com.imaec.workchecker.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle

import com.imaec.workchecker.R
import android.app.Activity
import android.view.View
import kotlinx.android.synthetic.main.dialog_login.*


/**
 * Created by imaec on 2018-06-23.
 */
class LoginDialog(context: Context, private val activity: Activity): Dialog(context) {

    private lateinit var joinListener: View.OnClickListener
    private lateinit var loginListener: View.OnClickListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_login)

        textJoin.setOnClickListener(joinListener)
        textLogin.setOnClickListener(loginListener)
    }

    override fun onBackPressed() {
        activity.finish()
    }

    fun setJoinListener(listener: View.OnClickListener) {
        joinListener = listener
    }

    fun setLoginListener(listener: View.OnClickListener) {
        loginListener = listener
    }

    fun getEmail(): String {
        return editEmail.text.toString()
    }

    fun getPassword(): String {
        return editPassword.text.toString()
    }
}
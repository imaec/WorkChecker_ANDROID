package com.imaec.workchecker.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.View
import android.widget.Toast
import com.imaec.workchecker.Preferences
import com.imaec.workchecker.R
import com.imaec.workchecker.WorkCheckerService
import com.imaec.workchecker.dialog.LoginDialog
import com.imaec.workchecker.fragment.HomeFragment
import com.imaec.workchecker.fragment.MyFragment
import com.imaec.workchecker.fragment.TodayFragment
import com.imaec.workchecker.model.LoginResult
import com.imaec.workchecker.model.UserInfo
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    private val fragmentManager: FragmentManager = supportFragmentManager
    private var fragment: Fragment? = null
    private lateinit var workCheckerService: WorkCheckerService
    private var userInfo: UserInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Preferences.remove(this@MainActivity, "LOGIN")
        Preferences.remove(this@MainActivity, "USER_INFO")
        if (Preferences.get(this, "LOGIN", false)) { // 로그인 상태
            val userInfoString = Preferences.get(this, "USER_INFO", "")
            val userInfoStrings = userInfoString.split(" / ")

            userInfo = UserInfo()
            userInfo!!._id = userInfoStrings[0]
            userInfo!!.profile= userInfoStrings[1]
            userInfo!!.name = userInfoStrings[2]
            userInfo!!.department = userInfoStrings[3]
            userInfo!!.rank = userInfoStrings[4]
            userInfo!!.email = userInfoStrings[5]
        } else { // 로그아웃 상태
            val loginDialog = LoginDialog(this, this)
            loginDialog.setCanceledOnTouchOutside(false)
            loginDialog.setJoinListener(View.OnClickListener {
                // 회원가입 화면으로 이동
                startActivityForResult(Intent(this@MainActivity, JoinActivity::class.java), 0)
            })
            loginDialog.setLoginListener(View.OnClickListener {
                // 로그인
                login(loginDialog.getEmail(), loginDialog.getPassword(), loginDialog)
            })
            loginDialog.show()
        }

        init()

        bottomNavigationMain.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.bottom_home -> {
                    fragment = HomeFragment()
                    if (userInfo != null) {
                        (fragment as HomeFragment).setUserInfo(userInfo!!)
                    }
                    val transaction = fragmentManager.beginTransaction()
                    transaction.replace(R.id.frame_container, fragment).commit()
                    true
                }
                R.id.bottom_today -> {
                    fragment = TodayFragment()
                    val transaction = fragmentManager.beginTransaction()
                    transaction.replace(R.id.frame_container, fragment).commit()
                    true
                }
                R.id.bottom_my -> {
                    fragment = MyFragment()
                    val transaction = fragmentManager.beginTransaction()
                    transaction.replace(R.id.frame_container, fragment).commit()
                    true
                }
                else -> false
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 100) { // 회원가입 성공
            Toast.makeText(this, "회원가입에 성공했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun init() {
        fragment = HomeFragment()
        if (userInfo != null) {
            (fragment as HomeFragment).setUserInfo(userInfo!!)
        }
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.frame_container, fragment).commit()

        workCheckerService = WorkCheckerService.retrofit.create(WorkCheckerService::class.java)
    }

    private fun login(email: String, password: String, dialog: LoginDialog) {
        val callUserList = workCheckerService.callUser(email, password)
        callUserList.clone().enqueue(object : Callback<LoginResult> {
            override fun onResponse(call: Call<LoginResult>?, response: Response<LoginResult>?) {
                val loginResult = response!!.body()

                if (loginResult != null) {
                    if (loginResult.msg == "success") {

                        userInfo = loginResult.result[0]
                        val userInfoString = userInfo!!._id + " / " +
                                userInfo!!.profile + " / " +
                                userInfo!!.name + " / " +
                                userInfo!!.department + " / " +
                                userInfo!!.rank + " / " +
                                userInfo!!.email

                        (fragmentManager.fragments[0] as HomeFragment).setUserInfo(userInfo!!)

                        Preferences.set(this@MainActivity, "LOGIN", true)
                        Preferences.set(this@MainActivity, "USER_INFO", userInfoString)
                        dialog.dismiss()
                    } else {
                        Toast.makeText(this@MainActivity, "이메일 또는 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@MainActivity, "잠시 후 다시 시도 해주세요.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResult>?, t: Throwable?) {

            }
        })
        // dialog.dismiss()
    }
}

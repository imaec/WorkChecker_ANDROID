package com.imaec.workchecker.activity

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.imaec.workchecker.R
import com.imaec.workchecker.WorkCheckerService
import kotlinx.android.synthetic.main.activity_join.*
import okhttp3.MediaType
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import okhttp3.RequestBody
import java.text.SimpleDateFormat
import java.util.*
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import android.content.Intent
import android.provider.MediaStore
import android.graphics.Bitmap
import android.R.attr.data
import android.media.MediaScannerConnection
import android.os.Environment
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.imaec.workchecker.Preferences
import com.imaec.workchecker.model.JoinResult
import com.imaec.workchecker.model.UserInfo
import kotlinx.android.synthetic.main.item_main.view.*
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


/**
 * Created by imaec on 2018-06-23.
 */
class JoinActivity: AppCompatActivity() {

    val PICK_IMAGE = 100
    var image_path = ""
    private lateinit var workCheckerService: WorkCheckerService
    private var userInfo: UserInfo? = null

    private var permissionlistener: PermissionListener = object : PermissionListener {
        override fun onPermissionGranted() {
            val galleryIntent = Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

            startActivityForResult(galleryIntent, PICK_IMAGE)
        }

        override fun onPermissionDenied(deniedPermissions: ArrayList<String>) {
            Toast.makeText(this@JoinActivity, "권한을 확인해주세요." + deniedPermissions.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)

        init()

        imageProfile.setOnClickListener {
            TedPermission.with(this)
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage("권한을 허용하지 않으면 앱을 이용할 수 없습니다.\n\n[설정] > [권한]에서 권한을 허용해주세요.")
                    .setPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
                    .check()
        }

        textJoin.setOnClickListener {
            if (checkEdit()) {
                join()
            } else {
                Toast.makeText(this@JoinActivity, "모든 항목을 정확히 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == PICK_IMAGE) {
            if (data != null) {
                val contentURI = data.data
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                    image_path = saveImage(bitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this@JoinActivity, "Failed!", Toast.LENGTH_SHORT).show()
                }

            }
        }
    }

    private fun init() {
        workCheckerService = WorkCheckerService.retrofit.create(WorkCheckerService::class.java)
    }

    private fun saveImage(bitmap: Bitmap): String {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes)

        val multi = MultiTransformation(CenterCrop(), RoundedCorners(300))
        Glide.with(this)
                .asBitmap()
                .load(bytes.toByteArray())
                .apply(RequestOptions.bitmapTransform(multi))
                .into(imageProfile)

        val dir = File(Environment.getExternalStorageDirectory().toString() + "/WCTemp/")
        if (!dir.exists()) {
            dir.mkdir()
        }

        try {
            val file = File(dir, Calendar.getInstance().timeInMillis.toString() + ".png")
            file.createNewFile()
            val fo = FileOutputStream(file)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(this,
                    arrayOf(file.path),
                    arrayOf("image/jpeg"),
                    null)
            fo.close()

            return file.absolutePath
        } catch (exception: IOException) {
            exception.printStackTrace()
        }

        return ""
    }

    private fun checkEdit(): Boolean {
        if (editName.text.toString() == "") return false
        if (editDep.text.toString() == "") return false
        if (editRank.text.toString() == "") return false
        if (editEmail.text.toString() == "") return false
        if (editPassword.text.toString() == "" && editPassword.text.toString().length < 4) return false
        if (editTel.text.toString() == "") return false
        return true
    }

    private fun join() {
        val imageFile = File(image_path)
        if (image_path == "") {
            Toast.makeText(this, "프로필사진을 등록해 주세요.", Toast.LENGTH_SHORT).show()
            return
        }
        val name = RequestBody.create(MediaType.parse("text/plain"), editName.text.toString())
        val rank = RequestBody.create(MediaType.parse("text/plain"), editRank.text.toString())
        val department = RequestBody.create(MediaType.parse("text/plain"), editDep.text.toString())
        val email = RequestBody.create(MediaType.parse("text/plain"), editEmail.text.toString())
        val password = RequestBody.create(MediaType.parse("text/plain"), editPassword.text.toString())
        val reg_date = RequestBody.create(MediaType.parse("text/plain"), curDate())
        val tel = RequestBody.create(MediaType.parse("text/plain"), editTel.text.toString())
        val imageBody = RequestBody.create(MediaType.parse("image/*"), imageFile)
        val image = MultipartBody.Part.createFormData("image", imageFile.name, imageBody)

        linearProgress.visibility = View.VISIBLE
        val callAddUser = workCheckerService.callAddUser(name, rank, department, email, password, reg_date, tel, image)
        callAddUser.clone().enqueue(object : Callback<JoinResult> {
            override fun onResponse(call: Call<JoinResult>?, response: Response<JoinResult>?) {
                linearProgress.visibility = View.GONE
                val joinResult = response!!.body()

                if (joinResult != null) {
                    if (joinResult.msg == "success") {
                        setResult(100)
                        finish()
                    }
                }
            }

            override fun onFailure(call: Call<JoinResult>?, t: Throwable?) {

            }

        })
    }

    @SuppressLint("SimpleDateFormat")
    private fun curDate(): String {
        val c = Calendar.getInstance().time
        val df = SimpleDateFormat("yyyyMMdd")
        return df.format(c)
    }
}
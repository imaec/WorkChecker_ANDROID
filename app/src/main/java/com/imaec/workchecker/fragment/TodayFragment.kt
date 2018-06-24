package com.imaec.workchecker.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.imaec.workchecker.Preferences
import com.imaec.workchecker.dialog.DatePickerDialog
import com.imaec.workchecker.R
import com.imaec.workchecker.WorkCheckerService
import com.imaec.workchecker.model.UserInfo
import com.imaec.workchecker.model.UserResult
import com.imaec.workchecker.model.Work
import com.imaec.workchecker.model.WorkResult
import kotlinx.android.synthetic.main.fragment_today.*
import kotlinx.android.synthetic.main.item_today.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by imaec on 2018-05-31.
 */
class TodayFragment: Fragment() {

    private lateinit var userInfo: UserInfo
    private val gridLayoutManager: GridLayoutManager = GridLayoutManager(context, 2)
    private val adapter: TodayAdapter = TodayAdapter()
    private var listUserInfo = ArrayList<UserInfo>()
    private var listWork = ArrayList<Work>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_today, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        getData("")

        linearDate.setOnClickListener {
            // 날짜 선택
            val datePicker = DatePickerDialog(context!!)
            datePicker.setTextView(textDate)
            datePicker.show()
        }

        textDate.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                getData(p0.toString().replace(" ", ""))
            }
        })
    }

    private fun init() {
        val userInfoString = Preferences.get(context!!, "USER_INFO", "")
        val userInfoStrings = userInfoString.split(" / ")

        userInfo = UserInfo()
        userInfo._id = userInfoStrings[0]
        userInfo.profile= userInfoStrings[1]
        userInfo.name = userInfoStrings[2]
        userInfo.department = userInfoStrings[3]
        userInfo.rank = userInfoStrings[4]
        userInfo.email = userInfoStrings[5]

        textDate.text = curDate("yyyy-MM-dd") + " (" + getDayOfWeek(curDate("yyyyMMdd")) + ")"
        recyclerToday.adapter = adapter
        recyclerToday.layoutManager = gridLayoutManager
    }

    private fun getData(date: String) {
        val workCheckerService = WorkCheckerService.retrofit.create(WorkCheckerService::class.java)
        val callUserList = workCheckerService.callUserList()
        callUserList.clone().enqueue(object : Callback<UserResult> {
            override fun onResponse(call: Call<UserResult>?, response: Response<UserResult>?) {
                val userResult = response!!.body()

                if (userResult != null) {
                    if (userResult.msg == "success") {
                        listUserInfo = userResult.result
                        adapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onFailure(call: Call<UserResult>?, t: Throwable?) {

            }
        })
        var today = date
        if (date == "") {
            today = curDate("yyyy-MM-dd") + "(" + getDayOfWeek(curDate("yyyyMMdd")) + ")"
        }
        listWork.clear()
        val callWorkToday = workCheckerService.callWorkToday("", today)
        callWorkToday.clone().enqueue(object : Callback<WorkResult> {
            override fun onResponse(call: Call<WorkResult>?, response: Response<WorkResult>?) {
                val workResult = response!!.body()

                if (workResult != null) {
                    if (workResult.msg == "success") {
                        listWork = workResult.result
                        adapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onFailure(call: Call<WorkResult>?, t: Throwable?) {

            }
        })
    }

    inner class TodayAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun getItemCount(): Int = listUserInfo.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_today, parent, false)
            return ItemViewHolder(v)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val user = listUserInfo[position]
            val itemViewHolder = holder as ItemViewHolder

            val multi = MultiTransformation(CenterCrop(), RoundedCorners(300))
            Glide.with(context)
                    .load(user.profile)
                    .apply(RequestOptions.bitmapTransform(multi))
                    .into(itemViewHolder.itemView.imageProfile)
            itemViewHolder.itemView.textName.text = user.name
            itemViewHolder.itemView.textDepartment.text = user.department + " / " + user.rank

            itemViewHolder.itemView.viewStatus.background = resources.getDrawable(R.drawable.bg_status_off)
            for (work in listWork) {
                if (user._id == work.user_id) {
                    if (work.time_a != "") {
                        itemViewHolder.itemView.viewStatus.background = resources.getDrawable(R.drawable.bg_status_on)
                        break
                    }
                }
            }
        }

        inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    }

    @SuppressLint("SimpleDateFormat")
    private fun curDate(format: String): String {
        val c = Calendar.getInstance().time
        val df = SimpleDateFormat(format)
        return df.format(c)
    }

    @SuppressLint("SimpleDateFormat")
    private fun getDayOfWeek(curDate: String): String {
        val formatter = SimpleDateFormat("yyyyMMdd")

        var date = formatter.parse(curDate)
        date = Date(date.time + 1000 * 60 * 60 * 24)

        val cal = Calendar.getInstance()
        cal.time = date

        val dayNum = cal.get(Calendar.DAY_OF_WEEK)   // 요일을 구해온다.
        when (dayNum) {
            1 -> return "토"
            2 -> return "일"
            3 -> return "월"
            4 -> return "화"
            5 -> return "수"
            6 -> return "목"
            7 -> return "금"
        }
        return ""
    }
}
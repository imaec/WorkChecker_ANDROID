package com.imaec.workchecker.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.os.Vibrator
import android.support.v7.widget.helper.ItemTouchHelper
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.imaec.workchecker.ItemTouchHelperCallback
import com.imaec.workchecker.ItemTouchHelperListener
import com.imaec.workchecker.R
import com.imaec.workchecker.WorkCheckerService
import com.imaec.workchecker.model.UserInfo
import com.imaec.workchecker.model.Work
import com.imaec.workchecker.model.WorkResult
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.item_main.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by imaec on 2018-05-24.
 */
class HomeFragment: Fragment() {

    private val linearLayoutManager: LinearLayoutManager = LinearLayoutManager(context)
    private val adapter: HomeAdapter = HomeAdapter()
    private var vibrator: Vibrator? = null
    private var itemTouchHelper: ItemTouchHelper? = null
    private var userInfo: UserInfo? = null
    private var work: Work? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        if (userInfo != null) getData()
    }

    private fun init() {
        vibrator = context!!.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback(context, adapter))

        recyclerHome.adapter = adapter
        recyclerHome.layoutManager = linearLayoutManager
        itemTouchHelper!!.attachToRecyclerView(recyclerHome)
    }

    private fun getData() {
        val workCheckerService = WorkCheckerService.retrofit.create(WorkCheckerService::class.java)
        val callWorkToday = workCheckerService.callWorkToday(userInfo!!._id, curDate("yyyy-MM-dd") + "(" + getDayOfWeek(curDate("yyyyMMdd")) + ")")
        callWorkToday.clone().enqueue(object : Callback<WorkResult> {
            override fun onResponse(call: Call<WorkResult>?, response: Response<WorkResult>?) {
                val workResult = response!!.body()

                if (workResult != null) {
                    if (workResult.msg == "success") {
                        work = workResult.result[0]
                        if (work!!.time_b == "") {
                            textStatus.text = "출근"
                        } else {
                            textStatus.text = "퇴근"
                        }
                    } else {
                        textStatus.text = "출근 전"
                    }
                }
            }

            override fun onFailure(call: Call<WorkResult>?, t: Throwable?) {

            }
        })
    }

    fun setUserInfo(userInfo: UserInfo) {
        this.userInfo = userInfo

        adapter.notifyDataSetChanged()
    }

    inner class HomeAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>(), ItemTouchHelperListener {

        override fun getItemCount(): Int = 1

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_main, parent, false)
            return ItemViewHolder(v)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val itemViewHolder = holder as ItemViewHolder

            if (userInfo != null) {
                val multi = MultiTransformation(CenterCrop(), RoundedCorners(300))
                Glide.with(context)
                        .load(userInfo!!.profile)
                        .apply(RequestOptions.bitmapTransform(multi))
                        .into(itemViewHolder.itemView.imageProfile)
                itemViewHolder.itemView.textName.text = userInfo!!.name
                itemViewHolder.itemView.textDepartment.text = userInfo!!.department + " / " + userInfo!!.rank
                itemViewHolder.itemView.textEmail.text = userInfo!!.email
            }
        }

        override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
            return false
        }

        override fun onItemSwipe(dX: Float) {
            textSwipeStatus.text = if (dX > 0) "출근" else "퇴근"
        }

        // 퇴근
        override fun onItemLeftSwipe(position: Int) {
            notifyDataSetChanged()
            val status = textStatus.text.toString()
            when (status) {
                "출근 전" -> Toast.makeText(context, "출근 먼저 해주세요^^", Toast.LENGTH_SHORT).show()
                "퇴근" -> Toast.makeText(context, "이미 퇴근 처리 되었습니다.", Toast.LENGTH_SHORT).show()
                else -> {
                    updateData()
                }
            }
        }

        // 출근
        override fun onItemRightSwipe(position: Int) {
            notifyDataSetChanged()
            val status = textStatus.text.toString()
            when (status) {
                "출근" -> Toast.makeText(context, "이미 출근 처리 되었습니다.", Toast.LENGTH_SHORT).show()
                "퇴근" -> Toast.makeText(context, "내일 다시 출근하세요^^", Toast.LENGTH_SHORT).show()
                else -> {
                    sendData()
                }
            }
        }

        inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    }

    private fun sendData() {
        val workCheckerService = WorkCheckerService.retrofit.create(WorkCheckerService::class.java)
        val callAddWork = workCheckerService.callAddWork(userInfo!!._id,
                "출근",
                curDate("yyyy-MM-dd") + "(" + getDayOfWeek(curDate("yyyyMMdd")) + ")",
                curDate("HH:mm:ss"),
                "")
        callAddWork.clone().enqueue(object : Callback<WorkResult> {
            override fun onResponse(call: Call<WorkResult>?, response: Response<WorkResult>?) {
                val workResult = response!!.body()

                if (workResult != null) {
                    if (workResult.msg == "success") {
                        vibrator!!.vibrate(250)
                        textStatus.text = "출근"
                        Toast.makeText(context, "출근 처리 되었습니다!", Toast.LENGTH_SHORT).show()

                        getData()
                    }
                }
            }

            override fun onFailure(call: Call<WorkResult>?, t: Throwable?) {

            }
        })
    }

    private fun updateData() {
//        val time_b = curDate("HH:mm:ss")
//        if (time_b.split(":")[0].toInt() < 18) {
//            // 퇴근시간 전 퇴근 체크
//        }
        val workCheckerService = WorkCheckerService.retrofit.create(WorkCheckerService::class.java)
        val callEditWork = workCheckerService.callEditWork(work!!._id, curDate("HH:mm:ss"))
        callEditWork.clone().enqueue(object : Callback<WorkResult> {
            override fun onResponse(call: Call<WorkResult>?, response: Response<WorkResult>?) {
                val workResult = response!!.body()

                if (workResult != null) {
                    if (workResult.msg == "success") {
                        val pattern = longArrayOf(0, 250, 250, 250)
                        vibrator!!.vibrate(pattern, -1)
                        textStatus.text = "퇴근"
                        Toast.makeText(context, "퇴근 처리 되었습니다!", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<WorkResult>?, t: Throwable?) {

            }
        })
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
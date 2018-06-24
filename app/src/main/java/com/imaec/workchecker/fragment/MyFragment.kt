package com.imaec.workchecker.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.imaec.workchecker.Preferences
import com.imaec.workchecker.R
import com.imaec.workchecker.WorkCheckerService
import com.imaec.workchecker.model.UserInfo
import com.imaec.workchecker.model.Work
import com.imaec.workchecker.model.WorkResult
import kotlinx.android.synthetic.main.fragment_my.*
import kotlinx.android.synthetic.main.item_my.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by imaec on 2018-06-02.
 */
class MyFragment: Fragment() {

    private lateinit var userInfo: UserInfo
    private val linearLayoutManager: LinearLayoutManager = LinearLayoutManager(context)
    private val adapter: MyAdapter = MyAdapter()
    private var listWork = ArrayList<Work>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_my, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        getData()
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

        recyclerMy.adapter = adapter
        recyclerMy.layoutManager = linearLayoutManager
    }

    private fun getData() {
        val workCheckerService = WorkCheckerService.retrofit.create(WorkCheckerService::class.java)
        val callWorkList = workCheckerService.callWorkList(userInfo._id)
        callWorkList.clone().enqueue(object : Callback<WorkResult> {
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

    inner class MyAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun getItemCount(): Int = listWork.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_my, parent, false)
            return ItemViewHolder(v)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val work = listWork[position]
            val itemViewHolder = holder as ItemViewHolder

            itemViewHolder.itemView.textDate.text = work.date
            if (work.time_a.split(":")[0].toInt() > 8) {
                itemViewHolder.itemView.textStatus.text = work.status + " (지각 " + work.time_a + ")"
            } else {
                itemViewHolder.itemView.textStatus.text = work.status
            }
        }

        inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    }
}
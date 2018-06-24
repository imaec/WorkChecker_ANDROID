package com.imaec.workchecker.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.imaec.workchecker.R
import kotlinx.android.synthetic.main.dialog_datepicker.*
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by imaec on 2018-06-02.
 */
class DatePickerDialog(context: Context) : Dialog(context) {

//    lateinit var positiveListener: View.OnClickListener
//    lateinit var negativeListener: View.OnClickListener
    lateinit var textDate: TextView
    var selectDate = ""

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_datepicker)

        textPositive.setOnClickListener {
            val curTimeMillis = Date(System.currentTimeMillis())
            val curDate = SimpleDateFormat("yyyyMMdd").format(curTimeMillis)

            val year = datePicker.year.toString()
            val month = if ((datePicker.month + 1).toString().length == 1) "0" + (datePicker.month + 1).toString() else (datePicker.month + 1).toString()
            val day = if (datePicker.dayOfMonth.toString().length == 1) "0" + datePicker.dayOfMonth.toString() else datePicker.dayOfMonth.toString()

            if (Integer.parseInt(curDate ) < Integer.parseInt("$year$month$day")) {
                Toast.makeText(context, "우리에게 내일은 없어!", Toast.LENGTH_SHORT).show()
            } else {
                val dayOfWeek = getDayOfWeek("$year$month$day")
                selectDate = "$year-$month-$day ($dayOfWeek)"

                Log.d("selectDate :::: ", selectDate)
                textDate.text = selectDate
            }
            dismiss()
        }
        textNegative.setOnClickListener {
            dismiss()
        }
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

    fun setTextView(textView: TextView) {
        textDate = textView
    }

//    fun setOnPositiveListener(listener: View.OnClickListener) {
//        positiveListener = listener
//    }
//
//    fun setOnNegativieListener(listener: View.OnClickListener) {
//        negativeListener = listener
//    }
}
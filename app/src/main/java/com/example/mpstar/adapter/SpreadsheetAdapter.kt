package com.example.mpstar.adapter

import android.content.Context
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mpstar.MainActivity
import com.example.mpstar.R
import com.example.mpstar.model.Student

class SpreadsheetAdapter(val items: MutableList<Student>){

    fun getItemCount(): Int = items.size

    fun showAdaptor(ctx: Context, showClassPlan: ()-> (Unit)){
        showClassPlan()
    }

}
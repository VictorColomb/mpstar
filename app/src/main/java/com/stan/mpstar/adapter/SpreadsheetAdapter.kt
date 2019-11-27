package com.stan.mpstar.adapter

import android.content.Context
import com.stan.mpstar.model.Student

class SpreadsheetAdapter(val items: MutableList<Student>){

    fun showAdaptor(ctx: Context, showClassPlan: ()-> (Unit)){
        showClassPlan()
    }

}
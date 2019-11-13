package com.example.mpstar.ui.plan_de_classe

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.mpstar.MainActivity
import com.example.mpstar.NetworkInformation

import com.example.mpstar.R
import java.lang.Exception

class plan_de_classeFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInsatnceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_plan_de_classe, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_plan_de_classe, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_orientation -> {
                val orientation = resources.configuration.orientation
                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    activity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                } else {
                    activity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                }
                return true
            }
            R.id.action_refresh -> {
                if (NetworkInformation.isNetworkAvailable(context)) {
                    val mainActivity: MainActivity = this.activity as MainActivity
                    try {
                        mainActivity.readSpreadsheetActivity.init(mainActivity.getThis())
                    } catch (ex :Exception) {
                        Toast.makeText(context, "Refresh failed", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Pas de connection à internet...", Toast.LENGTH_SHORT).show()
                }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }


    override fun onResume() {
        super.onResume()
        Log.i("DEBUG", "HEEEEEEEEEEEEEEEEEEEERE RESUME")
        val mainActivity: MainActivity = this.activity as MainActivity
        mainActivity.resumePlan()
    }
}

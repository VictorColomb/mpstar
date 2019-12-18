package com.stan.mpstar.ui.plan_de_classe

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.stan.mpstar.MainActivity
import com.stan.mpstar.NetworkInformation
import com.stan.mpstar.R

class PlanDeClasseFragment : Fragment() {

    //<editor-fold desc="onCreateView and onResume">
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInsatnceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_plan_de_classe, container, false)
    }

    override fun onResume() {
        super.onResume()
        val mainActivity: MainActivity = this.activity as MainActivity
        mainActivity.resumePlan()
    }
    //</editor-fold>


    //<editor-fold desc="Menu items">
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_plan_de_classe, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val mainActivity: MainActivity = this.activity as MainActivity
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
                    try {
                        mainActivity.refreshAll()
                    } catch (ex :Exception) {
                        Toast.makeText(context, "Refresh failed", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Pas de connection à internet...", Toast.LENGTH_SHORT).show()
                }
                return true
            }
            R.id.action_share -> {
                mainActivity.sharePDC()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
    //</editor-fold>
}

package com.example.mpstar.ui.emploi_du_temps

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mpstar.MainActivity

import com.example.mpstar.R

class EmploiDuTempsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInsatnceState: Bundle?): View? {
        activity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

        Log.i("DEBUG", "HEEEEEEEEEEEEEEEEEEEERE")

        return inflater.inflate(R.layout.fragment_emploi_du_temps, container, false)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        Log.i("DEBUG", "HEEEEEEEEEEEEEEEEEEEERE")
        super.onHiddenChanged(hidden)
        if (!hidden and isResumed){
            val mainActivity: MainActivity = this.activity as MainActivity
            mainActivity.resumePlan()
        }
    }
}

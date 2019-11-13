package com.jack.royer.kotlintest2.ui.read

import android.app.LauncherActivity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat.startActivityForResult
import com.example.mpstar.adapter.SpreadsheetAdapter
import com.example.mpstar.model.Personal
import com.example.mpstar.model.Student
import com.example.mpstar.sheets.AuthenticationManager
import com.example.mpstar.sheets.SheetsAPIDataSource
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.sheets.v4.SheetsScopes
import java.util.*


class ReadSpreadsheetActivity (
        private val launchAuthentication: (client: GoogleSignInClient) -> (Unit),
        private val showClassPlan: ()-> (Unit),
        val matchPersonal: (personals: MutableList<Personal>)-> (Unit)) {

    lateinit var presenter : ReadSpreadsheetPresenter

    private lateinit var spreadsheetAdapter: SpreadsheetAdapter

    fun init(context: Context) {
        initDependencies(context)
        presenter.init(launchAuthentication)
    }

    private fun initDependencies(context: Context) {
        val signInOptions : GoogleSignInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //.requestScopes(Scope(SheetsScopes.SPREADSHEETS_READONLY))
                //.requestScopes(Scope(SheetsScopes.SPREADSHEETS))
                //.requestScopes(Drive.SCOPE_FILE)
                .requestEmail()
                .build()
        val googleSignInClient = GoogleSignIn.getClient(context, signInOptions)
        val googleAccountCredential = GoogleAccountCredential
            .usingOAuth2(context, Arrays.asList(*AuthenticationManager.SCOPES))
            .setBackOff(ExponentialBackOff())
        val authManager =
                AuthenticationManager(
                        lazyOf(context),
                        googleSignInClient,
                        googleAccountCredential)
        val sheetsApiDataSource =
                SheetsAPIDataSource(authManager,
                        AndroidHttp.newCompatibleTransport(),
                        JacksonFactory.getDefaultInstance())
        presenter = ReadSpreadsheetPresenter(this, authManager, sheetsApiDataSource)

    }

    // View related implementations
    fun showError(context: Context, error: String) {
        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
    }

    fun initList(students: MutableList<Student>) {
        spreadsheetAdapter = SpreadsheetAdapter(students)
    }

    fun showPeople(context: Context) {
        spreadsheetAdapter.showAdaptor(context, showClassPlan)
    }

    companion object {
        const val TAG = "ReadSpreadsheetActivity"
        const val RQ_GOOGLE_SIGN_IN = 999
    }

}
package com.jack.royer.kotlintest2.ui.read

import android.content.Context
import android.util.Log
import com.example.mpstar.model.Personal
import com.example.mpstar.model.Student
import com.example.mpstar.sheets.AuthenticationManager
import com.example.mpstar.sheets.SheetsAPIDataSource
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

class ReadSpreadsheetPresenter(private val view: ReadSpreadsheetActivity,
                               private val authenticationManager: AuthenticationManager,
                               private val sheetsAPIDataSource: SheetsAPIDataSource) {

    private lateinit var readSpreadsheetDisposable : Disposable
    var students : MutableList<Student> = mutableListOf()
    var personal : MutableList<Personal> = mutableListOf()

    fun init(launchAuthentication: (client : GoogleSignInClient) -> (Unit)) {
        launchAuthentication(authenticationManager.googleSignInClient)
        view.initList(students)
    }

    fun dispose() {
        readSpreadsheetDisposable.dispose()
    }

    fun loginSuccessful(context: Context) {
        Log.i("kotlin test", "login was successful")
        Log.i("kotlin test", "setting up google account credentials")
        authenticationManager.setUpGoogleAccountCredential()
        startReadingSpreadsheetStudents(context)
    }

    private fun startReadingSpreadsheetStudents(context: Context){
        students.clear()
        readSpreadsheetDisposable=
            sheetsAPIDataSource.readSpreadSheet(spreadsheetId, rangeStudents)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError { view.showError(context, it.message!!) }
                .subscribe(Consumer {
                    students.addAll(it)
                    view.showPeople(context)
                })
    }

    private fun startReadingSpreadsheetPersonal(context: Context){
        personal.clear()
        readSpreadsheetDisposable=
                sheetsAPIDataSource.readSpreadSheetPersonal(spreadsheetId, rangePersonal)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnError { view.showError(context, it.message!!) }
                        .subscribe(Consumer {
                            personal.addAll(it)
                            view.matchPersonal(personal)
                        })
    }

    companion object {
        val spreadsheetId = "1VXDSYl2X5oXNXKeYbNrBH8b1zR_nIzqHbRhZaopWgCw"
        val rangeStudents = "Sheet1!A5:F"
        val rangePersonal = "Personal!A2:F"
    }
}
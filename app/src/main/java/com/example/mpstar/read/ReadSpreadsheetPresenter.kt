package com.jack.royer.kotlintest2.ui.read

import com.example.mpstar.MainActivity
import com.example.mpstar.model.Personal
import com.example.mpstar.model.Student
import com.example.mpstar.sheets.AuthenticationManager
import com.example.mpstar.sheets.SheetsAPIDataSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

class ReadSpreadsheetPresenter( private val view: MainActivity,
                                private val authenticationManager: AuthenticationManager,
                                private val sheetsAPIDataSource: SheetsAPIDataSource) {

    private lateinit var readSpreadsheetDisposable : Disposable
    var students : MutableList<Student> = mutableListOf()
    var personal : MutableList<Personal> = mutableListOf()


    fun loginSuccessful() {
        authenticationManager.setUpGoogleAccountCredential()
        startReadingSpreadsheetStudents()
    }

    fun startLogin(){
        view.launchAuthentication(authenticationManager.googleSignInClient)
    }

    private fun startReadingSpreadsheetStudents(){
        students.clear()
        readSpreadsheetDisposable=
            sheetsAPIDataSource.readSpreadSheet(spreadsheetId, rangeStudents)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError { view.showError(it.message!!) }
                .subscribe(Consumer {
                    students.addAll(it)
                    view.finishedReadingStudents()
                })
    }

    private fun startReadingSpreadsheetPersonal(){
        personal.clear()
        readSpreadsheetDisposable=
                sheetsAPIDataSource.readSpreadSheetPersonal(spreadsheetId, rangePersonal)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnError { view.showError(it.message!!) }
                        .subscribe(Consumer {
                            personal.addAll(it)
                            view.finishedReadingPersonal()
                        })
    }

    companion object {
        const val spreadsheetId = "1VXDSYl2X5oXNXKeYbNrBH8b1zR_nIzqHbRhZaopWgCw"
        const val rangeStudents = "Sheet1!A5:F"
        const val rangePersonal = "Personal!A2:G"
    }
}
package com.jack.royer.kotlintest2.ui.read

import android.util.Log
import com.example.mpstar.MainActivity
import com.example.mpstar.model.Colleurs
import com.example.mpstar.model.DS
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
    var ds : MutableList<DS> = mutableListOf()
    var colleurs : MutableList<Colleurs> = mutableListOf()


    fun loginSuccessful() {
        authenticationManager.setUpGoogleAccountCredential()
    }

    fun startLogin(requestCode: Int){
        view.launchAuthentication(authenticationManager.googleSignInClient, requestCode)
    }

    fun startReadingSpreadsheetStudents(){
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

    fun startReadingSpreadsheetPersonal(){
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

    fun startReadingSpreadsheetDS(){
        ds.clear()
        readSpreadsheetDisposable=
                sheetsAPIDataSource.readSpreadSheetDS(spreadsheetId, rangeDS)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnError { view.showError(it.message!!) }
                        .subscribe(Consumer {
                            ds.addAll(it)
                            view.finishedReadingDS()
                        })
    }

    fun startReadingSpreadsheetColleurs(){
        colleurs.clear()
        readSpreadsheetDisposable=
                sheetsAPIDataSource.readSpreadSheetColleurs(spreadsheetId, rangeColleurs)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnError { view.showError(it.message!!) }
                        .subscribe(Consumer {
                            colleurs.addAll(it)
                            view.finishedReadingColleurs()
                        })
    }



    companion object {
        const val spreadsheetId = "1VXDSYl2X5oXNXKeYbNrBH8b1zR_nIzqHbRhZaopWgCw"
        const val rangeStudents = "Sheet1!A5:F"
        const val rangePersonal = "Personal!A2:H"
        const val rangeDS = "DS!A2:E"
        const val rangeColleurs = "Colleurs!A2:F"
    }
}
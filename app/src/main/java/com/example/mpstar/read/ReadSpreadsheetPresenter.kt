package com.example.mpstar.read

import android.util.Log
import com.example.mpstar.MainActivity
import com.example.mpstar.model.*
import com.example.mpstar.sheets.AuthenticationManager
import com.example.mpstar.sheets.SheetsAPIDataSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers


class ReadSpreadsheetPresenter( private val view: MainActivity,
                                private val authenticationManager: AuthenticationManager,
                                private val sheetsAPIDataSource: SheetsAPIDataSource) {

    private lateinit var readSpreadsheetDisposable : Disposable
    var students : MutableList<Student> = mutableListOf()
    var personal : MutableList<Personal> = mutableListOf()

    fun setErrorHandler() {
        RxJavaPlugins.setErrorHandler {
            Log.i("PRESENTER", "Observable reported error while reading spreadsheet")
        }
    }

    fun loginSuccessful() {
        Log.i("PRESENTER", "Logged in successfully")
        view.signedIn = true
        authenticationManager.setUpGoogleAccountCredential()
    }

    fun startLogin(requestCode: Int){
        view.launchAuthentication(authenticationManager.googleSignInClient, requestCode)
    }

    fun startReadingSpreadsheetStudents(){
        setErrorHandler()
        students.clear()
        readSpreadsheetDisposable=
            sheetsAPIDataSource.readSpreadSheet(spreadsheetId, rangeStudents)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                    .doOnError {
                        view.refreshFailed(it) }
                .subscribe(Consumer {
                    students.addAll(it)
                    view.finishedReadingStudents()
                })
    }

    fun startReadingSpreadsheetPersonal(){
        setErrorHandler()
        personal.clear()
        readSpreadsheetDisposable=
                sheetsAPIDataSource.readSpreadSheetPersonal(spreadsheetId, rangePersonal)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(Consumer {
                            personal.addAll(it)
                            view.finishedReadingPersonal()
                        })
    }

    fun startReadingSpreadsheetDS(){
        setErrorHandler()
        val ds = mutableListOf<DS>()
        readSpreadsheetDisposable=
                sheetsAPIDataSource.readSpreadSheetDS(spreadsheetId, rangeDS)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(Consumer {
                            ds.addAll(it)
                            view.finishedReadingDS(ds)
                        })
    }

    fun startReadingSpreadsheetColleurs(){
        setErrorHandler()
        val colleurs = mutableListOf<Colleurs>()
        readSpreadsheetDisposable=
                sheetsAPIDataSource.readSpreadSheetColleurs(spreadsheetId, rangeColleurs)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(Consumer {
                            colleurs.addAll(it)
                            view.finishedReadingColleurs(colleurs)
                        })
    }

    fun startReadingSpreadsheetColleM(){
        setErrorHandler()
        val sheet : MutableList<List<Any>> = mutableListOf()
        readSpreadsheetDisposable=
                sheetsAPIDataSource.readSpreadSheetCM(spreadsheetId, rangeCM)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(Consumer {
                            sheet.addAll(it)
                            view.finishedReadingCollesM(sheet)
                        })
    }

    fun startReadingSpreadsheetColleA(){
        setErrorHandler()
        val sheet : MutableList<List<Any>> = mutableListOf()
        readSpreadsheetDisposable=
                sheetsAPIDataSource.readSpreadSheetCM(spreadsheetId, rangeCA)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(Consumer {
                            sheet.addAll(it)
                            view.finishedReadingCollesA(sheet)
                        })
    }

    fun startReadingSpreadsheetEDT(){
        setErrorHandler()
        val sheet : MutableList<List<Any>> = mutableListOf()
        readSpreadsheetDisposable=
                sheetsAPIDataSource.readSpreadSheetEDT(spreadsheetId, rangeEDT)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(Consumer {
                            sheet.addAll(it)
                            view.finishedReadingEDT(sheet)
                        })
    }



    companion object {
        const val spreadsheetId = "1VXDSYl2X5oXNXKeYbNrBH8b1zR_nIzqHbRhZaopWgCw"
        const val rangeStudents = "Sheet1!A5:F"
        const val rangePersonal = "Personal!A2:H"
        const val rangeDS = "DS!A2:E"
        const val rangeColleurs = "Colleurs!A2:F"
        const val rangeCM = "CollesMaths!A2:N"
        const val rangeCA = "CollesAutre!A2:N"
        const val rangeEDT = "EDT!B1:X"
    }
}
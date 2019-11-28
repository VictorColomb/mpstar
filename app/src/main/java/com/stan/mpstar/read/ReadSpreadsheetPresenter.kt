package com.stan.mpstar.read

import android.util.Log
import com.stan.mpstar.MainActivity
import com.stan.mpstar.model.*
import com.stan.mpstar.sheets.AuthenticationManager
import com.stan.mpstar.sheets.SheetsAPIDataSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*


class ReadSpreadsheetPresenter( private val view: MainActivity,
                                private val authenticationManager: AuthenticationManager,
                                private val sheetsAPIDataSource: SheetsAPIDataSource) {

    private lateinit var readSpreadsheetDisposable : Disposable
    var students : MutableList<Student> = mutableListOf()
    var planCreatedOn :Date? = null
    var personal : MutableList<Personal> = mutableListOf()
    private var customNotification: MutableList<Notif> = mutableListOf()

    private fun setErrorHandler() {RxJavaPlugins.setErrorHandler {}}

    fun loginSuccessful() {
        Log.i("PRESENTER", "Logged in successfully")
        view.signedIn = true
        authenticationManager.setUpGoogleAccountCredential()
        projectRed()
    }

    fun startLogin(requestCode: Int){
        view.launchAuthentication(authenticationManager.googleSignInClient, requestCode)
    }

    fun startReadingSpreadsheetStudents(){
        val df = SimpleDateFormat("MM/dd/yyyy", Locale.US)
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
        readSpreadsheetDisposable=
                sheetsAPIDataSource.readSpreadsheetDate(spreadsheetId, rangeCreatedDate)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            planCreatedOn = df.parse(it.toString())
                        }
    }

    private fun projectRed(){
        setErrorHandler()
        readSpreadsheetDisposable=
                sheetsAPIDataSource.readSpreadSheetCustom(spreadsheetId, rangeCreatedNotifs)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(Consumer {
                            customNotification.addAll(it)
                            view.finishedReadingRed(customNotification)
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
        const val rangePersonal = "Personal!A2:I"
        const val rangeDS = "DS!A2:E"
        const val rangeColleurs = "Colleurs!A2:F"
        const val rangeCM = "CollesMaths!A2:N"
        const val rangeCA = "CollesAutre!A2:N"
        const val rangeEDT = "EDT!B1:X"
        const val rangeCreatedDate = "Sheet1!I5"
        const val rangeCreatedNotifs = "Sheet1!R78:U"
    }
}
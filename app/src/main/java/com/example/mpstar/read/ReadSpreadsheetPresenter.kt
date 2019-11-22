package com.jack.royer.kotlintest2.ui.read

import android.util.Log
import com.example.mpstar.MainActivity
import com.example.mpstar.model.*
import com.example.mpstar.sheets.AuthenticationManager
import com.example.mpstar.sheets.SheetsAPIDataSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class ReadSpreadsheetPresenter( private val view: MainActivity,
                                private val authenticationManager: AuthenticationManager,
                                private val sheetsAPIDataSource: SheetsAPIDataSource) {

    private lateinit var readSpreadsheetDisposable : Disposable
    var students : MutableList<Student> = mutableListOf()
    var personal : MutableList<Personal> = mutableListOf()
    var ds : MutableList<DS> = mutableListOf()
    var colleurs : MutableList<Colleurs> = mutableListOf()
    var collesM : MutableList<Colles> = mutableListOf()


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

    fun startReadingSpreadsheetColleM(){
        val df = SimpleDateFormat("MM/dd/yyyy")
        collesM.clear()
        val sheet : MutableList<List<Any>> = mutableListOf()
        readSpreadsheetDisposable=
                sheetsAPIDataSource.readSpreadSheetCM(spreadsheetId, rangeCM)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnError { view.showError(it.message!!) }
                        .subscribe(Consumer {
                            sheet.addAll(it)
                            view.finishedReadingColleurs()
                        })
        for(i in 1 until sheet.size){
            var dict : MutableMap<Date, String> = HashMap<Date,String>()
            for (j in sheet[i].indices){
                dict.put(df.parse(sheet[0][j].toString()),'M'+sheet[i][j].toString())
            }
            collesM.add(Colles(i,dict.toMap()))
        }
    }



    companion object {
        const val spreadsheetId = "1VXDSYl2X5oXNXKeYbNrBH8b1zR_nIzqHbRhZaopWgCw"
        const val rangeStudents = "Sheet1!A5:F"
        const val rangePersonal = "Personal!A2:H"
        const val rangeDS = "DS!A2:E"
        const val rangeColleurs = "Colleurs!A2:F"
        const val rangeCM = "CollesMaths!A2:N"
    }
}
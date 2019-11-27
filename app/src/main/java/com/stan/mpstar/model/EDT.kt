package com.stan.mpstar.model

data class EDT(
        val myMonday : Map<Int, String>,
        val myTuesday : Map<Int, String>,
        val myWednesday : MutableMap<Int, String>,
        val myThursday : MutableMap<Int, String>,
        val myFriday : MutableMap<Int, String>
)
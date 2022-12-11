package com.valance.ency.util

object NetworkUtil {

    val isConnected: Boolean
        get() = Runtime.getRuntime().exec("ping -c 1 google.com").waitFor() == 0
}
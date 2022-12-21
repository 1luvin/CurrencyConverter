package com.valance.ency.extension

import androidx.lifecycle.MutableLiveData

fun <T> MutableLiveData<T>.forceRefresh() {
    value = value
}
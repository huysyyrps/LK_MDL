package com.example.lkmdl.util.file_util

interface ReadFileCallBack {
    fun fileCallbackSuccess(list: MutableList<String>)
    fun fileCallbackFaile(messahe:String)
}
package com.recorder.lecturenotes.smartfileexplorer.Datas

import android.content.Context
import java.io.File

/*
if file is a directory it returns no of childs else returns fil size
 */
fun File.getChildCounts() : String?
{
    val file : File = this
    if(file.isDirectory)
    {
        return "${file.list()?.size} items"
    }
    else
    {


        return android.text.format.Formatter.formatFileSize(Utils.appContext ,file.length() ).toString()
    }
}





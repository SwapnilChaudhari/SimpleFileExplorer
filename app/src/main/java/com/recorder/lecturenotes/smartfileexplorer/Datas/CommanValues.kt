package com.recorder.lecturenotes.smartfileexplorer.Datas

import android.content.Context
import android.content.ContextWrapper
import android.os.Environment
import android.util.Log
import android.webkit.MimeTypeMap
import java.io.File
import java.io.FileFilter
import java.util.*

object Utils {
    const val APP_TAG = "SmartFileExplorer"

    const val PATH_SEPARATOR = ">"

    var mCheckedFiles = mutableSetOf<String>()

    //true when long press on item filesordirs to enablethe selection process to copy files
    var itemLongPressed: Boolean = false

    var mOptionCopySelected: Boolean = false

    var pathQ: MutableList<File> = mutableListOf()

    var currentFileList: MutableList<File> = mutableListOf()

    var appContext: Context? = null

    //fill the list with files of current directory
    fun initListCurrentDirFiles(dir: File)
    {
        currentFileList.clear()
        Log.i(APP_TAG, "cleared")

        var myDirList = dir.listFiles(FileFilter { it.isDirectory && !it.name.startsWith(".")})?.toMutableList()
        var myFileList = dir.listFiles(FileFilter { !it.isDirectory && !it.name.startsWith(".")   })?.toMutableList()

        myDirList?.let {
            myDirList?.sortBy { it.name }
            currentFileList.addAll(myDirList)


        }
        Log.i(APP_TAG, "cleared 2")

        myFileList?.let {
            myFileList?.sortBy { it.name }
            currentFileList.addAll(myFileList)

        }
        Log.i(APP_TAG, "cleared 3")



    }

    fun get_mime_type(url: String): String? {
        var ext: String = MimeTypeMap.getFileExtensionFromUrl(url)
        var mime: String? = null
        if (ext != null) {
            mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
        }
        return mime;
    }

}
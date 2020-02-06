package com.recorder.lecturenotes.smartfileexplorer.interfaces

import java.io.File

interface OnDirectorySelectListener {
    open fun directorySelected(f : File)

    fun fileSelected(f : File)
}
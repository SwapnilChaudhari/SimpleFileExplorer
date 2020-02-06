package com.recorder.lecturenotes.smartfileexplorer

import adapters.MoviesAdapter
import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.recorder.lecturenotes.smartfileexplorer.Datas.Utils
import com.recorder.lecturenotes.smartfileexplorer.interfaces.OnDirectorySelectListener
import kotlinx.android.synthetic.main.content_main.*
import java.io.File
import java.io.FileFilter

//move btn
//select all
//progress bar

class MainActivity : AppCompatActivity(), OnDirectorySelectListener, //NavigationView.OnNavigationItemSelectedListener,
    View.OnLongClickListener {


    //var count = 0
    var mListFileSearch = mutableListOf<File>()
    var mListDirSearch = mutableListOf<File>()
    private lateinit var progress: ProgressDialog

    private lateinit var adapter: MoviesAdapter
    private lateinit var recycleViewfiles: RecyclerView
    private val handler = Handler()  //Optional. Define as a variable in your activity.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Utils.pathQ.size != 0) {
            Utils.pathQ.clear()
        }

        checkPermissions()
        Log.v(Utils.APP_TAG, "check permission ")


    }

    override fun onLongClick(carditem: View?): Boolean {


        Utils.itemLongPressed = !Utils.itemLongPressed


        if (!Utils.itemLongPressed)
            Utils.mCheckedFiles.clear()

        adapter.notifyDataSetChanged()


        if (Utils.itemLongPressed)
            checkboxSelectAll.visibility = View.VISIBLE
        if (!Utils.itemLongPressed)
            checkboxSelectAll.visibility = View.GONE

        checkSelectAllcbox()

        Log.v(Utils.APP_TAG, "onLongClick..${Utils.mCheckedFiles.size}")


        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            initData()
            Log.v(Utils.APP_TAG, "Data init done ")
            initUI()
            Log.v(Utils.APP_TAG, "initUi done")
        }
    }

    private fun getExternalCardDirectory() {

        val storageManager = getSystemService(Context.STORAGE_SERVICE)
        try {
            val storageVolumeClazz = Class.forName("android.os.storage.StorageVolume")
            val getVolumeList = storageManager.javaClass.getMethod("getVolumeList")
            val getPath = storageVolumeClazz.getMethod("getPath")
            val isRemovable = storageVolumeClazz.getMethod("isRemovable")

            val result = getVolumeList.invoke(storageManager) as Array<StorageVolume>
            result.forEach {
                Log.v(Utils.APP_TAG, File(getPath.invoke(it) as String).absolutePath)
                if (isRemovable.invoke(it) as Boolean) {
                    Log.v(Utils.APP_TAG, File(getPath.invoke(it) as String).absolutePath) //return File(getPath.invoke(it) as String)
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }

//            var storageVolumeList : MutableList<StorageVolume>
//            var storageManager: StorageManager = getSystemService(Context.STORAGE_SERVICE) as StorageManager
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
//                var storageVolumeList=  storageManager.storageVolumes
//                storageVolumeList.forEach { Log.v(Utils.APP_TAG,it.toString()) }
//            } else {
//                var storage:File=Environment.getExternalStorageDirectory()
//            }
    }

    private fun initData() {

        Utils.appContext = applicationContext

        Utils.pathQ.add(Environment.getExternalStorageDirectory())//"/storage/"))//Environment.getExternalStorageDirectory()

        Utils.initListCurrentDirFiles(Utils.pathQ.get(Utils.pathQ.lastIndex))

    }

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), 100
            )

        } else {
            if (Utils.pathQ.size == 0) {
                initData()
                Log.v(Utils.APP_TAG, "Data init done ")
                initUI()
                Log.v(Utils.APP_TAG, "initUi done")
            }
        }


    }

    private fun initUI() {
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

//        val fab: FloatingActionButton = findViewById(R.id.fab)
//        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }
//        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
//        val navView: NavigationView = findViewById(R.id.nav_view)
//        val toggle = androidx.appcompat.app.ActionBarDrawerToggle(
//            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
//        )
//        drawerLayout.addDrawerListener(toggle)
//        toggle.syncState()
//
//        navView.setNavigationItemSelectedListener(this)


        setTvRootPathClickSpan()

        initRecyclerView()


        checkboxSelectAll.setOnCheckedChangeListener { compoundButton, isChecked ->

            var curDir = Utils.pathQ.last()

            if (curDir.listFiles().isEmpty()) return@setOnCheckedChangeListener

            //Utils.mOptionSelectALl = isChecked

            var setFileSelection =
                curDir.listFiles((FileFilter { !it.name.startsWith(".") })).map { it.absolutePath }.toMutableSet()

            if (isChecked) {

                Utils.mCheckedFiles.addAll(setFileSelection)

            } else {
                Utils.mCheckedFiles.removeAll(setFileSelection)

            }

            adapter.notifyDataSetChanged()
            Log.v(Utils.APP_TAG, "Added all ${Utils.mCheckedFiles.size}")
        }

        btnSearch.setOnClickListener {
            var text: String = etFileSearch.text.toString()

            if (text.isEmpty()) {
                return@setOnClickListener
            }
            mListDirSearch.clear()
            mListFileSearch.clear()
            Log.v(Utils.APP_TAG, "getting list...")
            listAllFiles(Utils.pathQ[Utils.pathQ.lastIndex], text)

            mListDirSearch.sortBy { it.name.length }
            mListFileSearch.sortBy { it.name.length }

            var mListSearch = mutableListOf<File>()
            mListSearch.addAll(mListFileSearch)
            mListSearch.addAll(mListDirSearch)
            mListSearch.sortBy { it.name.length }

            //storing orignal list to restore when back btn pressed
            //mListBeforeSearch.addAll(Utils.currentFileList)

            //updating current list to search results
            Utils.currentFileList = mListFileSearch



            initRecyclerView()


        }

    }

    private fun setTvRootPathClickSpan() {
        tvPath.text = Utils.pathQ[0].name

        tvPath.movementMethod = LinkMovementMethod.getInstance()

        var ss = SpannableString(tvPath.text.toString())

        var clickSPan = object : ClickableSpan() {
            override fun onClick(p0: View) {

                Log.v(Utils.APP_TAG, "click")

                Utils.initListCurrentDirFiles(Utils.pathQ.get(Utils.pathQ.lastIndex))

                initRecyclerView()

            }
        }

        setSpan(ss, clickSPan, 0, ss.length)

        tvPath.text = ss

    }

    private fun initRecyclerView() {

        if (Utils.currentFileList == null) return

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = RecyclerView.VERTICAL

        recycleViewfiles = recycleView
        //set RecView's layout manager
        recycleViewfiles.layoutManager = layoutManager

        adapter = MoviesAdapter(this, Utils.currentFileList, this, this)

        recycleViewfiles.adapter = adapter


    }

    override fun directorySelected(dir: File) {
        Log.i(Utils.APP_TAG, "Interface Directory Selected called")
        //recycleView.adapter?.notifyDataSetChanged()

        //updating selected directory adding last in list
        Utils.pathQ.add(dir)

        updateCurrentPath()

        setTvPathClickableSpan()

        initRecyclerView()

        checkSelectAllcbox()

        Log.i(Utils.APP_TAG, "Interface Directory Selected called ${Utils.mCheckedFiles.size}")

    }

    private fun checkSelectAllcbox() {

        if (checkboxSelectAll.visibility == View.GONE) return

        var curDir = Utils.pathQ.last()

        if (curDir.listFiles().isNotEmpty()) {

            var setFileSelection =
                curDir.listFiles((FileFilter { !it.name.startsWith(".") })).map { it.absolutePath }.toMutableSet()

            if (Utils.mCheckedFiles.containsAll(setFileSelection)) {
                checkboxSelectAll.isChecked = true
            } else {
                checkboxSelectAll.isChecked = false
            }

        }
    }

    override fun fileSelected(f: File) {

        Log.v(Utils.APP_TAG, "sharing ${f.absolutePath}")
        try {

            var uri = FileProvider.getUriForFile(this, applicationContext.packageName + ".provider", f);
            var intentShare = Intent()
            intentShare.action = Intent.ACTION_VIEW
            intentShare.type = "*/*"
            intentShare.data = uri
            intentShare.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            Log.v(Utils.APP_TAG, "sharing 2")
            startActivity(Intent.createChooser(intentShare, "Open with"))
        } catch (e: Throwable) {
            //Log.v(Utils.APP_TAG,"err: ${e.message}")
            Toast.makeText(this, "No application found ", Toast.LENGTH_LONG).show()
            //e.printStackTrace()
        }
    }


    class GenericFileProvider : FileProvider() {

    }

    private fun updateCurrentPath() {

        Utils.initListCurrentDirFiles(Utils.pathQ.get(Utils.pathQ.lastIndex))

        var path: List<String> = Utils.pathQ.map { it.name }

        Log.i(Utils.APP_TAG, "PATH = ${path.joinToString(separator = Utils.PATH_SEPARATOR)}")

        tvPath.text = path.joinToString(separator = Utils.PATH_SEPARATOR)

        //setTvPathClickableSpan()

        //initRecyclerView()
    }

    private fun setTvPathClickableSpan() {

        val str: String = tvPath.text.toString()

        tvPath.movementMethod = LinkMovementMethod.getInstance()

        var ss = SpannableString(tvPath.text.toString())

        var index: Int = 0
        var start: Int = 0
        var countChild = 0

        for (i in str) {

            if (i == '>') { // set each folder name as clickable link on path textview
                Log.i(Utils.APP_TAG, "at : ${index}")

                var spClickSpan =
                    SpecialClickableSpan(ss.substring(start..index), Utils.pathQ.get(countChild++).absolutePath, this)

                setSpan(ss, spClickSpan, start, index)

                start = index + 1
            }

            index++

        }
        tvPath.text = ss

    }


    //set spanable text on start to end
    private fun setSpan(ss: SpannableString, clickSpan: ClickableSpan, start: Int, end: Int) {
        ss.setSpan(
            clickSpan,
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        ss.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.colorPrimaryDark)),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    class SpecialClickableSpan(internal var text: String, var path: String, var ct: Context) : ClickableSpan() {

        override fun onClick(widget: View) {

            Log.v(Utils.APP_TAG, "onClick [$text]")

            var index: Int = 0
            var count: Int = 0

            //removing subchilds of selected directory
            var len = Utils.pathQ.size - 1

            for (i in 0..len) {
                //Log.v(Utils.APP_TAG, "i: ${i}")
                if (Utils.pathQ.get(i).absolutePath == path) {
                    index = count
                    break
                }

                count++
            }



            while (Utils.pathQ.size - 1 > index) {
                Utils.pathQ.removeAt(Utils.pathQ.lastIndex)

            }


            //Utils.pathQ.clear()

            //Log.v(Utils.APP_TAG, "last file: ${Utils.pathQ[Utils.pathQ.lastIndex].name}")

            (ct as MainActivity).updateCurrentPath()

            (ct as MainActivity).setTvPathClickableSpan()

            if (Utils.pathQ.size == 1) {
                //Log.v(Utils.APP_TAG, "One size")

                (ct as MainActivity).setTvRootPathClickSpan()
            }

            (ct as MainActivity).initRecyclerView()


        }
    }

    override fun onBackPressed() {


        if (Utils.itemLongPressed && Utils.mCheckedFiles.size == 0) {
            //hide checkbox if none selected
            hideCHeckBox()

        }

        Log.v(Utils.APP_TAG, "One size ${Utils.pathQ.size}")

        if (Utils.pathQ.size > 1) {

            Utils.pathQ.removeAt(Utils.pathQ.lastIndex)

            updateCurrentPath()

            setTvPathClickableSpan()

            if (Utils.pathQ.size == 1) {
                //Log.v(Utils.APP_TAG, "One size")

                setTvRootPathClickSpan()
            }
            initRecyclerView()

            checkSelectAllcbox()


            return
        }


        super.onBackPressed()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.copy_items -> {


                hideCHeckBox()


                //?Utils.mCheckedFiles.addAll(Utils.mSetSelectall)

                if (Utils.mCheckedFiles.size == 0) {
                    Toast.makeText(applicationContext, "Please select items to copy ", Toast.LENGTH_LONG)

                    return true

                }
                Utils.mOptionCopySelected = true

                Toast.makeText(applicationContext, "Items copied , Open directory and select paste ", Toast.LENGTH_LONG)

                Log.v(Utils.APP_TAG, "Copying..${Utils.mCheckedFiles.size}")


                true
            }
            R.id.paste_items -> {

                if (!Utils.mOptionCopySelected) {
                    Toast.makeText(Utils.appContext, "Please select copy option to before pasting", Toast.LENGTH_LONG)
                        .show()
                    return true
                }

                hideCHeckBox()

                if (Utils.mCheckedFiles.size == 0) {
                    Toast.makeText(Utils.appContext, " No files selected.", Toast.LENGTH_LONG).show()
                    return true
                }

                copyFilesInBackground()

                //Log.v(Utils.APP_TAG, "Pasting..")
                true
            }
            R.id.cut_items -> {
                Log.v(Utils.APP_TAG, "Cutting..${Utils.mCheckedFiles.size}")

                if (!Utils.mOptionCopySelected) {
                    Toast.makeText(Utils.appContext, "Please select copy option before moving", Toast.LENGTH_LONG)
                        .show()
                    return true
                }

                if (Utils.mCheckedFiles.size == 0) {
                    Toast.makeText(Utils.appContext, " No files selected.", Toast.LENGTH_LONG).show()
                    return true
                }
                moveFilesInBG()

                true
            }
            R.id.delete_items -> {
                Log.v(Utils.APP_TAG, "Deleting..")

                //??Utils.mCheckedFiles.addAll(Utils.mSetSelectall)

                if (Utils.mCheckedFiles.size == 0) {
                    Toast.makeText(Utils.appContext, " No files to delete.", Toast.LENGTH_LONG).show()
                    return true
                }
                deleteFilesInBg()
                true
            }
            R.id.create_dir -> {

                createNewFolder("New Folder")
                true
            }

            R.id.rename -> {

                renameFile()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun renameFile() {

        hideCHeckBox()

        if (Utils.mCheckedFiles.size == 0) {
            Toast.makeText(applicationContext, "Select a file to renme.", Toast.LENGTH_LONG).show()
            return
        }
        if (Utils.mCheckedFiles.size > 1) {
            Toast.makeText(applicationContext, "Please select one file at a time ", Toast.LENGTH_LONG).show()
            return
        }

        createRenameAlterDialogue()

    }

    private fun createRenameAlterDialogue() {

        val alert = AlertDialog.Builder(this)

        val edittext = EditText(Utils.appContext)
        edittext.setText(File(Utils.mCheckedFiles.last()).name)
        alert.setMessage("Enter Your Message")
        alert.setTitle("Enter Your Title")
        alert.setView(edittext)
        alert.setPositiveButton("Rename", DialogInterface.OnClickListener { dialogInterface, i ->

            if(edittext.text.isEmpty()) return@OnClickListener

            Log.v(Utils.APP_TAG, " --- ${edittext.text}")

            var src = File(Utils.mCheckedFiles.last())

            try {

                val mime = Utils.get_mime_type(src.absolutePath)

                Log.v(Utils.APP_TAG, " --- ${mime}")

                var newfilename =  src.parentFile.absolutePath + "/${edittext.text}"

                if (!src.isDirectory)
                    newfilename = newfilename + ".${mime?.split("/")?.last()}"

                File(Utils.mCheckedFiles.last()).renameTo(File(newfilename))

                Utils.mCheckedFiles.clear()

                Utils.initListCurrentDirFiles(Utils.pathQ.last())

                adapter.notifyDataSetChanged()
            }
            catch (e: Throwable)
            {
                Toast.makeText(Utils.appContext,"Error in renaming: ${e.message}",Toast.LENGTH_LONG).show()
            }
        })

        alert.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialogInterface, i ->
            dialogInterface.dismiss()
            dialogInterface.cancel()
        });

        alert.show()
    }

    private fun showProgressDialogue(action: String) {
        progress = ProgressDialog(this)
        progress.setCancelable(true);
        progress.setMessage("$action")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setProgress(0)
        progress.setMax(100)
        progress.show()

    }

    private fun hideCHeckBox() {
        //int scroll
        Utils.itemLongPressed = false

        checkboxSelectAll.visibility = View.GONE

        //stop displaying
        adapter?.let { adapter.notifyDataSetChanged() }

    }

    private fun copyFilesInBackground() {


        val r = Runnable {
            // your code here

            val totalfiles = Utils.mCheckedFiles.size
            handler.post { showProgressDialogue("Copying.. ${totalfiles} Files") }

            var count = 0
            try {
                for (path in Utils.mCheckedFiles) {


                    var fname: String = path.split("/").last()

                    var destFile = File(Utils.pathQ[Utils.pathQ.lastIndex].absolutePath + "/${fname}")

                    Log.v(Utils.APP_TAG, " src: -${path}")

                    Log.v(Utils.APP_TAG, " dest:-${destFile.absolutePath}")

                    if (!path.equals(destFile.absoluteFile)) {
                        File(path).copyRecursively(File(destFile.absolutePath), true)
                    }


                    handler.post {
                        progress.setProgress(((count * 100) / totalfiles) as Int)

                        progress.setMessage("${totalfiles - count} items remaining")
                    }
                    count++


                }
            } catch (e: Throwable) {
                handler.post {
                    //progress.cancel()
                    Toast.makeText(Utils.appContext, "Error in Copying File ${e.message}", Toast.LENGTH_LONG)
                        .show()
                }
                Log.v(Utils.APP_TAG, "err: " + e.message)
            }

            showUpdate(count, "Files Copied")
        }

        val t = Thread(r)
        t.start()
    }

    private fun moveFilesInBG() {

        val r = Runnable {
            // your code here

            var count = 0
            try {

                val totalfiles = Utils.mCheckedFiles.size
                handler.post { showProgressDialogue("Copying.. ${totalfiles} Moving") }


                for (path in Utils.mCheckedFiles) {


                    var fname: String = path.split("/").last()

                    var destFile = File(Utils.pathQ[Utils.pathQ.lastIndex].absolutePath + "/${fname}")


                    Log.v(Utils.APP_TAG, " src: -${File(path).parentFile.absolutePath}")

                    Log.v(Utils.APP_TAG, " dest:-${destFile.parentFile.absolutePath}")

                    //dont move if source file and destination  are same , i.e moving in same directory
                    if (!File(path).parentFile.absolutePath.equals(destFile.parentFile.absolutePath)) {
                        File(path).copyRecursively(File(destFile.absolutePath), true)
                    }

                    handler.post {
                        progress.setProgress(((count * 100) / totalfiles) as Int)

                        progress.setMessage("${totalfiles - count} items remaining")
                    }

                    count++


                }


                for (path in Utils.mCheckedFiles) {


                    Log.v(Utils.APP_TAG, " deleting src: -${path}")


                    if (!File(path).parentFile.absolutePath.equals(Utils.pathQ.last().absolutePath)) {
                        File(path).deleteRecursively()
                    }

                    count++


                }
            } catch (e: Throwable) {
                Log.v(Utils.APP_TAG, "err: " + e.message)

                handler.post {
                    //progress.cancel()
                    Toast.makeText(Utils.appContext, "Error in moving File:  ${e.message}", Toast.LENGTH_LONG)
                        .show()
                }

            }

            showUpdate(count / 2, "Files Moved")
        }


        val t = Thread(r)
        t.start()
    }

    private fun deleteFilesInBg() {

        val r = Runnable {
            // your code here
            var count = 0
            val totalfiles = Utils.mCheckedFiles.size
            handler.post { showProgressDialogue("Deleting ${totalfiles} Files") }

            try {

                Utils.mCheckedFiles.forEach {
                    if (File(it).exists()) {
                        var name = File(it).name
                        Log.v(Utils.APP_TAG, "Deleting: " + name)
                        File(it).deleteRecursively()
                        //Log.v(Utils.APP_TAG, "Deleted: ")
                        handler.post {
                            progress.setProgress(((count * 100) / totalfiles) as Int)

                            progress.setMessage("${totalfiles - count} items remaining")
                        }

                        count++
                    }

                }
            } catch (e: Throwable) {
                Log.v(Utils.APP_TAG, "err: " + e.message)

                handler.post {
                    //progress.cancel()
                    Toast.makeText(Utils.appContext, "Error in deleting File ${e.message}", Toast.LENGTH_LONG)
                        .show()
                }

            }
            showUpdate(count, "Files Deleted ")

        }

        val t = Thread(r)
        t.start()
    }

    private fun createNewFolder(fname: String) {
        try {
            var name = fname
            var count = 0

            var list = Utils.currentFileList.map { it.name }
            while (list.contains(name)) {
                count++
                name = fname + "($count)"
            }
//            var flist = Utils.currentFileList.forEach {
//
//                if (it.name.equals(name)) {
//
//                    count++
//                    name = fname + "($count)/"
//                }
//            }

            var dir = File(Utils.pathQ.last().absolutePath + "/${name}/").mkdir()

            Utils.initListCurrentDirFiles(Utils.pathQ.last())

            initRecyclerView()

            if (count > 0) {
                Toast.makeText(
                    Utils.appContext,
                    " Already Exists File ${name} created",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    Utils.appContext,
                    " File ${name} created",
                    Toast.LENGTH_SHORT
                ).show()
            }


        } catch (e: Exception) {
            Log.v(Utils.APP_TAG, "err: ${e.message}")
        }
    }

    private fun showUpdate(count: Int, action: String) {
        handler.post(Runnable //If you want to update the UI, queue the code on the UI thread
        {
            progress.cancel()

            //ensuring all files are copied
            Utils.mOptionCopySelected = false

            if (count == Utils.mCheckedFiles.size) {
                //diap message and clear the file copy list
                Toast.makeText(
                    Utils.appContext,
                    "${Utils.mCheckedFiles.size}  $action.. Successfully",
                    Toast.LENGTH_SHORT
                )
                    .show()
                Utils.initListCurrentDirFiles(Utils.pathQ[Utils.pathQ.lastIndex])
                Log.v(Utils.APP_TAG, "$action")

            }

            Utils.mCheckedFiles.clear()

            hideCHeckBox()


        })
    }

    private fun selectAll(isChecked: Boolean) {


        adapter.notifyDataSetChanged()

    }

    private fun listAllFiles(directory: File, txt: String) {
        //Log.i(CommanValues.APP_TAG, " ${file.name}")
        val files = directory.listFiles()
        if (files != null) {
            for (file in files) {
                if (file != null) {
                    if (file.isDirectory) {
                        //count = count + 1
                        listAllFiles(file, txt)
                    }
                    if (!file.name.startsWith(".") && file.name.toLowerCase().contains(txt.toLowerCase())) {
                        mListFileSearch.add(file)
                        Log.i(Utils.APP_TAG, "Dir ${file.name} contains ${txt}")
                    }


                    // Log.i(CommanValues.APP_TAG, "File ${file.name}")
                }
            }
        }
        //Log.i(CommanValues.APP_TAG, " finish")

    }

//    override fun onNavigationItemSelected(item: MenuItem): Boolean {
//        // Handle navigation view item clicks here.
//        when (item.itemId) {
//            R.id.nav_home -> {
//                // Handle the camera action
//            }
//            R.id.nav_gallery -> {
//
//            }
//            R.id.nav_slideshow -> {
//
//            }
//            R.id.nav_tools -> {
//
//            }
//            R.id.nav_share -> {
//
//            }
//            R.id.nav_send -> {
//
//            }
//        }
//        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
//        drawerLayout.closeDrawer(GravityCompat.START)
//        return true
//    }


}

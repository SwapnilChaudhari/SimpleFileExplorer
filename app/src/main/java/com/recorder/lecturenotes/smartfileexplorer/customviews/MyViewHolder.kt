package adapters


import android.content.Context
import android.os.Environment
import android.os.FileUtils
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import com.recorder.lecturenotes.smartfileexplorer.Datas.Utils

import com.recorder.lecturenotes.smartfileexplorer.Datas.getChildCounts
import com.recorder.lecturenotes.smartfileexplorer.MainActivity
import com.recorder.lecturenotes.smartfileexplorer.R


import com.recorder.lecturenotes.smartfileexplorer.interfaces.OnDirectorySelectListener
import kotlinx.android.synthetic.main.card_item.view.*
import kotlinx.android.synthetic.main.content_main.*

import java.text.SimpleDateFormat
import java.util.*
import kotlin.Exception
import java.io.*


class MyViewHolder(
    itemView: View,
    val dirSelectedListener: OnDirectorySelectListener,
    val longClickListener: View.OnLongClickListener
) :
    RecyclerView.ViewHolder(itemView), View.OnClickListener {


    override fun onClick(v: View?) {
        var tag = v?.getTag(R.id.idCardView) as Int

        var file: File = Utils.currentFileList.get(tag)

        //Log.v(Utils.APP_TAG, "selected : ${Utils.currentFileList.get(tag).name}")

        if (file.isDirectory) {

            //updating Pathq for selected directory
            getParentDirsTillCurrentPath(file, Utils.pathQ)


            dirSelectedListener.directorySelected(file)

        } else {
            dirSelectedListener.fileSelected(file)
        }


    }


    /*
    in case of search , returns all parents of selected file/folder till directory where search was carried out
     do recursion till parent path of file mach with current folder (or last dir of PathQ)
     */
    fun getParentDirsTillCurrentPath(f: File, mList: MutableList<File>) {
        if (f.parentFile.absolutePath != Utils.pathQ[Utils.pathQ.lastIndex].absolutePath) {

            getParentDirsTillCurrentPath(f.parentFile, mList) //LIFO
            mList.add(f.parentFile)//FIFO
            Log.v(Utils.APP_TAG, "${f.parentFile.name} ---Added ${Utils.pathQ[Utils.pathQ.lastIndex].name}")

        }

    }

    fun setData(file: File, pos: Int) {
        itemView.tvFName.text = file.name

        var sdf = SimpleDateFormat("dd MMM yy hh:mm", Locale.getDefault())

        itemView.tvCountItem.text = file.getChildCounts()

        itemView.tvDate.text = sdf.format(Date(file.lastModified()))

        //itemView.setTag(R.id.idCardView,pos)

        //itemView.setOnClickListener(this)

        itemView.ivIconFile.setTag(R.id.idCardView, pos)

        itemView.ivIconFile.setOnClickListener(this)

        if (file.isDirectory) {
            itemView.ivIconFile.setImageResource(R.drawable.ic_folder)
        } else {
            itemView.ivIconFile.setImageResource(R.drawable.ic_file)
        }


        itemView.setOnLongClickListener(longClickListener)

        itemView.ivIconFile.setOnLongClickListener {

            var tag = it?.getTag(R.id.idCardView) as Int

            //Log.v(Utils.APP_TAG,"tag... ${tag}")
            var file: File = Utils.currentFileList.get(tag)
            //Log.v(Utils.APP_TAG,"name  copying... ${file.name}")

            Utils.itemLongPressed = !Utils.itemLongPressed


            //Log.v(Utils.APP_TAG,"tag... ${tag} ${adapterPosition}")


//            if(file != null)
//            {
//
//                copyFile(file,Environment.getExternalStorageDirectory().absolutePath)
//
//            }

            return@setOnLongClickListener true
        }


        if (Utils.itemLongPressed) {

            itemView.chqBoxSelection.visibility = View.VISIBLE

        } else {
            itemView.chqBoxSelection.visibility = View.GONE

        }
        itemView.chqBoxSelection.setTag(R.id.idCardView, pos)

        //itemView.chqBoxSelection.isChecked = false

        //var isChecked : Boolean = false
        if(!Utils.mCheckedFiles.contains(Utils.currentFileList[pos].absolutePath))
        {
          //  isChecked = false
            itemView.chqBoxSelection.isChecked = false
        }
        else
        {
            //isChecked = true
            itemView.chqBoxSelection.isChecked = true
        }


//        if(Utils.mOptionSelectALl)
//        {
//            //isChecked = true
//            itemView.chqBoxSelection.isChecked = true
//        }
//        else
//        {
//
//        }


        itemView.chqBoxSelection.setOnCheckedChangeListener { chqBtn, isChecked ->


            if (chqBtn.isChecked)
            //storing selected file path
            {

                Utils.mCheckedFiles.add(Utils.currentFileList[adapterPosition].absolutePath)

            }
            else
            {
                //checkboxSelectAll.visibility = View.GONE
                //var selectALl : CheckBox = (Utils.appContext as MainActivity).findViewById(R.id.checkboxSelectAll) //.isChecked = false
                //selectALl.isChecked = false
                //Utils.mSetSelectall.remove(Utils.currentFileList[adapterPosition].absolutePath)
                Utils.mCheckedFiles.remove(Utils.currentFileList[adapterPosition].absolutePath)
            }
            Log.v(
                Utils.APP_TAG,
                "selected : ${chqBtn.getTag(R.id.idCardView)}, ${Utils.currentFileList[adapterPosition].name}, " +
                        "${Utils.mCheckedFiles.size}"
            )


        }


    }

    fun copyFile(sourceFile: File, dest: String) {
        try {
            //file.copyTo()
            var destFile = File(dest + "/${sourceFile.name}")

            Log.v(Utils.APP_TAG, "Copying ...  ${sourceFile.name} to ${destFile.absolutePath} ")

            File(sourceFile.absolutePath.toString()).copyTo(File(destFile.absolutePath), false)

            Log.v(Utils.APP_TAG, "Copied ... ${destFile.length()} ")

        } catch (e: Exception) {
            Log.v(Utils.APP_TAG, "error copy: ${e.printStackTrace()}")
        }
    }

    fun setChqBoxVisiblility(bool: Boolean) {

    }

}
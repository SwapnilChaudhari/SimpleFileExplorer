package adapters


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import com.recorder.lecturenotes.smartfileexplorer.Datas.Utils

import com.recorder.lecturenotes.smartfileexplorer.R
import com.recorder.lecturenotes.smartfileexplorer.interfaces.OnDirectorySelectListener
import java.io.File

class MoviesAdapter(
    val context: Context,
    var files: MutableList<File>,
    val dirSelectedListener: OnDirectorySelectListener,
    val onLongClickListener:  View.OnLongClickListener
) : RecyclerView.Adapter<MyViewHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        var cardItem: View = LayoutInflater.from(context).inflate(
            R.layout.card_item,
            parent,
            false
        )




        return MyViewHolder(cardItem, dirSelectedListener, onLongClickListener)
    }


//    fun updateList( mlist : MutableList<File>)
//    {
//        files = Utils.currentFileList
//        notifyDataSetChanged()
//    }

    override fun getItemCount(): Int {
        return files.size
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the [ViewHolder.itemView] to reflect the item at the given
     * position.
     *
     *
     * If you need the position of an item later
     * on (e.g. in a click listener), use [ViewHolder.getAdapterPosition] which will
     * have the updated adapter position.
     */
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {


        holder.setData(files.get(position), position)


    }


}
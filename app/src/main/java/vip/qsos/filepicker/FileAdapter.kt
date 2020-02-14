package vip.qsos.filepicker

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.item_file.view.*
import vip.qsos.filepicker.lib.OnTListener

/**
 * @author : 华清松
 */
class FileAdapter(
        private val context: Context,
        private val list: List<Uri>,
        private val onDeleteListener: OnTListener<Int>
) : RecyclerView.Adapter<FileAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_file, null)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(list[position])
        holder.itemView.item_file_delete.setOnClickListener {
            onDeleteListener.back(position)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun setData(uri: Uri) {
            GlideApp.with(itemView.context)
                    .load(uri)
                    .placeholder(R.drawable.ic_launcher)
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .skipMemoryCache(false)
                    .into(itemView.item_file_icon)
        }
    }
}
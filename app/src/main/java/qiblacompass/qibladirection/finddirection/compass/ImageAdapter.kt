package qiblacompass.qibladirection.finddirection.compass

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import qiblacompass.qibladirection.finddirection.R
import qiblacompass.qibladirection.finddirection.databinding.ItemRowBinding
import qiblacompass.qibladirection.finddirection.helper.Constants


class ImageAdapter(
    var context: Activity,
    private var listener: OnCompassClickListener
) : RecyclerView.Adapter<ImageAdapter.RecyclerViewViewHolder>() {
    var flag = -1

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ImageAdapter.RecyclerViewViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemRowBinding.inflate(inflater)
        return RecyclerViewViewHolder(binding, context)
    }

    override fun onBindViewHolder(holder: RecyclerViewViewHolder, position: Int) {
        holder.bind(position, listener)
    }

    override fun getItemCount(): Int {
        return Constants.compassFaces.size
    }

    interface OnCompassClickListener {
        fun onCompassClick(position: View, adapterPosition: Int)
    }

    inner class RecyclerViewViewHolder(
        private val binding: ItemRowBinding,
        val context: Context,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(pos: Int, listener: OnCompassClickListener) {
            listener.onCompassClick(itemView, adapterPosition)
            binding.ivFilter.setImageResource(Constants.compassFaces[pos])
        }

    }

}
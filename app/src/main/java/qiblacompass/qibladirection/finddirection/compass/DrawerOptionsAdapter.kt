package qiblacompass.qibladirection.finddirection.compass

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.RecyclerView
import qiblacompass.qibladirection.finddirection.R
import qiblacompass.qibladirection.finddirection.databinding.LayoutDrawerOptionsBinding
import qiblacompass.qibladirection.finddirection.helper.DrawerHelper
import qiblacompass.qibladirection.finddirection.helper.GeneralUtils

class DrawerOptionsAdapter(
    var context: Activity
) : RecyclerView.Adapter<DrawerOptionsAdapter.RecyclerViewViewHolder>() {

    private var inflater: LayoutInflater? = null
    val icons =
        intArrayOf(R.drawable.ic_share, R.drawable.ic_star, R.drawable.ic_about, R.drawable.ic_app)
    val titles = arrayOf(context.resources.getString(R.string.share_app),
        context.resources.getString(R.string.rate_us),
        context.resources.getString(R.string.about_us),
        context.resources.getString(R.string.more_apps))

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): DrawerOptionsAdapter.RecyclerViewViewHolder {
        if(inflater==null) {
            inflater = LayoutInflater.from(parent.context)
        }
        val view = inflater?.inflate(R.layout.layout_drawer_options, parent, false)
        val binding = inflater?.let { LayoutDrawerOptionsBinding.inflate(it) }
//        return binding?.let { RecyclerViewViewHolder(it, context) }!!
        return RecyclerViewViewHolder(view, context)
    }

    override fun onBindViewHolder(holder: RecyclerViewViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return icons.size
    }

    inner class RecyclerViewViewHolder(
        private val binding: View?,
        val context: Context,
    ) :
        RecyclerView.ViewHolder(binding!!) {

        val tvOption: TextView = itemView.findViewById(R.id.tv_option)
        val lyBackground: LinearLayout = itemView.findViewById(R.id.ly_background)

        @SuppressLint("NotifyDataSetChanged")
        fun bind(pos: Int) {
            tvOption.text = titles[pos]
            tvOption.setCompoundDrawablesWithIntrinsicBounds(icons[pos], 0, 0, 0)

            itemView.setOnClickListener {
                GeneralUtils.optionsDrawerFlag = adapterPosition
                when (adapterPosition) {
                    0-> DrawerHelper.shareApp(context)
                    1-> DrawerHelper.rateApp(context)
                    3-> DrawerHelper.moreApps(context)
                }
                GeneralUtils.drawer?.closeDrawer(GravityCompat.START)
                notifyDataSetChanged()
            }

            if (position == GeneralUtils.optionsDrawerFlag) {
                lyBackground.setBackgroundResource(R.drawable.bg_solid_white)
            } else {
                lyBackground.setBackgroundResource(0)
            }
        }

    }

}
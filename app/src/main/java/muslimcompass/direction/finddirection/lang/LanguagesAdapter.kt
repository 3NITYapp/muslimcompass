package muslimcompass.direction.finddirection.lang

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import muslimcompass.direction.finddirection.R
import muslimcompass.direction.finddirection.databinding.LanguageCustomCellBinding
import muslimcompass.direction.finddirection.helper.Constants
import muslimcompass.direction.finddirection.helper.GeneralUtils
import muslimcompass.direction.finddirection.helper.LocaleManager
import muslimcompass.direction.finddirection.helper.PrefsUtils

class LanguagesAdapter(
    var context: Activity,
    val dialog: Dialog
) : RecyclerView.Adapter<LanguagesAdapter.RecyclerViewViewHolder>() {
    private var inflater: LayoutInflater? = null
    var flag = 0
    lateinit var prefs: PrefsUtils
    val locales = arrayOf("ru", "en", "ar", "es", "fr", "tr", )

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerViewViewHolder {
        if(inflater==null) {
            inflater = LayoutInflater.from(parent.context)
        }
        val view = inflater?.inflate(R.layout.language_custom_cell, parent, false)
        val binding = inflater?.let { LanguageCustomCellBinding.inflate(it) }
        prefs = PrefsUtils(context)
        return RecyclerViewViewHolder(view, context)
    }

    override fun onBindViewHolder(holder: RecyclerViewViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return Constants.languages.size
    }


    inner class RecyclerViewViewHolder(
        private val binding: View?,
        val context: Context,
    ) :
        RecyclerView.ViewHolder(binding!!) {

        val tv_language: TextView = itemView.findViewById(R.id.tv_language)
        val ly_background: ConstraintLayout = itemView.findViewById(R.id.ly_background)

        @SuppressLint("NotifyDataSetChanged")
        fun bind(pos: Int) {
            itemView.setOnClickListener {
                prefs.saveToPrefs("language", adapterPosition)
                prefs.saveToPrefs("locale", locales[pos])
                LocaleManager.setNewLocale(context, locales[pos])
                notifyDataSetChanged()
                dialog.dismiss()
                (context as Activity).finish()
                context.overridePendingTransition(0, 0)
                context.startActivity(context.intent)
                context.overridePendingTransition(0, 0)
            }
            tv_language.setText(Constants.languages[pos])

            GeneralUtils.languagesFlag = prefs.getFromPrefsWithDefault("language", 0)

            if (position == GeneralUtils.languagesFlag) {
                tv_language.setTextColor(context.resources.getColor(R.color.white))
                ly_background.setBackgroundResource(R.drawable.bg_rounded)
            } else {
                ly_background.setBackgroundResource(0)
                tv_language.setTextColor(context.resources.getColor(R.color.text_dialogs))
            }
        }

    }

}
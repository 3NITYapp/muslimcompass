package qiblacompass.qibladirection.finddirection.helper

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import qiblacompass.qibladirection.finddirection.R
import qiblacompass.qibladirection.finddirection.compass.ImageAdapter
import qiblacompass.qibladirection.finddirection.databinding.ActivityPrayerBinding
import qiblacompass.qibladirection.finddirection.databinding.CustomProgressBinding
import qiblacompass.qibladirection.finddirection.databinding.LySettingsBinding
import java.util.ArrayList
import java.util.HashMap
import java.util.HashSet

class DialogUtils(val context: Context, layoutInflater: LayoutInflater) {

    lateinit var _settingsSavedListener: OnSettingsSavedListener

    constructor(context: Context, layoutInflater: LayoutInflater, settingsSavedListener: OnSettingsSavedListener) : this(context, layoutInflater) {
        _settingsSavedListener = settingsSavedListener
    }

    private var inflater: LayoutInflater
    private var progressDialog: AlertDialog.Builder
    private var dialog: AlertDialog
    private var prefUtils: PrefsUtils
    private var binding: CustomProgressBinding = CustomProgressBinding.inflate(layoutInflater)

    init {
        progressDialog = AlertDialog.Builder(context).setView(binding.root)
        dialog = progressDialog.create()
        inflater = layoutInflater
        prefUtils = PrefsUtils(context)
    }

    fun showProgressDialog(){
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    fun hideProgressDialog(){
        dialog.dismiss()
    }

    fun showSettingDialog() {
        val dialog = Dialog(context!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        val binding = LySettingsBinding.inflate(inflater)
        dialog.setContentView(binding.root)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window!!.attributes = lp

        val timeFormats = arrayOf("24 hours format", "12 hours format", "12 hour no suffix")
        binding.spTimeFormats.adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, timeFormats)
        binding.spTimeFormats.setSelection(prefUtils.getFromPrefsWithDefault(PrefsUtils.timeFormat, 0))
        binding.spTimeFormats.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                (view as TextView).setTextColor(Color.BLACK)
            }
        }

        val calculationMethods = arrayOf("Jafari", "Karachi", "Isna", "Mwl", "Makkah", "Egypt", "Custom", "Tehran")
        binding.spCalculationMethod.adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, calculationMethods)
        binding.spCalculationMethod.setSelection(prefUtils.getFromPrefsWithDefault(PrefsUtils.calcMethod, 1))
        binding.spCalculationMethod.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                (view as TextView).setTextColor(Color.BLACK)
            }
        }

        val juristicMethods = arrayOf("Shafi", "Hanafi" )
        binding.spJuristicMethod.adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, juristicMethods)
        binding.spJuristicMethod.setSelection(prefUtils.getFromPrefsWithDefault(PrefsUtils.juristic, 0))
        binding.spJuristicMethod.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                (view as TextView).setTextColor(Color.BLACK)
            }
        }

        val adjustingMethods = arrayOf("None", "Mid night", "One seventh", "Angle based")
        binding.spAdjustingMethod.adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, adjustingMethods)
        binding.spAdjustingMethod.setSelection(prefUtils.getFromPrefsWithDefault(PrefsUtils.highLats, 0))
        binding.spAdjustingMethod.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                (view as TextView).setTextColor(Color.BLACK)
            }
        }

        binding.btnSave.setOnClickListener {
            prefUtils.saveToPrefs(PrefsUtils.timeFormat, binding.spTimeFormats.selectedItemPosition)
            prefUtils.saveToPrefs(PrefsUtils.calcMethod, binding.spCalculationMethod.selectedItemPosition)
            prefUtils.saveToPrefs(PrefsUtils.juristic, binding.spJuristicMethod.selectedItemPosition)
            prefUtils.saveToPrefs(PrefsUtils.highLats, binding.spAdjustingMethod.selectedItemPosition)
            _settingsSavedListener.onSettingsSaved(true)
            dialog.dismiss()
        }

        dialog.show()
    }

    interface OnSettingsSavedListener {
        fun onSettingsSaved(isSaved: Boolean)
    }

}
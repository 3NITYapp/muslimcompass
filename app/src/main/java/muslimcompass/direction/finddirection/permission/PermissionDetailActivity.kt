package muslimcompass.direction.finddirection.permission

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.permissionx.guolindev.PermissionX
import muslimcompass.direction.finddirection.base.BaseActivity
import muslimcompass.direction.finddirection.compass.MainActivity
import muslimcompass.direction.finddirection.databinding.ActivityPermissionsDetailBinding
import muslimcompass.direction.finddirection.helper.GpsTracker
import muslimcompass.direction.finddirection.helper.GeneralUtils

@SuppressLint("CustomPermissionScreen")
class PermissionDetailActivity : BaseActivity() {

    private lateinit var binding: ActivityPermissionsDetailBinding

    override fun observeViewModel() {
        // nothing
    }

    override fun initViewBinding() {
        binding = ActivityPermissionsDetailBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        val view = binding.root
        setContentView(view)
    }

    override fun setupViewModel() {
        // not required
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.btnGrant.setOnClickListener {
            grantPermissions()
        }

    }

    fun grantPermissions(){
        PermissionX.init(this)
            .permissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
            .onExplainRequestReason { scope, deniedList ->
                scope.showRequestReasonDialog(deniedList, "Core fundamental are based on these permissions", "OK", "Cancel")
            }
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(deniedList, "You need to allow necessary permissions in Settings manually", "OK", "Cancel")
            }
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    Toast.makeText(this, "All permissions are granted", Toast.LENGTH_LONG).show()

                    val gpsService = GpsTracker(this@PermissionDetailActivity)
                    val intent = Intent(this, gpsService.javaClass)
                    if (!GeneralUtils.isMyServiceRunning(this, gpsService.javaClass)) {
                        startService(intent)
                    }

                    MainActivity.startMe(this)
                } else {
                    grantPermissions()
                }
            }
    }

    companion object {
        @JvmStatic
        fun startMe(context: Context) {
            val intent = Intent(context, PermissionDetailActivity::class.java)
            context.startActivity(intent)
            (context as Activity).finish()
        }
    }
}
package muslimcompass.direction.finddirection.settings.splash

import android.location.LocationManager
import android.os.Bundle
import android.view.WindowManager
import androidx.lifecycle.ViewModelProvider
import muslimcompass.direction.finddirection.base.BaseActivity
import muslimcompass.direction.finddirection.databinding.ActivitySplashBinding
import muslimcompass.direction.finddirection.helper.*

class SplashActivity : BaseActivity() {

    private var isGPSEnabled = false
    private var isNetworkEnabled = false
    private var locationManager: LocationManager? = null
    private lateinit var dialog: DialogUtils
    private lateinit var binding: ActivitySplashBinding
    lateinit var viewModel: SplashViewModel
    lateinit var factory: SplashViewModelFactory
    lateinit var prefs: PrefsUtils

    override fun observeViewModel() {
        // nothing
    }

    override fun initViewBinding() {
        binding = ActivitySplashBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        val view = binding.root
        binding.viewModel = viewModel
        setContentView(view)
    }

    override fun setupViewModel() {
        factory = SplashViewModelFactory(this)
        viewModel = ViewModelProvider(this, factory).get(SplashViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        prefs = PrefsUtils(this)
        LocaleManager.setNewLocale(this, prefs.getFromPrefsWithDefault("locale", "en"))
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        dialog = DialogUtils(this, layoutInflater)
        dialog.showProgressDialog()
        locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager?
    }

    override fun onPause() {
        super.onPause()
        dialog.hideProgressDialog()
    }

    override fun onResume() {
        super.onResume()
        // Getting GPS status

        // Getting GPS status
        isGPSEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
        isNetworkEnabled = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (isGPSEnabled && isNetworkEnabled) {
            dialog.hideProgressDialog()
        } else {
            dialog.hideProgressDialog()
            GeneralUtils.showSettingsAlert(this)
        }
    }
}
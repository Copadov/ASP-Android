package asp.android.asppagos.ui.activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.tech.Ndef
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import asp.android.aspandroidmaterial.ui.layouts.ASPMaterialBottomNavigation
import asp.android.asppagos.R
import asp.android.asppagos.data.models.LoginResponseData
import asp.android.asppagos.data.repositories.AspTrackingRepository
import asp.android.asppagos.data.repositories.AspTrackingRepositoryImpl
import asp.android.asppagos.data.repositories.AspTrackingRepositoryImpl.Companion.APP_BACKGROUND
import asp.android.asppagos.data.repositories.AspTrackingRepositoryImpl.Companion.APP_FOREGROUND
import asp.android.asppagos.data.repositories.AspTrackingRepositoryImpl.Companion.ENTER_CODI
import asp.android.asppagos.data.repositories.AspTrackingRepositoryImpl.Companion.GET_INTERNET_CONNECTION
import asp.android.asppagos.data.repositories.AspTrackingRepositoryImpl.Companion.INACTIVITY_CLOSE_APP
import asp.android.asppagos.data.repositories.AspTrackingRepositoryImpl.Companion.LEAVE_CODI
import asp.android.asppagos.data.repositories.AspTrackingRepositoryImpl.Companion.LOST_INTERNET_CONNECTION
import asp.android.asppagos.databinding.ActivityMainDashboardBinding
import asp.android.asppagos.ui.viewmodels.MainDashboardViewModel
import asp.android.asppagos.utils.IS_USER_LOGIN
import asp.android.asppagos.utils.PROPERTY_ACCOUNT_ENCRIPTED
import asp.android.asppagos.utils.Prefs
import asp.android.asppagos.utils.decryptData
import asp.android.asppagos.utils.fromJson
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.koin.android.ext.android.inject


class MainDashboardActivity : AppCompatActivity(),
    ASPMaterialBottomNavigation.ASPMaterialBottomNavigationListener {

    private var isAppInBackground = false
    private val CHANNEL_ID = "session_closed_channel"
    private val NOTIFICATION_ID = 1

    /**
     * Adapter for NFC functions.
     */
    private var nfcAdapter: NfcAdapter? = null

    /**
     * View Model to handle QR message from NFC.
     */
    private val viewModel: MainDashboardViewModel by viewModel()

    companion object {
        lateinit var bottomNavigation: ASPMaterialBottomNavigation
        lateinit var accountData: LoginResponseData
        var isInitialized: Boolean = false
    }

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainDashboardBinding

    private val aspTrackingRepository by inject<AspTrackingRepository>()

    private val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build()

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            GlobalScope.async {
                try {
                    aspTrackingRepository.inform(
                        eventAction = AspTrackingRepositoryImpl.EventAction.APP_SESSION,
                        ticket = GET_INTERNET_CONNECTION
                    )
                } catch (exception: Exception) {
                    exception.printStackTrace()
                }
            }
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            GlobalScope.async {
                try {
                    aspTrackingRepository.inform(
                        eventAction = AspTrackingRepositoryImpl.EventAction.APP_SESSION,
                        ticket = LOST_INTERNET_CONNECTION
                    )
                } catch (exception: Exception) {
                    exception.printStackTrace()
                }
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        binding = ActivityMainDashboardBinding.inflate(layoutInflater)

        setContentView(binding.root)

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        navController = findNavController(R.id.nav_host_fragment_content_main_dashboard)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)


        binding.let {

            bottomNavigation =
                it.activityMainDashboardContent.contentMain
                    .findViewById(R.id.bottomNavigationDashboard)

            bottomNavigation.setASPMaterialBottomNavigationListener(this)

            if (accountData.cuenta.mostrarTarjetaAPP == 0) {
                bottomNavigation.setCustomValue(0)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Session Closed",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Session has been closed due to inactivity"
                enableLights(true)
                lightColor = Color.BLUE
                // Puedes personalizar aún más las opciones del canal de notificación según tus necesidades
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val connectivityManager =
            getSystemService(ConnectivityManager::class.java) as ConnectivityManager

        connectivityManager.requestNetwork(networkRequest, networkCallback)
    }

    private fun startCounter() {
        Thread {
            Thread.sleep(1000 * 60 * 5) // millis * 60 seg * 5
            if (isAppInBackground) {
                //val intent = Intent(this@MainDashboardActivity, SplashScreen::class.java)
                //startActivity(intent)

                /*val intent = Intent(this@MainDashboardActivity, SplashScreen::class.java)

                val pendingIntent = PendingIntent.getActivity(
                    this@MainDashboardActivity,
                    0,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
                )

                val notificationManager: NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                val notificationBuilder =
                    NotificationCompat.Builder(this@MainDashboardActivity, CHANNEL_ID)
                        .setSmallIcon(R.drawable.asp_banderas_menu_icon)
                        .setContentTitle("Sesión cerrada")
                        .setContentText("Tu sesión se ha cerrado por inactividad")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)

                notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())*/
                GlobalScope.async {
                    Prefs.set(IS_USER_LOGIN, false)
                    try {
                        aspTrackingRepository.close(INACTIVITY_CLOSE_APP)
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                    }
                }
                val intent = Intent(this@MainDashboardActivity, SplashScreen::class.java)
                startActivity(intent)
                finish()
            }
        }.start()
    }

    override fun onPause() {
        super.onPause()
        GlobalScope.async {
            try {
                aspTrackingRepository.inform(
                    eventAction = AspTrackingRepositoryImpl.EventAction.APP_SESSION,
                    ticket = APP_BACKGROUND
                )
            } catch(exception: Exception) {
                exception.printStackTrace()
            }
        }
        isAppInBackground = true
        startCounter()

        disableNfcForegroundDispatch()
    }

    override fun onResume() {
        super.onResume()
        GlobalScope.async {
            try {
                aspTrackingRepository.inform(
                    eventAction = AspTrackingRepositoryImpl.EventAction.APP_SESSION,
                    ticket = APP_FOREGROUND
                )
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }
        isAppInBackground = false

        enableNfcForegroundDispatch()
    }

    override fun onCreateView(
        parent: View?,
        name: String,
        context: Context,
        attrs: AttributeSet
    ): View? {

        val encryptedAccountData: String? = Prefs[PROPERTY_ACCOUNT_ENCRIPTED]

        if (!isInitialized) {
            if (encryptedAccountData != null) {
                accountData =
                    try {
                        Gson().fromJson<LoginResponseData>(decryptData(Prefs[PROPERTY_ACCOUNT_ENCRIPTED]))
                    } finally {
                    }
                if (accountData == null) {
                    Toast.makeText(this, "Error al deserializar los datos", Toast.LENGTH_SHORT)
                        .show()
                    finish()
                } else {
                    isInitialized = true
                }
            } else {
                Toast.makeText(this, "Datos encriptados nulos", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        return super.onCreateView(parent, name, context, attrs)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main_dashboard)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    override fun onClickHome() {
        resetStatesMenu()
    }

    private fun resetStatesMenu() {
        bottomNavigation.resetStates()
        navController.navigateUp()
        safeNavigate(R.id.action_global_dashboardMainFragment)
    }

    @Override
    override fun onBackPressed() {
        this.supportFragmentManager.executePendingTransactions()
        super.onBackPressed()
        resetStatesMenu()
    }

    override fun onClickOption(menuSelected: ASPMaterialBottomNavigation.TypeMenu) {

        var direction = when (menuSelected.ordinal) {
            ASPMaterialBottomNavigation.TypeMenu.CARDS.ordinal -> {
                GlobalScope.async() {
                    aspTrackingRepository.inform(
                        eventAction = AspTrackingRepositoryImpl.EventAction.APP_SESSION,
                        ticket = LEAVE_CODI
                    )
                }
                R.id.action_global_myCardsFragment
            }

            ASPMaterialBottomNavigation.TypeMenu.MOVEMENTS.ordinal -> {
                GlobalScope.async() {
                    try {
                        aspTrackingRepository.inform(
                            eventAction = AspTrackingRepositoryImpl.EventAction.APP_SESSION,
                            ticket = LEAVE_CODI
                        )
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                    }
                }
                R.id.action_global_myMovementsFragment
            }

            ASPMaterialBottomNavigation.TypeMenu.CODI.ordinal -> {
                GlobalScope.async() {
                    try {
                        aspTrackingRepository.inform(
                            eventAction = AspTrackingRepositoryImpl.EventAction.APP_SESSION,
                            ticket = ENTER_CODI
                        )
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                    }
                }
                R.id.action_global_coDiModuleFragment
            }

            ASPMaterialBottomNavigation.TypeMenu.CONFIGURATION.ordinal -> {
                GlobalScope.async() {
                    try {
                        aspTrackingRepository.inform(
                            eventAction = AspTrackingRepositoryImpl.EventAction.APP_SESSION,
                            ticket = LEAVE_CODI
                        )
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                    }
                }
                R.id.action_global_configurationOptionsFragment
            }

            else -> {
                GlobalScope.async() {
                    try {
                        aspTrackingRepository.inform(
                            eventAction = AspTrackingRepositoryImpl.EventAction.APP_SESSION,
                            ticket = LEAVE_CODI
                        )
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                    }
                }
                R.id.action_global_dashboardMainFragment
            }
        }

        navController.navigateUp()
        navController.navigateUp()

        if (menuSelected.ordinal == ASPMaterialBottomNavigation.TypeMenu.CARDS.ordinal
            && accountData.cuenta.mostrarTarjetaAPP != 0
        ) {
            safeNavigate(direction)
        } else if (menuSelected.ordinal != ASPMaterialBottomNavigation.TypeMenu.CARDS.ordinal){
            safeNavigate(direction)
        }
    }

    /**
     * Method to ensure that the destination is known by the current node.
     * Supports both navigating via an Action and directly to a Destination.
     *
     * @param actionId an action id or a destination id to navigate to.
     */
    fun safeNavigate(@IdRes actionId: Int) = with(navController) {
        if (currentDestination?.getAction(actionId) != null || findDestination(actionId) != null) {
            navigate(actionId)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        if (NfcAdapter.ACTION_TECH_DISCOVERED == intent.action) {

            val rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES
            )
            val msgs = mutableListOf<NdefMessage>()
            if (rawMsgs != null) {
                for (i in rawMsgs.indices) {
                    msgs.add(i, rawMsgs[i] as NdefMessage)
                }
                val message = String(msgs[0].records[0].payload)

                // Check if the current destination matches the destination of PagarCodiFragment
                val isFragmentActive = navController.currentDestination?.id == R.id.pagarCodiFragment

                if (isFragmentActive) {
                    viewModel.setNfcMessage(message)
                }
            }
        }
    }

    /**
     * Enable NFC foreground dispatcher.
     * Diable the app to be launched from NFC intent while is in background, to avoid multiple instances.
     */
    private fun enableNfcForegroundDispatch() {
        if (nfcAdapter != null) {
            val pendingIntent = PendingIntent.getActivity(
                this, 0, Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                PendingIntent.FLAG_IMMUTABLE
            )
            val filters = arrayOf(
                IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED)
            )
            val techLists = arrayOf(
                arrayOf(
                    Ndef::class.java.name
                )
            )
            nfcAdapter!!.enableForegroundDispatch(this, pendingIntent, filters, techLists)
        }
    }

    /**
     * Disable NFC foreground dispatcher.
     * Enable the app to be launched from NFC intent while is in background.
     */
    private fun disableNfcForegroundDispatch() {
        nfcAdapter?.disableForegroundDispatch(this)
    }
}
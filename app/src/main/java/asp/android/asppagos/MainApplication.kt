package asp.android.asppagos

import android.app.Application
import android.provider.Settings
import android.util.Log
import asp.android.asppagos.data.modules.createAppModules
import asp.android.asppagos.utils.BDObjectBox
import asp.android.asppagos.utils.DEVICE_ID_UNIQUE_GUID
import asp.android.asppagos.utils.Prefs
import asp.android.asppagos.utils.generateGUID
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        AppCenter.start(
            this,
            getString(R.string.app_center_id), Analytics::class.java, Crashes::class.java
        )

        Prefs.init(this)

        if (Prefs.get(DEVICE_ID_UNIQUE_GUID, "").isEmpty()) {
            Prefs.set(DEVICE_ID_UNIQUE_GUID, String.generateGUID())
        }

        //val appModules = createAppModules()

        stopKoin()
        startKoin{
            androidContext(this@MainApplication)
            //modules(appModules)
        }

        BDObjectBox.init(context = this@MainApplication)
    }

    companion object {
        lateinit var pass: String
        lateinit var phone: String
    }
}
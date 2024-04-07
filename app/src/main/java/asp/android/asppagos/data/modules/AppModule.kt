package asp.android.asppagos.data.modules

import CreditPaymentCodeSecurityViewModel
import android.content.Context
import asp.android.asppagos.BuildConfig
import asp.android.asppagos.R
import asp.android.asppagos.data.interfaces.AspTrackingAPI
import asp.android.asppagos.data.interfaces.BanxicoCodiFavoriteApi
import asp.android.asppagos.data.interfaces.CellphoneRefillAPI
import asp.android.asppagos.data.interfaces.CodiAPI
import asp.android.asppagos.data.interfaces.CodiAspAPI
import asp.android.asppagos.data.interfaces.FavoriteAccountApi
import asp.android.asppagos.data.interfaces.FavoriteCodiAPI
import asp.android.asppagos.data.interfaces.LoginAPI
import asp.android.asppagos.data.interfaces.MainAPI
import asp.android.asppagos.data.interfaces.OcrAPI
import asp.android.asppagos.data.interfaces.OnboardingAPI
import asp.android.asppagos.data.interfaces.PaymentServicesAPI
import asp.android.asppagos.data.interfaces.PinAPI
import asp.android.asppagos.data.interfaces.SaveFavoriteAPI
import asp.android.asppagos.data.interfaces.SpeiAPI
import asp.android.asppagos.data.interfaces.UpdateCodeApi
import asp.android.asppagos.data.models.PendingPaymentPushNotificationDBModel
import asp.android.asppagos.data.repositories.AspTrackingRepository
import asp.android.asppagos.data.repositories.AspTrackingRepositoryImpl
import asp.android.asppagos.data.repositories.CellphoneRefillsRepository
import asp.android.asppagos.data.repositories.CellphoneRefillsRepositoryImpl
import asp.android.asppagos.data.repositories.CheckUserPinRepository
import asp.android.asppagos.data.repositories.CheckUserPinRepositoryImpl
import asp.android.asppagos.data.repositories.CodiAspRepository
import asp.android.asppagos.data.repositories.CodiAspRepositoryImp
import asp.android.asppagos.data.repositories.CodiFavoriteAccountRepository
import asp.android.asppagos.data.repositories.CodiFavoriteAccountRepositoryImpl
import asp.android.asppagos.data.repositories.CodiRepository
import asp.android.asppagos.data.repositories.CodiRepositoryImp
import asp.android.asppagos.data.repositories.FavoriteAccountRepository
import asp.android.asppagos.data.repositories.FavoriteAccountRepositoryImpl
import asp.android.asppagos.data.repositories.FavoriteRepository
import asp.android.asppagos.data.repositories.FavoriteRepositoryImpl
import asp.android.asppagos.data.repositories.LoginRepository
import asp.android.asppagos.data.repositories.LoginRepositoryImpl
import asp.android.asppagos.data.repositories.MainDashboardRepository
import asp.android.asppagos.data.repositories.MainDashboardRepositoryImpl
import asp.android.asppagos.data.repositories.OCRRepository
import asp.android.asppagos.data.repositories.OCRRepositoryImpl
import asp.android.asppagos.data.repositories.OnboardingRepository
import asp.android.asppagos.data.repositories.OnboardingRepositoryImpl
import asp.android.asppagos.data.repositories.PaymentServiceRepository
import asp.android.asppagos.data.repositories.PaymentServicesRepositoryImpl
import asp.android.asppagos.data.repositories.SpeiRepository
import asp.android.asppagos.data.repositories.SpeiRepositoryImpl
import asp.android.asppagos.data.repositories.UpdateProfileRepository
import asp.android.asppagos.data.repositories.UpdateProfileRepositoryImpl
import asp.android.asppagos.network.interceptors.BasicAuthInterceptor
import asp.android.asppagos.network.interceptors.NetworkConnectionInterceptor
import asp.android.asppagos.ui.fragments.main.CoDiModuleFragment
import asp.android.asppagos.ui.fragments.codi.CobrarCodiFragment
import asp.android.asppagos.ui.fragments.codi.CodiConfirmCodeFragment
import asp.android.asppagos.ui.fragments.codi.CodiDevPayFragment
import asp.android.asppagos.ui.fragments.codi.CodiMovMadeFragment
import asp.android.asppagos.ui.fragments.codi.CodiMovMadeViewModel
import asp.android.asppagos.ui.fragments.codi.CodiMovPendingFragment
import asp.android.asppagos.ui.fragments.codi.DetailPagarCodiFragment
import asp.android.asppagos.ui.fragments.codi.GalleryCodiFragment
import asp.android.asppagos.ui.fragments.codi.PagarCodiFragment
import asp.android.asppagos.ui.fragments.codi.PayQrConfirmFragment
import asp.android.asppagos.ui.viewmodels.LoginViewModel
import asp.android.asppagos.ui.viewmodels.MainDashboardViewModel
import asp.android.asppagos.ui.viewmodels.MainSendMoneyViewModel
import asp.android.asppagos.ui.viewmodels.OnboardingViewModel
import asp.android.asppagos.ui.viewmodels.PinInputCodeSecurityViewModel
import asp.android.asppagos.ui.viewmodels.cellphone_refills.CellphoneRefillsAddNumberViewModel
import asp.android.asppagos.ui.viewmodels.cellphone_refills.CellphoneRefillsAmountViewModel
import asp.android.asppagos.ui.viewmodels.cellphone_refills.CellphoneRefillsCodeSecurityViewModel
import asp.android.asppagos.ui.viewmodels.cellphone_refills.CellphoneRefillsViewModel
import asp.android.asppagos.ui.viewmodels.configurations.ConfigurationOptionsViewModel
import asp.android.asppagos.ui.viewmodels.configurations.UpdatePersonalCodeSmsViewModel
import asp.android.asppagos.ui.viewmodels.configurations.UpdatePersonalCodeViewModel
import asp.android.asppagos.ui.viewmodels.credit_payment.CreditPaymentMainViewModel
import asp.android.asppagos.ui.viewmodels.payment_services.PaymentServiceCodeSecurityViewModel
import asp.android.asppagos.ui.viewmodels.payment_services.PaymentServiceInfoViewModel
import asp.android.asppagos.ui.viewmodels.payment_services.PaymentServicesMainViewModel
import asp.android.asppagos.ui.viewmodels.send_money.SendMoneyCodeSecurityViewModel
import asp.android.asppagos.ui.viewmodels.send_money.SendMoneyDetailViewModel
import asp.android.asppagos.ui.viewmodels.send_money.SendMoneyRegisterAccountViewModel
import asp.android.asppagos.utils.BDObjectBox
import asp.android.asppagos.utils.CODI_BASE_URL
import asp.android.asppagos.utils.HTTP_OCRPSS
import asp.android.asppagos.utils.HTTP_OCRUSR
import asp.android.asppagos.utils.HTTP_PASS
import asp.android.asppagos.utils.HTTP_URL
import asp.android.asppagos.utils.HTTP_URL_OCR
import asp.android.asppagos.utils.HTTP_USER
import asp.android.asppagos.utils.Prefs
import asp.android.asppagos.utils.TRACKING_URL
import asp.android.asppagos.utils.decryptData
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.fragment.dsl.fragment
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module
import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.InputStream
import java.security.KeyStore
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import java.time.Duration
import java.util.Collections
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

fun createAppCodiModules(): Module = module {
    var baseUrl = decryptData(Prefs[CODI_BASE_URL])

    if (baseUrl.length >= 2 && baseUrl[0] == '"' && baseUrl[baseUrl.length - 1] == '"') {
        baseUrl = baseUrl.substring(1, baseUrl.length - 1);
    }

    single { createBanxicoFavoriteCodiApi(baseUrl) }
    single { createCodiAPI(baseUrl) }

    // Repositories
    factory<CodiRepository> { CodiRepositoryImp(get()) }
    factory<CodiFavoriteAccountRepository> {
        CodiFavoriteAccountRepositoryImpl(
            get(),
            get(),
            get()
        )
    }

    // Views
    fragment { CoDiModuleFragment() }
    fragment { CodiConfirmCodeFragment() }
    fragment { PagarCodiFragment() }
    fragment { GalleryCodiFragment() }
}

fun createAppModules(): Module = module {

    val OCR_API_URL = Prefs[HTTP_URL_OCR, ""]
    val ASP_API_URL = Prefs[HTTP_URL, ""]
    val ocruser = Prefs[HTTP_OCRUSR, ""]
    val ocrpass = Prefs[HTTP_OCRPSS, ""]
    val aspuser = Prefs[HTTP_USER, ""]
    val asppass = Prefs[HTTP_PASS, ""]

    // Services
    single {
        createWebService<OcrAPI>(
            okHttpClient = createHttpClient(ocruser, ocrpass, get()),
            factory = RxJava2CallAdapterFactory.create(),
            baseUrl = OCR_API_URL
        )
    }

    single {
        createWebServiceRaw<OnboardingAPI>(
            okHttpClient = createCertificateHttpClient(aspuser, asppass, get()),
            factory = RxJava2CallAdapterFactory.create(),
            baseUrl = ASP_API_URL
        )
    }

    single {
        createWebServiceRaw<LoginAPI>(
            okHttpClient = createCertificateHttpClient(aspuser, asppass, get()),
            factory = RxJava2CallAdapterFactory.create(),
            baseUrl = ASP_API_URL
        )
    }

    single {
        createWebServiceRaw<MainAPI>(
            okHttpClient = createCertificateHttpClient(aspuser, asppass, get()),
            factory = RxJava2CallAdapterFactory.create(),
            baseUrl = ASP_API_URL
        )
    }

    single {
        createWebServiceRaw<SaveFavoriteAPI>(
            okHttpClient = createCertificateHttpClient(aspuser, asppass, get()),
            factory = RxJava2CallAdapterFactory.create(),
            baseUrl = ASP_API_URL
        )
    }

    single {
        createWebServiceRaw<SpeiAPI>(
            okHttpClient = createCertificateHttpClient(aspuser, asppass, get()),
            factory = RxJava2CallAdapterFactory.create(),
            baseUrl = ASP_API_URL
        )
    }

    single {
        createWebServiceRaw<PaymentServicesAPI>(
            okHttpClient = createCertificateHttpClient(aspuser, asppass, get()),
            factory = RxJava2CallAdapterFactory.create(),
            baseUrl = ASP_API_URL
        )
    }

    single {
        createWebServiceRaw<CellphoneRefillAPI>(
            okHttpClient = createCertificateHttpClient(aspuser, asppass, get()),
            factory = RxJava2CallAdapterFactory.create(),
            baseUrl = ASP_API_URL
        )
    }

    single {
        createWebServiceRaw<AspTrackingAPI>(
            okHttpClient = createCertificateHttpClient(aspuser, asppass, get()),
            factory = RxJava2CallAdapterFactory.create(),
            baseUrl = BuildConfig.TRACKING_EVENT_URL
        )
    }

    single {
        createWebServiceRaw<FavoriteAccountApi>(
            okHttpClient = createCertificateHttpClient(aspuser, asppass, get()),
            factory = RxJava2CallAdapterFactory.create(),
            baseUrl = ASP_API_URL
        )
    }

    single {
        createWebServiceRaw<PinAPI>(
            okHttpClient = createCertificateHttpClient(aspuser, asppass, get()),
            factory = RxJava2CallAdapterFactory.create(),
            baseUrl = ASP_API_URL
        )
    }

    single {
        createWebServiceRaw<UpdateCodeApi>(
            okHttpClient = createCertificateHttpClient(aspuser, asppass, get()),
            factory = RxJava2CallAdapterFactory.create(),
            baseUrl = ASP_API_URL
        )
    }

    single {
        createWebServiceRaw<FavoriteCodiAPI>(
            okHttpClient = createCertificateHttpClient(aspuser, asppass, get()),
            factory = RxJava2CallAdapterFactory.create(),
            baseUrl = ASP_API_URL
        )
    }

    //single { createBanxicoFavoriteCodiApi() }

    //single { createCodiAPI() }

    single {
        createWebServiceRaw<CodiAspAPI>(
            okHttpClient = createCertificateHttpClient(aspuser, asppass, get()),
            factory = RxJava2CallAdapterFactory.create(),
            baseUrl = ASP_API_URL
        )
    }

    // Repositories
    factory<OnboardingRepository> { OnboardingRepositoryImpl(onboardingAPI = get()) }
    factory<OCRRepository> { OCRRepositoryImpl(ocrapi = get()) }
    factory<LoginRepository> { LoginRepositoryImpl(loginAPI = get()) }
    factory<MainDashboardRepository> { MainDashboardRepositoryImpl(mainAPI = get()) }
    factory<FavoriteRepository> { FavoriteRepositoryImpl(get()) }
    factory<PaymentServiceRepository> { PaymentServicesRepositoryImpl(get()) }
    //factory<CodiRepository> { CodiRepositoryImp(get()) }
    factory<CodiAspRepository> { CodiAspRepositoryImp(get()) }
    factory<SpeiRepository> { SpeiRepositoryImpl(get()) }
    factory<CellphoneRefillsRepository> { CellphoneRefillsRepositoryImpl(get()) }
    factory<CheckUserPinRepository> { CheckUserPinRepositoryImpl(get()) }
    factory<UpdateProfileRepository> { UpdateProfileRepositoryImpl(get(), get()) }
    //factory<CodiFavoriteAccountRepository> { CodiFavoriteAccountRepositoryImpl(get(), get()) }
    factory<FavoriteAccountRepository> { FavoriteAccountRepositoryImpl(get()) }
    factory<AspTrackingRepository> { AspTrackingRepositoryImpl(get()) }

    // View Models
    viewModel { OnboardingViewModel(ocrRepository = get(), onboardingRepository = get()) }
    viewModel { LoginViewModel(loginRepository = get(), aspTrackingRepository = get()) }
    viewModel { MainDashboardViewModel(mainRepository = get()) }

    // Send Money Flow
    viewModel { MainSendMoneyViewModel(get()) }
    viewModel { SendMoneyDetailViewModel() }
    viewModel {
        SendMoneyCodeSecurityViewModel(
            speiRepository = get(),
            checkUserPinRepository = get()
        )
    }
    viewModel { CreditPaymentCodeSecurityViewModel(repository = get(), pinRepository = get()) }
    viewModel { SendMoneyRegisterAccountViewModel(sendMoneyRepository = get()) }
    viewModel { CreditPaymentMainViewModel() }
    viewModel { PaymentServicesMainViewModel(repository = get()) }
    viewModel { PaymentServiceInfoViewModel() }
    viewModel {
        PaymentServiceCodeSecurityViewModel(
            paymentServiceRepository = get(),
            pinRepository = get()
        )
    }
    viewModel { CellphoneRefillsViewModel(cellphoneRefillsRepository = get()) }
    viewModel { CellphoneRefillsAmountViewModel() }
    viewModel { CellphoneRefillsAddNumberViewModel() }
    viewModel { CellphoneRefillsCodeSecurityViewModel(get(), pinRepository = get()) }
    viewModel { UpdatePersonalCodeViewModel() }
    viewModel { UpdatePersonalCodeSmsViewModel(get()) }
    viewModel { ConfigurationOptionsViewModel(get()) }
    viewModel { PinInputCodeSecurityViewModel(pinRepository = get()) }

    // CODI ASP
    fragment { CobrarCodiFragment() }
    fragment { DetailPagarCodiFragment() }
    fragment { PayQrConfirmFragment() }
    fragment { CodiMovPendingFragment() }
    fragment { CodiMovMadeFragment() }
    fragment { CodiDevPayFragment() }

    // ViewModels
    viewModel { CodiMovMadeViewModel(get(), get()) }

    // Database
    single { BDObjectBox.store.boxFor(PendingPaymentPushNotificationDBModel::class.java) }
}

fun createCertificateHttpClient(user: String, pass: String, context: Context): OkHttpClient {
    val cf: CertificateFactory = CertificateFactory.getInstance("X.509")
    val caInput: InputStream =
        context.resources.openRawResource(obtenerTipoCertificado(BuildConfig.FLAVOR))
    val ca: Certificate = cf.generateCertificate(caInput)

    val keyStoreType = KeyStore.getDefaultType()
    val keyStore = KeyStore.getInstance(keyStoreType)
    keyStore.load(null, null)
    keyStore.setCertificateEntry("ca", ca)

    val tmfAlgorithm: String = TrustManagerFactory.getDefaultAlgorithm()
    val tmf: TrustManagerFactory = TrustManagerFactory.getInstance(tmfAlgorithm)
    tmf.init(keyStore)

    val sslContext = SSLContext.getInstance("TLS")
    sslContext.init(null, tmf.trustManagers, null)

    val trustManagers = tmf.trustManagers
    var x509TrustManager: X509TrustManager? = null
    for (trustManager in trustManagers) {
        if (trustManager is X509TrustManager) {
            x509TrustManager = trustManager
            break
        }
    }
    if (x509TrustManager == null) {
        throw IllegalStateException("No X509TrustManager found")
    }
    val client = OkHttpClient.Builder().apply {
        sslSocketFactory(sslContext.socketFactory, x509TrustManager)
        hostnameVerifier { _, _ -> true }
        addInterceptor(BasicAuthInterceptor(user, pass))
        addInterceptor(NetworkConnectionInterceptor(context))
        addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        readTimeout(5 * 60, TimeUnit.SECONDS)
        retryOnConnectionFailure(true)
    }

    return client.addInterceptor {
        val original = it.request()
        val requestBuilder = original.newBuilder()
        val request = requestBuilder.method(original.method, original.body).build()
        return@addInterceptor it.proceed(request)
    }.build()
}

fun createHttpClient(user: String, pass: String, context: Context): OkHttpClient {
    val client = OkHttpClient.Builder()
    client.addInterceptor(BasicAuthInterceptor(user, pass))
    client.addInterceptor(NetworkConnectionInterceptor(context))
    client.readTimeout(5 * 60, TimeUnit.SECONDS)
    client.retryOnConnectionFailure(true)
    return client.addInterceptor {
        val original = it.request()
        val requestBuilder = original.newBuilder()
        val request = requestBuilder.method(original.method, original.body).build()
        return@addInterceptor it.proceed(request)
    }.build()
}

inline fun <reified T> createWebServiceRaw(
    okHttpClient: OkHttpClient,
    factory: RxJava2CallAdapterFactory,
    baseUrl: String
): T {
    val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .addCallAdapterFactory(factory)
        .client(okHttpClient)
        .build()
    return retrofit.create(T::class.java)
}

inline fun <reified T> createWebService(
    okHttpClient: OkHttpClient,
    factory: CallAdapter.Factory, baseUrl: String
): T {
    val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .addCallAdapterFactory(factory)
        .client(okHttpClient)
        .build()
    return retrofit.create(T::class.java)
}

inline fun <reified T> createWebServiceCODI(
    okHttpClient: OkHttpClient,
    factory: CallAdapter.Factory, baseUrl: String
): T {
    val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .addConverterFactory(ScalarsConverterFactory.create())
        .client(okHttpClient)
        .build()
    return retrofit.create(T::class.java)
}


private fun createCodiAPI(baseUrl: String): CodiAPI {
    val interceptor = HttpLoggingInterceptor()
    interceptor.level = HttpLoggingInterceptor.Level.BODY
    val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)//.baseUrl("https://sintp.codi.org.mx/")
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .client(
            OkHttpClient.Builder()
                .callTimeout(Duration.ofMinutes(4L))
                .readTimeout(Duration.ofMinutes(4L))
                .writeTimeout(Duration.ofMinutes(4L))
                .connectionSpecs(Collections.singletonList(ConnectionSpec.COMPATIBLE_TLS))
                .retryOnConnectionFailure(false)
                .connectTimeout(Duration.ofMinutes(4L))
                .addInterceptor(interceptor)
                .build()
        )
        .build()
    return retrofit.create(CodiAPI::class.java)
}

private fun createBanxicoFavoriteCodiApi(baseUrl: String): BanxicoCodiFavoriteApi {
    val interceptor = HttpLoggingInterceptor()
    interceptor.level = HttpLoggingInterceptor.Level.BODY
    val retrofit = Retrofit.Builder()
        //.baseUrl("https://sintb3.codi.org.mx/")
        .baseUrl(baseUrl)//.baseUrl("https://sintp.codi.org.mx/")
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
        .addConverterFactory(ScalarsConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .addConverterFactory(GsonConverterFactory.create())
        .client(
            OkHttpClient.Builder()
                .callTimeout(Duration.ofMinutes(4L))
                .readTimeout(Duration.ofMinutes(4L))
                .writeTimeout(Duration.ofMinutes(4L))
                .connectionSpecs(Collections.singletonList(ConnectionSpec.COMPATIBLE_TLS))
                .retryOnConnectionFailure(false)
                .connectTimeout(Duration.ofMinutes(4L))
                .addInterceptor(interceptor)
                .build()
        )
        .build()
    return retrofit.create(BanxicoCodiFavoriteApi::class.java)
}

fun obtenerTipoCertificado(flavor: String): Int {
    return when (flavor) {
        "ProdBuild" -> R.raw.asp_cert_dev
        "DevBuild" -> R.raw.asp_cert_dev
        else -> throw IllegalArgumentException("Sabor/entorno no válido")
    }
}

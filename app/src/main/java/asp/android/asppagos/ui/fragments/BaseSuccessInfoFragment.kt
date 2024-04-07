package asp.android.asppagos.ui.fragments

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import asp.android.aspandroidmaterial.ui.toolbars.ASPMaterialToolbarMainDashboard
import asp.android.asppagos.R
import asp.android.asppagos.databinding.FragmentSuccessInfoBinding
import com.nmd.screenshot.Screenshot
import java.io.File
import java.io.FileOutputStream
import java.text.DecimalFormat
import java.util.*


abstract class BaseSuccessInfoFragment : BaseFragment(),
    ASPMaterialToolbarMainDashboard.ASPMaterialToolbarMainDashboardListeners {

    private val binding: FragmentSuccessInfoBinding by lazy {
        FragmentSuccessInfoBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupOnClickListener()
    }

    fun setupToolbar(idTitle: Int) {
        binding.toolbar.setTitle(getString(idTitle))
        binding.toolbar.setASPMaterialToolbarsListeners(this)
    }

    override fun onClickBackButton() {
        moveToDashboard()
    }

    private fun setupOnClickListener() {
        binding.apply {
            bContinueSendMoney.setOnClickListener {
                moveToDashboard()
            }
            ivShare.setOnClickListener {
                shareScreenshot()
            }
        }
    }

    private fun moveToDashboard() {
        safeNavigate(
            R.id.dashboardMainFragment,
            args = null,
            navOptions = NavOptions.Builder().apply {
                setLaunchSingleTop(true)
                setPopUpTo(R.id.dashboardMainFragment, inclusive = true)
            }.build()
        )
    }

    fun setInformation(
        amount: Double?,
        beneficiary: String,
        reference: String,
        trackingCode: String,
        isPaymentCreditFlow: Boolean = false,
        isPaymentServiceOperation: Boolean,
        isRechargeCellphone: Boolean = false,
        cellphoneNumber: String? = null
    ) {
        val amo = amount ?: 0.0
        val formater = DecimalFormat("#,##0.00")
        if (isPaymentServiceOperation.not()) {
            binding.apply {
                tvAmountValue.text = getString(R.string.amount, formater.format(amo))
                tvContactValue.text = beneficiary
                tvReferenceValue.text = reference
                tvTrackingCodeValue.text = trackingCode
                tvTrackingCodeLabel.isVisible = !isPaymentCreditFlow
                tvTrackingCodeValue.isVisible = !isPaymentCreditFlow
                trackingCodeDivider.isVisible = !isPaymentCreditFlow
            }
        } else {
            if (isRechargeCellphone.not()) {
                binding.apply {
                    dividerFifthElement.isVisible = true
                    containerFifthElement.isVisible = true

                    // Payment service
                    tvAmountLabel.text = "Pago de servicio"
                    tvAmountValue.text = beneficiary

                    // Reference
                    tvContactLabel.text = "Referencia"
                    tvContactValue.text = trackingCode

                    // Autorization number
                    tvContactLabel.text = "Número de autorización"
                    tvReferenceValue.text = reference

                    // Date
                    tvTrackingCodeLabel.text = "Fecha y Hora"
                    tvTrackingCodeValue.text = getDate()

                    // Amount
                    tvFithLabel.text = "Monto"
                    tvFifthValue.text = getString(R.string.amount, formater.format(amo))
                }
            } else {
                binding.apply {
                    dividerFifthElement.isVisible = true
                    containerFifthElement.isVisible = true

                    // Company
                    tvAmountLabel.text = "Compañía"
                    tvAmountValue.text = beneficiary

                    // Product
                    tvContactLabel.text = "Producto"
                    tvContactValue.text = reference

                    // Autorization number
                    tvReferenceLabel.text = "Número de autorización"
                    tvReferenceValue.text = trackingCode

                    // Cellphone number
                    tvTrackingCodeLabel.text = "Número celular"
                    tvTrackingCodeValue.text = cellphoneNumber

                    // Amount
                    tvFithLabel.text = "Monto"
                    tvFifthValue.text = getString(R.string.amount, formater.format(amo))
                }
            }
        }
    }

    private fun shareScreenshot() {
        binding.scrollView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            val diff: Int =
                binding.scrollView.getChildAt(binding.scrollView.getChildCount() - 1).top - (binding.scrollView.getScrollY())
            if (diff == 0) {
                this.share()
            }
        }
        if(binding.scrollView.scrollY == 0) {
            this.share()
        } else {
            binding.scrollView.fullScroll(ScrollView.FOCUS_UP);
        }
    }

    private fun share() {
        (requireActivity() as AppCompatActivity).apply {
            binding.scrollView.setOnScrollChangeListener(null)
            Screenshot(this).apply {
                fileName = "ASP_screenshot.jpg"
                saveScreenshot = true
                takeScreenshot(object : Screenshot.OnResultListener {

                    override fun result(success: Boolean, bitmap: Bitmap?) {
                        if (success) {
                            bitmap?.let {
                                val shareIntent = Intent(Intent.ACTION_SEND)
                                shareIntent.putExtra(
                                    Intent.EXTRA_STREAM,
                                    bitmap.mapToUri(requireContext())
                                )
                                shareIntent.type = "image/*"
                                shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                requireContext().startActivity(shareIntent)
                            }
                        } else {
                            Log.e(TAG, "ERROR AL COMPARTIR LA IMAGEN")
                        }
                    }
                })
            }
        }
    }

    private fun getDate(): String {
        val date = Calendar.getInstance(Locale.getDefault()).time
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        return simpleDateFormat.format(date)
    }
}

fun Bitmap?.mapToUri(context: Context): Uri? {
    val cache = context.applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val file = File.createTempFile("ASP_voucher_", ".png", cache)
    return try {
        this?.let {
            FileOutputStream(file, true).use {
                this.compress(Bitmap.CompressFormat.PNG, 80, it)
            }
            FileProvider.getUriForFile(
                context,
                context.applicationContext.packageName + ".provider",
                file
            )
        } ?: run {
            return@run null
        }
    } catch (exception: Exception) {
        exception.printStackTrace()
        null
    }
}

fun Bitmap?.mapToUriMedia(context: Context): Uri? {
    return try {
        this?.let {
            val path = MediaStore.Images.Media.insertImage(
                context.contentResolver,
                this, "Comprobante", null
            )
            Uri.parse(path)
        } ?: run {
            return@run null
        }
    } catch (exception: Exception) {
        exception.printStackTrace()
        null
    }
}
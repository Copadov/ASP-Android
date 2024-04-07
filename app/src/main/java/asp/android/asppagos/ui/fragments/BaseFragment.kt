package asp.android.asppagos.ui.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavDestination
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import asp.android.aspandroidmaterial.ui.layouts.ASPMaterialDialogCustom
import asp.android.aspandroidmaterial.ui.layouts.ASPMaterialInfoDialog
import asp.android.aspandroidmaterial.ui.layouts.ASPMaterialLoadingScreen
import asp.android.aspandroidmaterial.ui.layouts.ASPMaterialLoadingScreenOCR
import asp.android.aspandroidmaterial.ui.layouts.ASPMaterialLoadingScreenRegister
import asp.android.aspandroidmaterial.ui.toolbars.ASPTMaterialToolbar
import asp.android.asppagos.data.models.DataItem
import asp.android.asppagos.ui.activities.UserDataEditActivity
import asp.android.asppagos.utils.DATA_MODIFIED_CODE
import asp.android.asppagos.utils.MODIFY_DATA
import asp.android.asppagos.utils.showSingleButtonDialog

abstract class BaseFragment : Fragment(),
    ASPTMaterialToolbar.ASPMaterialToolbarsListeners,
    ASPMaterialInfoDialog.Companion.DialogDismissListener {

    private lateinit var dialogInfo: ASPMaterialInfoDialog
    abstract var TAG: String

    lateinit var dialog: ASPMaterialLoadingScreen
    lateinit var dialogOCR: ASPMaterialLoadingScreenOCR
    lateinit var dialogRegister: ASPMaterialLoadingScreenRegister
    lateinit var customDialog: ASPMaterialDialogCustom

    lateinit var intent: Intent
    val dotsCount: Int = 12

    fun updateItem(dataToEdit: DataItem) {
        val intent = Intent(requireContext(), UserDataEditActivity::class.java)
        intent.putExtra(MODIFY_DATA, dataToEdit)
        startActivityForResult(intent, DATA_MODIFIED_CODE)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog = ASPMaterialLoadingScreen()
        customDialog = ASPMaterialDialogCustom()
        intent = Intent()
    }

    override fun onClickBackPressed() {
        findNavController().popBackStack()
        childFragmentManager.executePendingTransactions()
    }

    override fun onClickWhatsappIcon() {
        /*requireActivity().showSingleButtonDialog(
            "Información",
            "call_center@aspintegraopciones.com\n664 204 1866",
            "Aceptar"
        )*/

        dialogInfo = ASPMaterialInfoDialog.newInstance(this)
        dialogInfo.show(childFragmentManager, ASPMaterialInfoDialog.TAG)
    }

    override fun onDialogDismissed() {
        val dialog = childFragmentManager.findFragmentByTag(ASPMaterialInfoDialog.TAG)
        if (dialog is ASPMaterialInfoDialog) {
            dialog.dismiss()
        }
    }

    /**
     * Method to ensure that the destination is known by the current node.
     * Supports both navigating via an Action and directly to a Destination.
     *
     * @param actionId an action id or a destination id to navigate to.
     */
    fun safeNavigate(@IdRes actionId: Int) = with(findNavController()) {
        if (currentDestination?.getAction(actionId) != null || findDestination(actionId) != null) {
            navigate(actionId)
        }
    }

    /**
     * Method to ensure that the destination is known by the current node.
     * Supports both navigating via an Action and directly to a Destination.
     *
     * @param actionId an action id or a destination id to navigate to.
     * @param args arguments to pass to the destination
     */
    fun safeNavigate(@IdRes actionId: Int, args: Bundle?) = with(findNavController()) {
        if (currentDestination?.getAction(actionId) != null || findDestination(actionId) != null) {
            navigate(actionId, args)
        }
    }

    /**
     * Method to ensure that the destination is known by the current node.
     * Supports both navigating via an Action and directly to a Destination.
     *
     * @param actionId an action id or a destination id to navigate to.
     * @param args arguments to pass to the destination.
     * @param navOptions special options for this navigation operation.
     */
    fun safeNavigate(@IdRes actionId: Int, args: Bundle?, navOptions: NavOptions?) = with(findNavController()) {
        if (currentDestination?.getAction(actionId) != null || findDestination(actionId) != null) {
            navigate(actionId, args, navOptions)
        }
    }

    /**
     * Function to check if gps service is enabled.
     *
     * @return true if gps is enabled, false otherwise.
     */
    fun isLocationEnabled(): Boolean {
        val locationManager = ContextCompat.getSystemService(requireContext(), LocationManager::class.java)
        return locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true
    }

    /**
     * Check if all needed location permissions have been granted.
     * @return True if all permissions are granted, false otherwise.
     */
    fun checkPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    enum class EditorType {
        Address,
        NumExt,
        NumInt,
        Cp,
        Colony,
        City,
        Name,
        LastName,
        SurName,
        BirthDate,
        Genre,
        CURP
    }
}
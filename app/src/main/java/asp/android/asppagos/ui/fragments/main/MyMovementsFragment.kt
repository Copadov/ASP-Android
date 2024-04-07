package asp.android.asppagos.ui.fragments.main

import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import asp.android.aspandroidmaterial.ui.layouts.ASPMaterialLoadingScreen
import asp.android.aspandroidmaterial.ui.layouts.ASPMaterialMovementsFilterDialog
import asp.android.aspandroidmaterial.ui.toolbars.ASPMaterialToolbarMainDashboard
import asp.android.asppagos.R
import asp.android.asppagos.data.models.MovementsQueryResponseData
import asp.android.asppagos.databinding.FragmentMyMovementsBinding
import asp.android.asppagos.ui.adapters.MovementItem
import asp.android.asppagos.ui.adapters.MovementType
import asp.android.asppagos.ui.adapters.MovementsAdapter
import asp.android.asppagos.ui.fragments.BaseFragment
import asp.android.asppagos.ui.viewmodels.MainDashboardViewModel
import asp.android.asppagos.utils.DIALOG_TYPE_FILTER
import asp.android.asppagos.utils.DIALOG_TYPE_MOVEMENTS
import asp.android.asppagos.utils.ServerErrorCodes
import asp.android.asppagos.utils.getCurrentDate
import asp.android.asppagos.utils.getDateMonthsAgo
import asp.android.asppagos.utils.showCustomDialogError
import asp.android.asppagos.utils.showCustomDialogInfo
import asp.android.asppagos.utils.toFormattedDate
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

class MyMovementsFragment : BaseFragment(), ASPMaterialMovementsFilterDialog.FilterDialogListener,
    ASPMaterialToolbarMainDashboard.ASPMaterialToolbarMainDashboardListeners {

    private lateinit var dialogFilter: ASPMaterialMovementsFilterDialog
    private lateinit var dialogMovements: ASPMaterialMovementsFilterDialog
    private lateinit var dialogMovementsQuery: ASPMaterialLoadingScreen
    private lateinit var dialogFilterAccount: ASPMaterialLoadingScreen
    private var _binding: FragmentMyMovementsBinding? = null
    private val binding get() = _binding!!
    val viewModel: MainDashboardViewModel by activityViewModel()
    override var TAG: String = this.javaClass.name

    /**
     * Variable to indicate if is querying all movements or is a filtered query.
     * Used to dismiss the corresponding loading dialog for each case.
     */
    private var isQueryFilter = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyMovementsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialogMovementsQuery = ASPMaterialLoadingScreen()
        dialogFilterAccount = ASPMaterialLoadingScreen()

        binding.let {

            it.myMovementsToolbar.setASPMaterialToolbarsListeners(this)
            it.myMovementsToolbar.setTitle(getString(R.string.movements_resume_title))

            it.filterOption.setOnClickListener {
                callFilterDialog()
            }

            it.movementsResumeText.setOnClickListener {
                callMovementsDialog()
            }

            val text = getString(R.string.my_movements_text_subtitle)
            val content = SpannableString(text)
            content.setSpan(UnderlineSpan(), 0, text.length, 0)

            it.movementsResumeText.text = content
        }
        initViewModel()
    }

    private fun initViewModel() {
        getMovementsQuery(1)

        viewModel.let {
            it.movementsQueryResponseData.observe(viewLifecycleOwner) { movements ->

                dialogMovementsQuery.dismiss()

                setAdapter(movements)
            }

            it.movementsQueryFilterResponseData.observe(viewLifecycleOwner) { movements ->

                dialogFilterAccount.dismiss()

                setAdapter(movements)
            }

            it.movementsFileResponseData.observe(viewLifecycleOwner) { codeResponseData ->

                dialogFilterAccount.dismiss()

                when (codeResponseData.codigo) {
                    ServerErrorCodes.SUCCESS.ordinal -> {
                        showCustomDialogInfo(
                            getString(R.string.information_dialog_text),
                            codeResponseData.mensaje,
                        )
                    }

                    else -> {
                        showCustomDialogError("Información", codeResponseData.mensaje)
                    }
                }
            }


            it.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
                if (isQueryFilter) {
                    dialogFilterAccount.dismiss()
                } else {
                    dialogMovementsQuery.dismiss()
                }
                showCustomDialogError("Información", errorMessage)

            }

            it.infoMessage.observe(viewLifecycleOwner) { infoMessage ->
                if (isQueryFilter) {
                    dialogFilterAccount.dismiss()
                } else {
                    dialogMovementsQuery.dismiss()
                }
                showCustomDialogInfo("Información", infoMessage)
            }
        }
    }

    private fun getMovementsQuery(monthsAgo: Int) {
        isQueryFilter = false
        dialogMovementsQuery.show(requireActivity().supportFragmentManager, "QUERY")
        viewModel.setStartDate("".getDateMonthsAgo(monthsAgo))
        viewModel.setFinishDate("".getCurrentDate())
        viewModel.movementsQuery()
    }

    private fun getMovementsQueryFilter(monthsAgo: Int) {
        isQueryFilter = true
        dialogFilterAccount.show(requireActivity().supportFragmentManager, "FILTER")
        viewModel.setStartDate("".getDateMonthsAgo(monthsAgo))
        viewModel.setFinishDate("".getCurrentDate())
        viewModel.movementsQueryFilter()
    }

    private fun setAdapter(movements: MovementsQueryResponseData?) {
        binding.recyclerViewMovements.adapter = MovementsAdapter(movementsConverter(movements)) {
        }

        binding.recyclerViewMovements.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun movementsConverter(movements: MovementsQueryResponseData?): List<MovementItem> {
        val list = mutableListOf<MovementItem>()

        movements?.forEach { mov ->
            list.add(
                MovementItem(
                    movementtype = when (mov.TipoMovimientoCargoAbono) {
                        "Cargo" -> {
                            MovementType.PAY
                        }

                        else -> {
                            MovementType.REFUND
                        }
                    },
                    titlemovement = mov.conceptoPago,
                    titlesubtitle = mov.TipoMovimientoCargoAbono,
                    description = mov.urlCep ?: "",
                    date = mov.fechaHora.toFormattedDate(),
                    amount = mov.importe
                )
            )
        }

        return list
    }

    override fun onClickBackPressed() {
        super.onClickBackPressed()
        findNavController().popBackStack()
    }

    private fun callMovementsDialog() {
        val previousMonths = getPreviousThreeMonths()

        dialogMovements = ASPMaterialMovementsFilterDialog().newInstance(
            "Solicita tu historial de movimientos",
            "cerrar",
            "Solicitar",
            previousMonths[0],
            previousMonths[1],
            previousMonths[2],
            DIALOG_TYPE_MOVEMENTS
        )

        dialogMovements.setListener(this)
        dialogMovements.show(this.parentFragmentManager, "MOVEMENTS")
    }

    private fun callFilterDialog() {
        dialogFilter = ASPMaterialMovementsFilterDialog().newInstance(
            "Ver por",
            "cerrar",
            "Filtrar",
            "Último mes",
            "Últimos dos meses",
            "Últimos tres meses",
            DIALOG_TYPE_FILTER
        )

        dialogFilter.setListener(this)
        dialogFilter.show(this.parentFragmentManager, "FILTER")
    }

    override fun onFilterSelected(selectedCheckboxes: List<Int>, dialogType: String) {
        if (selectedCheckboxes.isNotEmpty()) {
            when (dialogType) {
                DIALOG_TYPE_FILTER -> {

                    if (selectedCheckboxes.contains(1)) {
                        getMovementsQueryFilter(1)
                    }
                    if (selectedCheckboxes.contains(2)) {
                        getMovementsQueryFilter(2)
                    }
                    if (selectedCheckboxes.contains(3)) {
                        getMovementsQueryFilter(3)
                    }
                }

                DIALOG_TYPE_MOVEMENTS -> {

                    isQueryFilter = true

                    dialogFilterAccount.show(
                        requireActivity().supportFragmentManager,
                        "DIALOG_FILTER"
                    )

                    val fechaInicial = obtenerPrimerDiaMes()
                    val fechaFinal = LocalDate.now()

                    if (selectedCheckboxes.first() == 3) {
                        viewModel.requestMovementsFile(
                            convertirALong(fechaInicial),
                            convertirALong(fechaFinal)
                        )
                    }
                    if (selectedCheckboxes.first() == 2) {
                        viewModel.requestMovementsFile(
                            convertirALong(fechaInicial.minusMonths(1)),
                            convertirALong(obtenerUltimoDiaDelMes(fechaInicial.minusMonths(1)))
                        )
                    }
                    if (selectedCheckboxes.first() == 1) {
                        viewModel.requestMovementsFile(
                            convertirALong(fechaInicial.minusMonths(2)),
                            convertirALong(obtenerUltimoDiaDelMes(fechaInicial.minusMonths(2)))
                        )
                    }
                }

                else -> {
                    // No se ha seleccionado ningún checkbox
                }
            }
        }
    }

    fun obtenerPrimerDiaMes(): LocalDate {
        val fechaActual = LocalDate.now()
        val primerDiaMes = fechaActual.withDayOfMonth(1)
        return primerDiaMes
    }

    fun obtenerUltimoDiaDelMes(fecha: LocalDate): LocalDate {
        return fecha.with(TemporalAdjusters.lastDayOfMonth())
    }

    fun convertirALong(localDate: LocalDate): Long {
        val zonaHoraria = ZoneOffset.UTC
        val fechaHora = localDate.atStartOfDay(zonaHoraria)
        return fechaHora.toEpochSecond()
    }

    fun getPreviousThreeMonths(): List<String> {
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")
        val currentDate = LocalDate.now()
        val monthList = mutableListOf<String>()

        for (i in 2 downTo 0) {
            val previousMonth = currentDate.minusMonths(i.toLong())
            monthList.add(previousMonth.format(formatter))
        }

        return monthList
    }

    override fun onClickBackButton() {
        findNavController().popBackStack()
    }
}
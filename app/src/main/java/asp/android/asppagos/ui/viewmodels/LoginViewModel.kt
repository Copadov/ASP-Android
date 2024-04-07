package asp.android.asppagos.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import asp.android.aspandroidcore.utils.SingleLiveEvent
import asp.android.asppagos.data.models.CodeResponseData
import asp.android.asppagos.data.models.CodeValidateRequestData
import asp.android.asppagos.data.models.LoginEncriptedData
import asp.android.asppagos.data.models.LoginRequestData
import asp.android.asppagos.data.models.RecoverPassRequestData
import asp.android.asppagos.data.models.Safety
import asp.android.asppagos.data.models.SendCodeRequestData
import asp.android.asppagos.data.repositories.AspTrackingRepository
import asp.android.asppagos.data.repositories.AspTrackingRepositoryImpl
import asp.android.asppagos.data.repositories.AspTrackingRepositoryImpl.Companion.LOGIN_EVENT
import asp.android.asppagos.data.repositories.LoginRepository
import asp.android.asppagos.data.usecases.UseCaseResult
import asp.android.asppagos.utils.DEVICE_ID_UNIQUE_GUID
import asp.android.asppagos.utils.EncryptUtils.encryptByKeyPass
import asp.android.asppagos.utils.IS_USER_LOGIN
import asp.android.asppagos.utils.PROPERTY_FINGER_TOKEN
import asp.android.asppagos.utils.Prefs
import asp.android.asppagos.utils.SingletonPassword
import asp.android.asppagos.utils.encriptData
import asp.android.asppagos.utils.encriptDataLocal
import asp.android.asppagos.utils.encriptDataWithRecoverKey
import asp.android.asppagos.utils.encriptPassword
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class LoginViewModel(
    private val loginRepository: LoginRepository,
    private val aspTrackingRepository: AspTrackingRepository
) : ViewModel(), CoroutineScope {

    private val job = Job()

    override val coroutineContext: CoroutineContext = Dispatchers.Main + job

    val successResponse = SingleLiveEvent<CodeResponseData>()
    val sendCodeResponse = SingleLiveEvent<CodeResponseData>()
    val recoverPassResponse = SingleLiveEvent<CodeResponseData>()
    val loginDataResponse = SingleLiveEvent<CodeResponseData>()
    val loginResponse = SingleLiveEvent<CodeResponseData>()

    val errorMessage = SingleLiveEvent<String>()

    private val _email = MutableLiveData<String>()
    val email: LiveData<String>
        get() = _email

    private val _password = MutableLiveData<String>()
    val password: LiveData<String>
        get() = _password

    private val _phone = MutableLiveData<String>()
    val phone: LiveData<String>
        get() = _phone

    private val _token = MutableLiveData<String>()
    val token: LiveData<String>
        get() = _token

    fun setToken(token: String) {
        _token.value = token
    }

    fun setPhone(phone: String) {
        _phone.value = phone
    }

    fun setEmail(email: String) {
        _email.value = email
    }

    fun setPassword(password: String) {
        _password.value = password
        SingletonPassword.saveSessionPassword(password)
    }

    fun verifiedPhone() {
        launch {
            when (val result = withContext(Dispatchers.IO) {
                loginRepository.verifiedPhone(
                    encriptDataWithRecoverKey(SendCodeRequestData(phone.value!!))
                )
            }) {
                is UseCaseResult.Success -> {
                    successResponse.postValue(result.data)
                }

                is UseCaseResult.Error -> errorMessage.postValue(result.exception.message)
            }
        }
    }

    fun recoverPass() {
        launch {
            when (val result = withContext(Dispatchers.IO) {
                loginRepository.recoverPass(
                    encriptDataWithRecoverKey(
                        RecoverPassRequestData(
                            correo = email.value!!,
                            telefono = phone.value!!
                        )
                    )
                )
            }) {
                is UseCaseResult.Success -> {
                    recoverPassResponse.postValue(result.data)
                }

                is UseCaseResult.Error -> errorMessage.postValue(result.exception.message)
            }
        }
    }

    fun validatePhone() {
        launch {
            when (val result =
                withContext(Dispatchers.IO) {
                    loginRepository.validatePhone(
                        encriptData(
                            CodeValidateRequestData(
                                phone.value!!, token.value!!,
                                2,
                                "",
                                Prefs.get(DEVICE_ID_UNIQUE_GUID, "")
                            )
                        )
                    )
                }) {
                is UseCaseResult.Success -> {
                    successResponse.postValue(result.data)
                }

                is UseCaseResult.Error -> errorMessage.postValue(result.exception.message)
            }
        }
    }

    fun sendCode() {
        launch {
            when (val result = withContext(Dispatchers.IO) {
                loginRepository.sendCode(
                    encriptData(SendCodeRequestData(phone.value!!))
                )
            }) {
                is UseCaseResult.Success -> {
                    sendCodeResponse.postValue(result.data)
                }

                is UseCaseResult.Error -> errorMessage.postValue(result.exception.message)
            }
        }
    }

    fun login() {
        launch {
            when (val result = withContext(Dispatchers.IO) {
                loginRepository.loginV2(
                    encriptData(
                        LoginRequestData(
                            telefono = phone.value!!, data = encriptDataLocal(
                                LoginEncriptedData(
                                    idCanal = 1,
                                    password = encriptPassword(password.value!!),
                                    safety = Safety(
                                        mResult = "eyJhbGciOiJSUzI1NiIsIng1YyI6WyJNSUlGYkRDQ0JGU2dBd0lCQWdJUWVXM2IyVVlEZ1A4UVZIWjVPUHpQQ0RBTkJna3Foa2lHOXcwQkFRc0ZBREJHTVFzd0NRWURWUVFHRXdKVlV6RWlNQ0FHQTFVRUNoTVpSMjl2WjJ4bElGUnlkWE4wSUZObGNuWnBZMlZ6SUV4TVF6RVRNQkVHQTFVRUF4TUtSMVJUSUVOQklERkVOREFlRncweU1qRXdNamN3TmpNME5EZGFGdzB5TXpBeE1qVXdOak0wTkRaYU1CMHhHekFaQmdOVkJBTVRFbUYwZEdWemRDNWhibVJ5YjJsa0xtTnZiVENDQVNJd0RRWUpLb1pJaHZjTkFRRUJCUUFEZ2dFUEFEQ0NBUW9DZ2dFQkFMWnFzcldZNHV0Sk9DN2NPZlVTeDN1Rms1aEY5WWhNTFk3WlJzQjhOcFJMZExVMElLRTB5N0hURnl2V3NzMjczMVdJNUJYYUQyUldSQXc3UEdqK0Evd283K0prUnpiWXBtU3FscWFoZFI5bGNCcjJ1azVtWGhsRWdaTUhyL1prQ3RBdGFsYysyMFJZYUlRbUhKUVFoNG5EKy9nays1cW52dmxmNWhNQ3liOXFEVXdyYzlyV2JCUVRmVUdVb28rWWNJVExCUHlEb0ZSaWh1UU1iNlJmZW1DaU9ENEhXZE9KeFphMXRXNDRvL3NjaWQwOVVHS3IvYWJuRStlckZSbjhQd2ZLaGRrbThWV1FESTl6Z1FVaTNTZWdCdEFQY0tSV1pBTHN5T1lwcHlqbzFlR1k4Z1pxdmw3R0hoR2R6Y291MXZFejNSaXpzNGpDMElPdXFiQXhGZk1DQXdFQUFhT0NBbjB3Z2dKNU1BNEdBMVVkRHdFQi93UUVBd0lGb0RBVEJnTlZIU1VFRERBS0JnZ3JCZ0VGQlFjREFUQU1CZ05WSFJNQkFmOEVBakFBTUIwR0ExVWREZ1FXQkJTNm5Kcm1LQXZjeWdPODJpYk5qQkxMQnVSU0NqQWZCZ05WSFNNRUdEQVdnQlFsNGhnT3NsZVJsQ3JsMUYyR2tJUGVVN080a2pCN0JnZ3JCZ0VGQlFjQkFRUnZNRzB3T0FZSUt3WUJCUVVITUFHR0xHaDBkSEE2THk5dlkzTndMbkJyYVM1bmIyOW5MM012WjNSek1XUTBhVzUwTDE4Mk1YVTNlVnBMY21Kek1ERUdDQ3NHQVFVRkJ6QUNoaVZvZEhSd09pOHZjR3RwTG1kdmIyY3ZjbVZ3Ynk5alpYSjBjeTluZEhNeFpEUXVaR1Z5TUIwR0ExVWRFUVFXTUJTQ0VtRjBkR1Z6ZEM1aGJtUnliMmxrTG1OdmJUQWhCZ05WSFNBRUdqQVlNQWdHQm1lQkRBRUNBVEFNQmdvckJnRUVBZFo1QWdVRE1EOEdBMVVkSHdRNE1EWXdOS0F5b0RDR0xtaDBkSEE2THk5amNteHpMbkJyYVM1bmIyOW5MMmQwY3pGa05HbHVkQzg0YlhWaVVubzFNbU5rVlM1amNtd3dnZ0VDQmdvckJnRUVBZFo1QWdRQ0JJSHpCSUh3QU80QWRRRG9QdERhUHZVR05UTG5WeWk4aVd2SkE5UEwwUkZyN090cDRYZDliUWE5YmdBQUFZUVlYWGd3QUFBRUF3QkdNRVFDSUZldDlDQ0Q0b0F1Y0kxbkVGb1Y5VlNpdmdYQnpyTEtkUklzSjdocVdlQjJBaUJsWWtLTXRnYlhNRnNtR05renhIQjR5NEVBYzRXYlZrNEhZeUxkZWtySWFRQjFBTGMrK3lUZm5FMjZkZkk1eGJwWTlHeGQvRUxQZXA4MXhKNGRDWUVsN2JTWkFBQUJoQmhkZUQ0QUFBUURBRVl3UkFJZ1l0bVh1azlqK1BLRk9JTDd0WG1PTU1YV0xBMkR4TmFFbkRRT2w3cFZhZTRDSUJrdlJKZzNqNXdoa1c5b1FVRG1McllWRHFLVzJzYkk5RlY5VGdOUFA3ck1NQTBHQ1NxR1NJYjNEUUVCQ3dVQUE0SUJBUUF4aTVLVDN3WFl4WFFLdnM5T2loaSt6cXVMZzlNKy8xMXJ1aWIyMmtPNEVYZHFnNnBxamt5UDhqWEZkVkdDSDB6clJCZHZ1emV0YW5vMGpEZTRDUzJzN1g0SEY1UEpMbXc5SUdWUUxmRWlNNTV1R0dqMUduRkJVMFA1TzI3aW4yeEdCclJ5K29uTHhIVDVkR0lxMTNBdkllYW1XVnhMSzJVSkk0dTEzZFNJSjNEVXBHcEtINWYxdTJEdnp5Y1VOY1F2U1NUTzJyQnBlMjJUY2NBbHJZVjlsc3FlL3dkeGZUU3Y5VUFOQTZCSGQ4eEFvRTBOS0dKVmxsZGM0T3NVUzdSY2dURTRZQSs4LzY2OFFmWmlPUjRVa05vanpYekhJVFBMaXlBQ2ZtTmM3bjVPZTNQR1dkRkhMdkdkV2k4Nmp4R0ZnTEc5SGR5R2JKNHdKSWhRWHJTYyIsIk1JSUZqRENDQTNTZ0F3SUJBZ0lOQWdDT3NnSXpObVdMWk0zYm16QU5CZ2txaGtpRzl3MEJBUXNGQURCSE1Rc3dDUVlEVlFRR0V3SlZVekVpTUNBR0ExVUVDaE1aUjI5dloyeGxJRlJ5ZFhOMElGTmxjblpwWTJWeklFeE1RekVVTUJJR0ExVUVBeE1MUjFSVElGSnZiM1FnVWpFd0hoY05NakF3T0RFek1EQXdNRFF5V2hjTk1qY3dPVE13TURBd01EUXlXakJHTVFzd0NRWURWUVFHRXdKVlV6RWlNQ0FHQTFVRUNoTVpSMjl2WjJ4bElGUnlkWE4wSUZObGNuWnBZMlZ6SUV4TVF6RVRNQkVHQTFVRUF4TUtSMVJUSUVOQklERkVORENDQVNJd0RRWUpLb1pJaHZjTkFRRUJCUUFEZ2dFUEFEQ0NBUW9DZ2dFQkFLdkFxcVBDRTI3bDB3OXpDOGRUUElFODliQSt4VG1EYUc3eTdWZlE0YyttT1dobFVlYlVRcEsweXYycjY3OFJKRXhLMEhXRGplcStuTElITjFFbTVqNnJBUlppeG15UlNqaElSMEtPUVBHQk1VbGRzYXp0SUlKN08wZy84MnFqL3ZHRGwvLzN0NHRUcXhpUmhMUW5UTFhKZGVCKzJEaGtkVTZJSWd4NndON0U1TmNVSDNSY3NlamNxajhwNVNqMTl2Qm02aTFGaHFMR3ltaE1Gcm9XVlVHTzN4dElIOTFkc2d5NGVGS2NmS1ZMV0szbzIxOTBRMExtL1NpS21MYlJKNUF1NHkxZXVGSm0ySk05ZUI4NEZrcWEzaXZyWFdVZVZ0eWUwQ1FkS3ZzWTJGa2F6dnh0eHZ1c0xKekxXWUhrNTV6Y1JBYWNEQTJTZUV0QmJRZkQxcXNDQXdFQUFhT0NBWFl3Z2dGeU1BNEdBMVVkRHdFQi93UUVBd0lCaGpBZEJnTlZIU1VFRmpBVUJnZ3JCZ0VGQlFjREFRWUlLd1lCQlFVSEF3SXdFZ1lEVlIwVEFRSC9CQWd3QmdFQi93SUJBREFkQmdOVkhRNEVGZ1FVSmVJWURySlhrWlFxNWRSZGhwQ0QzbE96dUpJd0h3WURWUjBqQkJnd0ZvQVU1SzhySm5FYUswZ25oUzlTWml6djhJa1RjVDR3YUFZSUt3WUJCUVVIQVFFRVhEQmFNQ1lHQ0NzR0FRVUZCekFCaGhwb2RIUndPaTh2YjJOemNDNXdhMmt1WjI5dlp5OW5kSE55TVRBd0JnZ3JCZ0VGQlFjd0FvWWthSFIwY0RvdkwzQnJhUzVuYjI5bkwzSmxjRzh2WTJWeWRITXZaM1J6Y2pFdVpHVnlNRFFHQTFVZEh3UXRNQ3N3S2FBbm9DV0dJMmgwZEhBNkx5OWpjbXd1Y0d0cExtZHZiMmN2WjNSemNqRXZaM1J6Y2pFdVkzSnNNRTBHQTFVZElBUkdNRVF3Q0FZR1o0RU1BUUlCTURnR0Npc0dBUVFCMW5rQ0JRTXdLakFvQmdnckJnRUZCUWNDQVJZY2FIUjBjSE02THk5d2Eya3VaMjl2Wnk5eVpYQnZjMmwwYjNKNUx6QU5CZ2txaGtpRzl3MEJBUXNGQUFPQ0FnRUFJVlRveTI0andYVXIwckFQYzkyNHZ1U1ZiS1F1WXczbkxmbExmTGg1QVlXRWVWbC9EdTE4UUFXVU1kY0o2by9xRlpiaFhrQkgwUE5jdzk3dGhhZjJCZW9EWVk5Q2svYitVR2x1aHgwNnpkNEVCZjdIOVA4NG5ucndwUis0R0JEWksrWGgzSTB0cUp5MnJnT3FORGZscjVJTVE4WlRXQTN5bHRha3pTQktaNlhwRjBQcHF5Q1J2cC9OQ0d2MktYMlR1UENKdnNjcDEvbTJwVlR0eUJqWVBSUStRdUNRR0FKS2p0TjdSNURGcmZUcU1XdllnVmxwQ0pCa3dsdTcrN0tZM2NUSWZ6RTdjbUFMc2tNS05MdUR6K1J6Q2NzWVRzVmFVN1ZwM3hMNjBPWWhxRmt1QU9PeERaNnBIT2o5K09KbVlnUG1PVDRYMys3TDUxZlhKeVJIOUtmTFJQNm5UMzFENW5tc0dBT2daMjYvOFQ5aHNCVzF1bzlqdTVmWkxaWFZWUzVIMEh5SUJNRUt5R01JUGhGV3JsdC9oRlMyOE4xemFLSTBaQkdEM2dZZ0RMYmlEVDlmR1hzdHBrK0ZtYzRvbFZsV1B6WGU4MXZkb0VuRmJyNU0yNzJIZGdKV28rV2hUOUJZTTBKaSt3ZFZtblJmZlhnbG9Fb2x1VE5jV3pjNDFkRnBnSnU4ZkYzTEcwZ2wyaWJTWWlDaTlhNmh2VTBUcHBqSnlJV1hoa0pUY01KbFByV3gxVnl0RVVHclgybDBKRHdSalcvNjU2cjBLVkIwMnhIUkt2bTJaS0kwM1RnbExJcG1WQ0sza0JLa0tOcEJOa0Z0OHJoYWZjQ0tPYjlKeC85dHBORmxRVGw3QjM5ckpsSldrUjE3UW5acVZwdEZlUEZPUm9abUZ6TT0iLCJNSUlGWWpDQ0JFcWdBd0lCQWdJUWQ3ME5iTnMyK1JycUlRL0U4RmpURFRBTkJna3Foa2lHOXcwQkFRc0ZBREJYTVFzd0NRWURWUVFHRXdKQ1JURVpNQmNHQTFVRUNoTVFSMnh2WW1Gc1UybG5iaUJ1ZGkxellURVFNQTRHQTFVRUN4TUhVbTl2ZENCRFFURWJNQmtHQTFVRUF4TVNSMnh2WW1Gc1UybG5iaUJTYjI5MElFTkJNQjRYRFRJd01EWXhPVEF3TURBME1sb1hEVEk0TURFeU9EQXdNREEwTWxvd1J6RUxNQWtHQTFVRUJoTUNWVk14SWpBZ0JnTlZCQW9UR1VkdmIyZHNaU0JVY25WemRDQlRaWEoyYVdObGN5Qk1URU14RkRBU0JnTlZCQU1UQzBkVVV5QlNiMjkwSUZJeE1JSUNJakFOQmdrcWhraUc5dzBCQVFFRkFBT0NBZzhBTUlJQ0NnS0NBZ0VBdGhFQ2l4N2pvWGViTzl5L2xENjNsYWRBUEtIOWd2bDlNZ2FDY2ZiMmpILzc2TnU4YWk2WGw2T01TL2tyOXJINXpvUWRzZm5GbDk3dnVmS2o2YndTaVY2bnFsS3IrQ01ueTZTeG5HUGIxNWwrOEFwZTYyaW05TVphUncxTkVEUGpUckVUbzhnWWJFdnMvQW1RMzUxa0tTVWpCNkcwMGowdVlPRFAwZ21IdTgxSThFM0N3bnFJaXJ1Nnoxa1oxcStQc0Fld25qSHhnc0hBM3k2bWJXd1pEclhZZmlZYVJRTTlzSG1rbENpdEQzOG01YWdJL3Bib1BHaVVVKzZET29nckZaWUpzdUI2akM1MTFwenJwMVprajVaUGFLNDlsOEtFajhDOFFNQUxYTDMyaDdNMWJLd1lVSCtFNEV6Tmt0TWc2VE84VXBtdk1yVXBzeVVxdEVqNWN1SEtaUGZtZ2hDTjZKM0Npb2o2T0dhSy9HUDVBZmw0L1h0Y2QvcDJoL3JzMzdFT2VaVlh0TDBtNzlZQjBlc1dDcnVPQzdYRnhZcFZxOU9zNnBGTEtjd1pwRElsVGlyeFpVVFFBczZxemttMDZwOThnN0JBZStkRHE2ZHNvNDk5aVlINlRLWC8xWTdEemt2Z3RkaXpqa1hQZHNEdFFDdjlVdyt3cDlVN0RiR0tvZ1BlTWEzTWQrcHZlejdXMzVFaUV1YSsrdGd5L0JCakZGRnkzbDNXRnBPOUtXZ3o3enBtN0FlS0p0OFQxMWRsZUNmZVhra1VBS0lBZjVxb0liYXBzWld3cGJrTkZoSGF4MnhJUEVEZ2ZnMWF6Vlk4MFpjRnVjdEw3VGxMbk1RLzBsVVRiaVN3MW5INjlNRzZ6TzBiOWY2QlFkZ0FtRDA2eUs1Nm1EY1lCWlVDQXdFQUFhT0NBVGd3Z2dFME1BNEdBMVVkRHdFQi93UUVBd0lCaGpBUEJnTlZIUk1CQWY4RUJUQURBUUgvTUIwR0ExVWREZ1FXQkJUa3J5c21jUm9yU0NlRkwxSm1MTy93aVJOeFBqQWZCZ05WSFNNRUdEQVdnQlJnZTJZYVJRMlh5b2xRTDMwRXpUU28vL3o5U3pCZ0JnZ3JCZ0VGQlFjQkFRUlVNRkl3SlFZSUt3WUJCUVVITUFHR0dXaDBkSEE2THk5dlkzTndMbkJyYVM1bmIyOW5MMmR6Y2pFd0tRWUlLd1lCQlFVSE1BS0dIV2gwZEhBNkx5OXdhMmt1WjI5dlp5OW5jM0l4TDJkemNqRXVZM0owTURJR0ExVWRId1FyTUNrd0o2QWxvQ09HSVdoMGRIQTZMeTlqY213dWNHdHBMbWR2YjJjdlozTnlNUzluYzNJeExtTnliREE3QmdOVkhTQUVOREF5TUFnR0JtZUJEQUVDQVRBSUJnWm5nUXdCQWdJd0RRWUxLd1lCQkFIV2VRSUZBd0l3RFFZTEt3WUJCQUhXZVFJRkF3TXdEUVlKS29aSWh2Y05BUUVMQlFBRGdnRUJBRFNrSHJFb285QzBkaGVtTVhvaDZkRlNQc2piZEJaQmlMZzlOUjN0NVArVDRWeGZxN3ZxZk0vYjVBM1JpMWZ5Sm05YnZoZEdhSlEzYjJ0NnlNQVlOL29sVWF6c2FMK3l5RW45V3ByS0FTT3NoSUFyQW95WmwrdEphb3gxMThmZXNzbVhuMWhJVnc0MW9lUWExdjF2ZzRGdjc0elBsNi9BaFNydzlVNXBDWkV0NFdpNHdTdHo2ZFRaL0NMQU54OExaaDFKN1FKVmoyZmhNdGZUSnI5dzR6MzBaMjA5Zk9VMGlPTXkrcWR1Qm1wdnZZdVI3aFpMNkR1cHN6Zm53MFNrZnRoczE4ZEc5WktiNTlVaHZtYVNHWlJWYk5RcHNnM0JabHZpZDBsSUtPMmQxeG96Y2xPemdqWFBZb3ZKSkl1bHR6a011MzRxUWI5U3oveWlscmJDZ2o4PSJdfQ.eyJub25jZSI6IjBLQWpRQ2lJR3d1aHlwdU9jK2FvdjV6cmZocyszdlBLVTJGbVpYUjVNVFkzTWprMk9UZzNOVEk1T1E9PSIsInRpbWVzdGFtcE1zIjoxNjcyOTY5ODgyMjY5LCJhcGtQYWNrYWdlTmFtZSI6ImNvbS5jb2RpLmFzcGludGVncmFvcGNpb25lcy5hc3Bjb2RpIiwiYXBrRGlnZXN0U2hhMjU2IjoiVlc1K1NLa3hqZXMvcDdHR2NwaEUyTGxhaUhacnNvYjU3S1d0N2ZwUmNVaz0iLCJjdHNQcm9maWxlTWF0Y2giOnRydWUsImFwa0NlcnRpZmljYXRlRGlnZXN0U2hhMjU2IjpbIlU3Z21ydjFzU3NQSFdhREQySVpRTkZIeUFJYlErLytzbVI0Z3hFQnV0M2s9Il0sImJhc2ljSW50ZWdyaXR5Ijp0cnVlLCJldmFsdWF0aW9uVHlwZSI6IkJBU0lDLEhBUkRXQVJFX0JBQ0tFRCJ9.inACFBKeVAVaYUOWlc07TIXXGdwH9K8en-nb48mVnjUbBUnBAcWiiVwQa7D4_8Fm3FRmWWGJZRU6E5X9ATIfweTLkJf3DkXlIDhnSKVOirGg3oAuQTu8MBCLKylIDWKZID2mmZ40mgm9U_LBx_i1N-xnzBMXfgu803Zro4LEP8Ot5XfSop_PeGkByeL01osBXmSwtN0nJzlor0fQ_tFQytMWf18ntz7eWs78IP6NaX5ljv6snP_2R48SB5Ow8Hpg5oqvPk_EN8NGq8hVvUmRh0honXdmFfCqiciW23t_QEtQYD2pm55tkPGaWwIisr2Q65DcXaznJaxus_htxbs5QA",
                                        nonce = "Safety1672969875299"
                                    ),
                                    token = "",
                                    versionApp = "1.0.15",
                                    dispositivoId = Prefs.get(DEVICE_ID_UNIQUE_GUID, "")
                                ), phone.value!!, encriptPassword(password.value!!)
                            ), idCanal = 1
                        )
                    )
                )
            }) {
                is UseCaseResult.Success -> {

                    if (result.data.data.isNotEmpty()) {
                        Prefs.set(
                            "keysourcepass",
                            encriptPassword(password.value!!).encryptByKeyPass()
                        )
                        loginDataResponse.postValue(
                            result.data
                        )
                    }

                    // Login app
                    Prefs.set(IS_USER_LOGIN, true)
                    try {
                        aspTrackingRepository.inform(
                            eventAction = AspTrackingRepositoryImpl.EventAction.APP_SESSION,
                            ticket = LOGIN_EVENT
                        )
                    } catch(exception: Exception) {
                        exception.printStackTrace()
                    }
                    successResponse.postValue(result.data)
                }

                is UseCaseResult.Error -> errorMessage.postValue(result.exception.message)
            }
        }
    }

    fun loginWithBiometrics() {
        launch {
            when (val result = withContext(Dispatchers.IO) {
                loginRepository.loginV2(
                    encriptData(
                        LoginRequestData(
                            telefono = phone.value!!, data = encriptDataLocal(
                                LoginEncriptedData(
                                    idCanal = 1,
                                    password = "",
                                    safety = Safety(
                                        mResult = "eyJhbGciOiJSUzI1NiIsIng1YyI6WyJNSUlGYkRDQ0JGU2dBd0lCQWdJUWVXM2IyVVlEZ1A4UVZIWjVPUHpQQ0RBTkJna3Foa2lHOXcwQkFRc0ZBREJHTVFzd0NRWURWUVFHRXdKVlV6RWlNQ0FHQTFVRUNoTVpSMjl2WjJ4bElGUnlkWE4wSUZObGNuWnBZMlZ6SUV4TVF6RVRNQkVHQTFVRUF4TUtSMVJUSUVOQklERkVOREFlRncweU1qRXdNamN3TmpNME5EZGFGdzB5TXpBeE1qVXdOak0wTkRaYU1CMHhHekFaQmdOVkJBTVRFbUYwZEdWemRDNWhibVJ5YjJsa0xtTnZiVENDQVNJd0RRWUpLb1pJaHZjTkFRRUJCUUFEZ2dFUEFEQ0NBUW9DZ2dFQkFMWnFzcldZNHV0Sk9DN2NPZlVTeDN1Rms1aEY5WWhNTFk3WlJzQjhOcFJMZExVMElLRTB5N0hURnl2V3NzMjczMVdJNUJYYUQyUldSQXc3UEdqK0Evd283K0prUnpiWXBtU3FscWFoZFI5bGNCcjJ1azVtWGhsRWdaTUhyL1prQ3RBdGFsYysyMFJZYUlRbUhKUVFoNG5EKy9nays1cW52dmxmNWhNQ3liOXFEVXdyYzlyV2JCUVRmVUdVb28rWWNJVExCUHlEb0ZSaWh1UU1iNlJmZW1DaU9ENEhXZE9KeFphMXRXNDRvL3NjaWQwOVVHS3IvYWJuRStlckZSbjhQd2ZLaGRrbThWV1FESTl6Z1FVaTNTZWdCdEFQY0tSV1pBTHN5T1lwcHlqbzFlR1k4Z1pxdmw3R0hoR2R6Y291MXZFejNSaXpzNGpDMElPdXFiQXhGZk1DQXdFQUFhT0NBbjB3Z2dKNU1BNEdBMVVkRHdFQi93UUVBd0lGb0RBVEJnTlZIU1VFRERBS0JnZ3JCZ0VGQlFjREFUQU1CZ05WSFJNQkFmOEVBakFBTUIwR0ExVWREZ1FXQkJTNm5Kcm1LQXZjeWdPODJpYk5qQkxMQnVSU0NqQWZCZ05WSFNNRUdEQVdnQlFsNGhnT3NsZVJsQ3JsMUYyR2tJUGVVN080a2pCN0JnZ3JCZ0VGQlFjQkFRUnZNRzB3T0FZSUt3WUJCUVVITUFHR0xHaDBkSEE2THk5dlkzTndMbkJyYVM1bmIyOW5MM012WjNSek1XUTBhVzUwTDE4Mk1YVTNlVnBMY21Kek1ERUdDQ3NHQVFVRkJ6QUNoaVZvZEhSd09pOHZjR3RwTG1kdmIyY3ZjbVZ3Ynk5alpYSjBjeTluZEhNeFpEUXVaR1Z5TUIwR0ExVWRFUVFXTUJTQ0VtRjBkR1Z6ZEM1aGJtUnliMmxrTG1OdmJUQWhCZ05WSFNBRUdqQVlNQWdHQm1lQkRBRUNBVEFNQmdvckJnRUVBZFo1QWdVRE1EOEdBMVVkSHdRNE1EWXdOS0F5b0RDR0xtaDBkSEE2THk5amNteHpMbkJyYVM1bmIyOW5MMmQwY3pGa05HbHVkQzg0YlhWaVVubzFNbU5rVlM1amNtd3dnZ0VDQmdvckJnRUVBZFo1QWdRQ0JJSHpCSUh3QU80QWRRRG9QdERhUHZVR05UTG5WeWk4aVd2SkE5UEwwUkZyN090cDRYZDliUWE5YmdBQUFZUVlYWGd3QUFBRUF3QkdNRVFDSUZldDlDQ0Q0b0F1Y0kxbkVGb1Y5VlNpdmdYQnpyTEtkUklzSjdocVdlQjJBaUJsWWtLTXRnYlhNRnNtR05renhIQjR5NEVBYzRXYlZrNEhZeUxkZWtySWFRQjFBTGMrK3lUZm5FMjZkZkk1eGJwWTlHeGQvRUxQZXA4MXhKNGRDWUVsN2JTWkFBQUJoQmhkZUQ0QUFBUURBRVl3UkFJZ1l0bVh1azlqK1BLRk9JTDd0WG1PTU1YV0xBMkR4TmFFbkRRT2w3cFZhZTRDSUJrdlJKZzNqNXdoa1c5b1FVRG1McllWRHFLVzJzYkk5RlY5VGdOUFA3ck1NQTBHQ1NxR1NJYjNEUUVCQ3dVQUE0SUJBUUF4aTVLVDN3WFl4WFFLdnM5T2loaSt6cXVMZzlNKy8xMXJ1aWIyMmtPNEVYZHFnNnBxamt5UDhqWEZkVkdDSDB6clJCZHZ1emV0YW5vMGpEZTRDUzJzN1g0SEY1UEpMbXc5SUdWUUxmRWlNNTV1R0dqMUduRkJVMFA1TzI3aW4yeEdCclJ5K29uTHhIVDVkR0lxMTNBdkllYW1XVnhMSzJVSkk0dTEzZFNJSjNEVXBHcEtINWYxdTJEdnp5Y1VOY1F2U1NUTzJyQnBlMjJUY2NBbHJZVjlsc3FlL3dkeGZUU3Y5VUFOQTZCSGQ4eEFvRTBOS0dKVmxsZGM0T3NVUzdSY2dURTRZQSs4LzY2OFFmWmlPUjRVa05vanpYekhJVFBMaXlBQ2ZtTmM3bjVPZTNQR1dkRkhMdkdkV2k4Nmp4R0ZnTEc5SGR5R2JKNHdKSWhRWHJTYyIsIk1JSUZqRENDQTNTZ0F3SUJBZ0lOQWdDT3NnSXpObVdMWk0zYm16QU5CZ2txaGtpRzl3MEJBUXNGQURCSE1Rc3dDUVlEVlFRR0V3SlZVekVpTUNBR0ExVUVDaE1aUjI5dloyeGxJRlJ5ZFhOMElGTmxjblpwWTJWeklFeE1RekVVTUJJR0ExVUVBeE1MUjFSVElGSnZiM1FnVWpFd0hoY05NakF3T0RFek1EQXdNRFF5V2hjTk1qY3dPVE13TURBd01EUXlXakJHTVFzd0NRWURWUVFHRXdKVlV6RWlNQ0FHQTFVRUNoTVpSMjl2WjJ4bElGUnlkWE4wSUZObGNuWnBZMlZ6SUV4TVF6RVRNQkVHQTFVRUF4TUtSMVJUSUVOQklERkVORENDQVNJd0RRWUpLb1pJaHZjTkFRRUJCUUFEZ2dFUEFEQ0NBUW9DZ2dFQkFLdkFxcVBDRTI3bDB3OXpDOGRUUElFODliQSt4VG1EYUc3eTdWZlE0YyttT1dobFVlYlVRcEsweXYycjY3OFJKRXhLMEhXRGplcStuTElITjFFbTVqNnJBUlppeG15UlNqaElSMEtPUVBHQk1VbGRzYXp0SUlKN08wZy84MnFqL3ZHRGwvLzN0NHRUcXhpUmhMUW5UTFhKZGVCKzJEaGtkVTZJSWd4NndON0U1TmNVSDNSY3NlamNxajhwNVNqMTl2Qm02aTFGaHFMR3ltaE1Gcm9XVlVHTzN4dElIOTFkc2d5NGVGS2NmS1ZMV0szbzIxOTBRMExtL1NpS21MYlJKNUF1NHkxZXVGSm0ySk05ZUI4NEZrcWEzaXZyWFdVZVZ0eWUwQ1FkS3ZzWTJGa2F6dnh0eHZ1c0xKekxXWUhrNTV6Y1JBYWNEQTJTZUV0QmJRZkQxcXNDQXdFQUFhT0NBWFl3Z2dGeU1BNEdBMVVkRHdFQi93UUVBd0lCaGpBZEJnTlZIU1VFRmpBVUJnZ3JCZ0VGQlFjREFRWUlLd1lCQlFVSEF3SXdFZ1lEVlIwVEFRSC9CQWd3QmdFQi93SUJBREFkQmdOVkhRNEVGZ1FVSmVJWURySlhrWlFxNWRSZGhwQ0QzbE96dUpJd0h3WURWUjBqQkJnd0ZvQVU1SzhySm5FYUswZ25oUzlTWml6djhJa1RjVDR3YUFZSUt3WUJCUVVIQVFFRVhEQmFNQ1lHQ0NzR0FRVUZCekFCaGhwb2RIUndPaTh2YjJOemNDNXdhMmt1WjI5dlp5OW5kSE55TVRBd0JnZ3JCZ0VGQlFjd0FvWWthSFIwY0RvdkwzQnJhUzVuYjI5bkwzSmxjRzh2WTJWeWRITXZaM1J6Y2pFdVpHVnlNRFFHQTFVZEh3UXRNQ3N3S2FBbm9DV0dJMmgwZEhBNkx5OWpjbXd1Y0d0cExtZHZiMmN2WjNSemNqRXZaM1J6Y2pFdVkzSnNNRTBHQTFVZElBUkdNRVF3Q0FZR1o0RU1BUUlCTURnR0Npc0dBUVFCMW5rQ0JRTXdLakFvQmdnckJnRUZCUWNDQVJZY2FIUjBjSE02THk5d2Eya3VaMjl2Wnk5eVpYQnZjMmwwYjNKNUx6QU5CZ2txaGtpRzl3MEJBUXNGQUFPQ0FnRUFJVlRveTI0andYVXIwckFQYzkyNHZ1U1ZiS1F1WXczbkxmbExmTGg1QVlXRWVWbC9EdTE4UUFXVU1kY0o2by9xRlpiaFhrQkgwUE5jdzk3dGhhZjJCZW9EWVk5Q2svYitVR2x1aHgwNnpkNEVCZjdIOVA4NG5ucndwUis0R0JEWksrWGgzSTB0cUp5MnJnT3FORGZscjVJTVE4WlRXQTN5bHRha3pTQktaNlhwRjBQcHF5Q1J2cC9OQ0d2MktYMlR1UENKdnNjcDEvbTJwVlR0eUJqWVBSUStRdUNRR0FKS2p0TjdSNURGcmZUcU1XdllnVmxwQ0pCa3dsdTcrN0tZM2NUSWZ6RTdjbUFMc2tNS05MdUR6K1J6Q2NzWVRzVmFVN1ZwM3hMNjBPWWhxRmt1QU9PeERaNnBIT2o5K09KbVlnUG1PVDRYMys3TDUxZlhKeVJIOUtmTFJQNm5UMzFENW5tc0dBT2daMjYvOFQ5aHNCVzF1bzlqdTVmWkxaWFZWUzVIMEh5SUJNRUt5R01JUGhGV3JsdC9oRlMyOE4xemFLSTBaQkdEM2dZZ0RMYmlEVDlmR1hzdHBrK0ZtYzRvbFZsV1B6WGU4MXZkb0VuRmJyNU0yNzJIZGdKV28rV2hUOUJZTTBKaSt3ZFZtblJmZlhnbG9Fb2x1VE5jV3pjNDFkRnBnSnU4ZkYzTEcwZ2wyaWJTWWlDaTlhNmh2VTBUcHBqSnlJV1hoa0pUY01KbFByV3gxVnl0RVVHclgybDBKRHdSalcvNjU2cjBLVkIwMnhIUkt2bTJaS0kwM1RnbExJcG1WQ0sza0JLa0tOcEJOa0Z0OHJoYWZjQ0tPYjlKeC85dHBORmxRVGw3QjM5ckpsSldrUjE3UW5acVZwdEZlUEZPUm9abUZ6TT0iLCJNSUlGWWpDQ0JFcWdBd0lCQWdJUWQ3ME5iTnMyK1JycUlRL0U4RmpURFRBTkJna3Foa2lHOXcwQkFRc0ZBREJYTVFzd0NRWURWUVFHRXdKQ1JURVpNQmNHQTFVRUNoTVFSMnh2WW1Gc1UybG5iaUJ1ZGkxellURVFNQTRHQTFVRUN4TUhVbTl2ZENCRFFURWJNQmtHQTFVRUF4TVNSMnh2WW1Gc1UybG5iaUJTYjI5MElFTkJNQjRYRFRJd01EWXhPVEF3TURBME1sb1hEVEk0TURFeU9EQXdNREEwTWxvd1J6RUxNQWtHQTFVRUJoTUNWVk14SWpBZ0JnTlZCQW9UR1VkdmIyZHNaU0JVY25WemRDQlRaWEoyYVdObGN5Qk1URU14RkRBU0JnTlZCQU1UQzBkVVV5QlNiMjkwSUZJeE1JSUNJakFOQmdrcWhraUc5dzBCQVFFRkFBT0NBZzhBTUlJQ0NnS0NBZ0VBdGhFQ2l4N2pvWGViTzl5L2xENjNsYWRBUEtIOWd2bDlNZ2FDY2ZiMmpILzc2TnU4YWk2WGw2T01TL2tyOXJINXpvUWRzZm5GbDk3dnVmS2o2YndTaVY2bnFsS3IrQ01ueTZTeG5HUGIxNWwrOEFwZTYyaW05TVphUncxTkVEUGpUckVUbzhnWWJFdnMvQW1RMzUxa0tTVWpCNkcwMGowdVlPRFAwZ21IdTgxSThFM0N3bnFJaXJ1Nnoxa1oxcStQc0Fld25qSHhnc0hBM3k2bWJXd1pEclhZZmlZYVJRTTlzSG1rbENpdEQzOG01YWdJL3Bib1BHaVVVKzZET29nckZaWUpzdUI2akM1MTFwenJwMVprajVaUGFLNDlsOEtFajhDOFFNQUxYTDMyaDdNMWJLd1lVSCtFNEV6Tmt0TWc2VE84VXBtdk1yVXBzeVVxdEVqNWN1SEtaUGZtZ2hDTjZKM0Npb2o2T0dhSy9HUDVBZmw0L1h0Y2QvcDJoL3JzMzdFT2VaVlh0TDBtNzlZQjBlc1dDcnVPQzdYRnhZcFZxOU9zNnBGTEtjd1pwRElsVGlyeFpVVFFBczZxemttMDZwOThnN0JBZStkRHE2ZHNvNDk5aVlINlRLWC8xWTdEemt2Z3RkaXpqa1hQZHNEdFFDdjlVdyt3cDlVN0RiR0tvZ1BlTWEzTWQrcHZlejdXMzVFaUV1YSsrdGd5L0JCakZGRnkzbDNXRnBPOUtXZ3o3enBtN0FlS0p0OFQxMWRsZUNmZVhra1VBS0lBZjVxb0liYXBzWld3cGJrTkZoSGF4MnhJUEVEZ2ZnMWF6Vlk4MFpjRnVjdEw3VGxMbk1RLzBsVVRiaVN3MW5INjlNRzZ6TzBiOWY2QlFkZ0FtRDA2eUs1Nm1EY1lCWlVDQXdFQUFhT0NBVGd3Z2dFME1BNEdBMVVkRHdFQi93UUVBd0lCaGpBUEJnTlZIUk1CQWY4RUJUQURBUUgvTUIwR0ExVWREZ1FXQkJUa3J5c21jUm9yU0NlRkwxSm1MTy93aVJOeFBqQWZCZ05WSFNNRUdEQVdnQlJnZTJZYVJRMlh5b2xRTDMwRXpUU28vL3o5U3pCZ0JnZ3JCZ0VGQlFjQkFRUlVNRkl3SlFZSUt3WUJCUVVITUFHR0dXaDBkSEE2THk5dlkzTndMbkJyYVM1bmIyOW5MMmR6Y2pFd0tRWUlLd1lCQlFVSE1BS0dIV2gwZEhBNkx5OXdhMmt1WjI5dlp5OW5jM0l4TDJkemNqRXVZM0owTURJR0ExVWRId1FyTUNrd0o2QWxvQ09HSVdoMGRIQTZMeTlqY213dWNHdHBMbWR2YjJjdlozTnlNUzluYzNJeExtTnliREE3QmdOVkhTQUVOREF5TUFnR0JtZUJEQUVDQVRBSUJnWm5nUXdCQWdJd0RRWUxLd1lCQkFIV2VRSUZBd0l3RFFZTEt3WUJCQUhXZVFJRkF3TXdEUVlKS29aSWh2Y05BUUVMQlFBRGdnRUJBRFNrSHJFb285QzBkaGVtTVhvaDZkRlNQc2piZEJaQmlMZzlOUjN0NVArVDRWeGZxN3ZxZk0vYjVBM1JpMWZ5Sm05YnZoZEdhSlEzYjJ0NnlNQVlOL29sVWF6c2FMK3l5RW45V3ByS0FTT3NoSUFyQW95WmwrdEphb3gxMThmZXNzbVhuMWhJVnc0MW9lUWExdjF2ZzRGdjc0elBsNi9BaFNydzlVNXBDWkV0NFdpNHdTdHo2ZFRaL0NMQU54OExaaDFKN1FKVmoyZmhNdGZUSnI5dzR6MzBaMjA5Zk9VMGlPTXkrcWR1Qm1wdnZZdVI3aFpMNkR1cHN6Zm53MFNrZnRoczE4ZEc5WktiNTlVaHZtYVNHWlJWYk5RcHNnM0JabHZpZDBsSUtPMmQxeG96Y2xPemdqWFBZb3ZKSkl1bHR6a011MzRxUWI5U3oveWlscmJDZ2o4PSJdfQ.eyJub25jZSI6IjBLQWpRQ2lJR3d1aHlwdU9jK2FvdjV6cmZocyszdlBLVTJGbVpYUjVNVFkzTWprMk9UZzNOVEk1T1E9PSIsInRpbWVzdGFtcE1zIjoxNjcyOTY5ODgyMjY5LCJhcGtQYWNrYWdlTmFtZSI6ImNvbS5jb2RpLmFzcGludGVncmFvcGNpb25lcy5hc3Bjb2RpIiwiYXBrRGlnZXN0U2hhMjU2IjoiVlc1K1NLa3hqZXMvcDdHR2NwaEUyTGxhaUhacnNvYjU3S1d0N2ZwUmNVaz0iLCJjdHNQcm9maWxlTWF0Y2giOnRydWUsImFwa0NlcnRpZmljYXRlRGlnZXN0U2hhMjU2IjpbIlU3Z21ydjFzU3NQSFdhREQySVpRTkZIeUFJYlErLytzbVI0Z3hFQnV0M2s9Il0sImJhc2ljSW50ZWdyaXR5Ijp0cnVlLCJldmFsdWF0aW9uVHlwZSI6IkJBU0lDLEhBUkRXQVJFX0JBQ0tFRCJ9.inACFBKeVAVaYUOWlc07TIXXGdwH9K8en-nb48mVnjUbBUnBAcWiiVwQa7D4_8Fm3FRmWWGJZRU6E5X9ATIfweTLkJf3DkXlIDhnSKVOirGg3oAuQTu8MBCLKylIDWKZID2mmZ40mgm9U_LBx_i1N-xnzBMXfgu803Zro4LEP8Ot5XfSop_PeGkByeL01osBXmSwtN0nJzlor0fQ_tFQytMWf18ntz7eWs78IP6NaX5ljv6snP_2R48SB5Ow8Hpg5oqvPk_EN8NGq8hVvUmRh0honXdmFfCqiciW23t_QEtQYD2pm55tkPGaWwIisr2Q65DcXaznJaxus_htxbs5QA",
                                        nonce = "Safety1672969875299"
                                    ),
                                    token = Prefs.get(PROPERTY_FINGER_TOKEN, ""),
                                    versionApp = "1.0.15",
                                    dispositivoId = Prefs[DEVICE_ID_UNIQUE_GUID, ""]
                                ), phone.value!!, encriptPassword(password.value!!)
                            ), idCanal = 1
                        )
                    )
                )
            }) {
                is UseCaseResult.Success -> {

                    if (result.data.data.isNotEmpty()) {
                        Prefs.set(
                            "keysourcepass",
                            encriptPassword(password.value!!).encryptByKeyPass()
                        )
                        loginDataResponse.postValue(
                            result.data
                        )
                    }

                    successResponse.postValue(result.data)
                }

                is UseCaseResult.Error -> errorMessage.postValue(result.exception.message)
            }
        }
    }
}
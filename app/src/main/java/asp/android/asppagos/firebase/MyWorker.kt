package asp.android.asppagos.firebase

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

@SuppressLint("RestrictedApi")
class MyWorker(appContext: Context, workerParams: WorkerParameters): Worker(appContext,workerParams) {

    override fun doWork(): Result {
        Log.d(TAG, "Performing long running task in scheduled job")
        return Result.success()
    }

    companion object {
        private val TAG = "MyWorker"
    }
}
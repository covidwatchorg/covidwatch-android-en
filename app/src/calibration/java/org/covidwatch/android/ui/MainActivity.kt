package org.covidwatch.android.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import androidx.lifecycle.lifecycleScope
import androidx.work.WorkManager
import com.jaredrummler.android.device.DeviceName
import org.covidwatch.android.domain.ProvideDiagnosisKeysFromFileUseCase
import org.covidwatch.android.domain.ProvideDiagnosisKeysFromFileUseCase.Params
import org.covidwatch.android.extension.launchUseCase
import org.covidwatch.android.work.ProvideDiagnosisKeysFromFileWork
import org.koin.android.ext.android.inject
import timber.log.Timber

class MainActivity : BaseMainActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DeviceName.init(this)
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent?.action == Intent.ACTION_SEND && "application/zip" == intent.type) {
            (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let {
                Timber.d("Received a test diagnosis file: ${it.path}")
                ProvideDiagnosisKeysFromFileWork.start(it, WorkManager.getInstance(this))
            }
        }
    }
}

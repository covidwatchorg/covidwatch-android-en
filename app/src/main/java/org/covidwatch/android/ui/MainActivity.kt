package org.covidwatch.android.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import org.covidwatch.android.R
import org.covidwatch.android.domain.ProvideDiagnosisKeysFromFileUseCase
import org.covidwatch.android.domain.ProvideDiagnosisKeysFromFileUseCase.Params
import org.covidwatch.android.extension.launchUseCase
import org.koin.android.ext.android.inject
import java.io.File


class MainActivity : AppCompatActivity() {

    private val provideDiagnosisKeysFromFileUseCase: ProvideDiagnosisKeysFromFileUseCase by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (intent?.action == Intent.ACTION_SEND && "application/zip" == intent.type) {
            intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM)?.let {
                // TODO: 03.06.2020 Get the file properly 
                val file = (it as? Uri)?.path?.let { uri -> File(uri) } ?: return
                val files = listOf(file)
                lifecycleScope.launchUseCase(provideDiagnosisKeysFromFileUseCase, Params(files))
            }
        }
    }

    private val FragmentManager.currentNavigationFragment: Fragment?
        get() = primaryNavigationFragment?.childFragmentManager?.fragments?.first()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        supportFragmentManager.currentNavigationFragment?.onActivityResult(
            requestCode,
            resultCode,
            data
        )
    }
}

package org.covidwatch.android.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import org.covidwatch.android.R
import org.covidwatch.android.exposurenotification.Failure
import org.covidwatch.android.extension.observeEvent
import org.covidwatch.android.ui.Intents.openBrowser
import org.covidwatch.android.ui.Intents.playStoreWithServices

abstract class BaseViewModelFragment<T : ViewBinding, VM : BaseViewModel> : BaseFragment<T>() {

    protected abstract val viewModel: VM

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(viewModel) {
            observeEvent(status) {
                handleStatus(it)
            }
            observeEvent(resolvable) { resolvable ->
                resolvable.apiException.status.startResolutionForResult(
                    activity, resolvable.requestCode
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        lifecycleScope.launch {
            viewModel.handleResolution(requestCode, resultCode)
        }
    }

    protected fun handleStatus(it: Failure) {
        when (it) {
            Failure.EnStatus.Failed -> {
                val snackbar = Snackbar.make(
                    binding.root,
                    R.string.unknown_error,
                    BaseTransientBottomBar.LENGTH_INDEFINITE
                )
                snackbar.setAction(R.string.contact_us) { context?.openBrowser(Urls.SUPPORT) }
                snackbar.show()
            }
            Failure.EnStatus.NotSupported -> {
                context?.let { context ->
                    MaterialAlertDialogBuilder(context)
                        .setTitle(R.string.en_not_available_title)
                        .setMessage(R.string.en_not_available_text)
                        .setPositiveButton(R.string.update) { _, _ ->
                            context.playStoreWithServices?.let { startActivity(it) }
                        }
                        .create()
                        .show()
                }
            }
            Failure.EnStatus.Unauthorized -> {
                context?.let { context ->
                    MaterialAlertDialogBuilder(context)
                        .setTitle(R.string.app_unauthorized_title)
                        .setMessage(R.string.app_unauthorized_text)
                        .setPositiveButton(R.string.contact_us) { _, _ ->
                            context.openBrowser(Urls.SUPPORT)
                        }
                        .create()
                        .show()
                }
            }
            Failure.NetworkError -> {
                val snackbar = Snackbar.make(
                    binding.root,
                    R.string.no_connection_error,
                    BaseTransientBottomBar.LENGTH_INDEFINITE
                )
                snackbar.setAction(R.string.open_settings) { startActivity(Intents.wirelessSettings) }
                snackbar.show()
            }
            Failure.ServerError -> {
                val snackbar = Snackbar.make(
                    binding.root,
                    R.string.server_error,
                    BaseTransientBottomBar.LENGTH_INDEFINITE
                )
                snackbar.setAction(R.string.contact_us) { context?.openBrowser(Urls.SUPPORT) }
                snackbar.show()
            }
        }
    }
}

package com.nyahonk.odfreader.presentation

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.nyahonk.odfreader.R
import com.nyahonk.odfreader.di.DaggerApplication
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_document.*

class DocumentFragment : Fragment(R.layout.fragment_document) {

    companion object {
        const val KEY_BUNDLE_URI = "KEY_BUNDLE_URI"
        const val KEY_PERSISTENT_URI = "KEY_PERSISTENT_URI"
    }

    private val viewModel: DocumentFragmentViewModel by viewModelProvider {
        (requireActivity().application as DaggerApplication).getComponent().documentFragmentViewModel()
    }

    private val disposables = CompositeDisposable()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        page_view.setDocumentFragment(this)

        arguments?.let {
            viewModel.loadFile(
                it.getString(KEY_BUNDLE_URI)!!,
                it.getBoolean(KEY_PERSISTENT_URI)
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeObservers()
    }

    override fun onStop() {
        disposables.dispose()
        super.onStop()
    }

    private fun subscribeObservers() {
        val uriObserver = Observer<Uri> {
            loadData(it.toString())
        }

        viewModel.apply {
            uriLiveData.observe(viewLifecycleOwner, uriObserver)
        }

        disposables.add(
            viewModel.getErrorPublisher()
                .subscribe {
                    showError(it.message ?: it.javaClass.simpleName)
                }
        )
    }

    private fun showError(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
    }

    private fun loadData(url: String) {
        page_view.loadUrl(url)
    }
}
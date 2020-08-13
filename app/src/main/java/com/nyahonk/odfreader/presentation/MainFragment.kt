package com.nyahonk.odfreader.presentation

import android.Manifest
import android.content.ComponentName
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.nyahonk.odfreader.R
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment(R.layout.fragment_main) {

    private val DOCUMENT_FRAGMENT_TAG = "document_fragment"
    private val PERMISSION_CODE = 1353
    private val CREATE_CODE = 4213

    private var onPermissionRunnable: Runnable? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        open_file_button.setOnClickListener { findDocument() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        if (requestCode == 42 && resultCode == AppCompatActivity.RESULT_OK) {
            intent?.data?.let {
                loadUri(it)
            }
        }

    }

    fun requestPermission(permission: String, onPermissionRunnable: Runnable?): Boolean {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(permission),
                PERMISSION_CODE
            )
            this.onPermissionRunnable = onPermissionRunnable
            return false
        }
        this.onPermissionRunnable = null
        return true
    }

    private fun loadUri(uri: Uri) {
        val onPermission = Runnable { loadUri(uri) }
        val hasPermission: Boolean =
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, onPermission)
        if (!hasPermission) {
            return
        }

        var isPersistentUri = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                requireContext().grantUriPermission(requireContext().packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                requireContext().contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: java.lang.Exception) {
                // some providers dont support persisted permissions
                if (!uri.toString().startsWith("content://com.nyahonk.odfreader")) {
                    isPersistentUri = false
                }
            }
        }

        findNavController().navigate(R.id.navigate_to_doc_view, Bundle().apply {
            putString(DocumentFragment.KEY_BUNDLE_URI, uri.toString())
            putBoolean(DocumentFragment.KEY_PERSISTENT_URI, isPersistentUri)
        })
    }

    private fun findDocument() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "*/*"
            addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            addCategory(Intent.CATEGORY_OPENABLE)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }


        val pm = requireActivity().packageManager
        val targets = pm.queryIntentActivities(intent, 0)
        val size = targets.size + 1
        val targetNames = arrayOfNulls<String>(size)
        for (i in targets.indices) {
            targetNames[i] = targets[i].loadLabel(pm).toString()
        }
        val target = targets[0]

        targetNames[size - 1] = "recents"

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("filemanager")
        builder.setItems(targetNames, object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, which: Int) {
                val target = targets[which] ?: return
                intent.component = ComponentName(
                    target.activityInfo.packageName,
                    target.activityInfo.name
                )
                try {
                    startActivityForResult(intent, 42)
                } catch (e: java.lang.Exception) {
                    Snackbar.make(requireView(), e.message ?: "err", Snackbar.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
        })
        builder.show()
    }
}
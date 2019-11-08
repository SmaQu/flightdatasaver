package com.smaqu.rcgraphsystem.dialogs

import android.app.Activity
import android.app.Dialog
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.smaqu.rcgraphsystem.R
import com.smaqu.rcgraphsystem.dialogs.SimplyChooseDialog.Companion.DELETE_FILE_REQUEST
import com.smaqu.rcgraphsystem.fragmentcontrol.FragmentHandler
import com.smaqu.rcgraphsystem.fragments.SavedFilesFragment
import com.smaqu.rcgraphsystem.models.FileEntity
import com.smaqu.rcgraphsystem.viewmodel.MainVM
import com.smaqu.rcgraphsystem.viewmodel.SavedFilesVM
import kotlinx.android.synthetic.main.dialog_file_options.view.*

class FileOptionsDialog : DialogFragment() {

    private lateinit var savedFilesVM: SavedFilesVM
    private lateinit var mainVM: MainVM
    private lateinit var fragmentHandler: FragmentHandler
    private lateinit var fileEntity: FileEntity

    companion object {
        const val TAG = "FileOptionsDialog"
        private const val KEY_FILE_NAME = "key_file_name"
        fun create(fileEntity: FileEntity): FileOptionsDialog {
            return FileOptionsDialog().apply {
                arguments = Bundle().apply {
                    putSerializable(KEY_FILE_NAME, fileEntity)
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        fragmentHandler = context as FragmentHandler
        return AppCompatDialog(activity, R.style.DialogStyleNoTitle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_file_options, container, false)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        savedFilesVM = ViewModelProviders.of(this).get(SavedFilesVM::class.java)
        mainVM = ViewModelProviders.of(requireActivity()).get(MainVM::class.java)

        fileEntity = arguments?.getSerializable(KEY_FILE_NAME) as FileEntity

        view.text_open_graph.setOnClickListener {
            mainVM.graphFile.value = SavedFilesFragment.createGraphFileModel(requireActivity(), fileEntity)
            if (mainVM.graphFile.value == null) {
                Toast.makeText(context, "Your data is incompatible with require scheme. Try to fix it.", Toast.LENGTH_LONG).show()
            } else {
                fragmentHandler.showGraphFragment(false)
            }
            dismiss()
        }

        view.text_edit.setOnClickListener {
            fragmentHandler.showEditFileFragment(fileEntity)
            dismiss()
        }

        view.text_edit_name.setOnClickListener {
            EditFileNameDialog.create(fileEntity).also {
                it.show(fragmentManager, EditFileNameDialog.TAG)
            }
            dismiss()
        }

        view.text_delete.setOnClickListener {
            val dialogInfo = StringBuilder(resources.getString(R.string.delete_file_part))
                    .append(" ${fileEntity.name}")
                    .append("?")
                    .toString()
            SimplyChooseDialog.create(getString(R.string.delete_file), dialogInfo).also {
                it.setTargetFragment(this, DELETE_FILE_REQUEST)
                it.show(fragmentManager, SimplyChooseDialog.TAG)
            }
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            DELETE_FILE_REQUEST -> {
                if (resultCode == Activity.RESULT_OK) {
                    savedFilesVM.deleteFile(fileEntity)
                    dismiss()
                }
            }
        }
    }
}
package com.smaqu.rcgraphsystem.dialogs

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.smaqu.rcgraphsystem.R
import com.smaqu.rcgraphsystem.models.FileEntity
import com.smaqu.rcgraphsystem.roomdatabase.repository.FileRepository
import kotlinx.android.synthetic.main.dialog_change_text.view.*

class EditFileNameDialog : DialogFragment() {


    companion object {
        const val TAG = "EditFileNameDialog"
        private const val KEY_FILE_NAME = "key_file_name";
        fun create(fileEntity: FileEntity): EditFileNameDialog {
            return EditFileNameDialog().apply {
                arguments = Bundle().apply {
                    putSerializable(KEY_FILE_NAME, fileEntity)
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AppCompatDialog(activity, R.style.DialogStyleNoTitle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_change_text, container, false)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val fileEntity = arguments?.getSerializable(KEY_FILE_NAME) as FileEntity

        view.text_dialog_title_info.text = getString(R.string.edit_file_name)
        view.edit_new_separator_value.hint = fileEntity.name

        if (savedInstanceState == null) {
            view.edit_new_separator_value.setText(fileEntity.name)
        }

        view.button_save_new_state.setOnClickListener {
            val fileRepository = FileRepository(activity?.application!!)
            fileEntity.name = view.edit_new_separator_value.text.toString()
            fileRepository.updateFile(fileEntity)
            dismiss()
        }

        return view
    }
}
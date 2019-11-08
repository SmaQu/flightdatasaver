package com.smaqu.rcgraphsystem.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import com.smaqu.rcgraphsystem.R
import com.smaqu.rcgraphsystem.models.FileEntity
import com.smaqu.rcgraphsystem.roomdatabase.repository.FileRepository
import kotlinx.android.synthetic.main.fragment_edit_file.view.*

class EditFileFragment : Fragment(), View.OnClickListener {

    private lateinit var repository: FileRepository
    private lateinit var fileEntity: FileEntity

    private lateinit var editableViewEt: EditText
    private lateinit var saveFileBt: Button

    companion object {
        private const val TAG = "EditFileFragment"
        private const val KEY_NAME = "key_name"

        fun create(fileEntity: FileEntity): EditFileFragment {
            return EditFileFragment().apply {
                arguments = Bundle().also {
                    it.putSerializable(KEY_NAME, fileEntity)
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_edit_file, container, false)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        fileEntity = arguments?.getSerializable(KEY_NAME) as FileEntity
        repository = FileRepository(activity?.application!!)

        editableViewEt = view.et_edit_file
        saveFileBt = view.bt_save_file

        editableViewEt.setText(fileEntity.data)

        saveFileBt.setOnClickListener(this)

        return view
    }


    override fun onClick(v: View?) {
        fileEntity.data = editableViewEt.text.toString()
        repository.updateFile(fileEntity)
        activity?.supportFragmentManager?.popBackStack()
    }
}
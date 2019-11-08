package com.smaqu.rcgraphsystem.dialogs

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import com.smaqu.rcgraphsystem.R
import com.smaqu.rcgraphsystem.models.SeriesContainer
import com.smaqu.rcgraphsystem.models.SeriesContainer.Companion.FIRST_Y_AXIS
import com.smaqu.rcgraphsystem.models.SeriesContainer.Companion.NO_Y_AXIS
import com.smaqu.rcgraphsystem.models.SeriesContainer.Companion.SECOND_Y_AXIS
import kotlinx.android.synthetic.main.dialog_axis_picker.view.*

class PickAxisDialog : DialogFragment(), View.OnClickListener {

    companion object {
        const val TAG = "PickAxisDialog"
        const val AXIS_REQUEST_CODE = 777
        const val ARG_NEW_AXIS = "arg_axis_type"
        const val ARG_SERIES_ID = "arg_title_id"
        private const val ARG_SERIES = "arg_series"

        fun create(seriesContainer: SeriesContainer): PickAxisDialog {
            return PickAxisDialog().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_SERIES, seriesContainer)
                }
            }
        }
    }

    private lateinit var noAxisButton: RadioButton
    private lateinit var firstAxisButton: RadioButton
    private lateinit var secondAxisButton: RadioButton
    private lateinit var dialogTitle: TextView

    private var seriesId: Int = -1

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AppCompatDialog(activity, R.style.DialogStyleNoTitle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_axis_picker, container, false)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val series = arguments?.getSerializable(ARG_SERIES) as SeriesContainer
        seriesId = series.seriesId

        dialogTitle = view.text_axis_dialog_title
        noAxisButton = view.button_no_axis
        firstAxisButton = view.button_first_y_axis
        secondAxisButton = view.button_second_y_axis

        dialogTitle.text = series.label
        setSelectedAxis(series.yAxis)

        noAxisButton.setOnClickListener(this)
        firstAxisButton.setOnClickListener(this)
        secondAxisButton.setOnClickListener(this)
        return view
    }

    override fun onClick(view: View) {
        val intent = Intent()
        var pickedAxis = NO_Y_AXIS
        when (view.id) {
            R.id.button_no_axis -> {
                pickedAxis = NO_Y_AXIS
            }
            R.id.button_first_y_axis -> {
                pickedAxis = FIRST_Y_AXIS
            }
            R.id.button_second_y_axis -> {
                pickedAxis = SECOND_Y_AXIS
            }
        }
        intent.putExtra(ARG_NEW_AXIS, pickedAxis)
        intent.putExtra(ARG_SERIES_ID, seriesId)
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
        dismiss()
    }

    private fun setSelectedAxis(selectedAxis: Int) {
        when (selectedAxis) {
            NO_Y_AXIS -> {
                noAxisButton.isChecked = true
            }
            FIRST_Y_AXIS -> {
                firstAxisButton.isChecked = true
            }
            SECOND_Y_AXIS -> {
                secondAxisButton.isChecked = true
            }
        }
    }
}
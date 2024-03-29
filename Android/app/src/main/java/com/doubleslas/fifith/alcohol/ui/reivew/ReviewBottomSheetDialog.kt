package com.doubleslas.fifith.alcohol.ui.reivew

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import com.doubleslas.fifith.alcohol.R
import com.doubleslas.fifith.alcohol.databinding.LayoutWriteReviewBinding
import com.doubleslas.fifith.alcohol.dto.ReviewDetailData
import com.doubleslas.fifith.alcohol.model.base.ApiStatus
import com.doubleslas.fifith.alcohol.ui.auth.CustomDialog
import com.doubleslas.fifith.alcohol.ui.auth.CustomDialogInterface
import com.doubleslas.fifith.alcohol.ui.common.CalendarDialogFragment
import com.doubleslas.fifith.alcohol.ui.common.base.BaseBottomSheetDialogFragment
import kotlinx.android.synthetic.main.custom_dialog.*
import java.util.*


class ReviewBottomSheetDialog private constructor() :
    BaseBottomSheetDialogFragment<LayoutWriteReviewBinding>(), CustomDialogInterface {

    private val alcoholId by lazy { arguments!!.getInt(ARGUMENT_ALCOHOL_ID) }
    private val customDialog: CustomDialog by lazy { CustomDialog(context!!, this) }

    private val viewModel by lazy {
        ReviewViewModel()
    }

    private val calendarDialogFragment by lazy {
        CalendarDialogFragment().apply {
            setOnConfirmListener { year, month, day ->
                setDateText(year, month, day)
            }
        }
    }


    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): LayoutWriteReviewBinding {
        return LayoutWriteReviewBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.let { b ->
            b.btnClose.setOnClickListener {
                dismiss()
            }

            b.ratingReview.progress = 5
            b.ratingReview.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
                if (rating < 1) {
                    ratingBar.progress = 1
                }
            }

            b.layoutDetail.tvDate.setOnClickListener {
                activity!!.supportFragmentManager.let { fm ->
                    calendarDialogFragment.show(fm, null)
                }
            }

            b.etComment.addTextChangedListener {
                b.btnReviewConfirm.setBackgroundColor(
                    if (it?.length ?: 0 >= 20)
                        ResourcesCompat.getColor(resources, R.color.purple, null)
                    else
                        ResourcesCompat.getColor(resources, R.color.darken_gray, null)
                )
            }




            b.btnReviewConfirm.setOnClickListener {
                confirmReview()
            }

            b.ivDetailRecord.setImageResource(R.drawable.ic_review_button_plus)
            b.layoutDetail.visibility = View.GONE
            b.layoutDetailToggle.setOnClickListener {
                if (b.layoutDetail.visibility == View.GONE) {
                    b.ivDetailRecord.setImageResource(R.drawable.ic_review_button_x)
                    b.layoutDetail.visibility = View.VISIBLE
                    b.checkboxPrivate.visibility = View.VISIBLE
                } else {
                    b.ivDetailRecord.setImageResource(R.drawable.ic_review_button_plus)
                    b.layoutDetail.visibility = View.GONE
                    b.checkboxPrivate.visibility = View.INVISIBLE
                }
            }


            val now = Calendar.getInstance()
            setDateText(
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH) + 1,
                now.get(Calendar.DAY_OF_MONTH)
            )

            b.layoutDetail.setIndicator(false)
        }

    }


    private fun confirmReview() {
        binding?.let { b ->

            val detail =
                if (b.layoutDetail.isVisible)
                    ReviewDetailData(
                        b.layoutDetail.tvDate.text.toString(),
                        b.layoutDetail.etDrink.text.toString(),
                        b.layoutDetail.seekBarHangover.seekBar.progress,
                        b.layoutDetail.etPlace.text.toString(),
                        b.layoutDetail.etPrice.text.toString(),
                        b.checkboxPrivate.isChecked
                    )
                else
                    null

            val liveData = viewModel.sendReview(
                b.etComment.text.toString(),
                b.ratingReview.progress,
                detail,
                alcoholId
            )

            liveData.observe(viewLifecycleOwner, Observer {
                when (it) {
                    is ApiStatus.Loading -> {

                    }
                    is ApiStatus.Success -> {
                        onDialogBtnClicked("리뷰를 작성해주셔서\n감사합니다.")
                        Handler(Looper.myLooper()!!).postDelayed(
                            Runnable {
                                customDialog.dismiss()
                            },
                            2000
                        )
                        listener?.invoke()
                        dismiss()
                    }
                    is ApiStatus.Error -> {

                    }
                    is ApiStatus.ValidateFail ->
                        processValidate(it)
                }
            })
        }
    }

    private fun processValidate(v: ApiStatus.ValidateFail) {
        when (v) {
            is ReviewViewModel.ReviewValidateFail.CommentTooShort -> {
                binding?.etComment?.requestFocus()
                Toast.makeText(context, "리뷰는 20자 이상으로 작성해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onConfirmBtnClicked() {
        customDialog.dismiss()
    }

    private var listener: (() -> Unit)? = null
    fun onListener(listener: () -> Unit) {
        this.listener = listener
    }

    private fun onDialogBtnClicked(text: String?) {
        customDialog.show()
        customDialog.tv_nicknameCheck?.text = text
//        customDialog.customDialogInterface = object : CustomDialogInterface {
//            override fun onConfirmBtnClicked() {
//                customDialog.dismiss()
//                dismiss()
//            }
//        }
    }

    private fun setDateText(year: Int, month: Int, day: Int) {
        binding?.layoutDetail?.tvDate?.text = String.format("%4d.%02d.%02d", year, month, day)
    }

    companion object {
        private const val ARGUMENT_ALCOHOL_ID = "ARGUMENT_ALCOHOL_ID"
        fun create(id: Int): ReviewBottomSheetDialog {
            return ReviewBottomSheetDialog().apply {
                arguments = Bundle().apply {
                    putInt(ARGUMENT_ALCOHOL_ID, id)
                }
            }
        }
    }

}




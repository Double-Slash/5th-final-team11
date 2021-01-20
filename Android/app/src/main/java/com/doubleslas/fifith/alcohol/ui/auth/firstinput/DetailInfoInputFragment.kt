package com.doubleslas.fifith.alcohol.ui.auth.firstinput

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.doubleslas.fifith.alcohol.R
import com.doubleslas.fifith.alcohol.databinding.ChipFirstInputBinding
import com.doubleslas.fifith.alcohol.databinding.FragmentDetailInfoInputBinding
import com.doubleslas.fifith.alcohol.ui.common.base.BaseFragment
import com.doubleslas.fifith.alcohol.viewmodel.FirstInfoViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.android.synthetic.main.fragment_detail_info_input.*


class DetailInfoInputFragment : BaseFragment<FragmentDetailInfoInputBinding>() {
    private val viewModel by lazy { ViewModelProvider(activity!!).get(FirstInfoViewModel::class.java) }

    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDetailInfoInputBinding {
        return FragmentDetailInfoInputBinding.inflate(inflater, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.let { b ->
            b.rangeAbv.addOnChangeListener { slider, value, fromUser ->
                val text = "${slider.values[0].toInt()} 도 - ${slider.values[1].toInt()} 도"
                b.chipAbv.text = text
            }

            b.layoutLiquorLabel.setOnClickListener {
                toggleVisibility(b.layoutLiquorContent)
            }
            b.layoutBeerLabel.setOnClickListener {
                toggleVisibility(b.layoutBeerContent)
            }
            b.layoutWineLabel.setOnClickListener {
                toggleVisibility(b.layoutWineContent)
            }



            b.layoutLiquorTypeContent.let { layout ->
                for (str in viewModel.getTextLiquorType()) {
                    createChip(layout, str)
                }
            }

            b.layoutLiquorTasteContent.let { layout ->
                for (str in viewModel.getTextLiquorTaste()) {
                    createChip(layout, str)
                }
            }

            b.layoutWineTypeContent.let { layout ->
                for (str in viewModel.getTextWineType()) {
                    createChip(layout, str)
                }
            }

            b.seekBarWineTaste.tvLabel1.text = "Dry"
            b.seekBarWineTaste.tvLabel2.text = "Sweet"

            b.seekBarWineBody.tvLabel1.text = "Light"
            b.seekBarWineBody.tvLabel2.text = "Heavy"

            b.layoutBeerTypeContent.let { layout ->
                val list = viewModel.getTextBeerType()
                for (pair in list) {
                    val titleChip = createChip(layout, pair.first)


                    titleChip.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        marginEnd =
                            resources.getDimension(R.dimen.first_input_default_padding).toInt()
                        marginStart =
                            resources.getDimension(R.dimen.first_input_default_padding).toInt()
                        topMargin =
                            resources.getDimension(R.dimen.first_input_beer_type_title_margin_top)
                                .toInt()
                        bottomMargin =
                            resources.getDimension(R.dimen.first_input_beer_type_title_margin_bottom)
                                .toInt()
                    }

                    if (pair.second.isEmpty()) continue


                    val group = ChipGroup(context).apply {
                        setBackgroundColor(
                            ResourcesCompat.getColor(resources, R.color.gray, null)
                        )
                        setPadding(
                            resources.getDimension(R.dimen.first_input_beer_type_margin_horizontal)
                                .toInt(),
                            resources.getDimension(R.dimen.first_input_beer_type_margin_vertical)
                                .toInt(),
                            resources.getDimension(R.dimen.first_input_beer_type_margin_horizontal)
                                .toInt(),
                            resources.getDimension(R.dimen.first_input_beer_type_margin_vertical)
                                .toInt()
                        )
                    }

                    layout.addView(group)
                    for (str in pair.second) {
                        createChip(group, str)
                    }

                    titleChip.setOnClickListener {
                        it as Chip
                        val checked = it.isChecked

                        for (child in group.children) {
                            (child as? Chip)?.isChecked = checked
                        }
                    }
                }
            }

            b.layoutBeerPlaceContent.let { layout ->
                for (str in viewModel.getTextBeerPlace()) {
                    createChip(layout, str)
                }
            }

        }
    }

    override fun onResume() {
        super.onResume()

        binding?.layoutLiquor?.visibility =
            if (viewModel.checkLiquor.value == true) View.VISIBLE else View.GONE

        binding?.layoutBeer?.visibility =
            if (viewModel.checkBeer.value == true) View.VISIBLE else View.GONE

        binding?.layoutWine?.visibility =
            if (viewModel.checkWine.value == true) View.VISIBLE else View.GONE

    }

    private fun toggleVisibility(v: View) {
        v.isVisible = !v.isVisible

        scrollview.requestChildFocus(v, v)
    }

    private fun createChip(parent: ViewGroup, text: String): View {

        val chip = ChipFirstInputBinding.inflate(layoutInflater).root
        chip.text = text
        parent.addView(chip)

        return chip
    }
}
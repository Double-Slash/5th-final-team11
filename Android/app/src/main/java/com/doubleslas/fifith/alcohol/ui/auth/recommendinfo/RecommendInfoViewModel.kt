package com.doubleslas.fifith.alcohol.ui.auth.recommendinfo

import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.doubleslas.fifith.alcohol.R
import com.doubleslas.fifith.alcohol.databinding.FragmentRecommendInfoDetailBinding
import com.doubleslas.fifith.alcohol.dto.recommend.*
import com.doubleslas.fifith.alcohol.model.base.ApiLiveData
import com.doubleslas.fifith.alcohol.model.base.ApiStatus
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class RecommendInfoViewModel : ViewModel() {
    private val repository by lazy { RecommendInfoRepository() }

    private val mCheckWine = MutableLiveData<Boolean>()
    var checkWine: LiveData<Boolean> = mCheckWine

    private val mCheckBeer = MutableLiveData<Boolean>()
    var checkBeer: LiveData<Boolean> = mCheckBeer

    private val mCheckLiquor = MutableLiveData<Boolean>()
    var checkLiquor: LiveData<Boolean> = mCheckLiquor


    init {
        mCheckBeer.value = false
        mCheckWine.value = false
        mCheckLiquor.value = false
    }

    fun toggleCheckWine() {
        mCheckWine.value = !mCheckWine.value!!
    }

    fun toggleCheckBeer() {
        mCheckBeer.value = !mCheckBeer.value!!
    }

    fun toggleCheckLiquor() {
        mCheckLiquor.value = !mCheckLiquor.value!!
    }

    fun getLiquorTypeList(): List<String> {
        return listOf(
            "진",
            "럼",
            "위스키",
            "보드카",
            "데낄라",
            "리큐르"
        )
    }

    fun getLiquorFlavorList(): List<String> {
        return listOf(
            "상큼한",
            "풀 향의",
            "쓴",
            "독한",
            "톡 쏘는",
            "이국적인",
            "깔끔한",
            "오크향의",
            "스모키한",
            "달달한",
            "과일맛의",
            "쌉싸름한",
            "밀키한",
            "신맛의"
        )
    }

    fun getWineTypeList(): List<String> {
        return listOf(
            "레드 와인",
            "화이트 와인",
            "로제 와인",
            "스파클링 와인",
            "스위트 와인",
            "포티파이드 와인"
        )
    }

    fun getBeerTypeList(): List<Pair<String, List<String>>> {
        return listOf(
            Pair(
                "라거",
                listOf(
                    "일반라거",
                    "페일라거",
                    "필스너",
                    "둔켈 / 다크라거",
                    "그 외 라거"
                )
            ),
            Pair(
                "에일",
                listOf(
                    "페일에일",
                    "IPA",
                    "포터 / 스타우트",
                    "위트비어 / 윗비어",
                    "바이젠",
                    "그 외 에일"
                )
            )
        )
    }

    fun getBeerFlavorList(): List<String> {
        return listOf(
            "청량한",
            "씁쓸한",
            "고소한",
            "달달한",
            "텁텁한",
            "스파이시한",
            "꽃향의",
            "신맛의",
            "과일향의"
        )
    }

    fun getMinPrice(): Int {
        return 1000
    }

    fun getMaxPrice(): Int {
        return 6050000
    }

    fun submit(data: RecommendInfoData): ApiLiveData<Any> {
        return repository.submit(data)
    }

    fun createModel(binding: FragmentRecommendInfoDetailBinding): RecommendInfoData? {

        val liquorData =
            if (checkLiquor.value == true)
                RecommendInfoLiquor(
                    convertChipToList(binding.layoutLiquorTypeContent),
                    convertChipToList(binding.layoutLiquorFlavorContent)
                )
            else
                null

        val wineData =
            if (checkWine.value == true)
                RecommendInfoWine(
                    convertChipToList(binding.layoutWineTypeContent),
                    binding.seekBarWineBody.seekBar.progress,
                    binding.seekBarWineFlavor.seekBar.progress
                )
            else
                null

        val beerData =
            if (checkBeer.value == true) {
                val mainType = convertChipToList(binding.layoutBeerTypeContent)
                val subType = mutableListOf<String>()
                for (layout in binding.layoutBeerTypeContent.children) {
                    if (layout !is ChipGroup) continue
                    subType.addAll(convertChipToList(layout))
                }

                RecommendInfoBeer(
                    RecommendInfoBeerType(mainType, subType),
                    convertChipToList(binding.layoutBeerFlavorContent)
                )
            } else {
                null
            }

        val minPrice = if(binding.etPriceLow.text.isEmpty())
            getMinPrice() else binding.etPriceLow.text.toString().toInt()
        val maxPrice = if(binding.etPriceHigh.text.isEmpty())
            getMaxPrice() else binding.etPriceHigh.text.toString().toInt()

        val data = RecommendInfoData(
            binding.rangeAbv.values[0].toInt(),
            binding.rangeAbv.values[1].toInt(),
            minPrice,
            maxPrice,
            when (binding.chipGroupCarbotanted.checkedChipId) {
                R.id.chip_carbonated_yes -> "유"
                R.id.chip_carbonated_no -> "무"
                R.id.chip_carbonated_nothing -> "상관없음"
                else -> ""
            },
            RecommendInfoAlcoholContainer(liquorData, wineData, beerData)
        )

        return data
    }

    private fun convertChipToList(layout: ViewGroup): List<String> {
        return convertChipToList(layout.children)
    }

    private fun convertChipToList(viewList: Sequence<View>): List<String> {
        val list = mutableListOf<String>()
        for (chip in viewList) {
            if (chip !is Chip) continue
            if (chip.isChecked) {
                list.add(chip.text.toString())
            }
        }

        return list
    }

    sealed class RecommendInfoValidateFail : ApiStatus.ValidateFail() {

    }
}
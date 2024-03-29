package com.doubleslas.fifith.alcohol.dto

import com.doubleslas.fifith.alcohol.dto.interfaces.IPageLoadData
import com.google.gson.annotations.SerializedName

data class SearchList (
    @SerializedName("searchList") private val list: List<AlcoholSimpleData>,
    @SerializedName("totalCnt") private val totalCount: Int
) : IPageLoadData<AlcoholSimpleData>{
    override fun getTotalCount(): Int {
        return totalCount
    }

    override fun getList(): List<AlcoholSimpleData> {
        return list
    }

}
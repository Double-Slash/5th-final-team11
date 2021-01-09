package com.doubleslas.fifith.alcohol.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.doubleslas.fifith.alcohol.R
import com.doubleslas.fifith.alcohol.databinding.FragmentAlcoholListBinding
import com.doubleslas.fifith.alcohol.enum.SortType
import com.doubleslas.fifith.alcohol.model.network.base.ApiStatus
import com.doubleslas.fifith.alcohol.model.network.dto.AlcoholSimpleData
import com.doubleslas.fifith.alcohol.model.network.dto.IPageList
import com.doubleslas.fifith.alcohol.ui.common.base.BaseFragment
import com.doubleslas.fifith.alcohol.ui.detail.AlcoholDetailActivity
import com.doubleslas.fifith.alcohol.ui.search.SearchMainFragment
import com.doubleslas.fifith.alcohol.viewmodel.AlcoholListViewModel
import com.doubleslas.fifith.alcohol.viewmodel.ISortedPageLoader

class AlcoholListFragment private constructor() : BaseFragment<FragmentAlcoholListBinding>() {
    private val loader by lazy {
        arguments!!
            .getParcelable<ISortedPageLoader<IPageList<AlcoholSimpleData>>>(ARGUMENT_LOAD_INTERFACE)!!
    }

    private val listViewModel by lazy {
        ViewModelProvider(this, AlcoholListViewModel.Factory(loader))
            .get(AlcoholListViewModel::class.java)
    }
    private val adapter by lazy { AlcoholListAdapter() }

    private val sortDialog by lazy { SortBottomSheetDialog() }
    private var sortType: SortType? = null

    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAlcoholListBinding {
        return FragmentAlcoholListBinding.inflate(inflater, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.let {
            it.recyclerview.layoutManager = LinearLayoutManager(context)
            it.recyclerview.adapter = adapter

            it.tvSort.text = getString(R.string.sort_popular)
            it.tvSort.setOnClickListener {
                sortDialog.show(fragmentManager!!, null)
            }
        }

        adapter.setOnItemClickListener {
            val intent = AlcoholDetailActivity.getStartIntent(context!!, it.aid)
            startActivity(intent)
        }

        listViewModel.loadList()

        listViewModel.listLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is ApiStatus.Success -> {
                    adapter.setData(it.data)
                }
            }
        })

        sortDialog.setOnSortSelectListener {
            setSort(it)

            // Search 화면 일경우 다른 Category Fragment 에도 전달 해야함
            val pf = parentFragment as? SearchMainFragment
            pf?.setSort(this, it)
        }
    }

    override fun onResume() {
        super.onResume()
        if (sortType != null) {
            processSort()
        }
    }

    fun setSort(sortType: SortType) {
        this.sortType = sortType
        if (isResumed) {
            processSort()
        }
    }

    private fun processSort() {
        listViewModel.setSort(sortType!!)
        binding?.tvSort?.text = sortType!!.text
        sortType = null
    }

    companion object {
        const val ARGUMENT_LOAD_INTERFACE = "ARGUMENT_LOAD_INTERFACE"

        fun <T : IPageList<AlcoholSimpleData>> create(mInterface: ISortedPageLoader<T>): AlcoholListFragment {
            val fragment = AlcoholListFragment()
            fragment.arguments = Bundle().apply {
                putParcelable(ARGUMENT_LOAD_INTERFACE, mInterface)
            }
            return fragment
        }
    }
}
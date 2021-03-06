package com.doubleslas.fifith.alcohol.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.doubleslas.fifith.alcohol.App
import com.doubleslas.fifith.alcohol.R
import com.doubleslas.fifith.alcohol.databinding.FragmentSearchHistoryBinding
import com.doubleslas.fifith.alcohol.ui.common.base.BaseFragment


class SearchHistoryFragment : BaseFragment<FragmentSearchHistoryBinding>() {
    private val adapter by lazy { SearchHistoryAdapter() }
    private val vm by lazy { ViewModelProvider(this).get(SearchViewModel::class.java) }

    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSearchHistoryBinding {
        return FragmentSearchHistoryBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.let { b ->
            b.etSearch.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search(b.etSearch.text.toString())
                    return@setOnEditorActionListener true
                }

                return@setOnEditorActionListener false
            }

            b.btnSearch.setOnClickListener {
                search(b.etSearch.text.toString())
            }

            b.rvSearchHistory.layoutManager = LinearLayoutManager(context)
            b.rvSearchHistory.adapter = adapter
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setHasOptionsMenu(true)
        binding?.let { b ->
            setSupportActionBar(b.toolbar)
            getSupportActionBar()?.let {
                it.setDisplayHomeAsUpEnabled(true)
                it.setHomeAsUpIndicator(R.drawable.ic_back_12)
                it.setDisplayShowTitleEnabled(false)
            }
        }

        adapter.setOnItemClickListener(object : SearchHistoryAdapter.OnItemClickListener {
            override fun onItemClick(position: Int, keyword: String) {
                search(keyword)
            }

            override fun onDeleteClick(position: Int, keyword: String) {
                vm.deleteHistory(position)
                adapter.notifyItemRemoved(position)
            }

        })
    }


    override fun onResume() {
        super.onResume()
        adapter.setList(vm.getHistoryList())
        requestInit()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) requestInit()
        else App.hideKeyboard(binding?.etSearch)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                activity?.onBackPressed()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun search(keyword: String) {
        if (keyword.isEmpty()) return

        vm.search(keyword)
        (parentFragment as? SearchFragment)?.openSearchResultFragment(keyword)
    }

    private fun requestInit() {
        binding?.etSearch?.requestFocus()
        App.showKeyboard(binding?.etSearch)
    }

}
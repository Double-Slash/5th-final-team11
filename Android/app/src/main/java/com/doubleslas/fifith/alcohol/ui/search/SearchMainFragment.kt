package com.doubleslas.fifith.alcohol.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.doubleslas.fifith.alcohol.R
import com.doubleslas.fifith.alcohol.databinding.FragmentSearchMainBinding
import com.doubleslas.fifith.alcohol.databinding.TabCustomBinding
import com.doubleslas.fifith.alcohol.ui.common.base.BaseFragment
import com.google.android.material.tabs.TabLayoutMediator


class SearchMainFragment : BaseFragment<FragmentSearchMainBinding>() {
    private val categoryList by lazy {
        listOf(
            Pair(getString(R.string.category_all), "전체"),
            Pair(getString(R.string.category_liquor), "양주"),
            Pair(getString(R.string.category_wine), "와인"),
            Pair(getString(R.string.category_beer), "세계맥주")
        )
    }
    private lateinit var adapter: CategoryViewPagerAdapter
    private val fragmentList by lazy {
        List(categoryList.size) {
            val category = categoryList[it].second
            SearchListFragment.create(category)
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSearchMainBinding {
        return FragmentSearchMainBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = CategoryViewPagerAdapter()

        binding?.let { b ->
            b.toolbar.inflateMenu(R.menu.toolbar_menu)
            b.toolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu -> toolbarMenu.show(childFragmentManager!!, null)
                    else -> return@setOnMenuItemClickListener false
                }

                return@setOnMenuItemClickListener true
            }

            b.viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
            b.viewPager.adapter = adapter

            TabLayoutMediator(b.tabLayout, b.viewPager) { tab, position ->
                tab.text = categoryList[position].first
                tab.customView = getTabView(position)
            }.attach()

            b.layoutSearch.setOnClickListener {
                (parentFragment as? SearchFragment)?.openSearchHistoryFragment()
            }
        }
    }


    private fun getTabView(position: Int): View? {
        val b = TabCustomBinding.inflate(LayoutInflater.from(context))
        b.tvTab.text = categoryList[position].first
        return b.root
    }

    inner class CategoryViewPagerAdapter : FragmentStateAdapter(this) {
        override fun getItemCount(): Int {
            return categoryList.size
        }

        override fun createFragment(position: Int): Fragment {
            return fragmentList[position]
        }
    }
}
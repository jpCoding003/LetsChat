package com.tops.letschat.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.tops.letschat.fragments.ChatsFragment
import com.tops.letschat.fragments.ProfileFragment
import com.tops.letschat.fragments.RequestsFragment
import com.tops.letschat.fragments.UsersFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ChatsFragment()
            1 -> UsersFragment()
            2 -> RequestsFragment()
            3 -> ProfileFragment()
            else -> throw IllegalStateException("Invalid position: $position")
        }
    }
}

package com.farah.foodapp.admin;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class AdminPagerAdapter extends FragmentStateAdapter {

    public AdminPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new ActiveOrdersFragment();
            case 1: return new ManageMenuFragment();
            case 2: return new MyReelsFragment();
            default: return new ActiveOrdersFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

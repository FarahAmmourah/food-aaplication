package com.farah.foodapp.admin.admin_profile;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class AdminProfilePagerAdapter extends FragmentStateAdapter {

    public AdminProfilePagerAdapter(@NonNull FragmentActivity fa) {
        super(fa);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0)
            return new AdminProfileTabActivity();
        else
            return new StatsTabFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}

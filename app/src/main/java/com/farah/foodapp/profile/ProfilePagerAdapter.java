package com.farah.foodapp.profile;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.farah.foodapp.profile.rewards.AvailableRewardsActivity;

public class ProfilePagerAdapter extends FragmentStateAdapter {

    public ProfilePagerAdapter(@NonNull FragmentActivity fa) {
        super(fa);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new AvailableRewardsActivity();
        }
        return new ProfileTabActivity();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}

package com.farah.foodapp.profile;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.farah.foodapp.profile.rewards.AchievementsFragment;
import com.farah.foodapp.profile.rewards.AvailableRewardsFragment;

public class ProfilePagerAdapter extends FragmentStateAdapter {

    public ProfilePagerAdapter(@NonNull FragmentActivity fa) {
        super(fa);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new AvailableRewardsFragment();
            case 2:
                return new AchievementsFragment();
            default:
                return new ProfileTabFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

package com.farah.foodapp.admin;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.farah.foodapp.admin.activeorders.ActiveOrdersActivity;
import com.farah.foodapp.admin.admin_reels.MyReelsActivity;
import com.farah.foodapp.admin.managemenu.ManageMenuActivity;

public class AdminPagerAdapter extends FragmentStateAdapter {

    public AdminPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1: return new ManageMenuActivity();
            case 2: return new MyReelsActivity();
            case 0:
            default: return new ActiveOrdersActivity();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

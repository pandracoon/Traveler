package com.example.tripscheduler.Schedule;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class ScheduleAdapter extends FragmentStatePagerAdapter {
    int numCategories;
    Fragment fragment = null;

    String title;
    String email;

    public ScheduleAdapter(FragmentManager fm, int numCategories, String title, String email) {
        super(fm);
        this.numCategories = numCategories;
        this.title = title;
        this.email = email;
    }

    @Override
    public Fragment getItem(int position) {

        for (int i = 0; i < numCategories ; i++) {
            if (i == position) {
                fragment = ScheduleViewPagerFragment.newInstance(i, title, email);
                break;
            }
        }
        return fragment;

    }

    @Override
    public int getCount() {
        return numCategories;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return super.getPageTitle(position);
    }

}

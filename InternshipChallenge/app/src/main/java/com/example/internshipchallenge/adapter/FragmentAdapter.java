package com.example.internshipchallenge.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.internshipchallenge.fragment.PostFragment;
import com.example.internshipchallenge.fragment.TagFragment;

public class FragmentAdapter extends FragmentStateAdapter {
    public FragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new PostFragment();

            case 1:
                return new TagFragment();


            default:
                return new PostFragment();
        }

    }

    @Override
    public int getItemCount() {
        return 2;
    }
}

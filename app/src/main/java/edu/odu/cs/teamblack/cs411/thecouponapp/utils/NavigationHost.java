package edu.odu.cs.teamblack.cs411.thecouponapp.utils;

import androidx.fragment.app.Fragment;

public interface NavigationHost {
    void navigateTo(Fragment fragment, boolean addToBackstack);
}

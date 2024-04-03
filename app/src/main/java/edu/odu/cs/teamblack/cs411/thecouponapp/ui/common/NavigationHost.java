package edu.odu.cs.teamblack.cs411.thecouponapp.ui.common;

import androidx.fragment.app.Fragment;

public interface NavigationHost {
    void navigateTo(Fragment fragment, boolean addToBackstack);
}

package edu.odu.cs.teamblack.cs411.thecouponapp;

import static org.junit.Assert.assertEquals;

import android.Manifest;
import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

public class PermissionInstrumentedTest {
    @Test
    public void backgroundLocationPermission() throws Exception {
        //should fail

    }
    @Rule
    public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
}


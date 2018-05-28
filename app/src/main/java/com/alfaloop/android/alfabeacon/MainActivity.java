/**
 * © Copyright AlfaLoop Technology Co., Ltd. 2018
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/
package com.alfaloop.android.alfabeacon;

import android.content.pm.PackageManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.view.View;

import com.alfaloop.android.alfabeacon.base.MySupportActivity;
import com.alfaloop.android.alfabeacon.base.MySupportFragment;
import com.alfaloop.android.alfabeacon.fragment.ScannerFragment;
import com.polidea.rxandroidble2.RxBleClient;

import me.yokeyword.fragmentation.anim.FragmentAnimator;

public class MainActivity extends MySupportActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSION_COARSE_LOCATION = 0;
    private static final int REQUEST_ENABLE_BT = 1;

    private DrawerLayout mDrawer;
    private NavigationView mNavigationView;
    private RxBleClient rxBleClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        return super.onCreateFragmentAnimator();
    }

    private void initView() {
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = mNavigationView.getHeaderView(0);
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer.closeDrawer(GravityCompat.START);
                mDrawer.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // goProfile();
                    }
                }, 250);
            }
        });

        rxBleClient = RxBleClient.create(this);
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] { android.Manifest.permission.ACCESS_COARSE_LOCATION }, REQUEST_PERMISSION_COARSE_LOCATION);
        }

        MySupportFragment fragment = findFragment(ScannerFragment.class);
        if (fragment == null) {
            loadRootFragment(R.id.fragment_container, ScannerFragment.newInstance());
        }
    }

    // And catch the result like this:
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                                      int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_COARSE_LOCATION) {
            for (String permission : permissions) {
                if (android.Manifest.permission.ACCESS_COARSE_LOCATION.equals(permission)) {
                    // Do stuff if permission granted
                }
            }

        }
    }

    @Override
    public void onBackPressedSupport() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
                pop();
            } else {
                moveTaskToBack(true);
            }
        }
    }

    public RxBleClient getRxBluetooth() {return rxBleClient; }

}

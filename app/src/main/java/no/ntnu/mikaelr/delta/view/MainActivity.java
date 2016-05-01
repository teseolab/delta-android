package no.ntnu.mikaelr.delta.view;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import no.ntnu.mikaelr.delta.R;
import no.ntnu.mikaelr.delta.fragment.AddImageDialog;
import no.ntnu.mikaelr.delta.listener.ProjectDialogClickListener;
import no.ntnu.mikaelr.delta.presenter.MainPresenterImpl;
import no.ntnu.mikaelr.delta.presenter.signature.MainPresenter;
import no.ntnu.mikaelr.delta.util.Constants;
import no.ntnu.mikaelr.delta.util.SharedPrefsUtil;
import no.ntnu.mikaelr.delta.view.signature.MainView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MainView, AdapterView.OnItemClickListener,
        DrawerLayout.DrawerListener, ProjectDialogClickListener, AddImageDialog.AddImageDialogListener {

    private MainPresenter presenter;

    private DrawerLayout drawerView;
    private ActionBarDrawerToggle drawerToggle;
    private ListView drawerList;

//    private List<Fragment> fragments;

    private MapFragment mapFragment;
    private ProfileFragment profileFragment;
    private TopListFragment topListFragment;
    private ActivityLogFragment activityLogFragment;

    private int clickedDrawerMenuPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        presenter = new MainPresenterImpl(this);

        ToolbarUtil.initializeToolbar(this, R.drawable.ic_menu_white_24dp, getString(R.string.app_name));
        initializeNavigationDrawer();

        mapFragment = new MapFragment();
        profileFragment = new ProfileFragment();
        topListFragment = new TopListFragment();
        activityLogFragment = new ActivityLogFragment();

//        fragments = new ArrayList<Fragment>();
//        fragments.add(mapFragment);
//        fragments.add(profileFragment);
//        fragments.add(topListFragment);
//        fragments.add(activityLogFragment);

        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mapFragment).commit();
        ToolbarUtil.setTitle(this, "Delta");

    }

    // Initialization methods ------------------------------------------------------------------------------------------

    private void initializeNavigationDrawer() {
        drawerView = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerView.setDrawerListener(this);

        drawerToggle = new ActionBarDrawerToggle(this, drawerView, 0, 0);

        drawerList = (ListView) findViewById(R.id.drawer_menu);
        drawerList.setOnItemClickListener(this);
        drawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.list_item_drawer, presenter.getDrawerMenuItems()));
        drawerList.setItemChecked(0, true);
    }

    // Activity methods ------------------------------------------------------------------------------------------------

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    // Drawer menu listener --------------------------------------------------------------------------------------------

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        clickedDrawerMenuPosition = position;
        drawerView.closeDrawer(drawerList);
    }

    // Drawer listener -------------------------------------------------------------------------------------------------

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(View drawerView) {

    }

    @Override
    public void onDrawerClosed(View drawerView) {
        if (clickedDrawerMenuPosition == 0) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mapFragment).commit();
            ToolbarUtil.setTitle(this, "Delta");
        }

        else if (clickedDrawerMenuPosition == 1) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, profileFragment).commit();
            ToolbarUtil.setTitle(this, "Profil");
        }

        else if (clickedDrawerMenuPosition == 2) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, topListFragment).commit();
            ToolbarUtil.setTitle(this, "Toppliste");
        }
        else if (clickedDrawerMenuPosition == 3) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, activityLogFragment).commit();
            ToolbarUtil.setTitle(this, "Min aktivitet");
        }
        else if (clickedDrawerMenuPosition == 4) {
            SharedPrefsUtil.getInstance().setCookie("");
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

//    private void showOrAddFragment(Fragment f) {
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        for (Fragment fragment : fragments) {
//            if (fragment.equals(f)) {
//                if (fragment.isAdded()) {
//                    ft.show(fragment);
//                }
//                else {
//                    ft.add(R.id.content_frame, fragment);
//                }
//            } else if (fragment.isVisible()) {
//                ft.hide(fragment);
//            }
//        }
//        ft.commit();
//    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

    @Override
    public void onProjectDialogPositiveButtonClick(int projectId) {

    }

    @Override
    public void onProjectDialogNegativeButtonClick() {

    }

    // TODO: 30.04.2016
    // The dialog to add image is designed to be attached to Activities.
    // So even when the dialog is created in ProfileFragment
    // the Activity the Fragment is attached to (this) must be the callback listener.
    // Can a Dialog be made that can be attached to both Activities and Fragments?

    @Override
    public void onTakePhotoClicked(Dialog dialog) {
        dialog.dismiss();
        profileFragment.openCamera();
    }

    @Override
    public void onSelectPhotoClicked(Dialog dialog) {
        dialog.dismiss();
        profileFragment.openGallery();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}

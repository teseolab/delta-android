package no.ntnu.mikaelr.delta.view;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
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
import no.ntnu.mikaelr.delta.util.SharedPrefsUtil;
import no.ntnu.mikaelr.delta.view.signature.MainView;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MainView, AdapterView.OnItemClickListener,
        DrawerLayout.DrawerListener, ProjectDialogClickListener, AddImageDialog.AddImageDialogListener,
        FragmentManager.OnBackStackChangedListener {

    private DrawerLayout drawerView;
    private ActionBarDrawerToggle drawerToggle;
    private ListView drawerList;

    private FragmentManager fragmentManager;

    private MapFragment mapFragment;
    private ProfileFragment profileFragment;

    private int clickedDrawerMenuPosition;

    private final String MENU_ITEM_MAIN = "Delta";
    private final String MENU_ITEM_PROFILE = "Profil";
    private final String MENU_ITEM_TOP_LIST = "Toppliste";
    private final String MENU_ITEM_ACTIVITY_LOG = "Min aktivitet";
    private final String MENU_ITEM_LOG_OUT = "Logg ut";

    private final SparseArray<String> menuItemMap = new SparseArray<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ToolbarUtil.initializeToolbar(this, R.drawable.ic_menu_white_24dp, MENU_ITEM_MAIN);

        menuItemMap.put(0, MENU_ITEM_MAIN);
        menuItemMap.put(1, MENU_ITEM_PROFILE);
        menuItemMap.put(2, MENU_ITEM_TOP_LIST);
        menuItemMap.put(3, MENU_ITEM_ACTIVITY_LOG);
        menuItemMap.put(4, MENU_ITEM_LOG_OUT);

        initializeNavigationDrawer();

        fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(this);

        mapFragment = new MapFragment();

        fragmentManager
                .beginTransaction()
                .replace(R.id.content_frame, mapFragment, MENU_ITEM_MAIN)
                .commit();

    }

    // Initialization methods ------------------------------------------------------------------------------------------

    private void initializeNavigationDrawer() {
        drawerView = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerView.setDrawerListener(this);

        drawerToggle = new ActionBarDrawerToggle(this, drawerView, 0, 0);

        drawerList = (ListView) findViewById(R.id.drawer_menu);
        drawerList.setOnItemClickListener(this);

        List<String> menuItems = Arrays.asList(
                menuItemMap.get(0),
                menuItemMap.get(1),
                menuItemMap.get(2),
                menuItemMap.get(3),
                menuItemMap.get(4));

        drawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item_drawer, menuItems));
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
        String title = menuItemMap.get(position);
        ToolbarUtil.setTitle(this, title);
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
            Fragment visibleFragment = fragmentManager.findFragmentById(R.id.content_frame);
            if (!(visibleFragment instanceof MapFragment)) {
                fragmentManager.beginTransaction().remove(visibleFragment).commit();
                fragmentManager.popBackStack();
            }
        }

        else if (clickedDrawerMenuPosition == 1) {
            Fragment visibleFragment = fragmentManager.findFragmentById(R.id.content_frame);
            if (!(visibleFragment instanceof ProfileFragment)) {
                profileFragment = new ProfileFragment();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.content_frame, profileFragment, MENU_ITEM_PROFILE);
                if (visibleFragment instanceof MapFragment) {
                    transaction.addToBackStack("Main");
                }
                transaction.commit();
            }
        }

        else if (clickedDrawerMenuPosition == 2) {
            Fragment visibleFragment = fragmentManager.findFragmentById(R.id.content_frame);
            if (!(visibleFragment instanceof TopListFragment)) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.content_frame, new TopListFragment(), MENU_ITEM_TOP_LIST);
                if (visibleFragment instanceof MapFragment) {
                    transaction.addToBackStack("Main");
                }
                transaction.commit();
            }
        }

        else if (clickedDrawerMenuPosition == 3) {
            Fragment visibleFragment = fragmentManager.findFragmentById(R.id.content_frame);
            if (!(visibleFragment instanceof ActivityLogFragment)) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.content_frame, new ActivityLogFragment(), MENU_ITEM_ACTIVITY_LOG);
                if (visibleFragment instanceof MapFragment) {
                    transaction.addToBackStack("Main");
                }
                transaction.commit();
            }
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

    // -----------------------------------------------------------------------------------------------------------------
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

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        Fragment visibleFragment = fragmentManager.findFragmentById(R.id.content_frame);
        if (!(visibleFragment instanceof MapFragment)) {
            fragmentManager.beginTransaction().remove(visibleFragment).commit();
        }
        super.onBackPressed();
    }

    @Override
    public void onBackStackChanged() {
        Fragment visibleFragment = fragmentManager.findFragmentById(R.id.content_frame);
        String fragmentTag = visibleFragment.getTag();
        drawerList.setItemChecked(menuItemMap.indexOfValue(fragmentTag), true);
        ToolbarUtil.setTitle(this, fragmentTag);
    }
}

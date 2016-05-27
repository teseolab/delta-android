package no.ntnu.mikaelr.delta.view;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import no.ntnu.mikaelr.delta.R;
import no.ntnu.mikaelr.delta.fragment.AddImageDialog;
import no.ntnu.mikaelr.delta.listener.ProjectDialogClickListener;
import no.ntnu.mikaelr.delta.model.Project;
import no.ntnu.mikaelr.delta.presenter.MainPresenterImpl;
import no.ntnu.mikaelr.delta.presenter.signature.MainPresenter;
import no.ntnu.mikaelr.delta.util.SharedPrefsUtil;
import no.ntnu.mikaelr.delta.view.signature.MainView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MainView, AdapterView.OnItemClickListener,
        DrawerLayout.DrawerListener, ProjectDialogClickListener, AddImageDialog.AddImageDialogListener {

    private DrawerLayout drawerView;
    private ActionBarDrawerToggle drawerToggle;
    private ListView drawerList;

    private FragmentManager fragmentManager;

    private ProjectListFragment projectListFragment;
    private ProfileFragment profileFragment;

    private int clickedDrawerMenuPosition = 0;
    private int previousDrawerMenuPosition = 0;
    private boolean showsMap = false;
    private boolean mapIsLoading = false;

    private final String MENU_ITEM_MAIN = "Prosjekter";
    private final String MENU_ITEM_PROFILE = "Profil";
    private final String MENU_ITEM_TOP_LIST = "Toppliste";
    private final String MENU_ITEM_ACTIVITY_LOG = "Min aktivitet";
    private final String MENU_ITEM_LOG_OUT = "Logg ut";

    private final SparseArray<String> menuItemMap = new SparseArray<String>();
    private MainPresenter presenter;

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

        presenter = new MainPresenterImpl(this);
        presenter.loadProjects();

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
            MenuItem toggleMapButton = menu.findItem(R.id.action_show_project_list);

        if (clickedDrawerMenuPosition == 0) {

            ToolbarUtil.showSpinner(toggleMapButton, mapIsLoading);
            Drawable icon = showsMap ?
                    ResourcesCompat.getDrawable(getResources(), R.drawable.ic_list_white_24dp, null)
                    : ResourcesCompat.getDrawable(getResources(), R.drawable.ic_room_white_24dp, null);
            toggleMapButton.setIcon(icon);
            toggleMapButton.setVisible(true);

            if (presenter.getProjects() == null && icon != null) {
                ToolbarUtil.showSpinner(toggleMapButton, false);
                icon.mutate().setColorFilter(ContextCompat.getColor(this, R.color.white_trans_30), PorterDuff.Mode.SRC_IN);
                toggleMapButton.setEnabled(false);
            } else {
                toggleMapButton.setEnabled(true);
            }
        }

        else {
            toggleMapButton.setVisible(false);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_show_project_list) {
            if (showsMap) {
                fragmentManager.beginTransaction().replace(R.id.content_frame, projectListFragment, MENU_ITEM_MAIN).commit();
            } else {
                setMapIsLoading(true);
                MapFragment mapFragment = MapFragment.newInstance(presenter.getProjects());
                fragmentManager.beginTransaction().add(R.id.content_frame, mapFragment, MENU_ITEM_MAIN).commit();
            }
            showsMap = !showsMap;
            supportInvalidateOptionsMenu();
            return true;
        }
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
        if (clickedDrawerMenuPosition == 4) {
            SharedPrefsUtil.getInstance().setCookie("");
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        else {
            if (clickedDrawerMenuPosition == 0) {
                if (projectListFragment == null) {
                    presenter.loadProjects();
                } else if (previousDrawerMenuPosition != 0) {
                    fragmentManager.beginTransaction().replace(R.id.content_frame, projectListFragment, MENU_ITEM_MAIN).commit();
                    showsMap = false;
                }
            } else if (clickedDrawerMenuPosition == 1 && previousDrawerMenuPosition != 1) {
                profileFragment = new ProfileFragment();
                fragmentManager.beginTransaction().replace(R.id.content_frame, profileFragment, MENU_ITEM_PROFILE).commit();
            } else if (clickedDrawerMenuPosition == 2 && previousDrawerMenuPosition != 2) {
                fragmentManager.beginTransaction().replace(R.id.content_frame, new TopListFragment(), MENU_ITEM_TOP_LIST).commit();
            } else if (clickedDrawerMenuPosition == 3 && previousDrawerMenuPosition != 3) {
                fragmentManager.beginTransaction().replace(R.id.content_frame, new ActivityLogFragment(), MENU_ITEM_ACTIVITY_LOG).commit();
            }
            supportInvalidateOptionsMenu();
        }
        previousDrawerMenuPosition = clickedDrawerMenuPosition;
    }

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
        // TODO: Refactoring
        if (previousDrawerMenuPosition == 0) {
            if (showsMap) {
                fragmentManager.beginTransaction().replace(R.id.content_frame, projectListFragment, MENU_ITEM_MAIN).commit();
            } else {
                super.onBackPressed();
            }
        } else {
            fragmentManager.beginTransaction().replace(R.id.content_frame, projectListFragment, MENU_ITEM_MAIN).commit();
            drawerList.setItemChecked(0, true);
            ToolbarUtil.setTitle(this, menuItemMap.get(0));
            previousDrawerMenuPosition = 0;
            clickedDrawerMenuPosition = 0;
        }
        showsMap = false;
        supportInvalidateOptionsMenu();
    }

    @Override
    public void addProjectListFragment(ArrayList<Project> projects) {
        projectListFragment = ProjectListFragment.newInstance(projects);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, projectListFragment, MENU_ITEM_MAIN).commitAllowingStateLoss();
    }

    @Override
    public void removeProjectListFragment() {
        fragmentManager.beginTransaction().remove(projectListFragment).commit();
    }

    @Override
    public void setMapIsLoading(boolean isLoading) {
        mapIsLoading = isLoading;
    }

    @Override
    public void showMessage(String message, int length) {
        Toast.makeText(this, message, length).show();
    }

    @Override
    public void addEmptyMessageFragment() {
        fragmentManager.beginTransaction().replace(R.id.content_frame, new SimpleMessageFragment()).commitAllowingStateLoss();
    }

}

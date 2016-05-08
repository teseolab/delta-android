package no.ntnu.mikaelr.delta.view;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
        DrawerLayout.DrawerListener, ProjectDialogClickListener, AddImageDialog.AddImageDialogListener,
        FragmentManager.OnBackStackChangedListener {

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
        fragmentManager.addOnBackStackChangedListener(this);

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
        ToolbarUtil.showSpinner(toggleMapButton, mapIsLoading);

        Drawable icon = showsMap ?
                ResourcesCompat.getDrawable(getResources(), R.drawable.ic_cards_white_24dp, null)
                : ResourcesCompat.getDrawable(getResources(), R.drawable.ic_map_white_24dp, null);
        toggleMapButton.setIcon(icon);

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

            if (clickedDrawerMenuPosition == 0 && previousDrawerMenuPosition != 0) {
                if (showsMap) {
                    setMapIsLoading(true);
                    MapFragment mapFragment = MapFragment.newInstance(presenter.getProjects());
                    fragmentManager.beginTransaction().add(R.id.content_frame, mapFragment, MENU_ITEM_MAIN).commit();
                } else {
                    fragmentManager.beginTransaction().replace(R.id.content_frame, projectListFragment, MENU_ITEM_MAIN).commit();
                }
            } else if (clickedDrawerMenuPosition == 1 && previousDrawerMenuPosition != 1) {
                fragmentManager.beginTransaction().replace(R.id.content_frame, new ProfileFragment(), MENU_ITEM_PROFILE).commit();
            } else if (clickedDrawerMenuPosition == 2 && previousDrawerMenuPosition != 2) {
                fragmentManager.beginTransaction().replace(R.id.content_frame, new TopListFragment(), MENU_ITEM_TOP_LIST).commit();
            } else if (clickedDrawerMenuPosition == 3 && previousDrawerMenuPosition != 3) {
                fragmentManager.beginTransaction().replace(R.id.content_frame, new ActivityLogFragment(), MENU_ITEM_ACTIVITY_LOG).commit();
            }

//            if (clickedDrawerMenuPosition == 0 && previousDrawerMenuPosition != 0) {
//                Fragment visibleFragment = fragmentManager.findFragmentById(R.id.content_frame);
//                if (!(visibleFragment instanceof MapFragment || visibleFragment instanceof ProjectListFragment)) {
//                    fragmentManager.beginTransaction().remove(visibleFragment).commit();
//                    fragmentManager.popBackStack();
//                }
//            }
//
//            else {
//
//                if (clickedDrawerMenuPosition == 1 && previousDrawerMenuPosition != 1) {
//                    FragmentTransaction transaction = fragmentManager.beginTransaction();
//                    transaction.replace(R.id.content_frame, new ProfileFragment(), MENU_ITEM_PROFILE);
//                    if (previousDrawerMenuPosition == 0) {
//                        transaction.addToBackStack("Main");
//                    }
//                    transaction.commit();
//                }
//
//                else if (clickedDrawerMenuPosition == 2 && previousDrawerMenuPosition != 2) {
//                    FragmentTransaction transaction = fragmentManager.beginTransaction();
//                    transaction.replace(R.id.content_frame, new TopListFragment(), MENU_ITEM_TOP_LIST);
//                    if (previousDrawerMenuPosition == 0) {
//                        transaction.addToBackStack("Main");
//                    }
//                    transaction.commit();
//                } else if (clickedDrawerMenuPosition == 3 && previousDrawerMenuPosition != 3) {
//                    FragmentTransaction transaction = fragmentManager.beginTransaction();
//                    transaction.replace(R.id.content_frame, new ActivityLogFragment(), MENU_ITEM_ACTIVITY_LOG);
//                    if (previousDrawerMenuPosition == 0) {
//                        transaction.addToBackStack("Main");
//                    }
//                    transaction.commit();
//                }
//            }
        }

        previousDrawerMenuPosition = clickedDrawerMenuPosition;

        Log.w("BACKSTACKENTRYCOUNT", Integer.toString(fragmentManager.getBackStackEntryCount()));

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
//        Fragment visibleFragment = fragmentManager.findFragmentById(R.id.content_frame);
//        if (!(visibleFragment instanceof ProjectListFragment || visibleFragment instanceof MapFragment)) {
//            fragmentManager.beginTransaction().remove(visibleFragment).commit();
//            if (!showsMap) {
//                fragmentManager.beginTransaction().show(projectListFragment).hide(mapFragment).commit();
//            } else {
//                fragmentManager.beginTransaction().hide(projectListFragment).show(mapFragment).commit();
//            }
//        }
//        if (fragmentManager.getBackStackEntryCount() > 0) {
//            Fragment visibleFragment = fragmentManager.findFragmentById(R.id.content_frame);
//            fragmentManager.beginTransaction().remove(visibleFragment).commit();
//        }

//        Fragment visibleFragment = fragmentManager.findFragmentById(R.id.content_frame);
//        if (!(visibleFragment instanceof MapFragment || visibleFragment instanceof ProjectListFragment)) {
//            fragmentManager.beginTransaction().remove(visibleFragment).commit();
//        }

        if (previousDrawerMenuPosition == 0) {
            if (showsMap) {
                fragmentManager.beginTransaction().replace(R.id.content_frame, projectListFragment, MENU_ITEM_MAIN).commit();
                showsMap = false;
            } else {
                super.onBackPressed();
            }
        } else {
            showsMap = false;
            fragmentManager.beginTransaction().replace(R.id.content_frame, projectListFragment, MENU_ITEM_MAIN).commit();
            drawerList.setItemChecked(0, true);
            ToolbarUtil.setTitle(this, menuItemMap.get(0));
            previousDrawerMenuPosition = 0;
        }

    }

    @Override
    public void onBackStackChanged() {
//        Fragment visibleFragment = fragmentManager.findFragmentById(R.id.content_frame);
//        String fragmentTag = visibleFragment.getTag();
//        drawerList.setItemChecked(menuItemMap.indexOfValue(fragmentTag), true);
//        ToolbarUtil.setTitle(this, fragmentTag);
    }

    @Override
    public void addProjectListFragment(ArrayList<Project> projects) {
        projectListFragment = ProjectListFragment.newInstance(projects);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.add(R.id.content_frame, mapFragment, "KART");
        fragmentTransaction.replace(R.id.content_frame, projectListFragment, MENU_ITEM_MAIN).commit();
    }

    @Override
    public void removeProjectListFragment() {
        fragmentManager.beginTransaction().remove(projectListFragment).commit();
    }

    @Override
    public void setMapIsLoading(boolean isLoading) {
        mapIsLoading = isLoading;
    }
}

package no.ntnu.mikaelr.delta.view;

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
import no.ntnu.mikaelr.delta.listener.ProjectDialogClickListener;
import no.ntnu.mikaelr.delta.presenter.MainPresenter;
import no.ntnu.mikaelr.delta.presenter.MainPresenterImpl;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MainView, AdapterView.OnItemClickListener,
        DrawerLayout.DrawerListener, ProjectDialogClickListener {

    private MainPresenter presenter;

    private DrawerLayout drawerView;
    private ActionBarDrawerToggle drawerToggle;
    private ListView drawerList;

    List<Fragment> fragments;

    private MapFragment mapFragment;
    private ProfileFragment profileFragment;
    private TopListFragment topListFragment;
    private ActivityLog activityLog;

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
        activityLog = new ActivityLog();

        fragments = new ArrayList<Fragment>();
        fragments.add(mapFragment);
        fragments.add(profileFragment);
        fragments.add(topListFragment);
        fragments.add(activityLog);

        getSupportFragmentManager().beginTransaction().add(R.id.content_frame, mapFragment).commit();
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
            showOrAddFragment(mapFragment);
            ToolbarUtil.setTitle(this, "Delta");
        }

        else if (clickedDrawerMenuPosition == 1) {
            showOrAddFragment(profileFragment);
            ToolbarUtil.setTitle(this, "Profil");
        }

        else if (clickedDrawerMenuPosition == 2) {
            showOrAddFragment(topListFragment);
            ToolbarUtil.setTitle(this, "Toppliste");
        }
        else if (clickedDrawerMenuPosition == 3) {
            showOrAddFragment(activityLog);
            ToolbarUtil.setTitle(this, "Min aktivitet");
        }
    }

    private void showOrAddFragment(Fragment f) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        for (Fragment fragment : fragments) {
            if (fragment.equals(f)) {
                if (fragment.isAdded()) {
                    ft.show(fragment);
                }
                else {
                    ft.add(R.id.content_frame, fragment);
                }
            } else if (fragment.isVisible()) {
                ft.hide(fragment);
            }
        }
        ft.commit();
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
}

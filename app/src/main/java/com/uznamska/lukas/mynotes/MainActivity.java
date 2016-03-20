/*
 * Copyright (c) 2016.  Lukasz Fiszer
 */

package com.uznamska.lukas.mynotes;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.uznamska.lukas.mynotes.contentprovider.NotesContentProvider;
import com.uznamska.lukas.mynotes.items.NoteFactory;
import com.uznamska.lukas.mynotes.items.NoteRepository;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        RecyclerViewFragment.OnCardsSelectedListener,
        FragmentManager.OnBackStackChangedListener,
        Toolbar.OnMenuItemClickListener,
        View.OnClickListener,
        RecyclerViewFragment.OnListSyncedListener {

    private  int mNextItem = 0;
    GoogleUser mUser;

    class FragmentTransactionLoader {

        public void loadNoteFragment(int index, String type) {
            Log.d(TAG,"Loading type: " + type);
            NoteFragment noteFragment = new NoteFragment();
            mBackListener = noteFragment;
            Bundle args = new Bundle();
            args.putInt(NoteFragment.NEXT_POSITION, mNextItem);
            args.putString(NoteFragment.NOTE_TYPE, type);

            Uri noteUri = null;
            if(index >= 0) {
                noteUri = Uri.parse(NotesContentProvider.NOTES_CONTENT_URI + "/" + index);
            }
            args.putParcelable(NotesContentProvider.CONTENT_ITEM_TYPE, noteUri);

            noteFragment.setArguments(args);
            performTransaction(noteFragment, NoteFragment.FRAGMENT_NAME, true);
            mDrawerToggle.setDrawerIndicatorEnabled(false);
        }

        public void loadListFragment() {
            mRecycleFragment = new RecyclerViewFragment();
            performTransaction(mRecycleFragment, null, false);
        }

        private void performTransaction(Fragment fragment,String fragmentName, boolean isBackStack) {

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_view, fragment);
            if(isBackStack) {
                transaction.addToBackStack(fragmentName);
            }
            transaction.commit();
        }

        public void addAndLoad(String type) {
            //This is just load fragment no data,
            // data is going to be save and new note added after "the back button"
            // has been pressed in Fragment context.
            loadNoteFragment(-2, type);
        }
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Log.d(TAG, " menu item click");
        switch(item.getItemId()){
            case R.id.action_gallery :
                Log.d(TAG, "Gallery");
                NoteRepository.getInstance().populateWithGarbage();
                break;
            case(R.id.action_camera):
                Log.d(TAG, "Buton Camera");
                fragmentLoader.addAndLoad(NoteFactory.LIST_NOTE);
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG,"Onclick " + v.getTag());
        if(v.getTag().equals("take_note")) {
            Log.d(TAG,"Take not has been clicked");
            fragmentLoader.addAndLoad(NoteFactory.TEXT_NOTE);
        }
    }

    @Override
    public void onListSynced(int size) {
        mNextItem = size;
    }

    public interface BackPressedListener {
        void OnBackPressured();
    }

    private static final String TAG = "Notes:Activity";
    RecyclerViewFragment mRecycleFragment = null;
    ActionBarDrawerToggle mDrawerToggle = null;
    BackPressedListener mBackListener = null;
    FragmentTransactionLoader fragmentLoader = new FragmentTransactionLoader();
    Toolbar mToolbarBottom = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "ONCREATE");


        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            fragmentLoader.loadListFragment();
            // if(getSupportFragmentManager().getBackStackEntryCount() == 0) {
        }
            Intent intent = getIntent();
            String nameSurname = intent.getStringExtra("displayname");
            String imageUrl = intent.getStringExtra("imageurl");
            String useId = intent.getStringExtra("userid");
            String email = intent.getStringExtra("email");
            mUser = new GoogleUser.UserBuilder(nameSurname).
                    userId(useId).
                    imageUrl(imageUrl).
                    email(email).
                    build();
            Log.d(TAG, "name " + mUser.getNameSurname());
            getSupportFragmentManager().addOnBackStackChangedListener(this);

            new MenuBuilder().buildMenuStuff();
        onBackStackChanged();
        FragmentManager manager = getSupportFragmentManager();
        if(manager != null && savedInstanceState != null) {
            //updateAccordingToStackLevel(manager.getBackStackEntryCount());
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "On stop");
    }

    @Override
    public void onRestart() {
        super.onRestart();
        Log.d(TAG, "On restart");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "On start");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "On resume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "On pause");
    }

    @Override
    public void onBackPressed() {
        if(mBackListener != null)
            mBackListener.OnBackPressured();
        super.onBackPressed();
        if(mDrawerToggle != null)
            mDrawerToggle.setDrawerIndicatorEnabled(true);
        Log.d(TAG, "Back has been pressed");
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        Log.d(TAG, "onSaveInstanceState in Activity");
        super.onSaveInstanceState(bundle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        Log.d(TAG, "load menu");
         if(getSupportFragmentManager().getBackStackEntryCount() == 0) {
             getMenuInflater().inflate(R.menu.main, menu);
         }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.d(TAG, "Item clicked " + id);

        if( id == android.R.id.home) {
            Log.d(TAG, "Up button!" + id);
            onBackPressed();
            return true;
        } else if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Log.d(TAG, "On navigation item selected");
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onCardSelected(int index) {
        Log.d(TAG, "Main activity card " + index + " been clicked");

        //thats index from database
        fragmentLoader.loadNoteFragment(index, "");
    }

    @Override
    public void onBackStackChanged() {
        int level = getSupportFragmentManager().getBackStackEntryCount();
        Log.d(TAG, "Back stack changed to level " + level);
        updateAccordingToStackLevel(level);
    }

    public void updateAccordingToStackLevel(int stackLevel){
        if(stackLevel == 0) {
            Log.d(TAG, "Back stack changed to level main");
            ActionBar ab = getSupportActionBar();
            if (ab != null) {
                getSupportActionBar().setTitle(R.string.app_name);
                mBackListener = null;
                //getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                //getSupportActionBar().setDisplayShowHomeEnabled(false);
                // getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                //getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(
                        ContextCompat.getColor(getBaseContext(), R.color.colorPrimary)));
            }
            if(mToolbarBottom != null)
                mToolbarBottom.setVisibility(View.VISIBLE);
        } else {
            Log.d(TAG, "Back stack changed to level fragment");
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.rgb(255, 255, 255)));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            if(mToolbarBottom != null)
                mToolbarBottom.setVisibility(View.INVISIBLE);
        }
        invalidateOptionsMenu();
    }

    @Override
    public boolean onSupportNavigateUp() {
        //This method is called when the up button is pressed. Just the pop back stack.
        Log.d(TAG, "Navigate Up");
        getSupportFragmentManager().popBackStack();
        return true;
    }
//
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        mDrawerToggle.onConfigurationChanged(newConfig);
//    }

    class MenuBuilder {
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        public void buildMenuStuff() {
            Log.d(TAG, "BUILDMENUSTUFF");
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            if(toolbar != null) {
                toolbar.inflateMenu(R.menu.main);
                // inflater.inflate(R.menu.frag_menu, menu);
                toolbar.hideOverflowMenu();
                setSupportActionBar(toolbar);
            }

            mToolbarBottom = (Toolbar) findViewById(R.id.toolbar_bottom);
            if(mToolbarBottom != null) {
                mToolbarBottom.setBackground(new ColorDrawable(
                        Color.rgb(255, 255, 255)));
                TextView takeNote = (TextView) findViewById(R.id.btn_take_note);
                takeNote.setOnClickListener(MainActivity.this);
                mToolbarBottom.setOnMenuItemClickListener(MainActivity.this);
                // Inflate a menu to be displayed in the toolbar
                mToolbarBottom.inflateMenu(R.menu.main_bottom);
            }

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if(drawer != null) {
                mDrawerToggle = new ActionBarDrawerToggle(
                        MainActivity.this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

                    /**
                     * Called when a drawer has settled in a completely closed state.
                     */
                    public void onDrawerClosed(View view) {
                        super.onDrawerClosed(view);
                        invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                    }

                    /**
                     * Called when a drawer has settled in a completely open state.
                     */
                    public void onDrawerOpened(View drawerView) {
                        super.onDrawerOpened(drawerView);
                        invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                    }
                };
                drawer.setDrawerListener(mDrawerToggle);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);

                mDrawerToggle.syncState();

                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                navigationView.getHeaderView(0).setVisibility(View.GONE);
                View  headerView  = navigationView.inflateHeaderView(R.layout.nav_header_main);
                TextView tview = (TextView) headerView.findViewById(R.id.userName);
                TextView emailView = (TextView)headerView.findViewById(R.id.textView);
                tview.setText(mUser.getNameSurname());
                emailView.setText(mUser.getEmail());

                navigationView.setNavigationItemSelectedListener(MainActivity.this);
            }
        }
    }
}

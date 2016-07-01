package net.janusjanus.we4x4_v1;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {

    /** Variables **/

    public String UserID;

    private static final int REQUEST_SIGNUP = 0;

    /** layout elements **/

    private DrawerLayout mDrawerLayout;
    @Bind(R.id.registerBtn)
    Button ButtonRegister;

    @Bind(R.id.MembersDashBtn)
    Button MembersDashBtn;

    /** firebase links & variables **/

    Firebase firebaseRefUaers, firebaseRefData,firebaserRefMain;
    Firebase.AuthStateListener authStateListenerMain;
    AuthData authDataMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Firebase.setAndroidContext(this);
        ButterKnife.bind(this);

        // Adding Toolbar to Main screen
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Setting ViewPager for each Tabs
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        // Set Tabs inside Toolbar
        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        ButtonRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Start the Sign up activity
                Intent intent = new Intent(getApplicationContext(), RegisterSection.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });

        MembersDashBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Start the Members activity
                Intent intent = new Intent(getApplicationContext(), Member.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });


//        // Adding Floating Action Button to bottom right of main view
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Snackbar.make(v, "Hello Snackbar!",
//                        Snackbar.LENGTH_LONG).show();
//            }
//        });

        // Create Navigation drawer and inflate layout
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);

// Adding menu icon to Toolbar
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

// Set behavior of Navigation drawer
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    // This method will trigger on item Click of navigation menu
                    @Override
                    public boolean onNavigationItemSelected(MenuItem item) {
                        // Set item in checked state
                        int id = item.getItemId();
                        if(id == R.id.navHome) {
                            Toast.makeText(getApplicationContext(),
                                    "Home",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivityForResult(intent, REQUEST_SIGNUP);

                        }else if(id == R.id.navUpoad) {

                            Intent intent = new Intent(getApplicationContext(), upload.class);
                            startActivityForResult(intent, REQUEST_SIGNUP);

                        }else if (id == R.id.navChat) {

                            Intent intent = new Intent(getApplicationContext(), chatSection.class);
                            startActivityForResult(intent, REQUEST_SIGNUP);

                        }else if (id == R.id.navGPS) {
                            Toast.makeText(getApplicationContext(),
                                    "GPS",
                                    Toast.LENGTH_SHORT).show();
                        }else if (id == R.id. navInfo){

                            Intent intent = new Intent(getApplicationContext(), MyInfo.class);
                            startActivityForResult(intent, REQUEST_SIGNUP);

                        }else if(id == R.id.navRegister){
                            userStatCehck();
                            Toast.makeText(getApplicationContext(),
                                    "Register",
                                    Toast.LENGTH_SHORT).show();
                        }else if(id ==R.id.navLogout){
                            logout();
                            Toast.makeText(getApplicationContext(),
                                    "Logout",
                                    Toast.LENGTH_SHORT).show();
                        }else if (id == R.id.navAbout){
                            AlertDialog.Builder builder =
                                    new AlertDialog.Builder(MainActivity.this, R.style.AppTheme_Dark_Dialog);
                            builder.setTitle("About");
                            builder.setMessage("wi4x4 v 1.0 developed by A.A./JanusJanus@riseup.net");
                            builder.setPositiveButton("OK", null);
//                            builder.setNegativeButton("Cancel", null);
                            builder.show();
                        }
                        item.setChecked(true);
                        // TODO: handle navigation
                        // Closing drawer on item click
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });

        /** firebase links shortcuts **/

        firebaserRefMain = new Firebase(getResources().getString(R.string.firebase_url));
        firebaseRefData = new Firebase(getResources().getString(R.string.firebase_data));
        firebaseRefUaers = new Firebase(getResources().getString(R.string.firebase_users));

        if (savedInstanceState == null) {
            Firebase.setAndroidContext(this);
        }
        authStateListenerMain = new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                if(authData != null){

                    /** if authentication present setAuthenticatedUser as authData retrieved **/

                    setAuthenticatedUserMM(authData);

                }else{
                    /**if no authentication detected null user info, and clear layout elements
                     related to logged in user;**/

                //TODO: CHANGES BASED ON AUTHENTICATIONS

                }
            }
        };

        /** adding Authentication state listener **/
        firebaserRefMain.addAuthStateListener(authStateListenerMain);

    }

    /** When user authenticated update layout & request user Info. **/
    private void setAuthenticatedUserMM(AuthData authData) {
        if(authData != null) {

            this.authDataMain = authData;

            //TODO:CHANGES FOR ATUHENTICATED USER

            UserID = authData.getUid();
            ButtonRegister.setVisibility(View.GONE);
            MembersDashBtn.setVisibility(View.VISIBLE);

        }
    }

    // Add Fragments to Tabs

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new PicContentFragment(), "Pictures");
        adapter.addFragment(new LocationsContentFragment(), "Locations");
        viewPager.setAdapter(adapter);
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.navHome) {
            return true;
        }else if ( id == R.id.navUpoad) {
            return true;
        }else if(id == R.id.navChat) {
            return true;
        }else if (id == R.id. navGPS) {
            return true;
        }else if (id == R.id.navInfo){
            return true;
        } else if (id == R.id.navRegister) {
            return true;

        } else if (id == R.id.navLogout) {
            return true;
        } else if (id == R.id.navAbout) {
            return true;
        }else if(id == android.R.id.home){
            mDrawerLayout.openDrawer(GravityCompat.START);
        }

        return super.onOptionsItemSelected(item);
    }

    private void userStatCehck(){
        if(authDataMain !=null){
            Toast.makeText(getApplicationContext(),
                    "You have a user account active, logout to register a new account",
                    Toast.LENGTH_LONG).show();
        }else{
            Intent intent = new Intent(getApplicationContext(), RegisterSection.class);
            startActivityForResult(intent, REQUEST_SIGNUP);
        }
    }

    /** Actions whe user log out; null user Info. and clear layout elements related to user **/
    private void logout() {
        firebaserRefMain.unauth();
        authDataMain = null;
        setAuthenticatedUserMM(null);
        ButtonRegister.setVisibility(View.VISIBLE);
        MembersDashBtn.setVisibility(View.GONE);
    }
}
package net.janusjanus.we4x4_v1;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;


public class logLocation extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public String UserID;

    Button currentLocBtn, locationSubmit;
    EditText editTextLOC;

    private static final int REQUEST_SIGNUP = 0;

    private DrawerLayout mDrawerLayout;

    Firebase firebaseRefUsersMM, firebaseRefDataMM, firebaserRefMM;
    Firebase.AuthStateListener authStateListenerMM;
    AuthData authDataMM;
    long numOfAdrs;


    Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    String lat, lon, locationTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_log);
        Firebase.setAndroidContext(this);

        editTextLOC = (EditText) findViewById(R.id.editTextLOC);
        currentLocBtn = (Button) findViewById(R.id.currentLocBtn);
        locationSubmit = (Button) findViewById(R.id.locationSubmit);

        buildGoogleApiClient();

        // Adding Toolbar to Main screen
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Setting ViewPager for each Tabs
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        // Set Tabs inside Toolbar
        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        // Create Navigation drawer and inflate layout
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);

// Adding menu icon to Toolbar
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        currentLocBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO:REQUESTING LOCATION

                Log.i("MyTag_location", "button_clicked");

                checkSetting();

            }

        });

        locationSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitLocation();
            }
        });

// Set behavior of Navigation drawer
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    // This method will trigger on item Click of navigation menu
                    @Override
                    public boolean onNavigationItemSelected(MenuItem item) {
                        // Set item in checked state
                        int id = item.getItemId();
                        if (id == R.id.navHome) {
                            Toast.makeText(getApplicationContext(),
                                    "Home",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivityForResult(intent, REQUEST_SIGNUP);
                        } else if (id == R.id.navUpoad) {
                            Toast.makeText(getApplicationContext(),
                                    "Upload",
                                    Toast.LENGTH_SHORT).show();
                        } else if (id == R.id.navChat) {

                            Intent intent = new Intent(getApplicationContext(), chatSection.class);
                            startActivityForResult(intent, REQUEST_SIGNUP);

                        } else if (id == R.id.navGPS) {
                            Toast.makeText(getApplicationContext(),
                                    "GPS",
                                    Toast.LENGTH_SHORT).show();
                        } else if (id == R.id.navInfo) {
                            Toast.makeText(getApplicationContext(),
                                    "My Info.",
                                    Toast.LENGTH_SHORT).show();
                        } else if (id == R.id.navRegister) {
                            userStatCehck();
                            Toast.makeText(getApplicationContext(),
                                    "Register",
                                    Toast.LENGTH_SHORT).show();
                        } else if (id == R.id.navLogout) {
                            logout();
                            Toast.makeText(getApplicationContext(),
                                    "Logout",
                                    Toast.LENGTH_SHORT).show();
                        } else if (id == R.id.navAbout) {
                            AlertDialog.Builder builder =
                                    new AlertDialog.Builder(logLocation.this, R.style.AppTheme_Dark_Dialog);
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

        firebaserRefMM = new Firebase(getResources().getString(R.string.firebase_url));
        firebaseRefDataMM = new Firebase(getResources().getString(R.string.firebase_data));
        firebaseRefUsersMM = new Firebase(getResources().getString(R.string.firebase_users));

        if (savedInstanceState == null) {
            Firebase.setAndroidContext(this);
        }
        authStateListenerMM = new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                if (authData != null) {

                    /** if authentication present setAuthenticatedUser as authData retrieved **/

                    setAuthenticatedUserMM(authData);

                } else {
                    /**if no authentication detected null user info, and clear layout elements
                     related to logged in user;**/

                    //TODO: CHANGES BASED ON AUTHENTICATIONS

                }
            }
        };

        /** adding Authentication state listener **/
        firebaserRefMM.addAuthStateListener(authStateListenerMM);
    }


    /** When user authenticated update layout & request user Info. **/
    private void setAuthenticatedUserMM(AuthData authData) {
        if (authData != null) {

            this.authDataMM = authData;

            //TODO:CHANGES FOR ATUHENTICATED USER

            UserID = authData.getUid();
        }
    }

    public void submitLocation(){
        /** extracting # of addresses from Firebase **/

        firebaserRefMM.child("data").child("locations").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                numOfAdrs = (dataSnapshot.getChildrenCount() + 1);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        /** saving new location to firebase in different locations **/

        firebaserRefMM.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                firebaserRefMM.child("data").child("locations").child("location"+numOfAdrs).child("author").setValue(UserID);
                firebaserRefMM.child("data").child("locations").child("location"+numOfAdrs).child("latitude").setValue(lat);
                firebaserRefMM.child("data").child("locations").child("location"+numOfAdrs).child("longitude").setValue(lon);
                firebaserRefMM.child("locations").child("location"+numOfAdrs).setValue(locationTag);

//                        firebaserRefLC.child("locations").child("location"+numOfAdrs).setValue(curr);

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void checkSetting() {
        Log.i("MyTag_location", "checkingSettings");

        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

// check if enabled and if not send user to the GSP settings
// Better solution would be to display a dialog and suggesting to
// go to the settings
        if (!enabled) {


            AlertDialog.Builder builder = new AlertDialog.Builder(logLocation.this, R.style.AppTheme_Dark_Dialog);
            builder.setTitle("Enable GPS");
            builder.setMessage("You must enable GPS service from your device setting");
            builder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            builder.show();


        } else {
            buildGoogleApiClient();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {


        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(100); // Update location every second

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);


        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            lat = String.valueOf(mLastLocation.getLatitude());
            lon = String.valueOf(mLastLocation.getLongitude());

        }
        updateUI();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        lat = String.valueOf(location.getLatitude());
        lon = String.valueOf(location.getLongitude());
        locationTag = (lat + "-" + lon);
        updateUI();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        buildGoogleApiClient();
    }

    synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
    }

    void updateUI() {
        if(lat == null && lon == null){
            editTextLOC.setText("0.00 - 0.00");
        }else {


            editTextLOC.setText(lat + " - " + lon);
        }
    }

    // Add Fragments to Tabs

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
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
        } else if (id == R.id.navUpoad) {
            return true;
        } else if (id == R.id.navChat) {
            return true;
        } else if (id == R.id.navGPS) {
            return true;
        } else if (id == R.id.navInfo) {
            return true;
        } else if (id == R.id.navRegister) {
            return true;

        } else if (id == R.id.navLogout) {
            return true;
        } else if (id == R.id.navAbout) {
            return true;
        } else if (id == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }

        return super.onOptionsItemSelected(item);
    }


    private void userStatCehck(){
        if(authDataMM !=null){
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
        firebaserRefMM.unauth();
        authDataMM = null;
        setAuthenticatedUserMM(null);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(intent, REQUEST_SIGNUP);
    }

}

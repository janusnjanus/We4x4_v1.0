package net.janusjanus.we4x4_v1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;


public class RegisterSection extends AppCompatActivity  {


    /** Variables **/

    public String UserID;

    /** layout elements **/
    private DrawerLayout mDrawerLayout;

    private static final String TAG = "RegisterAndLogin";
    private static final int REQUEST_SIGNUP = 0;

    /**Firebase links and elements **/
    private Firebase firebaseRefReg, firebaseRefUserReg,firebaseRefUaers;
    private Firebase.AuthStateListener firebaseAuthListnerReg;
    private AuthData authDataReg;

    @Bind(R.id.input_email)
    EditText _emailText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.btn_login)
    Button _loginButton;
    @Bind(R.id.link_signup)
    TextView _signupLink;
    @Bind(R.id.memberBtn) Button memberBtn;
    @Bind(R.id.RegisterSubm) Button RegisterSubm;
    @Bind(R.id.input_name) EditText input_name;
    @Bind(R.id.link_login) TextView link_login;

    @Override
    public void onCreate (Bundle savedInstanceState) {
        Firebase.setAndroidContext(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        Firebase.setAndroidContext(this);
        ButterKnife.bind(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        // Adding Toolbar to Main screen
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                            Toast.makeText(getApplicationContext(),
                                    "Upload",
                                    Toast.LENGTH_SHORT).show();
                        }else if (id == R.id.navChat) {

                            Intent intent = new Intent(getApplicationContext(), chatSection.class);
                            startActivityForResult(intent, REQUEST_SIGNUP);

                        }else if (id == R.id.navGPS) {
                            Toast.makeText(getApplicationContext(),
                                    "GPS",
                                    Toast.LENGTH_SHORT).show();
                        }else if (id == R.id. navInfo){
                            Toast.makeText(getApplicationContext(),
                                    "My Info.",
                                    Toast.LENGTH_SHORT).show();
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
                                    new AlertDialog.Builder(RegisterSection.this, R.style.AppTheme_Dark_Dialog);
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


        /** firebase links **/
        firebaseRefReg = new Firebase(getResources().getString(R.string.firebase_url));
        firebaseRefUserReg = new Firebase(getResources().getString(R.string.firebase_users));
        firebaseRefUaers = new Firebase(getResources().getString(R.string.firebase_users));
        if (savedInstanceState == null) {
            Firebase.setAndroidContext(this);
        }
        firebaseAuthListnerReg = new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                if(authData != null){

                    /** if authentication present setAuthenticatedUser as authData retrieved **/

                    setAuthenticatedUserReg(authData);

                }else{
                    /**if no authentication detected null user info, and clear layout elements
                     related to logged in user;**/

                    //TODO: CHANGES BASED ON AUTHENTICATIONS
                    memberBtn.setVisibility(View.GONE);


                }
            }
        };

        /** adding Authentication state listener **/
        firebaseRefReg.addAuthStateListener(firebaseAuthListnerReg);


        memberBtn.setVisibility(View.GONE);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                _signupLink.setVisibility(View.GONE);
                _loginButton.setVisibility(View.GONE);
                memberBtn.setVisibility(View.GONE);
                RegisterSubm.setVisibility(View.VISIBLE);
                input_name.setVisibility(View.VISIBLE);
                link_login.setVisibility(View.VISIBLE);

            }
        });

        memberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Member.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });

        RegisterSubm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signup();


            }
        });

        link_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                link_login.setVisibility(View.GONE);
                input_name.setVisibility(View.GONE);
                memberBtn.setVisibility(View.GONE);
                RegisterSubm.setVisibility(View.GONE);
                _signupLink.setVisibility(View.VISIBLE);
                _loginButton.setVisibility(View.VISIBLE);

            }
        });
    }

    /** When user authenticated update layout & request user Info. **/
    private void setAuthenticatedUserReg(AuthData authData) {
        if(authData != null) {

            this.authDataReg = authData;

            //TODO:CHANGES FOR ATUHENTICATED USER

            UserID = authData.getUid();
            memberBtn.setVisibility(View.VISIBLE);
            RegisterSubm.setVisibility(View.GONE);
        }
    }


    public void signup() {
        Log.d(TAG, "Signup");

        if (!validateRegisteration()) {
            onSignupFailed();
            return;
        }

        RegisterSubm.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(RegisterSection.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        final String name = input_name.getText().toString();
        final String email = _emailText.getText().toString();
        final String password = _passwordText.getText().toString();

        // TODO: Implement your own signup logic here.

        firebaseRefReg.createUser(email, password, new Firebase.ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> stringObjectMap) {
                firebaseRefReg.authWithPassword(email, password, new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData) {
                        firebaseRefUserReg.child(authData.getUid()).child("email").setValue(email);
                        firebaseRefUserReg.child(authData.getUid()).child("username").setValue(name);
                        firebaseRefUserReg.child(authData.getUid()).child("rank").setValue(1);
                        onSignupSuccess();
                        progressDialog.dismiss();


                    }

                    @Override
                    public void onAuthenticationError(FirebaseError firebaseError) {
                        onSignupFailed();
                        progressDialog.dismiss();


                    }
                });

            }

            @Override
            public void onError(FirebaseError firebaseError) {
                onSignupFailed();
                progressDialog.dismiss();


            }
        });

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignupSuccess();
                        // onSignupFailed();
                        progressDialog.dismiss();

                    }
                }, 3000);
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(RegisterSection.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        // TODO: Implement your own authentication logic here.

        firebaseRefReg.authWithPassword(email, password, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                // On complete call either onLoginSuccess or onLoginFailed
                                onLoginSuccess();
                                // onLoginFailed();
                                progressDialog.dismiss();
                            }
                        }, 3000);
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {

                progressDialog.dismiss();
                onLoginFailed();
            }
        });




    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        finish();
        Intent intent = new Intent(getApplicationContext(), Member.class);
        startActivityForResult(intent, REQUEST_SIGNUP);

    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
        memberBtn.setVisibility(View.GONE);
        RegisterSubm.setVisibility(View.GONE);

    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    public void onSignupSuccess() {
        RegisterSubm.setEnabled(true);
        setResult(RESULT_OK, null);
        Intent intent = new Intent(getApplicationContext(), Member.class);
        startActivityForResult(intent, REQUEST_SIGNUP);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        RegisterSubm.setEnabled(true);
    }

    public boolean validateRegisteration() {
        boolean valid = true;

        String name = input_name.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            input_name.setError("at least 3 characters");
            valid = false;
        } else {
            input_name.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
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
        if(authDataReg !=null){
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
        firebaseRefReg.unauth();
        RegisterSubm.setVisibility(View.GONE);
        authDataReg = null;
        setAuthenticatedUserReg(null);
        memberBtn.setVisibility(View.GONE);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(intent, REQUEST_SIGNUP);
        input_name.setVisibility(View.GONE);

    }
}
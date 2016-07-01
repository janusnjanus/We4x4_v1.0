package net.janusjanus.we4x4_v1;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class ChangePasswordFragment extends Fragment {

    private static int LENGTH = 1;
    private static final int REQUEST_SIGNUP = 0;

    String UserID, email;;

    Firebase firebaseRefUsersMI,firebaserRefMI;
    Firebase.AuthStateListener authStateListenerMI;
    AuthData authDataMI;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final RecyclerView recyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);


        firebaserRefMI = new Firebase(getResources().getString(R.string.firebase_url));
        firebaseRefUsersMI = new Firebase(getResources().getString(R.string.firebase_users));

        authStateListenerMI = new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                if (authData != null) {

                    /** if authentication present setAuthenticatedUser as authData retrieved **/

                    setAuthenticatedUserMI(authData);

                } else {

                    /**if no authentication detected null user info, and clear layout elements
                     related to logged in user;**/

                    //TODO: CHANGES BASED ON AUTHENTICATIONS

                }
            }
        };

        /** adding Authentication state listener **/
        firebaserRefMI.addAuthStateListener(authStateListenerMI);


        ContentAdapter adapter = new ContentAdapter(recyclerView.getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return recyclerView;


    }

    /** When user authenticated update layout & request user Info. **/
    private void setAuthenticatedUserMI(AuthData authData) {
        if (authData != null) {

            this.authDataMI = authData;
            UserID = authData.getUid();
            getUserInfo();

            //TODO:CHANGES FOR ATUHENTICATED USER

        }
    }

    public void getUserInfo() {

        firebaseRefUsersMI.child(UserID).child("email").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                email = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public EditText input_password;
        public EditText input_passwordSecond;
        public Button submitPassBtn;
        public String firstPass,secondPass;

        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.change_password_fragment, parent, false));

            input_password = (EditText) itemView.findViewById(R.id.input_password);
            input_passwordSecond = (EditText) itemView.findViewById(R.id.input_passwordSecond);
            submitPassBtn = (Button) itemView.findViewById(R.id.submitPassBtn);

            submitPassBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(validate()){
                        submitPassword();
                    }

                }


            });


        }

        public boolean validate() {
            boolean valid = true;

             firstPass = input_password.getText().toString();
             secondPass = input_passwordSecond.getText().toString();

            if( firstPass != secondPass){
                if (firstPass.isEmpty() || firstPass.length() < 4 || firstPass.length() > 10) {
                    input_password.setError("between 4 and 10 alphanumeric characters");
                    valid = false;
                } else {
                    input_password.setError(null);
                }

                if (secondPass.isEmpty() || secondPass.length() < 4 || secondPass.length() > 10) {
                    input_passwordSecond.setError("between 4 and 10 alphanumeric characters");
                    valid = false;
                } else {
                    input_passwordSecond.setError(null);
                }
            }

            return valid;
        }

        public void submitPassword(){
            final ProgressDialog progressDialog = new ProgressDialog(ChangePasswordFragment.this.getContext(),
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Changing password...");
            progressDialog.show();

            firebaserRefMI.changePassword(email, firstPass, secondPass, new Firebase.ResultHandler() {
                @Override
                public void onSuccess() {
                    Toast.makeText(getActivity().getApplicationContext(), "Password Change Successes", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    logout();
                }

                @Override
                public void onError(FirebaseError firebaseError) {
                    Toast.makeText(getActivity(),
                            "Error, could not change password, please try again"
                            , Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            });
        }

    }

    public void logout(){
        if (this.authDataMI != null) {

            firebaserRefMI.unauth();
            authDataMI = null;

        }
        setAuthenticatedUserMI(null);

        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivityForResult(intent, REQUEST_SIGNUP);
    }


    public class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {
        // Set numbers of List in RecyclerView.

        public ContentAdapter(Context context) {

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return LENGTH;
        }
    }
}

package net.janusjanus.we4x4_v1;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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


public class ResetPassFragment extends android.support.v4.app.Fragment {

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

        public EditText input_email;
        public EditText input_emailConfirm;
        public Button submitPassResetBtn;
        public String emailInput,emailConfirmInput;

        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.reset_pass_fragment, parent, false));

            input_email = (EditText) itemView.findViewById(R.id.input_email);
            input_emailConfirm = (EditText) itemView.findViewById(R.id.input_emailConfirm);
            submitPassResetBtn = (Button) itemView.findViewById(R.id.submitPassResetBtn);

            submitPassResetBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(validate()){
                        submitResetRequest();
                    }

                }


            });


        }

        public boolean validate() {
            boolean valid = true;

            emailInput = input_email.getText().toString();
            emailConfirmInput = input_emailConfirm.getText().toString();

            if(emailInput != emailConfirmInput){
                Toast.makeText(getActivity().getApplicationContext(), " Please enter matching Emails", Toast.LENGTH_SHORT).show();

            }else {
                if (emailInput.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {

                    input_email.setError("enter a valid email address");
                    valid = false;
                } else {
                    input_email.setError(null);
                }
                if (emailConfirmInput.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailConfirmInput).matches()) {

                    input_emailConfirm.setError("enter a valid email address");
                    valid = false;
                } else {
                    input_emailConfirm.setError(null);
                }
            }
            return valid;
        }




        public void submitResetRequest() {
            final ProgressDialog progressDialog = new ProgressDialog(ResetPassFragment.this.getActivity(),
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Changing password...");
            progressDialog.show();

            firebaserRefMI.resetPassword(emailInput.toString(), new Firebase.ResultHandler() {
                @Override
                public void onSuccess() {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Check your Email for instructions"
                            , Toast.LENGTH_LONG).show();
                    logout();
                }

                @Override
                public void onError(FirebaseError firebaseError) {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Error, could not send an password reset email, please contact Admin."
                            , Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    private void logout() {
        firebaserRefMI.unauth();
        authDataMI = null;
        setAuthenticatedUserMI(null);
        Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
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


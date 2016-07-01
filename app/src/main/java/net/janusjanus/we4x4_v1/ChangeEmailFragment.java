package net.janusjanus.we4x4_v1;

import android.app.ProgressDialog;
import android.content.Context;
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

/**
 * Created by PK on 16-06-29.
 */
public class ChangeEmailFragment extends android.support.v4.app.Fragment {


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

        public EditText input_emailOd;
        public EditText input_emailNew;
        public EditText input_emailOldConfirm;
        public Button submitEmailBtn;
        public String emailOld,emailNew,emailOldConfirm;

        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.change_email_fragment, parent, false));

            input_emailOd = (EditText) itemView.findViewById(R.id.input_password);
            input_emailOldConfirm = (EditText) itemView.findViewById(R.id.input_emailOldConfirm);
            input_emailNew = (EditText) itemView.findViewById(R.id.input_passwordSecond);
            submitEmailBtn = (Button) itemView.findViewById(R.id.submitEmailBtn);

            submitEmailBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(validate()){
                        submitEmail();
                    }

                }


            });


        }

        public boolean validate() {
            boolean valid = true;

            emailOld = input_emailOd.getText().toString();
            emailOldConfirm = input_emailOldConfirm.getText().toString();
            emailNew = input_emailNew.getText().toString();

            if(emailOld != emailOldConfirm){
                Toast.makeText(getActivity().getApplicationContext(), "Passwords don't match", Toast.LENGTH_SHORT).show();

            }else {
                if (emailOld.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailOld).matches()) {

                    input_emailOd.setError("enter a valid email address");
                    valid = false;
                } else {
                    input_emailOd.setError(null);
                }
                if (emailNew.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailNew).matches()) {

                    input_emailNew.setError("enter a valid email address");
                    valid = false;
                } else {
                    input_emailNew.setError(null);
                }
            }
                return valid;
            }




        public void submitEmail() {
            final ProgressDialog progressDialog = new ProgressDialog(ChangeEmailFragment.this.getActivity(),
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Changing password...");
            progressDialog.show();
        firebaserRefMI.changeEmail(emailOld, emailOldConfirm, emailNew, new Firebase.ResultHandler() {
            @Override
            public void onSuccess() {
                firebaseRefUsersMI.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        firebaseRefUsersMI.child(UserID.toString()).child("email").setValue(emailNew);
                        progressDialog.dismiss();
                        Toast.makeText(getActivity().getApplicationContext(), "Email Change Successes", Toast.LENGTH_SHORT).show();


                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
            }

            @Override
            public void onError(FirebaseError firebaseError) {

                Toast.makeText(getActivity().getApplicationContext(),
                        "Error, Please contact Admin to update your Information"
                        , Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
            });
        }

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


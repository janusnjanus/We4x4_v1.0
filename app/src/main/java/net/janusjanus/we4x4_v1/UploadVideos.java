package net.janusjanus.we4x4_v1;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.firebase.client.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UploadVideos extends android.support.v4.app.Fragment {

    private static int LENGTH = 1;
    private Firebase firebaseRefUsersUP, firebaserRefUP, firebaseRefUploadsUP, firebaseRefRatingUP, firebaseRefData;
    Firebase.AuthStateListener authStateListenerMM;
    AuthData authDataMM;

    private static ClipboardManager myClipboard;
    private static ClipData myClip;

    String uploadedContentURL, uploadPublicID, username, locationTag, UserID, Username;
    long numOfAdrs;
    Uri RealFilePath, selectedVidPath;
    public static final int RESULT_LOAD_IMAGE = 1;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static final String TAG = UploadPictures.class.getSimpleName();

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final RecyclerView recyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);

        myClipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        firebaserRefUP = new Firebase(getResources().getString(R.string.firebase_url));
        firebaseRefData = new Firebase(getResources().getString(R.string.firebase_data));
        firebaseRefUsersUP = new Firebase(getResources().getString(R.string.firebase_users));
        firebaseRefUploadsUP = new Firebase(getResources().getString(R.string.firebase_uploads));
        firebaseRefRatingUP = new Firebase(getResources().getString(R.string.firebase_rating));

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
        firebaserRefUP.addAuthStateListener(authStateListenerMM);


        ContentAdapter adapter = new ContentAdapter(recyclerView.getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return recyclerView;

    }

    /** When user authenticated update layout & request user Info. **/
    private void setAuthenticatedUserMM(AuthData authData) {
        if (authData != null) {

            this.authDataMM = authData;
            UserID = authData.getUid();
            getUserInfo();

            //TODO:CHANGES FOR ATUHENTICATED USER

        }
    }


    public void getUserInfo() {

        firebaseRefUsersUP.child(UserID).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                username = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public VideoView video;
        public EditText tagEditText;
        public Button tagCurLoc, chooseVid, uploadContent;

        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.video_upload, parent, false));

            video = (VideoView) itemView.findViewById(R.id.VidToUpload);
            tagEditText = (EditText) itemView.findViewById(R.id.tagEditText);

            chooseVid = (Button) itemView.findViewById(R.id.chooseVid);
            chooseVid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    verifyStoragePermissions(getActivity());
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, RESULT_LOAD_IMAGE);
                    video.setVideoURI(selectedVidPath);
                }
            });


            uploadContent = (Button) itemView.findViewById(R.id.uploadContent);
            uploadContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    video.setVideoURI(selectedVidPath);
                    UploadTask uploadTask = new UploadTask();
                    uploadTask.execute(String.valueOf(video));
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            onActivityResult(requestCode, resultCode, data);
            Toast.makeText(getActivity().getApplicationContext(), "Canceled ", Toast.LENGTH_SHORT).show();


        } else {
            selectedVidPath = data.getData();
            Log.i("MyTag_uploadFile", selectedVidPath.toString());

//                .setImageURI(selectedImgPath);
            RealFilePath = Uri.parse(getPath(selectedVidPath));
            Toast.makeText(getActivity().getApplicationContext(), " " + RealFilePath, Toast.LENGTH_SHORT).show();

        }
    }


    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);

    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }


    /**
     * Adapter to display recycler view.
     */
    public class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {
        // Set numbers of List in RecyclerView.

        private Context mContext;

        public ContentAdapter(Context context) {

            this.mContext = context;
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);

        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {

            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        }

        @Override
        public int getItemCount() {
            return LENGTH;
        }
    }



    class UploadTask extends AsyncTask<String, Void, Void> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {

            /** starting progress dialog **/

            progressDialog = new ProgressDialog(UploadVideos.this.getContext(),
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setTitle("Uploading ...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {

            /** commented code for Cloudinary Admin API to implement extra privileges  **/

            //TODO:Adding the api key and api secret provided in cloudinary dashboard

            /** configuring cloud information for unsigned upload**/
            Map config = new HashMap();
            config.put("cloud_name", "we4x4");
            Cloudinary cloudinary = new Cloudinary(config);

            try {

                /** Creating Strings Array to pass Tags with the uploaded content**/

                String[] tags;
                tags = new String[3];
                tags[0] = new String("uploads");
                tags[1] = new String(UserID);
                if(locationTag !=null){
                    tags[2] = new String(locationTag);
                }

                /** configuring content information for unsigned upload with tags **/

                JSONObject result = new JSONObject(cloudinary.uploader().unsignedUpload("" + RealFilePath, "frtkzlwz",
                        ObjectUtils.asMap("tags", tags, "resource_type", "auto")));

                uploadedContentURL = (String) result.get("url");
                Log.i("MyTag_uploadFile", uploadedContentURL);
                uploadPublicID = (String) result.get("public_id");
                Log.i("MyTag_uploadFile", uploadPublicID);

            } catch (IOException e) {
                e.printStackTrace();

                /** handling errors **/

                progressDialog.setMessage("Error uploading file");
                progressDialog.hide();

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            /** updating the information on firebase and recodring new information for last upload **/

            final Long[] currentRank = new Long[1];
            progressDialog.hide();
            firebaseRefUploadsUP.child("images").child(UserID).child(uploadPublicID).setValue(uploadedContentURL);
            firebaseRefRatingUP.child("images").child(UserID).child(uploadPublicID).setValue(0);
            firebaseRefData.child("images").child(uploadPublicID).child("author").setValue(username);
            firebaseRefData.child("images").child(uploadPublicID).child("author_id").setValue(UserID);
            firebaseRefData.child("images").child(uploadPublicID).child("rating").setValue(0);
            firebaseRefData.child("images").child(uploadPublicID).child("address").setValue(uploadedContentURL);
            firebaseRefData.child("addresses").child(uploadPublicID).setValue(uploadedContentURL);

            if(locationTag != null){

                firebaseRefData.child("images").child(uploadPublicID).child("location").setValue(locationTag);
                firebaserRefUP.child("data").child("locations").child("location"+numOfAdrs).child("author").setValue(UserID);
//                firebaserRefUP.child("data").child("locations").child("location"+numOfAdrs).child("latitude").setValue(currentLatitude);
//                firebaserRefUP.child("data").child("locations").child("location"+numOfAdrs).child("longitude").setValue(currentLongitude);
                firebaserRefUP.child("locations").child("location"+numOfAdrs).setValue(locationTag);
            }else{
                firebaseRefData.child("images").child(uploadPublicID).child("location").setValue(0);

            }

            firebaseRefUsersUP.child(UserID).child("rank").runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData currentData) {
                    if (currentData.getValue() == null) {
                        currentData.setValue(1);
                        currentRank[0] = (Long) currentData.getValue();
                    } else {
                        currentData.setValue((Long) currentData.getValue() + 1);
                        currentRank[0] = (Long) currentData.getValue();

                    }
                    return Transaction.success(currentData);
                }

                @Override
                public void onComplete(FirebaseError firebaseError, boolean b, DataSnapshot dataSnapshot) {
                    if (firebaseError != null) {
                        Toast.makeText(getActivity().getApplicationContext(), "Error updating your information", Toast.LENGTH_SHORT).show();
                        Log.i("MyTag","Error updating your information" );
                    } else {
                        Log.i("MyTag","Content have been uploaded" );

                        Toast.makeText(getActivity().getApplicationContext(), "Content have been uploaded", Toast.LENGTH_SHORT).show();
//                        tagText.setText("");
                        locationTag = "0";

                    }
                }
            });

            super.onPostExecute(aVoid);
        }
    }


}

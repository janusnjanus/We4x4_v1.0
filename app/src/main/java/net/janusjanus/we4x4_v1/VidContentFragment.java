package net.janusjanus.we4x4_v1;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.firebase.client.ValueEventListener;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class VidContentFragment extends android.support.v4.app.Fragment {

    private static String[] mVideos;
    private static String [] author;
    private static String [] ratingV;
    private static String [] locationV;
    private static String [] publicID;
    public Long numbOfAdrs;
    public static String authorString;
    public static String author_id_String;
    public static String public_id;



    private static int LENGTH = 0;
    private static ClipboardManager myClipboard;
    private static ClipData myClip;
    ArrayList<String > videosfeedsList = new ArrayList<String>();
    ArrayList<String > authorfeedsList = new ArrayList<String>();
    ArrayList<String > ratingfeedsList = new ArrayList<String>();
    ArrayList<String > locationfeedsList = new ArrayList<String>();
    ArrayList<String > publicIDfeedsList = new ArrayList<String>();


    public static Firebase firebaseDataRef, firebaseRefUsersMM, firebaserRefMM;
    Firebase.AuthStateListener authStateListenerMM;
    AuthData authDataMM;
    public static String UserID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final RecyclerView recyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);

        Log.i("MyTag_onCreate","vidContentFragment_Loaded");

        myClipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        firebaserRefMM = new Firebase(getResources().getString(R.string.firebase_url));
        firebaseDataRef = new Firebase(getResources().getString(R. string.firebase_data));
        firebaseRefUsersMM = new Firebase(getResources().getString(R. string.firebase_users));



        Firebase ref = new Firebase("https://wi4x4.firebaseio.com/data/videos");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                videosfeedsList.clear();
                authorfeedsList.clear();
                ratingfeedsList.clear();
                locationfeedsList.clear();
                publicIDfeedsList.clear();

                if(snapshot !=null){
                    for (DataSnapshot child: snapshot.getChildren()) {
                        Log.i("MyTag_Vid", child.getValue().toString());
                        videosfeedsList.add(child.child("address").getValue(String.class));
                        authorfeedsList.add(child.child("author").getValue(String.class));
                        ratingfeedsList.add(child.child("rating").getValue(String.class));
                        locationfeedsList.add(child.child("location").getValue(String.class));
                        publicIDfeedsList.add(child.child("public_id").getValue(String.class));

                    }
                    Log.i("MyTag_VidDir", videosfeedsList.toString());

                    mVideos = videosfeedsList.toArray(new String[videosfeedsList.size()]);
                    author = authorfeedsList.toArray(new String[authorfeedsList.size()]);
                    ratingV = ratingfeedsList.toArray(new String[ratingfeedsList.size()]);
                    locationV = locationfeedsList.toArray(new String[locationfeedsList.size()]);
                    publicID = publicIDfeedsList.toArray(new String[publicIDfeedsList.size()]);

                    numbOfAdrs = Long.valueOf(videosfeedsList.size());
                    LENGTH = Integer.valueOf(String.valueOf(numbOfAdrs));
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }


        });
        authStateListenerMM = new Firebase.AuthStateListener() {
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
        firebaserRefMM.addAuthStateListener(authStateListenerMM);


        ContentAdapter adapter = new ContentAdapter(recyclerView.getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return recyclerView;
    }

    /** When user authenticated update layout & request user Info. **/
    private void setAuthenticatedUserMM(AuthData authData) {
        if(authData != null) {

            this.authDataMM = authData;
            UserID = authData.getUid();


            //TODO:CHANGES FOR ATUHENTICATED USER

        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public VideoView video;
        public TextView authorName;
        public TextView ratingValue;
        public TextView locationValue;
        public RatingBar ratingB;
        public Button submitRating;
        public LinearLayout placeNameHolder;
        public int newRating;


        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_card_vid, parent, false));
            authorName = (TextView) itemView.findViewById(R.id.card_title);
            ratingValue = (TextView) itemView.findViewById(R.id.ratingValue);
            locationValue = (TextView) itemView.findViewById(R.id.locationValue);
            video = (VideoView) itemView.findViewById(R.id.placeVid);
            ratingB = (RatingBar)itemView.findViewById(R.id.ratingBarMM);
            ratingB.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    newRating = (int) ratingB.getRating();
                }
            });
            submitRating = (Button) itemView.findViewById(R.id.submitRatingMM);
            submitRating.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    public_id = publicID[getPosition()];

                    Log.i("MyTag_Rating",public_id);

                    firebaseDataRef.child("videos").child(public_id).child("author").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            authorString = dataSnapshot.getValue().toString();
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });

                    firebaseDataRef.child("videos").child(public_id).child("author_id").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            author_id_String = dataSnapshot.getValue().toString();
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });

                    /** Checking if the user voted before **/

                    firebaseDataRef.child("videos").child(public_id).child(UserID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(final DataSnapshot dataSnapshot) {
                            boolean userVotStat = dataSnapshot.exists();
                            //if user voted before display the following message;
                            if(userVotStat){
                                Log.e("MyTag_rating","Sorry, you can't vote more than once");
                            }else{
                                //if user did not vote before execute Transaction/ save rating

                                /** Rating is saved in two different location on firebase
                                 * One; under the image specific folder with all other information
                                 * such as author, authur_id, etc
                                 * Two; under the Rating only folder for faster rating value retrieval**/

                                //Saving submitted rating to the specific image folder
                                firebaseDataRef.child("videos").child(public_id).child("rating").runTransaction(new Transaction.Handler() {

                                    @Override
                                    public Transaction.Result doTransaction(MutableData currentData) {
                                        if(currentData.getValue() == null){
                                            currentData.setValue(newRating);
                                            firebaseRefUsersMM.child(author_id_String).child("rank").runTransaction(new Transaction.Handler() {
                                                @Override
                                                public Transaction.Result doTransaction(MutableData mutableData) {
                                                    mutableData.setValue((Long) mutableData.getValue() + 1);

                                                    return Transaction.success(mutableData);
                                                }

                                                @Override
                                                public void onComplete(FirebaseError firebaseError, boolean b, DataSnapshot dataSnapshot) {

                                                }
                                            });
                                            //Saving submitted rating to the Rating folder
                                            firebaserRefMM.child("rating").child("videos").child(author_id_String).child(public_id).runTransaction(new Transaction.Handler() {
                                                @Override
                                                public Transaction.Result doTransaction(MutableData mutableData) {
                                                    mutableData.setValue((Long) mutableData.getValue() + newRating);

                                                    return Transaction.success(mutableData);
                                                }

                                                @Override
                                                public void onComplete(FirebaseError firebaseError, boolean b, DataSnapshot dataSnapshot) {

                                                }
                                            });


                                        }else {
                                            currentData.setValue((Long) currentData.getValue() + newRating);

                                            firebaseDataRef.child("videos").child(public_id).child(UserID).setValue(newRating);
                                            firebaserRefMM.child("rating").child("videos").child(author_id_String).child(public_id).setValue((Long) currentData.getValue() + newRating);

                                            firebaseRefUsersMM.child(author_id_String).child("rank").runTransaction(new Transaction.Handler() {
                                                @Override
                                                public Transaction.Result doTransaction(MutableData mutableData) {
                                                    mutableData.setValue((Long) mutableData.getValue() + 1);

                                                    return Transaction.success(mutableData);
                                                }

                                                @Override
                                                public void onComplete(FirebaseError firebaseError, boolean b, DataSnapshot dataSnapshot) {

                                                }
                                            });

                                        }
                                        return Transaction.success(currentData);
                                    }

                                    @Override
                                    public void onComplete(FirebaseError firebaseError, boolean b, DataSnapshot dataSnapshot) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });


                }
            });
            placeNameHolder = (LinearLayout) itemView.findViewById(R.id.placeNameHolder);

        }
    }

    /**
     * Adapter to display recycler view.
     */
    public static class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {
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

            holder.authorName.setText(author[position]);

            holder.ratingValue.setText(ratingV[position]);

            holder.locationValue.setText(locationV[position]);

            Uri video = Uri.parse(mVideos[position]);
            holder.video.setVideoURI(video);
            holder.video.setMediaController(new MediaController(mContext));
            holder.video.requestFocus();
            holder.video.seekTo(1000);
            holder.video.pause();

            try{
                String url1 = mVideos[position];
                URL ulrn = new URL(url1);
                HttpURLConnection con = (HttpURLConnection)ulrn.openConnection();
                InputStream is = con.getInputStream();
                Bitmap bmp = BitmapFactory.decodeStream(is);
                if (null != bmp)

                    Palette.generateAsync(bmp, new Palette.PaletteAsyncListener() {
                        public void onGenerated(Palette palette) {
                            int bgColor = palette.getVibrantColor(mContext.getResources().getColor(android.R.color.black));
                            holder.placeNameHolder.setBackgroundColor(bgColor);
                        }
                    });
                else
                    Log.e("MyTag_BMP","The Bitmap is NULL");

            }catch (Exception e){

            }

        }

        @Override
        public int getItemCount() {
            return LENGTH;
        }
    }
}

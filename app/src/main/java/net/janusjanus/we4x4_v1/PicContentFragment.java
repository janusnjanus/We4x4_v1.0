package net.janusjanus.we4x4_v1;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class PicContentFragment extends android.support.v4.app.Fragment {

    private static String[] mImages;
    private static String [] author;

    private static int LENGTH = 0;
    private static ClipboardManager myClipboard;
    private static ClipData myClip;
    ArrayList<String > imagesfeedsList = new ArrayList<String>();
    ArrayList<String > authorfeedsList = new ArrayList<String>();

    Firebase firebaserRefMM;
    Firebase.AuthStateListener authStateListenerMM;
    AuthData authDataMM;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final RecyclerView recyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);
        myClipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        firebaserRefMM = new Firebase(getResources().getString(R.string.firebase_url));





        Firebase ref = new Firebase("https://wi4x4.firebaseio.com/data/images");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if(snapshot !=null){
                    for (DataSnapshot child: snapshot.getChildren()) {
                        Log.i("MyTag", child.getValue().toString());
                        imagesfeedsList.add(child.child("address").getValue(String.class));
                        authorfeedsList.add(child.child("author").getValue(String.class));
                    }
                    Log.i("MyTag_imagesDirFinal", imagesfeedsList.toString());

                    mImages = imagesfeedsList.toArray(new String[imagesfeedsList.size()]);
                    author = authorfeedsList.toArray(new String[authorfeedsList.size()]);

                    Long numbOfAdrs = Long.valueOf(imagesfeedsList.size());
                    LENGTH = Integer.valueOf(String.valueOf(numbOfAdrs));

                    ContentAdapter adapter = new ContentAdapter(recyclerView.getContext());
                    recyclerView.setAdapter(adapter);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

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



        return recyclerView;
    }

    /** When user authenticated update layout & request user Info. **/
    private void setAuthenticatedUserMM(AuthData authData) {
        if(authData != null) {

            this.authDataMM = authData;


            //TODO:CHANGES FOR ATUHENTICATED USER

        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView picture;
        public TextView authorName;
        public RelativeLayout placeNameHolder;



        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_pic, parent, false));
            picture = (ImageView) itemView.findViewById(R.id.placeImage);
            authorName = (TextView) itemView.findViewById(R.id.placeName);
            placeNameHolder = (RelativeLayout) itemView.findViewById(R.id.placeNameHolder);


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

            Picasso.with(mContext).load(mImages[position]).into(holder.picture);

            try{
                String url1 = mImages[position];
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
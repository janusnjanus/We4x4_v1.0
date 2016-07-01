package net.janusjanus.we4x4_v1;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class LocationsContentFragment extends Fragment {

    private static String[] mPlaces;
    private static int LENGTH = 0;
    private static ClipboardManager myClipboard;
    private static ClipData myClip;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final RecyclerView recyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);
        myClipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);


        final ArrayList<Long> addressList = new ArrayList<Long>();
        Firebase ref = new Firebase("https://wi4x4.firebaseio.com/locations");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Map<Long, Long> allADR = (HashMap<Long, Long>) snapshot.getValue();
                if (allADR != null) {
                    Collection<Long> BlkADR = allADR.values();
                    addressList.addAll(BlkADR);
                    Log.i("TAG", addressList.toString());

                    mPlaces = addressList.toArray(new String[addressList.size()]);
                    Log.i("TAG_mplaces.length", String.valueOf(mPlaces.length));

                    Long numbOfAdrs = snapshot.getChildrenCount();

                    Log.i("TAG_numOfAdrs", String.valueOf(numbOfAdrs));

                    LENGTH = Integer.valueOf(String.valueOf(numbOfAdrs));

                    Log.i("TAG_Length", String.valueOf(numbOfAdrs));

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

        return recyclerView;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public ImageButton share_button;

        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_card, parent, false));
            name = (TextView) itemView.findViewById(R.id.card_title);
            share_button = (ImageButton) itemView.findViewById(R.id.share_button);

        }
    }

    /**
     * Adapter to display recycler view.
     */
    public static class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {
        // Set numbers of List in RecyclerView.

        public ContentAdapter(Context context) {

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {

            holder.name.setText(mPlaces[position]);
            holder.share_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text;
                    text = holder.name.getText().toString();

                    myClip = ClipData.newPlainText("text", text);
                    myClipboard.setPrimaryClip(myClip);
                    Toast.makeText(v.getContext(), "Location Copied", Toast.LENGTH_SHORT).show();
                }

            });
        }

        @Override
        public int getItemCount() {
            return LENGTH;
        }
    }

}
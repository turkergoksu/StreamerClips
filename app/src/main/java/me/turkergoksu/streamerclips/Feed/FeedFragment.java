package me.turkergoksu.streamerclips.Feed;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import me.turkergoksu.streamerclips.Classes.Clip;
import me.turkergoksu.streamerclips.MainActivity;
import me.turkergoksu.streamerclips.R;
import me.turkergoksu.streamerclips.Classes.TwitchClient;

public class FeedFragment extends Fragment {

    // TODO: 10-Sep-19 Sadece FeedFragment a özel o gün izlenen kliplerde hangi zaman aralığında
    //  ise o kliplerin sıralaması idleri ile beraber Firebase'e gönderilmeli.
    // TODO: 10-Sep-19 Pagination ekle
    // TODO: 10-Sep-19 Kullanıcı istediği yayıncıları ignorelayabilir
    // TODO: 11-Sep-19 İzlendi indicator ı
    // TODO: 11-Sep-19 Klip süresi gösterilmeli
    // TODO: 11-Sep-19 mp4 ile videoyu gösterme kesinlikle eklenmeli
    // TODO: 12-Sep-19 React with Like Dislike Pepega
    // TODO: 12-Sep-19 Kullanıcılar tarafından dünün son 7 günün gibi en beğenilen klipleri listesi

    private static final String TAG = FeedFragment.class.getSimpleName();

    private TwitchClient twitchClient;

    private ArrayList<Clip> clipArrayList;
    private ClipAdapter clipAdapter;

    private DatabaseReference trDatabaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        initializeViews(view);

        return view;
    }

    private void initializeViews(View view){
        trDatabaseReference = FirebaseDatabase.getInstance().getReference().child("TR");

        if (clipArrayList == null) {
            clipArrayList = new ArrayList<>();
        }

        twitchClient = new TwitchClient(
                getResources().getString(R.string.twitch_client_id),
                getResources().getString(R.string.twitch_access_token));

        RecyclerView clipsRecyclerView = view.findViewById(R.id.rv_clips);
        clipsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        clipAdapter = new ClipAdapter(clipArrayList, getContext(), twitchClient);
        clipsRecyclerView.setAdapter(clipAdapter);

        Spinner timeIntervalSpinner = view.findViewById(R.id.spinner_timeInterval);
        timeIntervalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                int lastSelectedTimeInterval = ((MainActivity)getContext()).lastSelectedTimeIntervalPosition;

                if (lastSelectedTimeInterval == -1){
                    trDatabaseReference.child("oneDay").child("clips").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                                Clip clip = childSnapshot.getValue(Clip.class);
                                clipArrayList.add(clip);
                            }
                            clipAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else if (lastSelectedTimeInterval == position){
                    // Empty
                    return;
                } else {
                    Log.d(TAG, "onItemSelected: " + position);
                    Log.d(TAG, "onItemSelected:  "+ clipArrayList);
                    // Clear clipArrayList
                    clipArrayList.clear();
                    Log.d(TAG, "onItemSelected: " + clipArrayList);
                    clipAdapter.notifyDataSetChanged();

                    switch (position){
                        case 0:
                            trDatabaseReference.child("oneDay").child("clips").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                                        Clip clip = childSnapshot.getValue(Clip.class);
                                        clipArrayList.add(clip);
                                    }
                                    clipAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            break;
                        case 1:
                            trDatabaseReference.child("oneWeek").child("clips").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                                        Clip clip = childSnapshot.getValue(Clip.class);
                                        clipArrayList.add(clip);
                                    }
                                    clipAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            break;
                        case 2:
                            trDatabaseReference.child("oneMonth").child("clips").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                                        Clip clip = childSnapshot.getValue(Clip.class);
                                        clipArrayList.add(clip);
                                    }
                                    clipAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            break;
                    }
                }

                ((MainActivity)getContext()).lastSelectedTimeIntervalPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

}

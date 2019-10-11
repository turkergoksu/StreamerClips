package me.turkergoksu.streamerclips.Feed;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.angmarch.views.NiceSpinner;
import org.angmarch.views.OnSpinnerItemSelectedListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import me.turkergoksu.streamerclips.Classes.Clip;
import me.turkergoksu.streamerclips.MainActivity;
import me.turkergoksu.streamerclips.R;
import me.turkergoksu.streamerclips.Classes.TwitchClient;

public class FeedFragment extends Fragment {

    // TODO: 10-Sep-19 Pagination ekle
    // TODO: 10-Sep-19 Kullanıcı istediği yayıncıları ignorelayabilir
    // TODO: 12-Sep-19 Kullanıcılar tarafından dünün son 7 günün gibi en beğenilen klipleri listesi

    private static final String TAG = FeedFragment.class.getSimpleName();

    private TwitchClient twitchClient;

    private ArrayList<Clip> clipArrayList;
    private ClipAdapter clipAdapter;

    private DatabaseReference trDatabaseReference;

    private RelativeLayout cantGetClipsLayout;
    private RelativeLayout loadingLayout;
    private SwipeRefreshLayout clipsSwipeRefreshLayout;
    private NiceSpinner timeIntervalSpinner;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Set<String> watchedSet;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        initializeViews(view);

        return view;
    }

    private void initializeViews(View view){
        prefs = getContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        editor = prefs.edit();

        trDatabaseReference = FirebaseDatabase.getInstance().getReference().child("TR");

        watchedSet = prefs.getStringSet("watchedSet", null);
        if (watchedSet == null){
            watchedSet = new HashSet<>();
        }

        if (clipArrayList == null) {
            clipArrayList = new ArrayList<>();
        }

        twitchClient = new TwitchClient(
                getResources().getString(R.string.twitch_client_id),
                getResources().getString(R.string.twitch_access_token));

        cantGetClipsLayout = view.findViewById(R.id.rl_cant_get_clips);
        loadingLayout = view.findViewById(R.id.loadingPanel);
        clipsSwipeRefreshLayout = view.findViewById(R.id.srl_clips);
        clipsSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showLoadingAnimation(true);
                cantGetClipsLayout.setVisibility(View.GONE);
                getClipsFromDatabase(timeIntervalSpinner.getSelectedIndex());
                clipsSwipeRefreshLayout.setRefreshing(false);
            }
        });

        final RecyclerView clipsRecyclerView = view.findViewById(R.id.rv_clips);
        clipsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        clipAdapter = new ClipAdapter(clipArrayList, getContext(), twitchClient, new ClipAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final View view, final Clip clip) {
                FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
                Bundle args = new Bundle();
                args.putParcelable("clip", clip);
                final ClipDialogFragment clipDialogFragment = new ClipDialogFragment();

                clipDialogFragment.setArguments(args);
                clipDialogFragment.show(fragmentTransaction, "clipDialogFragment");

                new CountDownTimer(10 * 1000, 10){
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        if (clipDialogFragment.getDialog() != null){
                            clip.setWatched(true);
                            watchedSet.add(clip.getId());
                            editor.remove("watchedSet");
                            editor.commit();
                            editor.putStringSet("watchedSet", watchedSet);
                            editor.commit();

                            // Set the view as 'watched'
                            ((CardView)view).setForeground(getContext().getDrawable(R.drawable.watched_clip_overlay));
                            TextView isWatchedTextView = ((CardView)view).findViewById(R.id.tv_watched);
                            isWatchedTextView.setVisibility(View.VISIBLE);
                        }
                    }
                }.start();
            }
        });
        clipsRecyclerView.setAdapter(clipAdapter);

        timeIntervalSpinner = view.findViewById(R.id.spinner_timeInterval);

        // initialization
        clipArrayList.clear();
        getClipsFromDatabase(timeIntervalSpinner.getSelectedIndex());

        timeIntervalSpinner.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                clipArrayList.clear();

                showLoadingAnimation(true);
                switch (position){
                    case 0:
                        getClipsFromDatabase(position);
                        break;
                    case 1:
                        getClipsFromDatabase(position);
                        break;
                    case 2:
                        getClipsFromDatabase(position);
                        break;
                }
            }
        });

    }

    private void getClipsFromDatabase(int selectedItemIndex){
        clipArrayList.clear();
        String timeInterval = "oneDay";
        switch (selectedItemIndex){
            case 0:
                timeInterval = "oneDay";
                break;
            case 1:
                timeInterval = "oneWeek";
                break;
            case 2:
                timeInterval = "oneMonth";
                break;
        }
        trDatabaseReference.child(timeInterval).child("clips").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                    Clip clip = childSnapshot.getValue(Clip.class);
                    for (String watchedClipID : watchedSet){
                        if (clip.getId().equals(watchedClipID)){
                            clip.setWatched(true);
                            break;
                        }
                    }
                    clipArrayList.add(clip);
                    clipAdapter.notifyDataSetChanged();
                }

                // Hide the cantGetClipsRelativeLayout
                cantGetClipsLayout.setVisibility(View.GONE);

                // End loading animation and show clips
                showLoadingAnimation(false);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                cantGetClipsLayout.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Klipleri alamadık :( Bence tekrar dene.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showLoadingAnimation(boolean isShow){
        if (isShow){
            loadingLayout.setVisibility(View.VISIBLE);
            clipsSwipeRefreshLayout.setVisibility(View.GONE);
        } else {
            loadingLayout.setVisibility(View.GONE);
            clipsSwipeRefreshLayout.setVisibility(View.VISIBLE);
        }
    }

}

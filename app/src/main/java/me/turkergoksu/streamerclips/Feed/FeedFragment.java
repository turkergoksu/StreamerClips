package me.turkergoksu.streamerclips.Feed;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import me.turkergoksu.streamerclips.Classes.Clip;
import me.turkergoksu.streamerclips.MainActivity;
import me.turkergoksu.streamerclips.R;
import me.turkergoksu.streamerclips.Classes.TwitchClient;

public class FeedFragment extends Fragment {

    private static final String TAG = FeedFragment.class.getSimpleName();

    private RequestQueue requestQueue;
    private TwitchClient twitchClient;

    private ArrayList<Clip> clipArrayList;
    private ClipAdapter clipAdapter;

    private HashMap<Clip, Integer> notSortedHashMap;
    private HashMap<Clip, Integer> sortedHashmap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top_clips, container, false);

        initalizeViews(view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        Date oneDayAgo = calendar.getTime();

        for (String streamerID : ((MainActivity) getActivity()).getStreamerIdList()){
            JsonObjectRequest jsonObjectRequest = twitchClient.getClipsRequest(streamerID, 5, oneDayAgo, now,
                    new TwitchClient.VolleyCallback() {
                @Override
                public void onComplete(HashMap<Clip, Integer> clips) {
                    notSortedHashMap.putAll(clips);
                }

                @Override
                public void onComplete(String userImageURL) {
                    // Empty
                }
            });

            if (getContext() != null){
                requestQueue.add(jsonObjectRequest);
            }

        }

    }

    private void initalizeViews(View view){
        clipArrayList = new ArrayList<>();
        notSortedHashMap = new HashMap<>();
        sortedHashmap = new HashMap<>();

        requestQueue = Volley.newRequestQueue(getContext());
        twitchClient = new TwitchClient(
                getResources().getString(R.string.twitch_client_id),
                getResources().getString(R.string.twitch_access_token));

        Button refreshButton = view.findViewById(R.id.button_refresh);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clipArrayList.clear();
                sortedHashmap = sortByValue(notSortedHashMap);
                for (Map.Entry<Clip, Integer> clip : sortedHashmap.entrySet()){
                    clipArrayList.add(clip.getKey());
                }
                clipAdapter.notifyDataSetChanged();
            }
        });

        RecyclerView clipsRecyclerView = view.findViewById(R.id.rv_clips);
        clipsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        clipAdapter = new ClipAdapter(clipArrayList, getContext(), twitchClient);
        clipsRecyclerView.setAdapter(clipAdapter);


    }

    public static HashMap<Clip, Integer> sortByValue(HashMap<Clip, Integer> hm) {
        // Create a list from elements of HashMap
        List<Map.Entry<Clip, Integer> > list =
                new LinkedList<Map.Entry<Clip, Integer> >(hm.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<Clip, Integer> >() {
            public int compare(Map.Entry<Clip, Integer> o1,
                               Map.Entry<Clip, Integer> o2)
            {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        // put data from sorted list to hashmap
        HashMap<Clip, Integer> temp = new LinkedHashMap<Clip, Integer>();
        for (Map.Entry<Clip, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

}

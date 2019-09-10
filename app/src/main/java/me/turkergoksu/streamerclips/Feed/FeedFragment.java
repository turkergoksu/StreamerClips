package me.turkergoksu.streamerclips.Feed;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

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

    // TODO: 10-Sep-19 Sadece FeedFragment a özel o gün izlenen kliplerde hangi zaman aralığında
    //  ise o kliplerin sıralaması idleri ile beraber Firebase'e gönderilmeli.
    // TODO: 10-Sep-19 Pagination ekle
    // TODO: 10-Sep-19 Kullanıcı istediği yayıncıları ignorelayabilir

    private static final String TAG = FeedFragment.class.getSimpleName();

    private RequestQueue requestQueue;
    private TwitchClient twitchClient;

    private ArrayList<Clip> clipArrayList;
    private ClipAdapter clipAdapter;

    private HashMap<Clip, Integer> notSortedHashMap;
    private HashMap<Clip, Integer> sortedHashMap;

    private SharedPreferences prefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        initializeViews(view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void initializeViews(View view){
        prefs = getContext().getSharedPreferences("FeedPrefs", Context.MODE_PRIVATE);
        Log.d(TAG, "initializeViews: " + prefs.getAll());

        if (clipArrayList == null) {
            clipArrayList = new ArrayList<>();
        }

        notSortedHashMap = new HashMap<>();
        sortedHashMap = new HashMap<>();

        requestQueue = Volley.newRequestQueue(getContext());
        twitchClient = new TwitchClient(
                getResources().getString(R.string.twitch_client_id),
                getResources().getString(R.string.twitch_access_token));

//        Button refreshButton = view.findViewById(R.id.button_refresh);
//        refreshButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                clipArrayList.clear();
//                sortedHashMap = sortByValue(notSortedHashMap);
//                for (Map.Entry<Clip, Integer> clip : sortedHashMap.entrySet()){
//                    clipArrayList.add(clip.getKey());
//                }
//                clipAdapter.notifyDataSetChanged();
//            }
//        });

        RecyclerView clipsRecyclerView = view.findViewById(R.id.rv_clips);
        clipsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        clipAdapter = new ClipAdapter(clipArrayList, getContext(), twitchClient);
        clipsRecyclerView.setAdapter(clipAdapter);

        Spinner timeIntervalSpinner = view.findViewById(R.id.spinner_timeInterval);
        timeIntervalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                Log.d(TAG, "onItemSelected: " + position);
                int lastSelectedTimeInterval = prefs.getInt("lastSelectedTimeInterval", -1);
                Log.d(TAG, "onItemSelected:last " + lastSelectedTimeInterval);

                // IF lastSelectedItemInterval not stored in SharedPrefs then It's called for the first time
                if (lastSelectedTimeInterval == -1){
                    Calendar calendar = Calendar.getInstance();
                    Date now = calendar.getTime();
                    Date firstTimeInterval = null;
                    notSortedHashMap.clear();
                    sortedHashMap.clear();
                    clipArrayList.clear();
                    clipAdapter.notifyDataSetChanged();
                    // TODO: 10-Sep-19 Add a loading animation

                    switch (position){
                        case 0:
                            calendar.add(Calendar.DAY_OF_MONTH, -1);
                            firstTimeInterval = calendar.getTime();
                            break;
                        case 1:
                            calendar.add(Calendar.DAY_OF_MONTH, -7);
                            firstTimeInterval = calendar.getTime();
                            break;
                        case 2:
                            calendar.add(Calendar.DAY_OF_MONTH, -30);
                            firstTimeInterval = calendar.getTime();
                            break;
                    }

                    for (String streamerID : ((MainActivity) getActivity()).getStreamerIdList()){
                        JsonObjectRequest jsonObjectRequest = twitchClient.getClipsRequest(streamerID, 5, firstTimeInterval, now,
                                new TwitchClient.VolleyCallback() {
                                    @Override
                                    public void onComplete(HashMap<Clip, Integer> clips) {
                                        notSortedHashMap.putAll(clips);
                                        clipArrayList.clear();
                                        sortedHashMap = sortByValue(notSortedHashMap);
                                        for (Map.Entry<Clip, Integer> clip : sortedHashMap.entrySet()){
                                            clipArrayList.add(clip.getKey());
                                        }
                                        clipAdapter.notifyDataSetChanged();
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
                // IF lastSelectedItemInterval equals current selected position then we don't need to initialize clipArrayList
                else if (lastSelectedTimeInterval == position){
                    // Empty
                }
                // IF lastSelectedItemInterval not equals selected position we need to initialize clipArrayList
                else {
                    Calendar calendar = Calendar.getInstance();
                    Date now = calendar.getTime();
                    Date firstTimeInterval = null;
                    notSortedHashMap.clear();
                    sortedHashMap.clear();
                    clipArrayList.clear();
                    clipAdapter.notifyDataSetChanged();
                    // TODO: 10-Sep-19 Add a loading animation

                    switch (position){
                        case 0:
                            calendar.add(Calendar.DAY_OF_MONTH, -1);
                            firstTimeInterval = calendar.getTime();
                            break;
                        case 1:
                            calendar.add(Calendar.DAY_OF_MONTH, -7);
                            firstTimeInterval = calendar.getTime();
                            break;
                        case 2:
                            calendar.add(Calendar.DAY_OF_MONTH, -30);
                            firstTimeInterval = calendar.getTime();
                            break;
                    }

                    for (String streamerID : ((MainActivity) getActivity()).getStreamerIdList()){
                        JsonObjectRequest jsonObjectRequest = twitchClient.getClipsRequest(streamerID, 5, firstTimeInterval, now,
                                new TwitchClient.VolleyCallback() {
                                    @Override
                                    public void onComplete(HashMap<Clip, Integer> clips) {
                                        notSortedHashMap.putAll(clips);
                                        clipArrayList.clear();
                                        sortedHashMap = sortByValue(notSortedHashMap);
                                        for (Map.Entry<Clip, Integer> clip : sortedHashMap.entrySet()){
                                            clipArrayList.add(clip.getKey());
                                        }
                                        clipAdapter.notifyDataSetChanged();
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

                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("lastSelectedTimeInterval", position).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: " + prefs.getAll());
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("lastSelectedTimeInterval").apply();
        Log.d(TAG, "onStop: " + prefs.getAll());
    }
}

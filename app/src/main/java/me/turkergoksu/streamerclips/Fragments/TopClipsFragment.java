package me.turkergoksu.streamerclips.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import me.turkergoksu.streamerclips.Clip;
import me.turkergoksu.streamerclips.ClipAdapter;
import me.turkergoksu.streamerclips.MainActivity;
import me.turkergoksu.streamerclips.R;

public class TopClipsFragment extends Fragment {

    private static final String TAG = TopClipsFragment.class.getSimpleName();

    private RequestQueue requestQueue;

    private ArrayList<Clip> clipArrayList;
    private ClipAdapter clipAdapter;

    HashMap<Clip, Integer> notSortedHashMap;
    HashMap<Clip, Integer> sortedHashmap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top_clips, container, false);
        clipArrayList = new ArrayList<>();
        notSortedHashMap = new HashMap<>();
        sortedHashmap = new HashMap<>();

        Button refreshButton = view.findViewById(R.id.button_refresh);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortedHashmap = sortByValue(notSortedHashMap);
                for (Map.Entry<Clip, Integer> clip : sortedHashmap.entrySet()){
                    clipArrayList.add(clip.getKey());
                }
                clipAdapter.notifyDataSetChanged();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView clipsRecyclerView = view.findViewById(R.id.rv_clips);
        clipsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        clipAdapter = new ClipAdapter(clipArrayList, getContext());
        clipsRecyclerView.setAdapter(clipAdapter);

        //        String url = "https://api.twitch.tv/helix/users?id=6768122";
//        String url = "https://api.twitch.tv/helix/clips?broadcaster_id=6768122&first=5&started_at=2019-08-23T20:32:00Z";
//        String url = "https://api.twitch.tv/helix/clips?id=AmazingBloodyAnteaterMau5";
//        String url = "https://api.twitch.tv/kraken/streams/?game=Overwatch";

        for (String streamerId : ((MainActivity) getActivity()).getStreamerIdList()){
            String url = clipUrlBuilder(streamerId, 20, "2019-08-24T20:00:00Z");
            addToRequestQueue(jsonObjectGetRequest(url), "headerRequest");
        }

    }

    public RequestQueue getRequestQueue() {
//        if (requestQueue == null)
        requestQueue = Volley.newRequestQueue(getContext());
        return requestQueue;
    }

    public void addToRequestQueue(Request request, String tag) {
        request.setTag(tag);
        getRequestQueue().add(request);
    }

    public JsonObjectRequest jsonObjectGetRequest(String url){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Success Callback
                        try {
                            JSONArray data = (JSONArray) response.get("data");

                            for (int i = 0; i < data.length(); ++i){
                                JSONObject child = (JSONObject) data.get(i);
                                Clip clip = new Clip(
                                        child.getString("id"),
                                        child.getString("url"),
                                        child.getString("embed_url"),
                                        child.getString("broadcaster_id"),
                                        child.getString("broadcaster_name"),
                                        child.getString("creator_id"),
                                        child.getString("creator_name"),
                                        child.getString("title"),
                                        child.getInt("view_count"),
                                        child.getString("created_at"),
                                        child.getString("thumbnail_url")
                                );
//                                clipArrayList.add(clip);
                                notSortedHashMap.put(clip, clip.getViewCount());
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Failure Callback
                        Log.d(TAG, "onErrorResponse: " + error.getLocalizedMessage());
                    }
                })
        {

            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");

                // TODO: 30-Aug-19 ignore twitch_client_id.xml
                headers.put("Client-ID", getResources().getString(R.string.twitch_client_id));
                return headers;
            }

        };

        return jsonObjectRequest;
    }

    public String clipUrlBuilder(String broadcasterID, int first, String startedAt){
        StringBuilder url = new StringBuilder("https://api.twitch.tv/helix/clips?");
        url.append("broadcaster_id=").append(broadcasterID).append("&");
        url.append("first=").append(first).append("&");
        url.append("started_at=").append(startedAt);

        return url.toString();
    }

    public String streamersUrlBuilder(ArrayList<String> streamerArray){
        StringBuilder url = new StringBuilder("https://api.twitch.tv/helix/users?");
        url.append("login=").append(streamerArray.get(0));
        for (String username : streamerArray){
            url.append("&login=").append(username);
        }

        return url.toString();
    }

    public static HashMap<Clip, Integer> sortByValue(HashMap<Clip, Integer> hm)
    {
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

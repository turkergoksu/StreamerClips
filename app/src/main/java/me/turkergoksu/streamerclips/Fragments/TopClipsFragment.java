package me.turkergoksu.streamerclips.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import java.util.HashMap;
import java.util.Map;

import me.turkergoksu.streamerclips.Clip;
import me.turkergoksu.streamerclips.ClipAdapter;
import me.turkergoksu.streamerclips.R;

public class TopClipsFragment extends Fragment {

    private static final String TAG = TopClipsFragment.class.getSimpleName();

    private RequestQueue requestQueue;

    private ArrayList<Clip> clipArrayList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top_clips, container, false);
        clipArrayList = new ArrayList<>();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //        String url = "https://api.twitch.tv/helix/users?id=6768122";
        String url = "https://api.twitch.tv/helix/clips?broadcaster_id=6768122&first=5&started_at=2019-08-23T20:32:00Z";
//        String url = "https://api.twitch.tv/helix/clips?id=AmazingBloodyAnteaterMau5";
//        String url = "https://api.twitch.tv/kraken/streams/?game=Overwatch";

        addToRequestQueue(jsonObjectGetRequest(url), "headerrequest");
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null)
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
                        Log.d(TAG, "onResponse: " + response.toString());
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
                                clipArrayList.add(clip);
                            }

                            Log.d(TAG, "onResponse: " + clipArrayList.get(0).getCreatorName());
                            RecyclerView clipsRecyclerView = getView().findViewById(R.id.rv_clips);
                            clipsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            clipsRecyclerView.setAdapter(new ClipAdapter(clipArrayList, getContext()));

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

}

package me.turkergoksu.streamerclips;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import me.turkergoksu.streamerclips.Fragments.TopClipsFragment;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction
                .replace(R.id.frame_layout, new TopClipsFragment())
                .addToBackStack(null)
                .commit();

    }



    public String clipUrlBuilder(String broadcasterID, int first, String startedAt){
        StringBuilder url = new StringBuilder("https://api.twitch.tv/helix/clips?");
        url.append("broadcaster_id=").append(broadcasterID).append("&");
        url.append("first=").append(first).append("&");
        url.append("started_at=").append(startedAt);

        return url.toString();
    }

    public String streamersUrlBuilder(String[] streamerArray){
        StringBuilder url = new StringBuilder("https://api.twitch.tv/helix/users?");
        url.append("login=").append(streamerArray[0]);
        for (String username : streamerArray){
            url.append("&login=").append(username);
        }

        return url.toString();
    }

    public String singleUserUrlBuilder(String username){
        StringBuilder url = new StringBuilder("https://api.twitch.tv/helix/users?");
        url.append("login=").append(username);

        return url.toString();
    }


    public JsonArrayRequest jsonArrayRequest(String url){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, "onResponse: " + response.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                Log.d(TAG, "getHeaders: ");
                headers.put("Content-Type", "application/json");

                // TODO: 30-Aug-19 ignore twitch_client_id.xml
                headers.put("Client-ID", getResources().getString(R.string.twitch_client_id));
                return headers;
            }
        };

        return jsonArrayRequest;
    }
}

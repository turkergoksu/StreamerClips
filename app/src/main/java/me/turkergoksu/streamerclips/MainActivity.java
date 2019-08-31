package me.turkergoksu.streamerclips;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

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

    private ArrayList<String> streamerIdList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO: 31-Aug-19 temporary. I will take the streamer id list later from database
        streamerIdList = new ArrayList<>();
        streamerIdList.add("6768122");
        streamerIdList.add("24233423");
        streamerIdList.add("51950404");

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction
                .replace(R.id.frame_layout, new TopClipsFragment())
                .addToBackStack(null)
                .commit();

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

    public ArrayList<String> getStreamerIdList() {
        return streamerIdList;
    }

    public void backButton(View view){
        getSupportFragmentManager().popBackStack();
    }
}

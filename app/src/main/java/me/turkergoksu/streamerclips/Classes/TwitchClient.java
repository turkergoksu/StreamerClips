package me.turkergoksu.streamerclips.Classes;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import me.turkergoksu.streamerclips.Classes.Clip;

public class TwitchClient {

    private final static String API_URL = "https://api.twitch.tv/helix/";

    /**
     * To get unique access token from user
     */
    private final static String AUTHORIZATION_URL = "https://id.twitch.tv/oauth2/authorize?";
    private final static String REDIRECT_URL = "https://www.twitch.tv";
    private final static String RESPONSE_TYPE = "token";
    private final static String SCOPE_NAME = "user:read:email";

    private final static String CLIENT_ID_QUERY_PARAMETER = "client_id";
    private final static String REDIRECT_URI_QUERY_PARAMETER = "redirect_uri";
    private final static String RESPONSE_TYPE_QUERY_PARAMETER = "response_type";
    private final static String SCOPE_QUERY_PARAMETER = "scope";
    private final static String ASSIGN_URL_NAMESPACE = "=";
    private final static String AND_URL_NAMESPACE = "&";

    /**
     * To get clips
     */
    private final static String CLIPS_NAMESPACE = "clips?";
    private final static String BROADCASTER_ID_QUERY_PARAMETER = "broadcaster_id";
    private final static String FIRST_QUERY_PARAMETER = "first";
    private final static String STARTED_AT_QUERY_PARAMETER = "started_at";
    private final static String ENDED_AT_QUERY_PARAMETER = "ended_at";
    private final static String T_IN_DATE_NAMESPACE = "T";
    private final static String Z_IN_DATE_NAMESPACE = "Z";

    /**
     * To get users
     */
    private final static String USERS_NAMESPACE = "users?";
    private final static String ID_QUERY_PARAMETER = "id";

    private String clientID;
    private String accessToken;

    public TwitchClient(String clientID, String accessToken) {
        this.clientID = clientID;
        this.accessToken = accessToken;
    }

    public interface VolleyCallback{
        void onComplete(HashMap<Clip, Integer> clips);
        void onComplete(String userImageURL);
    }

    private String generateAuthorizationURL(){
        StringBuilder authorizationURL = new StringBuilder();
        // Initialize authorization url
        authorizationURL.append(AUTHORIZATION_URL);

        // Add clientID
        authorizationURL
                .append(CLIENT_ID_QUERY_PARAMETER)
                .append(ASSIGN_URL_NAMESPACE)
                .append(clientID)
                .append(AND_URL_NAMESPACE);

        // Add redirectURL
        authorizationURL
                .append(REDIRECT_URI_QUERY_PARAMETER)
                .append(ASSIGN_URL_NAMESPACE)
                .append(REDIRECT_URL)
                .append(AND_URL_NAMESPACE);

        // Add response type
        authorizationURL
                .append(RESPONSE_TYPE_QUERY_PARAMETER)
                .append(ASSIGN_URL_NAMESPACE)
                .append(RESPONSE_TYPE)
                .append(AND_URL_NAMESPACE);

        // Add scope name
        authorizationURL
                .append(SCOPE_QUERY_PARAMETER)
                .append(ASSIGN_URL_NAMESPACE)
                .append(SCOPE_NAME);

        return authorizationURL.toString();
    }

    private String generateGetClipURL(String broadcasterID, int first, Date startedAt, Date endedAt){
        SimpleDateFormat dateSDF = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeSDF = new SimpleDateFormat("HH:mm:ss");

        StringBuilder clipRequestURL = new StringBuilder();

        // Initialization
        clipRequestURL
                .append(API_URL)
                .append(CLIPS_NAMESPACE);

        // Add broadcasterID
        clipRequestURL
                .append(BROADCASTER_ID_QUERY_PARAMETER)
                .append(ASSIGN_URL_NAMESPACE)
                .append(broadcasterID)
                .append(AND_URL_NAMESPACE);

        // Add first
        clipRequestURL
                .append(FIRST_QUERY_PARAMETER)
                .append(ASSIGN_URL_NAMESPACE)
                .append(first)
                .append(AND_URL_NAMESPACE);

        // Add startedAt
        StringBuilder startDate = new StringBuilder();
        startDate
                .append(dateSDF.format(startedAt))
                .append(T_IN_DATE_NAMESPACE)
                .append(timeSDF.format(startedAt))
                .append(Z_IN_DATE_NAMESPACE);

        clipRequestURL
                .append(STARTED_AT_QUERY_PARAMETER)
                .append(ASSIGN_URL_NAMESPACE)
                .append(startDate)
                .append(AND_URL_NAMESPACE);

        // Add endedAt
        StringBuilder endDate = new StringBuilder();
        endDate
                .append(dateSDF.format(endedAt))
                .append(T_IN_DATE_NAMESPACE)
                .append(timeSDF.format(endedAt))
                .append(Z_IN_DATE_NAMESPACE);

        clipRequestURL
                .append(ENDED_AT_QUERY_PARAMETER)
                .append(ASSIGN_URL_NAMESPACE)
                .append(endDate);

        return clipRequestURL.toString();
    }

    public JsonObjectRequest getClipsRequest(String broadcasterID, int first, Date startedAt,
                                                  Date endedAt, final VolleyCallback callback){
        String requestURL = generateGetClipURL(broadcasterID, first, startedAt, endedAt);

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                requestURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Success Callback
                        try {
                            HashMap<Clip, Integer> clips = new HashMap<>();
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
                                clips.put(clip, clip.getViewCount());
                            }
                            callback.onComplete(clips);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Failure Callback
                    }
                })
        {

            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");

                headers.put("Client-ID", clientID);
                headers.put("Authorization", "Bearer " + accessToken);
                return headers;
            }

        };

        return jsonObjectRequest;
    }

    private String generateGetUsersURL(String userID){
        StringBuilder clipRequestURL = new StringBuilder();

        // Initialization
        clipRequestURL
                .append(API_URL)
                .append(USERS_NAMESPACE);

        // Add userID
        clipRequestURL
                .append(ID_QUERY_PARAMETER)
                .append(ASSIGN_URL_NAMESPACE)
                .append(userID);

        return clipRequestURL.toString();
    }

    public JsonObjectRequest getUsersRequest(final String userID, final VolleyCallback callback){
        final String requestURL = generateGetUsersURL(userID);

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                requestURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Success Callback
                        try {
                            JSONArray data = (JSONArray) response.get("data");
                            JSONObject user = (JSONObject) data.get(0);
                            String userImageURL = user.getString("profile_image_url");
                            callback.onComplete(userImageURL);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Failure Callback
                    }
                })
        {

            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");

                headers.put("Client-ID", clientID);
                headers.put("Authorization", "Bearer " + accessToken);
                return headers;
            }

        };

        return jsonObjectRequest;
    }

}

package me.turkergoksu.streamerclips.TwitchLogin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import me.turkergoksu.streamerclips.R;
import me.turkergoksu.streamerclips.Classes.TwitchClient;

public class LoginFragment extends Fragment {

    private WebView twitchLoginWebView;

    private TwitchClient twitchClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        initializeViews(view);

        return view;
    }

    private void initializeViews(View view){
        // Set Webview
        twitchLoginWebView = view.findViewById(R.id.wv_twitch_login_page);
        twitchLoginWebView.setWebViewClient(new WebViewClient(){

        });
        twitchLoginWebView.getSettings().setJavaScriptEnabled(true);

        // Set TwitchClient
//        twitchClient = new TwitchClient(getContext(), getResources().getString(R.string.twitch_client_id));
    }
}

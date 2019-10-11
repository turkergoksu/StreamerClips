package me.turkergoksu.streamerclips.Feed;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import me.turkergoksu.streamerclips.Classes.Clip;
import me.turkergoksu.streamerclips.MainActivity;
import me.turkergoksu.streamerclips.R;

public class ClipDialogFragment extends DialogFragment {

    private static final String TAG = ClipDialogFragment.class.getSimpleName();
    private Clip clip;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_clip, container, false);

        // TODO: 06-Sep-19 Belki embed linki yerine direkt olarak .mp4 linkini koymayı dene
        if (getArguments() != null) {
            clip = getArguments().getParcelable("clip");
        }

        initializeViews(view);

        return view;
    }

    private void initializeViews(View view){
        WebView webView = view.findViewById(R.id.wv_embed_clip);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl(clip.getEmbedURL());

        ImageButton dismissButton = view.findViewById(R.id.ib_dismiss_dialog_button);
        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        // Resize dialog according to current orientaion
        int width = 0;
        int height = 0;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            width = getResources().getDimensionPixelSize(R.dimen.portrait_clip_dialog_popup_width);
            height = getResources().getDimensionPixelSize(R.dimen.portrait_clip_dialog_popup_height);
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            width = getResources().getDimensionPixelSize(R.dimen.landscape_clip_dialog_popup_width);
            height = getResources().getDimensionPixelSize(R.dimen.landscape_clip_dialog_popup_height);
        }

        getDialog().getWindow().setLayout(width, height);
        getDialog().getWindow().setGravity(Gravity.CENTER);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Resize dialog when orientation changes
        int width = 0;
        int height = 0;
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            width = getResources().getDimensionPixelSize(R.dimen.portrait_clip_dialog_popup_width);
            height = getResources().getDimensionPixelSize(R.dimen.portrait_clip_dialog_popup_height);
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            width = getResources().getDimensionPixelSize(R.dimen.landscape_clip_dialog_popup_width);
            height = getResources().getDimensionPixelSize(R.dimen.landscape_clip_dialog_popup_height);
        }
        getDialog().getWindow().setLayout(width, height);
        getDialog().getWindow().setGravity(Gravity.CENTER);
    }
}

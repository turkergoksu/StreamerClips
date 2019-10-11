package me.turkergoksu.streamerclips;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import me.turkergoksu.streamerclips.Feed.FeedFragment;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();

        FirebaseApp.initializeApp(this);

        final RelativeLayout loadingPanelRelativeLayout = findViewById(R.id.loadingPanel);
        final ProgressBar progressBar = findViewById(R.id.progress_horizontal);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest.Builder builder = new NetworkRequest.Builder();

        // Check if user has available connection
        if (!cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected() || !cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected()){
            TextView fixedOpeningTextView = findViewById(R.id.tv_fixed_opening);
            fixedOpeningTextView.setText("Bağlantın yok.. ( ͡° ͜ʖ ͡°)╭∩╮");
            progressBar.setIndeterminate(false);
        }

        // Listener for internet connection
        cm.registerNetworkCallback(builder.build(), new ConnectivityManager.NetworkCallback(){
            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView fixedOpeningTextView = findViewById(R.id.tv_fixed_opening);
                        ProgressBar progressBar = findViewById(R.id.progress_horizontal);
                        fixedOpeningTextView.setText("Açılıyor.. (▀̿Ĺ̯▀̿ ̿)");
                        progressBar.setIndeterminate(true);
                    }
                });

                auth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            loadingPanelRelativeLayout.setVisibility(View.GONE);
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.frame_layout, new FeedFragment())
                                    .addToBackStack(null)
                                    .commit();
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TextView fixedOpeningTextView = findViewById(R.id.tv_fixed_opening);
                                    fixedOpeningTextView.setText("Sunucuya erişemedik..  ¯\\_(ツ)_/¯");
                                    progressBar.setIndeterminate(false);
                                }
                            });
                        }
                    }
                });
            }
        });

    }

}

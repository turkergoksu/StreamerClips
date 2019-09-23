package me.turkergoksu.streamerclips;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
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

    // Time interval position for Spinner. -1 = first time initialization.
    public int lastSelectedTimeIntervalPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();

        FirebaseApp.initializeApp(this);

        auth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction
                            .replace(R.id.frame_layout, new FeedFragment())
                            .addToBackStack(null)
                            .commit();
                } else {
                    Toast.makeText(getApplicationContext(), "Issue while signin in!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

}

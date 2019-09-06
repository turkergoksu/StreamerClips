package me.turkergoksu.streamerclips;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import me.turkergoksu.streamerclips.Feed.FeedFragment;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ArrayList<String> streamerIdList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (streamerIdList == null)
            streamerIdList = new ArrayList<>();

        FirebaseApp.initializeApp(this);
        FirebaseAuth.getInstance().signInWithEmailAndPassword(getResources().getString(R.string.email),
                getResources().getString(R.string.password)).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                FirebaseDatabase.getInstance().getReference().child("StreamerList").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                            streamerIdList.add((String) childSnapshot.getValue());
                        }

                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction
                                .replace(R.id.frame_layout, new FeedFragment())
                                .addToBackStack(null)
                                .commit();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

    }

    public String singleUserUrlBuilder(String username){
        StringBuilder url = new StringBuilder("https://api.twitch.tv/helix/users?");
        url.append("login=").append(username);

        return url.toString();
    }

    public ArrayList<String> getStreamerIdList() {
        return streamerIdList;
    }

    public void backButton(View view){
        getSupportFragmentManager().popBackStack();
    }
}

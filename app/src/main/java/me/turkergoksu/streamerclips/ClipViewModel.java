package me.turkergoksu.streamerclips;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import me.turkergoksu.streamerclips.Classes.Clip;

public class ClipViewModel extends ViewModel {

    private MutableLiveData<List<Clip>> clips;

    public LiveData<List<Clip>> getClips(String timeInterval) {
        if (clips == null){
            clips = new MutableLiveData<>();
            fetchClips(timeInterval);
        }
        return clips;
    }

    private void fetchClips(String timeInterval){
        DatabaseReference trDatabaseReference = FirebaseDatabase.getInstance().getReference()
                .child("TR").child(timeInterval).child("clips");

        trDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Clip> newClips = new ArrayList<>();

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    Clip clip = childSnapshot.getValue(Clip.class);
                    newClips.add(clip);
                    Log.d("TEST", "onDataChange: " + clip.getBroadcasterName());
                }

                clips.postValue(newClips);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

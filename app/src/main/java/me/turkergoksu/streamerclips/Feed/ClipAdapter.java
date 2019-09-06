package me.turkergoksu.streamerclips.Feed;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;

import me.turkergoksu.streamerclips.Classes.Clip;
import me.turkergoksu.streamerclips.Classes.TwitchClient;
import me.turkergoksu.streamerclips.ClipDetails.ClipDetailsFragment;
import me.turkergoksu.streamerclips.MainActivity;
import me.turkergoksu.streamerclips.R;

public class ClipAdapter extends RecyclerView.Adapter<ClipAdapter.ClipViewHolder> {

    private ArrayList<Clip> clipArrayList;
    private Context context;
    private TwitchClient twitchClient;

    public static class ClipViewHolder extends RecyclerView.ViewHolder {
        private ImageView thumbnailImageView;
        private TextView clipTitleTextView;
        private ImageView channelImageView;
        private TextView channelNameTextView;
        private TextView clipViewCountTextView;
        private TextView clipCreatorNameTextView;
        private TextView placeNumberTextView;

        public ClipViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnailImageView = itemView.findViewById(R.id.iv_clip_thumbnail);
            clipTitleTextView = itemView.findViewById(R.id.tv_clip_title);
            channelImageView = itemView.findViewById(R.id.iv_broadcaster_channel_image);
            channelNameTextView = itemView.findViewById(R.id.tv_broadcaster_channel_name);
            clipViewCountTextView = itemView.findViewById(R.id.tv_clip_view_count);
            clipCreatorNameTextView = itemView.findViewById(R.id.tv_clip_creator_name);
            placeNumberTextView = itemView.findViewById(R.id.tv_place_number);
        }
    }

    public ClipAdapter(ArrayList<Clip> clipArrayList, Context context, TwitchClient twitchClient) {
        this.clipArrayList = clipArrayList;
        this.context = context;
        this.twitchClient = twitchClient;
    }

    @NonNull
    @Override
    public ClipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_clip, parent, false);

        return new ClipViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ClipViewHolder holder, int position) {
        final Clip clip = clipArrayList.get(position);

        // Set clip thumbnail image
        Glide.with(context).load(Uri.parse(clip.getThumbnailImageURL())).into(holder.thumbnailImageView);

        holder.clipTitleTextView.setText(clip.getTitle());
        holder.channelNameTextView.setText(clip.getBroadcasterName());
        holder.clipViewCountTextView.setText(String.valueOf(clip.getViewCount()));
        holder.clipCreatorNameTextView.setText(clip.getCreatorName());
        holder.placeNumberTextView.setText(new StringBuilder().append("#").append(position+1).toString());

        // User request for profile image
        Volley.newRequestQueue(context).add(twitchClient.getUsersRequest(clip.getBroadcasterID(), new TwitchClient.VolleyCallback() {
            @Override
            public void onComplete(HashMap<Clip, Integer> clips) {
                // Empty
            }

            @Override
            public void onComplete(String userImageURL) {
                Glide.with(context).load(userImageURL).into(holder.channelImageView);
            }
        }));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = ((MainActivity) context).getSupportFragmentManager().beginTransaction();

                Bundle args = new Bundle();
                args.putParcelable("clip", clip);

                ClipDetailsFragment clipDetailsFragment = new ClipDetailsFragment();
                clipDetailsFragment.setArguments(args);

                fragmentTransaction
                        .replace(R.id.frame_layout, clipDetailsFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return clipArrayList.size();
    }
}

package me.turkergoksu.streamerclips.Feed;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import me.turkergoksu.streamerclips.Classes.Clip;
import me.turkergoksu.streamerclips.Classes.TwitchClient;
import me.turkergoksu.streamerclips.MainActivity;
import me.turkergoksu.streamerclips.R;

public class ClipAdapter extends RecyclerView.Adapter<ClipAdapter.ClipViewHolder> {

    private static final String TAG = ClipAdapter.class.getSimpleName();

    public interface OnItemClickListener {
        void onItemClick(View view, Clip clip);
    }

    private ArrayList<Clip> clipArrayList;
    private Context context;
//    private TwitchClient twitchClient;
    private OnItemClickListener onItemClickListener;

    public static class ClipViewHolder extends RecyclerView.ViewHolder {
        private ImageView thumbnailImageView;
        private TextView clipTitleTextView;
        private ImageView channelImageView;
        private TextView channelNameTextView;
        private TextView clipViewCountTextView;
        private TextView clipCreatorNameTextView;
        private TextView placeNumberTextView;
//        private TextView clipDateTextView;
        private TextView isWatchedTextView;
        private ImageView optionsImageView;

        public ClipViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnailImageView = itemView.findViewById(R.id.iv_clip_thumbnail);
            clipTitleTextView = itemView.findViewById(R.id.tv_clip_title);
            channelImageView = itemView.findViewById(R.id.iv_broadcaster_channel_image);
            channelNameTextView = itemView.findViewById(R.id.tv_broadcaster_channel_name);
            clipViewCountTextView = itemView.findViewById(R.id.tv_clip_view_count);
            clipCreatorNameTextView = itemView.findViewById(R.id.tv_clip_creator_name);
            placeNumberTextView = itemView.findViewById(R.id.tv_place_number);
//            clipDateTextView = itemView.findViewById(R.id.tv_clip_date);
            isWatchedTextView = itemView.findViewById(R.id.tv_watched);
            optionsImageView = itemView.findViewById(R.id.iv_clip_options);
        }
    }

    public ClipAdapter(ArrayList<Clip> clipArrayList, Context context, OnItemClickListener onItemClickListener) {
        this.clipArrayList = clipArrayList;
        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ClipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 0 = not watched, 1 = watched
        if (viewType == 0){
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_clip, parent, false);
            return new ClipViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_watched_clip, parent, false);
            return new ClipViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ClipViewHolder holder, final int position) {
        final Clip clip = clipArrayList.get(position);

        // Set clip thumbnail image
        Glide.with(context).load(Uri.parse(clip.getThumbnailImageURL())).into(holder.thumbnailImageView);

        holder.clipTitleTextView.setText(clip.getTitle());
        holder.channelNameTextView.setText(clip.getBroadcasterName());
        holder.clipViewCountTextView.setText(String.valueOf(clip.getViewCount()));
        holder.clipCreatorNameTextView.setText(clip.getCreatorName());
        holder.placeNumberTextView.setText(new StringBuilder().append("#").append(position + 1).toString());

        // Set clip date
//        StringBuilder sb = new StringBuilder(clip.getCreatedAt());
//        sb.setCharAt(sb.indexOf("T"), ' '); // remove T
//        sb.deleteCharAt(sb.length() - 1);  // remove Z
//        try {
//            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(sb.toString());
//            long timeInMillis = date.getTime();
//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM HH:mm");
//            Date time = new Date(timeInMillis);
//            String createdAt = simpleDateFormat.format(time);
//            holder.clipDateTextView.setText(createdAt);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

//        boolean isWatched = clip.isWatched();
//        if (clip.isWatched()){
//            // Set the view as 'watched'
//            ((CardView)holder.itemView).setForeground(context.getDrawable(R.drawable.watched_clip_overlay));
//            holder.isWatchedTextView.setVisibility(View.VISIBLE);
//        }

        // User request for profile image
//        Volley.newRequestQueue(context).add(twitchClient.getUsersRequest(clip.getBroadcasterID(), new TwitchClient.VolleyCallback() {
//            @Override
//            public void onComplete(HashMap<Clip, Long> clips) {
//                // Empty
//            }
//
//            @Override
//            public void onComplete(String userImageURL) {
//                Glide.with(context).load(userImageURL).into(holder.channelImageView);
//            }
//        }));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(view, clip);
            }
        });

        holder.optionsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context, view);
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.item_clip_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.notwatched:
                                clipArrayList.get(position).setWatched(false);
                                ((CardView)holder.itemView).setForeground(null);
                                holder.isWatchedTextView.setVisibility(View.GONE);

                                SharedPreferences prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();
                                Set<String> watchedSet = prefs.getStringSet("watchedSet", null);
                                if (watchedSet != null){
                                    watchedSet.remove(clip.getId());
                                    editor.remove("watchedSet");
                                    editor.commit();
                                    editor.putStringSet("watchedSet", watchedSet);
                                    editor.commit();
                                }
                                return true;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        // 0 = not watched, 1 = watched
        if (!clipArrayList.get(position).isWatched()){
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public int getItemCount() {
        return clipArrayList.size();
    }
}

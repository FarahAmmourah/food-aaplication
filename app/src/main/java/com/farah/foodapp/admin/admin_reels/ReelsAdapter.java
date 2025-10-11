package com.farah.foodapp.admin.admin_reels;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.farah.foodapp.R;

import java.io.IOException;
import java.util.List;

public class ReelsAdapter extends RecyclerView.Adapter<ReelsAdapter.ReelViewHolder> {

    private Context context;
    private List<ReelModel> reels;

    public ReelsAdapter(Context context, List<ReelModel> reels) {
        this.context = context;
        this.reels = reels;
    }

    @NonNull
    @Override
    public ReelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reel_thumbnail, parent, false);
        return new ReelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReelViewHolder holder, int position) {
        ReelModel reel = reels.get(position);

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(reel.getVideoUrl(), new java.util.HashMap<>());
            holder.thumbnail.setImageBitmap(retriever.getFrameAtTime(1000000));
        } catch (Exception e) {
            holder.thumbnail.setImageResource(R.drawable.ic_video_placeholder);
        } finally {
            try {
                retriever.release();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ReelDetailActivity.class);
            intent.putExtra("title", reel.getTitle());
            intent.putExtra("description", reel.getDescription());
            intent.putExtra("videoUrl", reel.getVideoUrl());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return reels.size();
    }

    public static class ReelViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        public ReelViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.imgThumbnail);
        }
    }
}

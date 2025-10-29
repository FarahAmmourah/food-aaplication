package com.farah.foodapp.admin.admin_reels;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.fragment.app.Fragment;
import androidx.media3.common.MediaItem;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.cache.CacheDataSource;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.farah.foodapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyReelsFragment extends Fragment {

    private Uri selectedVideoUri;
    private ActivityResultLauncher<Intent> pickVideoLauncher;

    private RecyclerView recyclerReels;
    private ReelsAdapter adapter;
    private final List<ReelModel> reelList = new ArrayList<>();
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_reels, container, false);

        Button btnAddReel = view.findViewById(R.id.btnAddReel);
        recyclerReels = view.findViewById(R.id.recyclerViewReels);
        progressBar = view.findViewById(R.id.progressBar);

        recyclerReels.setLayoutManager(new GridLayoutManager(getContext(), 3));
        adapter = new ReelsAdapter(getContext(), reelList);
        recyclerReels.setAdapter(adapter);

        pickVideoLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        selectedVideoUri = result.getData().getData();
                        Toast.makeText(getContext(), "Video Selected ✅", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        btnAddReel.setOnClickListener(v -> openAddReelDialog());

        new Handler().postDelayed(this::loadReelsFromFirestore, 600);

        return view;
    }

    private void openAddReelDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_add_reel, null);

        EditText inputName = dialogView.findViewById(R.id.inputReelName);
        EditText inputDescription = dialogView.findViewById(R.id.inputReelDescription);
        EditText inputVideoUrl = dialogView.findViewById(R.id.inputVideoUrl);
        Button btnUpload = dialogView.findViewById(R.id.btnUploadVideo);
        Button btnAdd = dialogView.findViewById(R.id.btnAddReel);
        Button btnGenerateAI = dialogView.findViewById(R.id.btnGenerateAI);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();

        btnGenerateAI.setOnClickListener(aiView -> {
            String title = inputName.getText().toString().trim();
            if (title.isEmpty()) {
                Toast.makeText(getContext(), "Enter a title first", Toast.LENGTH_SHORT).show();
                return;
            }
            String generatedDesc = "This reel showcases " + title + " with amazing content!";
            inputDescription.setText(generatedDesc);
        });

        btnUpload.setOnClickListener(uploadView -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("video/*");
            pickVideoLauncher.launch(intent);
        });

        btnAdd.setOnClickListener(addView -> {
            String title = inputName.getText().toString().trim();
            String description = inputDescription.getText().toString().trim();
            String urlFromInput = inputVideoUrl.getText().toString().trim();

            if (title.isEmpty() || description.isEmpty()) {
                Toast.makeText(getContext(), "Please fill title & description", Toast.LENGTH_SHORT).show();
                return;
            }

            String finalVideoUrl = null;
            if (selectedVideoUri != null) {
                finalVideoUrl = selectedVideoUri.toString();
            } else if (!urlFromInput.isEmpty()) {
                finalVideoUrl = urlFromInput;
            }

            if (finalVideoUrl == null) {
                Toast.makeText(getContext(), "Please select a video or enter URL", Toast.LENGTH_SHORT).show();
                return;
            }

            saveReelToFirestore(title, description, finalVideoUrl);
            dialog.dismiss();
        });
    }

    private void saveReelToFirestore(String title, String description, String videoUrl) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("restaurants")
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String restaurantName = doc.getString("name");
                        String restaurantImage = doc.getString("imageUrl"); // optional

                        if (restaurantName == null || restaurantName.isEmpty()) {
                            Toast.makeText(getContext(), "Restaurant name not found", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        HashMap<String, Object> reelData = new HashMap<>();
                        reelData.put("title", title);
                        reelData.put("description", description);
                        reelData.put("videoUrl", videoUrl);
                        reelData.put("restaurantId", uid);
                        reelData.put("restaurant", restaurantName);
                        reelData.put("likesCount", 0);
                        reelData.put("commentsCount", 0);
                        reelData.put("price", 0.0);
                        reelData.put("comments", new ArrayList<String>());
                        reelData.put("imageUrl", restaurantImage != null ? restaurantImage : "");
                        reelData.put("createdAt", System.currentTimeMillis());

                        db.collection("restaurants")
                                .document(uid)
                                .collection("reels")
                                .add(reelData)
                                .addOnSuccessListener(docRef -> {
                                    Toast.makeText(getContext(), "Reel Saved ✅", Toast.LENGTH_SHORT).show();
                                    loadReelsFromFirestore();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "Error saving reel: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(getContext(), "Restaurant profile not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error fetching restaurant: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void loadReelsFromFirestore() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance()
                .collection("restaurants")
                .document(uid)
                .collection("reels")
                .get()
                .addOnSuccessListener(query -> {
                    reelList.clear();
                    for (QueryDocumentSnapshot doc : query) {
                        ReelModel reel = doc.toObject(ReelModel.class);
                        reelList.add(reel);
                    }
                    adapter.notifyDataSetChanged();

                    if (!reelList.isEmpty()) {
                        preloadSequentially(requireContext(), 0);
                    }

                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @OptIn(markerClass = UnstableApi.class)
    private void preloadSequentially(Context context, int index) {
        if (index >= reelList.size()) return;

        String videoUrl = reelList.get(index).getVideoUrl();

        new Thread(() -> {
            try {
                CacheDataSource.Factory cacheFactory = VideoCache.getCacheDataSourceFactory(context);
                ExoPlayer tempPlayer = new ExoPlayer.Builder(context)
                        .setMediaSourceFactory(new DefaultMediaSourceFactory(cacheFactory))
                        .build();

                MediaItem mediaItem = MediaItem.fromUri(Uri.parse(videoUrl));
                tempPlayer.setMediaItem(mediaItem);
                tempPlayer.prepare();
                tempPlayer.setPlayWhenReady(false);
                Thread.sleep(1000);
                tempPlayer.release();

                preloadSequentially(context, index + 1);

            } catch (Exception ignored) {}
        }).start();
    }
}

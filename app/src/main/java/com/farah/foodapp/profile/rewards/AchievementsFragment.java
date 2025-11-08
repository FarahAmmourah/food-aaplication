package com.farah.foodapp.profile.rewards;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.farah.foodapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AchievementsFragment extends Fragment {

    private LinearLayout achievementsContainer;
    private TextView btnAll, btnUnlocked, btnLocked;
    private List<Achievement> allAchievements = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_achievements, container, false);

        achievementsContainer = view.findViewById(R.id.achievementsContainer);

        btnAll = view.findViewById(R.id.btnFilterAll);
        btnUnlocked = view.findViewById(R.id.btnFilterUnlocked);
        btnLocked = view.findViewById(R.id.btnFilterLocked);

        setupFilterButtons();
        loadAchievements();

        return view;
    }

    private void setupFilterButtons() {
        View.OnClickListener listener = v -> {
            resetButtonStyles();
            ((TextView) v).setBackgroundResource(R.drawable.bg_tab_selected);
            ((TextView) v).setTextColor(Color.WHITE);

            String filter = ((TextView) v).getText().toString();
            applyFilter(filter);
        };

        btnAll.setOnClickListener(listener);
        btnUnlocked.setOnClickListener(listener);
        btnLocked.setOnClickListener(listener);

        btnAll.setBackgroundResource(R.drawable.bg_tab_selected);
        btnAll.setTextColor(Color.WHITE);
    }

    private void resetButtonStyles() {
        btnAll.setBackgroundResource(R.drawable.bg_filter_unselected);
        btnUnlocked.setBackgroundResource(R.drawable.bg_filter_unselected);
        btnLocked.setBackgroundResource(R.drawable.bg_filter_unselected);

        int textColor = getResources().getColor(R.color.foreground);
        btnAll.setTextColor(textColor);
        btnUnlocked.setTextColor(textColor);
        btnLocked.setTextColor(textColor);
    }

    private void loadAchievements() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("orders")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(orderSnapshot -> {
                    int totalOrders = orderSnapshot.size();
                    Set<String> restaurantNames = new HashSet<>();
                    for (DocumentSnapshot doc : orderSnapshot) {
                        String name = doc.getString("restaurantName");
                        if (name != null) restaurantNames.add(name);
                    }

                    db.collection("reward_redemptions")
                            .whereEqualTo("userId", userId)
                            .get()
                            .addOnSuccessListener(rewardSnapshot -> {
                                int totalRewards = rewardSnapshot.size();

                                db.collection("achievements")
                                        .get()
                                        .addOnSuccessListener(snapshot -> {
                                            allAchievements.clear();
                                            for (DocumentSnapshot doc : snapshot) {
                                                String title = doc.getString("title");
                                                String description = doc.getString("description");
                                                String type = doc.getString("conditionType");
                                                Long min = doc.getLong("minCount");

                                                int minCount = (min != null) ? min.intValue() : 0;
                                                boolean unlocked = false;

                                                if ("orders".equals(type)) {
                                                    unlocked = totalOrders >= minCount;
                                                } else if ("restaurants".equals(type)) {
                                                    unlocked = restaurantNames.size() >= minCount;
                                                } else if ("rewards".equals(type)) {
                                                    unlocked = totalRewards >= minCount;
                                                }

                                                allAchievements.add(new Achievement(
                                                        title != null ? title : "Untitled",
                                                        description != null ? description : "",
                                                        type,
                                                        minCount,
                                                        unlocked
                                                ));
                                            }

                                            applyFilter("All");
                                        })
                                        .addOnFailureListener(e ->
                                                Toast.makeText(getContext(), "Failed to load achievements", Toast.LENGTH_SHORT).show()
                                        );
                            });
                });
    }

    private void applyFilter(String filter) {
        if (achievementsContainer == null) return;

        List<Achievement> filtered = new ArrayList<>(allAchievements);

        switch (filter) {
            case "Unlocked":
                filtered.removeIf(a -> !a.isUnlocked());
                break;
            case "Locked":
                filtered.removeIf(Achievement::isUnlocked);
                break;
            default:
                break;
        }

        renderAchievements(filtered);
    }

    private void renderAchievements(List<Achievement> achievements) {
        achievementsContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(getContext());

        for (Achievement ach : achievements) {
            View item = inflater.inflate(R.layout.item_achievement, achievementsContainer, false);

            TextView tvTitle = item.findViewById(R.id.tvAchievementTitle);
            TextView tvSubtitle = item.findViewById(R.id.tvAchievementSubtitle);
            TextView tvStatus = item.findViewById(R.id.tvAchievementStatus);
            ImageView icon = item.findViewById(R.id.imgAchievementIcon);

            tvTitle.setText(ach.getTitle());
            tvSubtitle.setText(ach.getDescription());

            if (ach.isUnlocked()) {
                tvStatus.setText("Unlocked");
                tvStatus.setTextColor(Color.parseColor("#2E7D32"));
                icon.setImageResource(R.drawable.ic_trophy);
                item.setBackgroundResource(R.drawable.bg_card_unlocked);
            } else {
                tvStatus.setText("Locked");
                tvStatus.setTextColor(Color.parseColor("#9E9E9E"));
                icon.setImageResource(R.drawable.ic_lock);
                item.setBackgroundResource(R.drawable.bg_locked_card);
            }

            achievementsContainer.addView(item);
        }
    }
}

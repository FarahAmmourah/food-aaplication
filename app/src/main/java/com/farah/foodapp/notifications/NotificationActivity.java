package com.farah.foodapp.notifications;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.farah.foodapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NotificationActivity extends AppCompatActivity {

    private NotificationsAdapter adapter;
    private List<NotificationModel> notifications = new ArrayList<>();

    private Button btnBack;

    private static final String TAG = "NotificationActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        RecyclerView recyclerNotifications = findViewById(R.id.recyclerNotifications);
        recyclerNotifications.setLayoutManager(new LinearLayoutManager(this));
        btnBack = findViewById(R.id.btn_back);

        adapter = new NotificationsAdapter(notifications);
        recyclerNotifications.setAdapter(adapter);
        btnBack.setOnClickListener(v -> finish());

        loadNotifications();
    }

    private void loadNotifications() {
        String userId = null;
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        if (userId == null) {
            Log.e(TAG, "User not logged in, cannot load notifications");
            return;
        }

        FirebaseFirestore.getInstance()
                .collection("notifications")
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Error loading notifications", e);
                        return;
                    }

                    if (snapshots != null) {
                        notifications.clear();
                        for (QueryDocumentSnapshot doc : snapshots) {
                            NotificationModel notification = doc.toObject(NotificationModel.class);
                            notifications.add(notification);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}

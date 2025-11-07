package com.farah.foodapp.menu;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import com.farah.foodapp.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class RatingsChartActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private BarChart barChart;
    private PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ratings_chart);

        ImageButton backBtn = findViewById(R.id.btnBack);
        backBtn.setOnClickListener(v -> onBackPressed());

        barChart = findViewById(R.id.barChart);
        pieChart = findViewById(R.id.pieChart);
        db = FirebaseFirestore.getInstance();

        String restaurantId = getIntent().getStringExtra("restaurantId");
        if (restaurantId != null) {
            loadRatingsData(restaurantId);
        }
    }

    private void loadRatingsData(String restaurantId) {
        db.collection("restaurants").document(restaurantId)
                .collection("menu")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<BarEntry> barEntries = new ArrayList<>();
                    ArrayList<PieEntry> pieEntries = new ArrayList<>();
                    ArrayList<String> labels = new ArrayList<>();

                    int index = 0;
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Double ratingObj = doc.getDouble("rating");
                        String name = doc.getString("name");

                        if (ratingObj != null && name != null) {
                            float rating = ratingObj.floatValue();
                            barEntries.add(new BarEntry(index, rating));
                            pieEntries.add(new PieEntry(rating, formatLabel(name)));
                            labels.add(formatLabel(name));
                            index++;
                        }
                    }

                    int[] reds = {
                            Color.parseColor("#8B0000"),
                            Color.parseColor("#A40000"),
                            Color.parseColor("#C21807"),
                            Color.parseColor("#E53935")
                    };

                    // BAR CHART
                    BarDataSet dataSet = new BarDataSet(barEntries, "Meal Ratings");
                    List<Integer> colorList = new ArrayList<>();
                    for (int i = 0; i < labels.size(); i++) {
                        colorList.add(reds[i % reds.length]);
                    }
                    dataSet.setColors(colorList);
                    dataSet.setValueTextColor(Color.BLACK);
                    dataSet.setValueTextSize(13f);

                    BarData barData = new BarData(dataSet);
                    barData.setBarWidth(0.4f);
                    barChart.setData(barData);

                    XAxis xAxis = barChart.getXAxis();
                    xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxis.setGranularity(1f);
                    xAxis.setDrawGridLines(false);
                    xAxis.setTextSize(11f);
                    xAxis.setTextColor(Color.BLACK);
                    xAxis.setLabelRotationAngle(0f);
                    xAxis.setLabelCount(labels.size());
                    xAxis.setYOffset(40f);
                    xAxis.setAvoidFirstLastClipping(true);
                    barChart.setExtraBottomOffset(110f);

                    Description barDesc = new Description();
                    barDesc.setText("Meal Ratings Overview");
                    barChart.setDescription(barDesc);
                    barChart.getAxisLeft().setAxisMinimum(0f);
                    barChart.getAxisLeft().setAxisMaximum(5f);
                    barChart.getAxisRight().setEnabled(false);
                    barChart.getLegend().setEnabled(false);
                    barChart.animateY(1200);
                    barChart.invalidate();

                    // PIE CHART
                    PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
                    pieDataSet.setColors(colorList);
                    pieDataSet.setValueTextSize(12f);
                    pieDataSet.setValueTextColor(Color.WHITE);
                    PieData pieData = new PieData(pieDataSet);

                    pieChart.setData(pieData);
                    pieChart.setUsePercentValues(false);
                    pieChart.getDescription().setEnabled(false);
                    pieChart.setCenterText("Ratings %");
                    pieChart.setCenterTextSize(14f);
                    pieChart.setHoleRadius(35f);
                    pieChart.animateY(1500);
                    pieChart.invalidate();
                });
    }

    private String formatLabel(String label) {
        String[] words = label.split(" ");
        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            formatted.append(words[i]);
            if ((i + 1) % 2 == 0 && i != words.length - 1) {
                formatted.append("\n");
            } else {
                formatted.append(" ");
            }
        }
        return formatted.toString().trim();
    }
}

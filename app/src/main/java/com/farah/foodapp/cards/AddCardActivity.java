package com.farah.foodapp.cards;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.farah.foodapp.R;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AddCardActivity extends AppCompatActivity {

    private PaymentSheet paymentSheet;
    private String paymentIntentClientSecret;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);
        PaymentConfiguration.init(
                getApplicationContext(),
                "pk_test_51SEjk8AtRQUYdXhWjSg5BMTdsvRdH4jJcllY1aygfB36eQyTHwDbRUKMBQbroLBmUGK9gpHBFxCQwhqrXxONOTlX003ULuDrrI"
        );

        paymentSheet = new PaymentSheet(this, this::onPaymentSheetResult);
        createPaymentIntentOnBackend();
    }

    private void createPaymentIntentOnBackend() {
        new Thread(() -> {
            try {
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("amount", 1000);

//                URL url = new URL("http://10.0.2.2:8000/api/payments/create-payment-intent/");
//                URL url = new URL("http://192.168.100.23:8000/api/payments/create-payment-intent/");

//                URL url = new URL("https://carduaceous-nonphotographic-lashaun.ngrok-free.dev/api/payments/create-payment-intent/");
                URL url = new URL("https://foodapp-backend-vcay.onrender.com/api/payments/create-payment-intent/");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                os.write(jsonBody.toString().getBytes("UTF-8"));
                os.close();

                int responseCode = conn.getResponseCode();
                InputStream is = (responseCode == HttpURLConnection.HTTP_OK)
                        ? conn.getInputStream()
                        : conn.getErrorStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject jsonResponse = new JSONObject(response.toString());

                if (responseCode == HttpURLConnection.HTTP_OK && jsonResponse.optBoolean("success", false)) {
                    paymentIntentClientSecret = jsonResponse.getString("client_secret");
                    runOnUiThread(this::presentPaymentSheet);
                } else {
                    String errorMessage = jsonResponse.optString("message", "Unknown error");
                    runOnUiThread(() -> Toast.makeText(this, "Backend error: " + errorMessage, Toast.LENGTH_LONG).show());
                }

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Exception: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private void presentPaymentSheet() {
        PaymentSheet.Configuration configuration = new PaymentSheet.Configuration("Your Merchant Name");
        paymentSheet.presentWithPaymentIntent(paymentIntentClientSecret, configuration);
    }

    private void onPaymentSheetResult(PaymentSheetResult paymentSheetResult) {
        if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            Toast.makeText(this, "Payment successful!", Toast.LENGTH_SHORT).show();

            Intent data = new Intent();
            data.putExtra("payment_success", true);
            setResult(RESULT_OK, data);

            finish();
        } else if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            Toast.makeText(this, "Payment canceled.", Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED);
            finish();
        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            String errorMsg = ((PaymentSheetResult.Failed) paymentSheetResult).getError().getMessage();
            Toast.makeText(this, "Payment failed: " + errorMsg, Toast.LENGTH_LONG).show();
            setResult(RESULT_CANCELED);
            finish();
        }
    }
}

package com.farah.foodapp.cards;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

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

public class AddCardDialog extends DialogFragment {

    private PaymentSheet paymentSheet;
    private String paymentIntentClientSecret;

    private AddCardListener listener;

    public interface AddCardListener {
        void onPaymentSuccess(String last4, String expiry, String holderName);
    }

    public AddCardDialog(AddCardListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog);
        builder.setMessage("Processing payment...")
                .setCancelable(false);

        // Initialize Stripe
        PaymentConfiguration.init(requireContext(),
                "pk_test_51SEjk8AtRQUYdXhWjSg5BMTdsvRdH4jJcllY1aygfB36eQyTHwDbRUKMBQbroLBmUGK9gpHBFxCQwhqrXxONOTlX003ULuDrrI"
        );

        paymentSheet = new PaymentSheet(this, this::onPaymentSheetResult);

        createPaymentIntentOnBackend();

        return builder.create();
    }

    private void createPaymentIntentOnBackend() {
        new Thread(() -> {
            try {
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("amount", 1000); // example amount in cents

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
                        ? conn.getInputStream() : conn.getErrorStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) response.append(line);
                reader.close();

                JSONObject jsonResponse = new JSONObject(response.toString());

                if (responseCode == HttpURLConnection.HTTP_OK && jsonResponse.optBoolean("success", false)) {
                    paymentIntentClientSecret = jsonResponse.getString("client_secret");
                    requireActivity().runOnUiThread(this::presentPaymentSheet);
                } else {
                    String errorMessage = jsonResponse.optString("message", "Unknown error");
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), "Backend error: " + errorMessage, Toast.LENGTH_LONG).show()
                    );
                    dismiss();
                }
            } catch (Exception e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Exception: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    dismiss();
                });
            }
        }).start();
    }

    private void presentPaymentSheet() {
        PaymentSheet.Configuration config = new PaymentSheet.Configuration("Your Merchant Name");
        paymentSheet.presentWithPaymentIntent(paymentIntentClientSecret, config);
    }

    private void onPaymentSheetResult(PaymentSheetResult result) {
        if (result instanceof PaymentSheetResult.Completed) {
            // For demo purposes, we use hardcoded card info; in a real app, get this from Stripe PaymentMethod
            String last4 = "4242";        // last 4 digits
            String expiry = "12/25";      // expiry date
            String holderName = "Aya";    // cardholder name

            if (listener != null) {
                listener.onPaymentSuccess(last4, expiry, holderName);
            }

            Toast.makeText(requireContext(), "Payment successful!", Toast.LENGTH_SHORT).show();

        } else if (result instanceof PaymentSheetResult.Canceled) {
            Toast.makeText(requireContext(), "Payment canceled.", Toast.LENGTH_SHORT).show();
        } else if (result instanceof PaymentSheetResult.Failed) {
            String error = ((PaymentSheetResult.Failed) result).getError().getMessage();
            Toast.makeText(requireContext(), "Payment failed: " + error, Toast.LENGTH_LONG).show();
        }
        dismiss();
    }
}

package com.farah.foodapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ChangePasswordActivity extends AppCompatActivity {

    EditText etOldPassword, etNewPassword, etConfirmPassword;
    Button btnChangePassword, btnChangeLanguage, btnLogout; // أضفنا زر اللوغ اوت

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // ربط عناصر الواجهة
        etOldPassword = findViewById(R.id.et_old_password);
        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnChangePassword = findViewById(R.id.btn_change_password);
        btnChangeLanguage = findViewById(R.id.btn_change_language);
        btnLogout = findViewById(R.id.btn_logout); // زر اللوغ اوت

        // زر تغيير كلمة المرور
        btnChangePassword.setOnClickListener(v -> {
            String oldPass = etOldPassword.getText().toString().trim();
            String newPass = etNewPassword.getText().toString().trim();
            String confirmPass = etConfirmPassword.getText().toString().trim();

            if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else if (!newPass.equals(confirmPass)) {
                Toast.makeText(this, "New passwords do not match", Toast.LENGTH_SHORT).show();
            } else {
                // هون بتحطي منطق الحفظ (SharedPreferences أو API)
                Toast.makeText(this, "Password changed successfully!", Toast.LENGTH_SHORT).show();
                etOldPassword.setText("");
                etNewPassword.setText("");
                etConfirmPassword.setText("");
            }
        });

        // زر تغيير اللغة
        btnChangeLanguage.setOnClickListener(v -> {
            final String[] languages = {"English", "عربي"};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Choose Language")
                    .setItems(languages, (dialog, which) -> {
                        if (which == 0) {
                            Toast.makeText(this, "Language set to English", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "تم تغيير اللغة للعربية", Toast.LENGTH_SHORT).show();
                        }
                    });
            builder.create().show();
        });

        // زر اللوغ اوت
        btnLogout.setOnClickListener(v -> {
            // ترجيع المستخدم لصفحة تسجيل الدخول
            Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // يمنع الرجوع للخلف
            startActivity(intent);
            finish();
        });
    }
}

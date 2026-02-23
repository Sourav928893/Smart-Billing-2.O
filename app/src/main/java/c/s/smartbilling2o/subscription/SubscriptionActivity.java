package c.s.smartbilling2o.subscription;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import c.s.smartbilling2o.MainActivity;
import c.s.smartbilling2o.R;
import c.s.smartbilling2o.databinding.ActivitySubscriptionBinding;

public class SubscriptionActivity extends AppCompatActivity {

    private ActivitySubscriptionBinding binding;
    private SubscriptionManager subscriptionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubscriptionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        subscriptionManager = new SubscriptionManager(this);

        binding.btnMonthly.setOnClickListener(v -> openPaymentUrl(SubscriptionManager.PLAN_MONTHLY, "https://rzp.io/rzp/TZeZ2zqN"));
        binding.btnYearly.setOnClickListener(v -> openPaymentUrl(SubscriptionManager.PLAN_YEARLY, "https://rzp.io/rzp/ZCLHrO03"));

        // Block back button if trial is expired
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!subscriptionManager.isTrialExpired()) {
                    finish();
                } else {
                    Toast.makeText(SubscriptionActivity.this, "Please subscribe to continue", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openPaymentUrl(String plan, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
        showActivationDialog(plan);
    }

    private void showActivationDialog(String plan) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Activate Premium");
        builder.setMessage("Enter the activation code sent to your email after payment.");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_activation, null);
        EditText etCode = view.findViewById(R.id.etActivationCode);
        builder.setView(view);

        builder.setPositiveButton("Activate", (dialog, which) -> {
            String code = etCode.getText().toString().trim();
            if (subscriptionManager.validateActivationCode(code, plan)) {
                long duration = plan.equals(SubscriptionManager.PLAN_MONTHLY) ? 
                        SubscriptionManager.DURATION_MONTHLY : SubscriptionManager.DURATION_YEARLY;
                
                subscriptionManager.setPremium(true, plan, duration);
                Toast.makeText(this, "Premium Activated Successfully!", Toast.LENGTH_LONG).show();
                
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Invalid Activation Code", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}

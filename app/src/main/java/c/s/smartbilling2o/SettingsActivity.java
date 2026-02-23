package c.s.smartbilling2o;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import c.s.smartbilling2o.databinding.ActivitySettingsBinding;
import c.s.smartbilling2o.utils.ThemeUtils;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;
    private DbClass dbHelper;
    private String selectedColor = "#1976D2"; 
    private String logoPath = "";
    private static final String PREFS_NAME = "SmartBillingPrefs";
    private static final String KEY_MANUAL_DATE = "manual_date_enabled";

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        saveImageToInternalStorage(imageUri);
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DbClass(this);

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        loadSettings();

        binding.btnChangeLogo.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImageLauncher.launch(intent);
        });

        binding.btnRemoveLogo.setOnClickListener(v -> {
            removeLogo();
        });

        binding.rgThemes.setOnCheckedChangeListener((group, checkedId) -> {
            int themeId = ThemeUtils.THEME_BLUE;
            if (checkedId == R.id.rbBlue) {
                themeId = ThemeUtils.THEME_BLUE;
                selectedColor = "#1976D2";
            } else if (checkedId == R.id.rbGreen) {
                themeId = ThemeUtils.THEME_GREEN;
                selectedColor = "#388E3C";
            } else if (checkedId == R.id.rbPurple) {
                themeId = ThemeUtils.THEME_PURPLE;
                selectedColor = "#7B1FA2";
            } else if (checkedId == R.id.rbOrange) {
                themeId = ThemeUtils.THEME_ORANGE;
                selectedColor = "#F57C00";
            } else if (checkedId == R.id.rbTeal) {
                themeId = ThemeUtils.THEME_TEAL;
                selectedColor = "#00796B";
            }
            ThemeUtils.saveTheme(this, themeId);
        });

        binding.btnSave.setOnClickListener(v -> saveSettings());
    }

    private void removeLogo() {
        if (logoPath != null && !logoPath.isEmpty()) {
            try {
                File file = new File(logoPath);
                if (file.exists()) {
                    file.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        logoPath = "";
        binding.ivShopLogo.setImageResource(R.mipmap.logobill);
        Toast.makeText(this, "Logo removed", Toast.LENGTH_SHORT).show();
    }

    private void saveImageToInternalStorage(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            
            if (bitmap == null) return;

            File directory = new File(getFilesDir(), "logos");
            if (!directory.exists()) directory.mkdirs();
            
            File file = new File(directory, "shop_logo.png");
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            
            logoPath = file.getAbsolutePath();
            binding.ivShopLogo.setImageBitmap(bitmap);
            
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadSettings() {
        Cursor cursor = dbHelper.getShopInfo();
        if (cursor != null && cursor.moveToFirst()) {
            binding.etShopName.setText(cursor.getString(cursor.getColumnIndexOrThrow("shopName")));
            binding.etOwnerName.setText(cursor.getString(cursor.getColumnIndexOrThrow("ownerName")));
            binding.etShopAddress.setText(cursor.getString(cursor.getColumnIndexOrThrow("shopAddress")));
            binding.etShopContact.setText(cursor.getString(cursor.getColumnIndexOrThrow("shopContact")));
            binding.etGstNumber.setText(cursor.getString(cursor.getColumnIndexOrThrow("gstNumber")));
            binding.etCurrency.setText(cursor.getString(cursor.getColumnIndexOrThrow("currency")));
            selectedColor = cursor.getString(cursor.getColumnIndexOrThrow("themeColor"));
            logoPath = cursor.getString(cursor.getColumnIndexOrThrow("logoPath"));
            
            if (logoPath != null && !logoPath.isEmpty()) {
                File imgFile = new File(logoPath);
                if (imgFile.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    if (myBitmap != null) {
                        binding.ivShopLogo.setImageBitmap(myBitmap);
                    } else {
                        binding.ivShopLogo.setImageResource(R.mipmap.logobill);
                        logoPath = ""; // Path exists but file is corrupted or not an image
                    }
                } else {
                    binding.ivShopLogo.setImageResource(R.mipmap.logobill);
                    logoPath = "";
                }
            } else {
                binding.ivShopLogo.setImageResource(R.mipmap.logobill);
            }
            cursor.close();
        }

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        binding.swManualDate.setChecked(prefs.getBoolean(KEY_MANUAL_DATE, false));

        int currentTheme = ThemeUtils.getSelectedTheme(this);
        switch (currentTheme) {
            case ThemeUtils.THEME_BLUE: binding.rbBlue.setChecked(true); break;
            case ThemeUtils.THEME_GREEN: binding.rbGreen.setChecked(true); break;
            case ThemeUtils.THEME_PURPLE: binding.rbPurple.setChecked(true); break;
            case ThemeUtils.THEME_ORANGE: binding.rbOrange.setChecked(true); break;
            case ThemeUtils.THEME_TEAL: binding.rbTeal.setChecked(true); break;
        }
    }

    private void saveSettings() {
        String name = binding.etShopName.getText().toString().trim();
        String owner = binding.etOwnerName.getText().toString().trim();
        String address = binding.etShopAddress.getText().toString().trim();
        String contact = binding.etShopContact.getText().toString().trim();
        String gst = binding.etGstNumber.getText().toString().trim();
        String currency = binding.etCurrency.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Shop Name is required", Toast.LENGTH_SHORT).show();
            return;
        }

        dbHelper.updateShopInfo(name, owner, address, contact, gst, logoPath, currency, selectedColor);

        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(KEY_MANUAL_DATE, binding.swManualDate.isChecked());
        editor.apply();

        Toast.makeText(this, "Settings Saved Successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    public static boolean isManualDateEnabled(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getBoolean(KEY_MANUAL_DATE, false);
    }
}

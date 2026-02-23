package c.s.smartbilling2o.subscription;

import android.content.Context;
import android.content.SharedPreferences;

public class SubscriptionManager {
    private static final String PREF_NAME = "subscription_prefs";
    private static final String KEY_INSTALL_TIME = "install_time";
    private static final String KEY_IS_PREMIUM = "is_premium";
    private static final String KEY_PLAN_TYPE = "plan_type";
    private static final String KEY_PURCHASE_TIME = "purchase_time";
    private static final String KEY_EXPIRY_TIME = "expiry_time";

    public static final String PLAN_MONTHLY = "monthly";
    public static final String PLAN_YEARLY = "yearly";

    public static final long TRIAL_DURATION = 2L * 24 * 60 * 60 * 1000; // 2 Days
    public static final long DURATION_MONTHLY = 30L * 24 * 60 * 60 * 1000; // 30 Days
    public static final long DURATION_YEARLY = 365L * 24 * 60 * 60 * 1000; // 365 Days

    private SharedPreferences prefs;

    public SubscriptionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        if (prefs.getLong(KEY_INSTALL_TIME, 0) == 0) {
            prefs.edit().putLong(KEY_INSTALL_TIME, System.currentTimeMillis()).apply();
        }
    }

    public boolean isTrialExpired() {
        if (isPremium()) return false;
        long installTime = prefs.getLong(KEY_INSTALL_TIME, 0);
        return (System.currentTimeMillis() - installTime) > TRIAL_DURATION;
    }

    public long getRemainingTrialTime() {
        long installTime = prefs.getLong(KEY_INSTALL_TIME, 0);
        long timeLeft = TRIAL_DURATION - (System.currentTimeMillis() - installTime);
        return Math.max(0, timeLeft);
    }

    public boolean isPremium() {
        boolean isPremium = prefs.getBoolean(KEY_IS_PREMIUM, false);
        if (!isPremium) return false;

        long expiryTime = prefs.getLong(KEY_EXPIRY_TIME, 0);
        if (System.currentTimeMillis() > expiryTime) {
            // Plan expired
            setPremium(false, null, 0);
            return false;
        }
        return true;
    }

    public long getRemainingPremiumTime() {
        long expiryTime = prefs.getLong(KEY_EXPIRY_TIME, 0);
        return Math.max(0, expiryTime - System.currentTimeMillis());
    }

    public void setPremium(boolean isPremium, String planType, long duration) {
        long expiryTime = System.currentTimeMillis() + duration;
        prefs.edit()
                .putBoolean(KEY_IS_PREMIUM, isPremium)
                .putString(KEY_PLAN_TYPE, planType)
                .putLong(KEY_PURCHASE_TIME, System.currentTimeMillis())
                .putLong(KEY_EXPIRY_TIME, expiryTime)
                .apply();
    }

    public String getFormattedRemainingTime(long millis) {
        long hours = millis / (1000 * 60 * 60);
        long mins = (millis / (1000 * 60)) % 60;
        
        if (hours >= 24) {
            long days = hours / 24;
            return days + " Days remaining";
        }
        return String.format("%02d Hours : %02d Mins remaining", hours, mins);
    }

    public boolean validateActivationCode(String code, String plan) {
        // Professional dynamic logic: Code format SB-PLAN-YYYYMMDD
        // For now using fixed codes for demonstration
        if (PLAN_MONTHLY.equals(plan) && code.equals("SB-MONTH-299")) return true;
        if (PLAN_YEARLY.equals(plan) && code.equals("SB-YEAR-999")) return true;
        return false;
    }
}

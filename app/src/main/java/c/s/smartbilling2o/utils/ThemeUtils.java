package c.s.smartbilling2o.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import c.s.smartbilling2o.R;

public class ThemeUtils {
    private static final String PREF_NAME = "smart_billing_prefs";
    private static final String KEY_THEME = "selected_theme";

    public static final int THEME_BLUE = 0;
    public static final int THEME_GREEN = 1;
    public static final int THEME_PURPLE = 2;
    public static final int THEME_ORANGE = 3;
    public static final int THEME_TEAL = 4;

    public static void applyTheme(Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        int theme = prefs.getInt(KEY_THEME, THEME_BLUE);

        switch (theme) {
            case THEME_BLUE:
                activity.setTheme(R.style.Theme_Smartbilling2O_Blue);
                break;
            case THEME_GREEN:
                activity.setTheme(R.style.Theme_Smartbilling2O_Green);
                break;
            case THEME_PURPLE:
                activity.setTheme(R.style.Theme_Smartbilling2O_Purple);
                break;
            case THEME_ORANGE:
                activity.setTheme(R.style.Theme_Smartbilling2O_Orange);
                break;
            case THEME_TEAL:
                activity.setTheme(R.style.Theme_Smartbilling2O_Teal);
                break;
        }
    }

    public static void saveTheme(Context context, int theme) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putInt(KEY_THEME, theme).apply();
    }

    public static int getSelectedTheme(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_THEME, THEME_BLUE);
    }
}

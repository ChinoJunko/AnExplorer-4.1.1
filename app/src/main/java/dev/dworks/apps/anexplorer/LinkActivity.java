package dev.dworks.apps.anexplorer;

import static dev.dworks.apps.anexplorer.DocumentsActivity.getStatusBarHeight;
import static dev.dworks.apps.anexplorer.DocumentsApplication.isTelevision;
import static dev.dworks.apps.anexplorer.misc.Utils.getSuffix;
import static dev.dworks.apps.anexplorer.misc.Utils.openFeedback;
import static dev.dworks.apps.anexplorer.misc.Utils.openPlaystore;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ShareCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import dev.dworks.apps.anexplorer.misc.AnalyticsManager;
import dev.dworks.apps.anexplorer.misc.ColorUtils;
import dev.dworks.apps.anexplorer.misc.SystemBarTintManager;
import dev.dworks.apps.anexplorer.misc.Utils;
import dev.dworks.apps.anexplorer.setting.SettingsActivity;

public class LinkActivity extends LinkVariantFlavour implements View.OnClickListener{

    public static final String TAG = "Link";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Utils.hasKitKat() && !Utils.hasLollipop()){
            setTheme(R.style.DocumentsTheme_Translucent);
        }
        setContentView(R.layout.activity_link);

        int color = SettingsActivity.getPrimaryColor();
        View view = findViewById(R.id.toolbar);
        if(view instanceof Toolbar){
            Toolbar mToolbar = (Toolbar) view;
            mToolbar.setTitleTextAppearance(this, R.style.TextAppearance_AppCompat_Widget_ActionBar_Title);
            if(Utils.hasKitKat() && !Utils.hasLollipop()) {
                mToolbar.setPadding(0, getStatusBarHeight(this), 0, 0);
            }
            mToolbar.setBackgroundColor(color);
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(null);
            setUpDefaultStatusBar();
        } else {
            view.setBackgroundColor(color);
        }
        initAd();
        initControls();
    }

    @Override
    public String getTag() {
        return TAG;
    }

    private void initControls() {

        int accentColor = ColorUtils.getTextColorForBackground(SettingsActivity.getPrimaryColor());
        TextView logo = (TextView)findViewById(R.id.logo);
        logo.setTextColor(accentColor);
        String header = logo.getText() + getSuffix() + " v" + BuildConfig.VERSION_NAME + (BuildConfig.DEBUG ? " Debug" : "");
        logo.setText(header);

        TextView action_rate = (TextView)findViewById(R.id.action_rate);
        TextView action_support = (TextView)findViewById(R.id.action_support);
        TextView action_share = (TextView)findViewById(R.id.action_share);
        TextView action_feedback = (TextView)findViewById(R.id.action_feedback);
        TextView action_sponsor = (TextView)findViewById(R.id.action_sponsor);
        TextView action_web = (TextView)findViewById(R.id.action_web);

        action_rate.setOnClickListener(this);
        action_support.setOnClickListener(this);
        action_share.setOnClickListener(this);
        action_feedback.setOnClickListener(this);
        action_sponsor.setOnClickListener(this);
        action_web.setOnClickListener(this);

        if(Utils.isOtherBuild()){
            action_rate.setVisibility(View.GONE);
            action_support.setVisibility(View.GONE);
        } else if(isTelevision()){
            action_share.setVisibility(View.GONE);
            action_feedback.setVisibility(View.GONE);
        }

        if(!DocumentsApplication.isPurchased()){
            action_sponsor.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void startActivity(Intent intent) {
        if(Utils.isIntentAvailable(this, intent)) {
            super.startActivity(intent);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.action_feedback:
                openFeedback(this);
                break;
            case R.id.action_rate:
                openPlaystore(this);
                AnalyticsManager.logEvent("app_rate");
                break;
            case R.id.action_sponsor:
                showAd();
                AnalyticsManager.logEvent("app_sponsor");
                break;
            case R.id.action_support:
                if(Utils.isProVersion()){
                    Intent intentMarketAll = new Intent("android.intent.action.VIEW");
                    intentMarketAll.setData(Utils.getAppProStoreUri());
                    startActivity(intentMarketAll);
                } else {
                    DocumentsApplication.openPurchaseActivity(this);
                }
                AnalyticsManager.logEvent("app_love");
                break;
            case R.id.action_share:

                String shareText = "I found this file mananger very useful. Give it a try. "
                        + Utils.getAppShareUri().toString();
                ShareCompat.IntentBuilder
                        .from(this)
                        .setText(shareText)
                        .setType("text/plain")
                        .setChooserTitle("Share AnExplorer")
                        .startChooser();
                AnalyticsManager.logEvent("app_share");
                break;
            case R.id.action_web:
                showWeb();
                AnalyticsManager.logEvent("app_web");
                break;
        }
    }

    public void setUpDefaultStatusBar() {
        int color = Utils.getStatusBarColor(SettingsActivity.getPrimaryColor());
        if(Utils.hasLollipop()){
            getWindow().setStatusBarColor(color);
        }
        else if(Utils.hasKitKat()){
            SystemBarTintManager systemBarTintManager = new SystemBarTintManager(this);
            systemBarTintManager.setTintColor(Utils.getStatusBarColor(color));
            systemBarTintManager.setStatusBarTintEnabled(true);
        }
    }
}
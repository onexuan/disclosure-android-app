package de.philipphager.disclosure.feature.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v7.widget.Toolbar;
import butterknife.BindView;
import de.philipphager.disclosure.ApplicationComponent;
import de.philipphager.disclosure.R;
import de.philipphager.disclosure.feature.settings.nested.LicenseFragment;
import de.philipphager.disclosure.util.ui.BaseActivity;

public class NestedSettingsActivity extends BaseActivity {
  private static final String EXTRA_SETTINGS_SCREEN = "EXTRA_SETTINGS_SCREEN";
  public static final String LICENSE = "LICENSE";
  @StringDef(value = {LICENSE})
  public @interface SettingsScreen {
  }

  @BindView(R.id.toolbar) Toolbar toolbar;

  public static Intent launch(Context context, @SettingsScreen String screen) {
    Intent intent = new Intent(context, NestedSettingsActivity.class);
    intent.putExtra(EXTRA_SETTINGS_SCREEN, screen);
    return intent;
  }

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_nested_settings);

    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    if (savedInstanceState == null) {
      String screenId = getIntent().getStringExtra(EXTRA_SETTINGS_SCREEN);
      switch (screenId) {
        case LICENSE:
          getSupportActionBar().setTitle(R.string.fragment_license_title);
          getSupportFragmentManager()
              .beginTransaction()
              .add(R.id.content_frame, LicenseFragment.newInstance(), LICENSE)
              .commit();
          break;
        default:
          throw new RuntimeException(String.format("No settings screen for %s", screenId));
      }
    }
  }

  @Override protected void injectActivity(ApplicationComponent appComponent) {
    appComponent.inject(this);
  }
}
package de.philipphager.disclosure.feature.navigation;

import android.app.Activity;
import de.philipphager.disclosure.feature.list.AppListActivity;
import javax.inject.Inject;

public class Navigator {
  private Activity activity;

  @SuppressWarnings("PMD.UnnecessaryConstructor") @Inject public Navigator() {
    // Needed for dagger injection.
  }

  public void setActivity(Activity activity) {
    this.activity = activity;
  }

  public void toList() {
    activity.startActivity(AppListActivity.launch(activity));
  }
}
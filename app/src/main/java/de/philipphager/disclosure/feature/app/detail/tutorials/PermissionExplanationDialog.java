package de.philipphager.disclosure.feature.app.detail.tutorials;

import android.app.Dialog;
import android.content.pm.PermissionInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import de.philipphager.disclosure.ApplicationComponent;
import de.philipphager.disclosure.R;
import de.philipphager.disclosure.database.permission.model.Permission;
import de.philipphager.disclosure.util.ui.BaseDialogFragment;
import de.philipphager.disclosure.util.ui.components.ProtectionLevelView;
import javax.inject.Inject;

public class PermissionExplanationDialog extends BaseDialogFragment
    implements PermissionExplanationDialogView {
  private static final String EXTRA_APP_ID = "EXTRA_APP_ID";
  private static final String EXTRA_PERMISSION = "EXTRA_PERMISSION";
  @Inject protected PermissionExplanationDialogPresenter presenter;

  public static PermissionExplanationDialog newInstance(String packageName, Permission permission) {
    PermissionExplanationDialog fragment = new PermissionExplanationDialog();
    Bundle args = new Bundle();
    args.putString(EXTRA_APP_ID, packageName);
    args.putParcelable(EXTRA_PERMISSION, permission);
    fragment.setArguments(args);
    return fragment;
  }

  @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    View view = LayoutInflater.from(getContext())
        .inflate(R.layout.dialog_permission_explanation, null);

    ProtectionLevelView protectionLevelView =
        (ProtectionLevelView) view.findViewById(R.id.protectionLevel);

    TextView description = (TextView) view.findViewById(R.id.txtDescription);

    Permission permission = getPermission();
    ProtectionLevelView.ProtectionLevel protectionLevel =
        getProtectionLevel(permission.protectionLevel());

    presenter.onCreate(this, getPackageName(), getPermission());

    protectionLevelView.setProtectionLevel(protectionLevel);
    description.setText(permission.description());

    return getDialog(view, permission, presenter.permissionCanBeRevoked());
  }

  @Override protected void injectFragment(ApplicationComponent appComponent) {
    appComponent.inject(this);
  }

  private Permission getPermission() {
    return ((Permission) getArguments().getParcelable(EXTRA_PERMISSION));
  }

  private String getPackageName() {
    return getArguments().getString(EXTRA_APP_ID);
  }

  private ProtectionLevelView.ProtectionLevel getProtectionLevel(int level) {
    switch (level) {
      case PermissionInfo.PROTECTION_NORMAL:
        return ProtectionLevelView.ProtectionLevel.NORMAL;
      case PermissionInfo.PROTECTION_DANGEROUS:
        return ProtectionLevelView.ProtectionLevel.DANGEROUS;
      case PermissionInfo.PROTECTION_SIGNATURE:
        return ProtectionLevelView.ProtectionLevel.SIGNATURE;
      default:
        throw new IllegalArgumentException("No protection level for " + level);
    }
  }

  private AlertDialog getDialog(View view, Permission permission, boolean canBeRevoked) {
    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext())
        .setTitle(permission.title())
        .setNegativeButton(R.string.action_back, null)
        .setView(view);

    if (canBeRevoked) {
      TextView hint = (TextView) view.findViewById(R.id.txtHint);
      hint.setVisibility(View.VISIBLE);

      alertDialog.setPositiveButton(R.string.action_revoke_permission, (dialog, which) ->
          presenter.onRevokePermissionClicked());
    }

    return alertDialog.create();
  }
}

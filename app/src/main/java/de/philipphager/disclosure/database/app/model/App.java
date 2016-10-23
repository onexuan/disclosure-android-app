package de.philipphager.disclosure.database.app.model;

import android.support.annotation.Nullable;
import com.google.auto.value.AutoValue;
import com.squareup.sqldelight.RowMapper;

@AutoValue public abstract class App implements AppModel {
  public static final Factory<App> FACTORY = new Factory<>(
      (id, label, packageName, process, sourceDir, flags) -> builder().id(id)
          .label(label)
          .packageName(packageName)
          .process(process)
          .sourceDir(sourceDir)
          .flags(flags)
          .build());

  public static final Mapper<App> MAPPER = FACTORY.selectAllMapper();

  public static Builder builder() {
    return new AutoValue_App.Builder();
  }

  @SuppressWarnings("PMD.ShortMethodName") @Nullable public abstract Long id();

  @Nullable public abstract String label();

  public abstract String packageName();

  public abstract String process();

  public abstract String sourceDir();

  public abstract Integer flags();

  public boolean hasLabel() {
    return label() != null;
  }

  @AutoValue.Builder public interface Builder {
    @SuppressWarnings("PMD.ShortMethodName") Builder id(Long id);

    Builder label(@Nullable String label);

    Builder packageName(String name);

    Builder process(String name);

    Builder sourceDir(String path);

    Builder flags(Integer flags);

    App build();
  }

  @AutoValue public static abstract class Info implements SelectAllInfosModel {
    public static final RowMapper<Info> MAPPER =
        FACTORY.selectAllInfosMapper(AutoValue_App_Info::create);

    public static Info create(String packageName, int versionCode) {
      return new AutoValue_App_Info(packageName, versionCode);
    }
  }
}
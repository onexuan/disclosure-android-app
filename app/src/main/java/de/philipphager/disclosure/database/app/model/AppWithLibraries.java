package de.philipphager.disclosure.database.app.model;

import android.support.annotation.Nullable;
import com.google.auto.value.AutoValue;
import com.squareup.sqldelight.RowMapper;
import org.threeten.bp.LocalDateTime;

import static de.philipphager.disclosure.database.app.model.App.FACTORY;

@AutoValue public abstract class AppWithLibraries implements App.SelectAllWithLibraryCountModel {
  public static final RowMapper<AppWithLibraries> MAPPER = FACTORY.selectAllWithLibraryCountMapper(
      (id, label, packageName, process, sourceDir, flags, analyzedAt, isTrusted, libraryCount) -> builder().id(
          id)
          .label(label)
          .packageName(packageName)
          .process(process)
          .sourceDir(sourceDir)
          .flags(flags)
          .isTrusted(isTrusted)
          .analyzedAt(analyzedAt)
          .libraryCount(libraryCount)
          .build());

  public static Builder builder() {
    return new AutoValue_AppWithLibraries.Builder();
  }

  @SuppressWarnings("PMD.ShortMethodName") @Nullable public abstract Long id();

  public abstract String label();

  public abstract String packageName();

  public abstract String process();

  public abstract String sourceDir();

  public abstract Integer flags();

  public abstract Boolean isTrusted();

  @Nullable public abstract LocalDateTime analyzedAt();

  public abstract long libraryCount();

  @SuppressWarnings("PMD.UnnecessaryWrapperObjectCreation")
  public int libraryCountInt() {
    return Long.valueOf(libraryCount()).intValue();
  }

  @AutoValue.Builder public interface Builder {
    @SuppressWarnings("PMD.ShortMethodName") Builder id(Long id);

    Builder label(String label);

    Builder packageName(String name);

    Builder process(String name);

    Builder sourceDir(String path);

    Builder flags(Integer flags);

    Builder isTrusted(Boolean isTrusted);

    Builder analyzedAt(@Nullable LocalDateTime analyzedAt);

    Builder libraryCount(long libraryCount);

    AppWithLibraries build();
  }
}


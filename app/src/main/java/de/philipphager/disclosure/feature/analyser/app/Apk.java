package de.philipphager.disclosure.feature.analyser.app;

import dalvik.system.DexFile;
import de.philipphager.disclosure.database.app.model.App;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import rx.Observable;
import timber.log.Timber;

public class Apk {
  private static final int MIN_INDEX = 0;
  private final App app;
  private List<String> sortedClassNames;

  public Apk(App app) throws IOException {
    this.app = app;
    load();
  }

  private void load() throws IOException {
    DexFile dexFile = new DexFile(app.sourceDir());
    List<String> classNames = Collections.list(dexFile.entries());

    sortedClassNames = Observable.from(classNames)
        .distinct()
        .toSortedList()
        .toBlocking()
        .first();
  }

  public boolean containsPackage(String packageName) {
    String currentThread = Thread.currentThread().getName();
    Timber.d("%s : Searching for package %s in app %s", currentThread, packageName, app.label());

    int index = Collections.binarySearch(sortedClassNames, packageName, (currentItem, key) -> {
      if(currentItem.startsWith(key)) {
        return 0;
      }
      return currentItem.compareTo(key);
    });
    return index >= MIN_INDEX;
  }
}

package de.philipphager.disclosure.database.app;

import android.database.Cursor;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;
import com.squareup.sqldelight.SqlDelightStatement;
import de.philipphager.disclosure.database.app.model.App;
import de.philipphager.disclosure.database.app.model.AppInfo;
import de.philipphager.disclosure.database.app.model.AppModel;
import de.philipphager.disclosure.database.app.model.AppReport;
import de.philipphager.disclosure.database.app.model.AppWithPermissions;
import de.philipphager.disclosure.database.util.mapper.CursorToListMapper;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;

public class AppRepository {
  private static final int SQL_ERROR = -1;
  private final AppModel.InsertApp insertApp;
  private final AppModel.UpdateApp updateApp;

  @Inject public AppRepository(App.InsertApp insertApp, App.UpdateApp updateApp) {
    this.insertApp = insertApp;
    this.updateApp = updateApp;
  }

  public long insert(BriteDatabase db, App app) {
    synchronized (this) {
      insertApp.bind(
          app.label(),
          app.packageName(),
          app.process(),
          app.sourceDir(),
          app.targetSdk(),
          app.flags(),
          app.analyzedAt());
      return db.executeInsert(App.TABLE_NAME, insertApp.program);
    }
  }

  public int update(BriteDatabase db, App app) {
    synchronized (this) {
      updateApp.bind(
          app.label(),
          app.process(),
          app.sourceDir(),
          app.targetSdk(),
          app.flags(),
          app.analyzedAt(),
          app.packageName());
      return db.executeUpdateDelete(App.TABLE_NAME, updateApp.program);
    }
  }

  public long insertOrUpdate(BriteDatabase db, App app) {
    synchronized (this) {
      int updatedRows = update(db, app);

      if (updatedRows == 0) {
        return insert(db, app);
      }
      return getAppId(db, app.packageName());
    }
  }

  public int delete(BriteDatabase db, String where) {
    synchronized (this) {
      return db.delete(App.TABLE_NAME, where);
    }
  }

  public Observable<List<App>> all(BriteDatabase db) {
    CursorToListMapper<App> cursorToList = new CursorToListMapper<>(App.FACTORY.selectAllMapper());
    SqlDelightStatement selectAll = App.FACTORY.selectAll();

    return db.createQuery(selectAll.tables, selectAll.statement)
        .map(SqlBrite.Query::run)
        .map(cursorToList);
  }

  public Observable<App> byPackageName(BriteDatabase db, String packageName) {
    SqlDelightStatement selectByPackage = App.FACTORY.selectByPackage(packageName);
    CursorToListMapper<App> cursorToList =
        new CursorToListMapper<>(App.FACTORY.selectByLibraryMapper());

    return db.createQuery(selectByPackage.tables, selectByPackage.statement, selectByPackage.args)
        .map(SqlBrite.Query::run)
        .map(cursorToList)
        .flatMap(apps -> Observable.from(apps).first());
  }

  public Observable<List<App>> byLibrary(BriteDatabase db, String libraryId) {
    SqlDelightStatement selectByLibrary = App.FACTORY.selectByLibrary(libraryId);
    CursorToListMapper<App> cursorToList =
        new CursorToListMapper<>(App.FACTORY.selectByLibraryMapper());

    return db.createQuery(selectByLibrary.tables, selectByLibrary.statement, selectByLibrary.args)
        .map(SqlBrite.Query::run)
        .map(cursorToList);
  }

  public Observable<List<AppInfo>> allInfos(BriteDatabase db) {
    CursorToListMapper<AppInfo> cursorToList = new CursorToListMapper<>(AppInfo.MAPPER);
    SqlDelightStatement selectAllInfos = App.FACTORY.selectAllInfos();

    return db.createQuery(selectAllInfos.tables, selectAllInfos.statement)
        .map(SqlBrite.Query::run)
        .map(cursorToList);
  }

  public Observable<List<AppWithPermissions>> byLibraryWithPermissionCount(BriteDatabase db,
      String libraryId) {
    SqlDelightStatement selectByLibrary = App.FACTORY.selectAllWithPermissionCount(libraryId);
    CursorToListMapper<AppWithPermissions> cursorToList =
        new CursorToListMapper<>(AppWithPermissions.MAPPER);

    return db.createQuery(selectByLibrary.tables, selectByLibrary.statement, selectByLibrary.args)
        .map(SqlBrite.Query::run)
        .map(cursorToList);
  }

  public Observable<List<AppReport>> selectByQuery(BriteDatabase db, String query) {
    String likeClause = "%" + query + "%";
    SqlDelightStatement selectByQuery =
        App.FACTORY.selectByQuery(likeClause, likeClause, likeClause);

    CursorToListMapper<AppReport> cursorToList =
        new CursorToListMapper<>(AppReport.MAPPER);

    return db.createQuery(selectByQuery.tables, selectByQuery.statement, selectByQuery.args)
        .map(SqlBrite.Query::run)
        .map(cursorToList);
  }

  public Observable<List<AppReport>> selectReport(BriteDatabase db) {
    CursorToListMapper<AppReport> cursorToList = new CursorToListMapper<>(AppReport.MAPPER);
    SqlDelightStatement selectReport = App.FACTORY.selectReport();

    return db.createQuery(selectReport.tables, selectReport.statement)
        .map(SqlBrite.Query::run)
        .map(cursorToList);
  }

  private long getAppId(BriteDatabase db, String packageName) {
    SqlDelightStatement selectAppId = App.FACTORY.selectAppId(packageName);
    Cursor cursor = db.query(selectAppId.statement, selectAppId.args);

    if (cursor.getCount() > 0) {
      cursor.moveToFirst();
      return cursor.getLong(0);
    }
    return SQL_ERROR;
  }
}

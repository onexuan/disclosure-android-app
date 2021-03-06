package de.philipphager.disclosure.feature.home;

import de.philipphager.disclosure.feature.analyser.app.usecase.AnalyseUsedLibraries;
import de.philipphager.disclosure.feature.sync.api.ApiSyncer;
import de.philipphager.disclosure.feature.sync.db.DBSyncer;
import de.philipphager.disclosure.service.AppService;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class HomePresenter {
  private final DBSyncer dbSyncer;
  private final ApiSyncer apiSyncer;
  private final AnalyseUsedLibraries analyseUsedLibraries;
  private final AppService appService;
  private CompositeSubscription subscriptions;
  private HomeView view;

  @Inject public HomePresenter(DBSyncer dbSyncer,
      ApiSyncer apiSyncer,
      AnalyseUsedLibraries analyseUsedLibraries,
      AppService appService) {
    this.dbSyncer = dbSyncer;
    this.apiSyncer = apiSyncer;
    this.analyseUsedLibraries = analyseUsedLibraries;
    this.appService = appService;
  }

  public void onCreate(HomeView homeView) {
    this.view = homeView;
    this.subscriptions = new CompositeSubscription();

    sync();
  }

  public void onDestroy() {
    this.subscriptions.clear();
  }

  private void sync() {
    subscriptions.add(Observable.concat(
        Observable.merge(
            syncDBWithDevice(),
            syncWithServerApi()),
        analyzeUsedLibrariesAndPermissions())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnCompleted(() -> Timber.d("sync completed"))
        .subscribe(result -> {
        }, throwable -> {
          Timber.e(throwable, "while syncing with device and api");
        }));
  }

  private Observable<Void> syncDBWithDevice() {
    return dbSyncer.sync();
  }

  private Observable<Void> syncWithServerApi() {
    return apiSyncer.sync();
  }

  private Observable<?> analyzeUsedLibrariesAndPermissions() {
    return appService.all()
        .first()
        .flatMap(Observable::from)
        .flatMap(app -> Observable.just(app)
            .subscribeOn(Schedulers.computation())
            .flatMap(analyseUsedLibraries::analyse))
        .toList();
  }

  public boolean onTabSelected(int position, boolean wasSelected) {
    if (!wasSelected) {
      view.setCurrentTab(position);
    }
    return true;
  }
}

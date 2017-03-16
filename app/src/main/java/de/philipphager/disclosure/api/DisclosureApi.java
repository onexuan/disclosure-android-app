package de.philipphager.disclosure.api;

import de.philipphager.disclosure.database.feature.model.Feature;
import de.philipphager.disclosure.database.library.model.Library;
import de.philipphager.disclosure.database.library.model.LibraryFeature;
import java.util.List;
import org.threeten.bp.OffsetDateTime;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface DisclosureApi {
  int PAGE_SIZE = 50;

  @GET("/libraries")
  Observable<List<Library>> allLibraries();

  @GET("/libraries")
  Observable<List<Library>> allLibraries(@Query("updatedSince") OffsetDateTime date,
      @Query("page") int page, @Query("limit") int limit);

  @GET("/features")
  Observable<List<Feature>> allFeatures();

  @GET("/features")
  Observable<List<Feature>> allFeatures(@Query("updatedSince") OffsetDateTime date);

  @GET("/libraryFeatures")
  Observable<List<LibraryFeature>> allLibraryFeatures();

  @GET("/libraryFeatures")
  Observable<List<LibraryFeature>> allLibraryFeatures(@Query("updatedSince") OffsetDateTime date);
}

package de.philipphager.disclosure.database.library.repositories;

import com.squareup.sqlbrite.BriteDatabase;
import de.philipphager.disclosure.database.library.model.Library;
import de.philipphager.disclosure.database.mocks.MockLibrary;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.verify;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
    BriteDatabase.class,
    Library.InsertLibrary.class,
    Library.UpdateLibrary.class
})
public class LibraryRepositoryShould {
  protected BriteDatabase database;
  protected LibraryRepository libraryRepository;
  protected Library.InsertLibrary insertLibrary;
  protected Library.UpdateLibrary updateLibrary;

  @Before public void setUp() {
    insertLibrary = PowerMockito.mock(Library.InsertLibrary.class);
    updateLibrary = PowerMockito.mock(Library.UpdateLibrary.class);
    database = PowerMockito.mock(BriteDatabase.class);
    libraryRepository = new LibraryRepository(insertLibrary, updateLibrary);
  }

  @Test public void insertLibraryIntoDatabase() {
    Library library = MockLibrary.TEST;
    libraryRepository.insert(database, library);

    verify(insertLibrary).bind(library.id(),
        library.packageName(),
        library.sourceDir(),
        library.title(),
        library.subtitle(),
        library.description(),
        library.websiteUrl(),
        library.type(),
        library.createdAt(),
        library.updatedAt());
    verify(database).executeInsert(insertLibrary.table, insertLibrary.program);
  }

  @Test public void updateLibraryInDatabase() {
    Library library = MockLibrary.TEST;
    libraryRepository.update(database, library);

    verify(updateLibrary).bind(library.packageName(),
        library.sourceDir(),
        library.title(),
        library.subtitle(),
        library.description(),
        library.websiteUrl(),
        library.type(),
        library.createdAt(),
        library.updatedAt(),
        library.id());
    verify(database).executeUpdateDelete(updateLibrary.table, updateLibrary.program);
  }
}



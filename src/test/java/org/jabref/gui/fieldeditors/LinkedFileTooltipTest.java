package org.jabref.gui.fieldeditors;

import javafx.application.Platform;
import org.jabref.gui.DialogService;
import org.jabref.gui.preferences.GuiPreferences;
import org.jabref.logic.FilePreferences;
import org.jabref.logic.util.TaskExecutor;
import org.jabref.model.database.BibDatabaseContext;
import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.LinkedFile;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.util.WaitForAsyncUtils;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LinkedFileTooltipTest {

    private BibDatabaseContext databaseContext;
    private TaskExecutor taskExecutor;
    private DialogService dialogService;
    private GuiPreferences preferences;
    private FilePreferences filePreferences;

    @BeforeAll
    static void initJavaFX() {
        Platform.startup(() -> {});
    }

    @BeforeEach
    void setUp() {
        databaseContext = mock(BibDatabaseContext.class);
        taskExecutor = mock(TaskExecutor.class);
        dialogService = mock(DialogService.class);
        preferences = mock(GuiPreferences.class);
        filePreferences = mock(FilePreferences.class);

        when(preferences.getFilePreferences()).thenReturn(filePreferences);
    }

    @Test   // Test to PASS
    void tooltipUpdatesOnRename() {
        BibEntry entry = new BibEntry();
        Path originalPath = Paths.get("original.pdf");
        Path renamedPath = Paths.get("renamed.pdf");

        LinkedFile linkedFile = new LinkedFile("desc", originalPath, "pdf");
        LinkedFileViewModel viewModel = new LinkedFileViewModel(
                linkedFile,
                entry,
                databaseContext,
                taskExecutor,
                dialogService,
                preferences
        );

        String before = viewModel.getDescriptionAndLink();

        Platform.runLater(() -> linkedFile.setLink(renamedPath.toString()));
        WaitForAsyncUtils.waitForFxEvents();

        String after = viewModel.getDescriptionAndLink();
        assertNotEquals(before, after, "Tooltip should update after file rename.");
    }

    @Test  // Test to PASS
    void descriptionIsCorrect() {
        LinkedFile file = new LinkedFile("Appendix", "appendix.pdf", "pdf");
        assertEquals("Appendix", file.getDescription());
    }


    @Test  // Test to PASS
    void linkedFilesAreEqual() {
        LinkedFile file1 = new LinkedFile("desc", "file.pdf", "pdf");
        LinkedFile file2 = new LinkedFile("desc", "file.pdf", "pdf");

        assertEquals(file1, file2);
        assertEquals(file1.hashCode(), file2.hashCode());
    }

    @Test  // Test to FAIL
    void tooltipChangesOnTypeChange() {
        BibEntry entry = new BibEntry();
        Path filePath = Paths.get("doc.pdf");

        LinkedFile linkedFile = new LinkedFile("desc", filePath, "pdf");
        LinkedFileViewModel viewModel = new LinkedFileViewModel(
                linkedFile,
                entry,
                databaseContext,
                taskExecutor,
                dialogService,
                preferences
        );

        String before = viewModel.getDescriptionAndLink();

        Platform.runLater(() -> linkedFile.setFileType("txt"));  // Change only file type
        WaitForAsyncUtils.waitForFxEvents();

        String after = viewModel.getDescriptionAndLink();

        // This assumes incorrect behavior — that the tooltip should update on file type change
        assertNotEquals(before, after, "Tooltip should update when file type changes (this is incorrect)");
    }

    @Test  // Test to FAIL
    void tooltipFailsSameLink() {
        BibEntry entry = new BibEntry();
        Path originalPath = Paths.get("file.pdf");

        LinkedFile linkedFile = new LinkedFile("desc", originalPath, "pdf");
        LinkedFileViewModel viewModel = new LinkedFileViewModel(
                linkedFile,
                entry,
                databaseContext,
                taskExecutor,
                dialogService,
                preferences
        );

        String before = viewModel.getDescriptionAndLink();

        // Simulate redundant set
        Platform.runLater(() -> linkedFile.setLink(originalPath.toString()));
        WaitForAsyncUtils.waitForFxEvents();

        String after = viewModel.getDescriptionAndLink();

        // Assumes bad behavior — will pass if tooltip incorrectly changes
        assertNotEquals(before, after, "Tooltip should update even if path hasn’t changed (this is wrong)");
    }
}

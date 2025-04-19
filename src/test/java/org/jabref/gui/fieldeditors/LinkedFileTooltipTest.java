package org.jabref.gui.fieldeditors;

import java.nio.file.Files;
import java.nio.file.Path;

import javafx.application.Platform;
import java.util.concurrent.CountDownLatch;

import org.jabref.gui.DialogService;
import org.jabref.gui.preferences.GuiPreferences;
import org.jabref.logic.FilePreferences;
import org.jabref.logic.util.TaskExecutor;
import org.jabref.model.database.BibDatabaseContext;
import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.LinkedFile;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LinkedFileTooltipTest {

    private LinkedFile linkedFile;
    private LinkedFileViewModel viewModel;
    private BibEntry entry;
    private BibDatabaseContext databaseContext;
    private TaskExecutor taskExecutor;
    private DialogService dialogService;
    private GuiPreferences preferences;
    private FilePreferences filePreferences;

    private Path tempFile;

    @BeforeEach
    void setUp() throws Exception {
=        CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(latch::countDown);
        latch.await();

        tempFile = Files.createTempFile("tooltip-test", ".pdf");

        linkedFile = new LinkedFile("Test Description", tempFile, "pdf");
        entry = new BibEntry();
        databaseContext = mock(BibDatabaseContext.class);
        taskExecutor = mock(TaskExecutor.class);
        dialogService = mock(DialogService.class);
        preferences = mock(GuiPreferences.class);
        filePreferences = mock(FilePreferences.class);

        when(preferences.getFilePreferences()).thenReturn(filePreferences);

        viewModel = new LinkedFileViewModel(
                linkedFile,
                entry,
                databaseContext,
                taskExecutor,
                dialogService,
                preferences
        );
    }

    @Test   // Test to PASS
    void testTooltipUpdatesAfterLinkChange() {
        String oldTooltip = viewModel.getDescriptionAndLink();
        String newPath = tempFile.resolveSibling("renamed-tooltip-test.pdf").toString();

        // Update the link
        linkedFile.setLink(newPath);

        // Re-fetch tooltip text
        String updatedTooltip = viewModel.getDescriptionAndLink();

        assertNotEquals(oldTooltip, updatedTooltip, "Tooltip should update after file rename");
    }

    @Test   // Test to FAIL
    void testTooltipDoesNotUpdateWithoutLinkChange() {
        String originalTooltip = viewModel.getDescriptionAndLink();

        // Do nothing
        String stillSameTooltip = viewModel.getDescriptionAndLink();

        assertEquals(originalTooltip, stillSameTooltip, "Tooltip should not change if the link doesn't change");
    }
}

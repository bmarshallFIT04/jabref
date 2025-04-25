package org.jabref.gui.entryeditor;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.jabref.gui.LibraryTab;
import org.jabref.model.entry.BibEntry;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.*;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class FieldJumpTest {

    @BeforeAll
    public static void setupJavaFX() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(latch::countDown);
        latch.await();
    }

    public static class DummyEditor {
        private final TextField field;

        public DummyEditor(String label) {
            this.field = new TextField();
            this.field.setAccessibleText(label);
        }

        public Node getNode() {
            return field;
        }
    }

    private EntryEditor createEntryEditor(BibEntry entry, List<DummyEditor> dummyEditors) throws Exception {
        Constructor<?> constructor = null;
        for (Constructor<?> c : EntryEditor.class.getDeclaredConstructors()) {
            if (c.getParameterCount() == 3) {
                constructor = c;
                break;
            }
        }

        if (constructor == null) {
            throw new IllegalStateException("EntryEditor constructor not found");
        }

        constructor.setAccessible(true);
        EntryEditor editor = (EntryEditor) constructor.newInstance(mock(LibraryTab.class), new Object(), new Object());
        editor.setEntry(entry);

        Class<?> fieldEditorFXClass = Class.forName("org.jabref.gui.entryeditor.FieldEditorFX");
        List<Object> fieldEditorProxies = dummyEditors.stream()
                .map(de -> Proxy.newProxyInstance(
                        fieldEditorFXClass.getClassLoader(),
                        new Class<?>[]{fieldEditorFXClass},
                        (proxy, method, args) -> {
                            if ("getNode".equals(method.getName())) {
                                return de.getNode();
                            }
                            return null;
                        }
                ))
                .toList();

        Method setter = EntryEditor.class.getMethod("setFieldEditorsForTest", List.class);
        setter.invoke(editor, fieldEditorProxies);

        return editor;
    }

    @Test  // Test to PASS
    public void focusAuthor() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                DummyEditor authorEditor = new DummyEditor("author");
                DummyEditor journalEditor = new DummyEditor("journal");

                BibEntry entry = new BibEntry();
                EntryEditor editor = createEntryEditor(entry, List.of(authorEditor, journalEditor));

                Stage stage = new Stage();
                stage.setScene(new Scene(new VBox(editor)));
                stage.show();

                editor.jumpToField("author");

                assertTrue(authorEditor.getNode().isFocused(), "Author field should be focused.");
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Test failed", e);
            } finally {
                latch.countDown();
            }
        });

        boolean done = latch.await(10, TimeUnit.SECONDS);
        assertTrue(done, "Test timed out.");
    }

    @Test  // Test to FAIL
    void labelMismatchFails() {
        TextField field = new TextField();
        field.setAccessibleText("journal");

        String label = field.getAccessibleText();
        assertTrue("author".equals(label), "Expected label to be 'author' (this is incorrect)");
    }
}

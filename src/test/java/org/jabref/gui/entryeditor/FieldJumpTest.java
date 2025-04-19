package org.jabref.gui.entryeditor;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FieldJumpTest {

    private interface FieldEditorStub {
        Node getNode();
    }

    @BeforeAll
    public static void initToolkit() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(latch::countDown);
        latch.await();
    }

    private static class DummyFieldEditor implements FieldEditorStub {
        private final TextField field;

        public DummyFieldEditor(String label) {
            this.field = new TextField();
            this.field.setAccessibleText(label);
        }

        @Override
        public Node getNode() {
            return field;
        }

        public TextField getField() {
            return field;
        }
    }

    @Test   // Test to PASS
    public void jumpToAuthor() throws Exception {
        DummyFieldEditor authorEditor = new DummyFieldEditor("author");
        DummyFieldEditor journalEditor = new DummyFieldEditor("journal");

        Map<String, DummyFieldEditor> nameToEditor = new LinkedHashMap<>();
        nameToEditor.put("author", authorEditor);
        nameToEditor.put("journal", journalEditor);

        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            VBox root = new VBox(authorEditor.getNode(), journalEditor.getNode());
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            // Simulate jump
            Node node = nameToEditor.get("author").getNode();
            node.requestFocus();

            assertTrue(node.isFocused(), "Editor should be focused");
            latch.countDown();
        });

        latch.await();
    }

    @Test   // Test to FAIL
    public void jumpToMissingField() throws Exception {
        DummyFieldEditor authorEditor = new DummyFieldEditor("author");
        DummyFieldEditor journalEditor = new DummyFieldEditor("journal");

        Map<String, DummyFieldEditor> nameToEditor = new LinkedHashMap<>();
        nameToEditor.put("author", authorEditor);
        nameToEditor.put("journal", journalEditor);

        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            VBox root = new VBox(authorEditor.getNode(), journalEditor.getNode());
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            // Simulate jump to a nonexistent field
            DummyFieldEditor missingEditor = nameToEditor.get("abstract");

            // This should be null, meaning no jump possible
            if (missingEditor != null) {
                Node node = missingEditor.getNode();
                node.requestFocus();
            }

            // Ensure no node got focused accidentally
            boolean anyFocused = authorEditor.getField().isFocused() || journalEditor.getField().isFocused();
            assertTrue(!anyFocused, "No editor should be focused if field doesn't exist");

            latch.countDown();
        });

        latch.await();
    }
}

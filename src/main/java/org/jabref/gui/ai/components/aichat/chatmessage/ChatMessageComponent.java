package org.jabref.gui.ai.components.aichat.chatmessage;

import java.util.function.Consumer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;

import org.jabref.logic.ai.util.ErrorMessage;
import org.jabref.logic.l10n.Localization;

import com.airhacks.afterburner.views.ViewLoader;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Markdown support via Flexmark
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.html.HtmlRenderer;

public class ChatMessageComponent extends HBox {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatMessageComponent.class);

    // Markdown parser and renderer
    private static final Parser MARKDOWN_PARSER = Parser.builder().build();
    private static final HtmlRenderer HTML_RENDERER = HtmlRenderer.builder().build();

    private final ObjectProperty<ChatMessage> chatMessage = new SimpleObjectProperty<>();
    private final ObjectProperty<Consumer<ChatMessageComponent>> onDelete = new SimpleObjectProperty<>();

    @FXML private HBox wrapperHBox;
    @FXML private VBox vBox;
    @FXML private Label sourceLabel;
    @FXML private WebView contentWebView;
    @FXML private VBox buttonsVBox;

    public ChatMessageComponent() {
        ViewLoader.view(this)
                  .root(this)
                  .load();

        chatMessage.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loadChatMessage();
            }
        });
    }

    public ChatMessageComponent(ChatMessage chatMessage, Consumer<ChatMessageComponent> onDeleteCallback) {
        this();
        setChatMessage(chatMessage);
        setOnDelete(onDeleteCallback);
    }

    public void setChatMessage(ChatMessage chatMessage) {
        this.chatMessage.set(chatMessage);
    }

    public ChatMessage getChatMessage() {
        return chatMessage.get();
    }

    public void setOnDelete(Consumer<ChatMessageComponent> onDeleteCallback) {
        this.onDelete.set(onDeleteCallback);
    }

    private void loadChatMessage() {
        String markdown;
        switch (chatMessage.get()) {
            case UserMessage userMessage -> {
                setColor("-jr-ai-message-user", "-jr-ai-message-user-border");
                setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
                sourceLabel.setText(Localization.lang("User"));
                markdown = userMessage.singleText();
            }

            case AiMessage aiMessage -> {
                setColor("-jr-ai-message-ai", "-jr-ai-message-ai-border");
                setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
                sourceLabel.setText(Localization.lang("AI"));
                markdown = aiMessage.text();
            }

            case ErrorMessage errorMessage -> {
                setColor("-jr-ai-message-error", "-jr-ai-message-error-border");
                setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
                sourceLabel.setText(Localization.lang("Error"));
                markdown = errorMessage.getText();
            }

            default -> {
                LOGGER.error("Unsupported message type: {}", chatMessage.get().type().name());
                return;
            }
        }
        // Render Markdown and load into WebView
        String html = HTML_RENDERER.render(MARKDOWN_PARSER.parse(markdown));
        WebEngine engine = contentWebView.getEngine();
        engine.loadContent(html);
    }

    @FXML
    private void initialize() {
        buttonsVBox.visibleProperty().bind(wrapperHBox.hoverProperty());
    }

    @FXML
    public void onDeleteClick() {
        if (onDelete.get() != null) {
            onDelete.get().accept(this);
        }
    }

    private void setColor(String fillColor, String borderColor) {
        vBox.setStyle(
            "-fx-background-color: " + fillColor + "; " +
            "-fx-border-radius: 10; -fx-background-radius: 10; " +
            "-fx-border-color: " + borderColor + "; -fx-border-width: 3;"
        );
    }
}

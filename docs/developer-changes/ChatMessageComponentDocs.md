# ChatMessageComponent

A JavaFX component for displaying chat messages (user, AI, or error) with Markdown rendering and a delete button.

---

## Table of Contents
1. [Overview](#overview)
2. [Features](#features)
3. [Dependencies](#dependencies)
4. [FXML Layout](#fxml-layout)
5. [Usage](#usage)
6. [API Reference](#api-reference)

---

## Overview

`ChatMessageComponent` is a reusable JavaFX control that renders chat messages in Markdown format inside a `WebView`, differentiating between user messages, AI messages, and error messages. It also provides a hover‑revealed delete button to remove a message from the chat history.

## Features

- **Markdown support** via [Flexmark](https://github.com/vsch/flexmark-java)
- **Dynamic styling** and orientation based on message type (user, AI, error)
- **Hover‑activated delete button** with callback
- **i18n** support for labels via JabRef's `Localization`

## Dependencies

- Java 17+
- JavaFX (controls, web)
- Afterburner.fx (`ViewLoader`)
- Flexmark Java (`flexmark-parser`, `flexmark-html-renderer`)
- LangChain4j (`ChatMessage`, `UserMessage`, `AiMessage`)
- JabRef logic (`ErrorMessage`, `Localization`)
- JabRef UI (`JabRefIconView`)
- SLF4J (`LoggerFactory`)

## FXML Layout

```xml
<?xml version="1.0" encoding="UTF-8"?>
<fx:root type="Pane"
         xmlns="http://javafx.com/javafx/17.0.2-ea"
         xmlns:fx="http://javafx.com/fxml/1"
         fx:controller="org.jabref.gui.ai.components.aichat.chatmessage.ChatMessageComponent">

  <HBox fx:id="wrapperHBox" spacing="10">

    <!-- Message box -->
    <VBox fx:id="vBox" spacing="5" styleClass="chat-message-box">
      <Label fx:id="sourceLabel" styleClass="chat-message-source"/>
      <WebView fx:id="contentWebView"
               styleClass="chat-message-text-area"
               prefWidth="400" prefHeight="100"/>
    </VBox>

    <!-- Delete button (hidden until hover) -->
    <VBox fx:id="buttonsVBox" alignment="BASELINE_RIGHT">
      <Button onAction="#onDeleteClick"
              styleClass="icon-button,narrow"
              textAlignment="CENTER">
        <graphic>
          <JabRefIconView glyph="DELETE_ENTRY"/>
        </graphic>
        <tooltip>
          <Tooltip text="%Delete message from chat history"/>
        </tooltip>
      </Button>
    </VBox>

  </HBox>
</fx:root>
```

## Usage

### Instantiation

```java
// 1. Create the component with a ChatMessage and deletion callback
ChatMessageComponent comp = new ChatMessageComponent(
    someChatMessage,
    component -> messagesContainer.getChildren().remove(component)
);

// 2. Add to your JavaFX layout
messagesVBox.getChildren().add(comp);
```

### Message Types

- **UserMessage**: right‑aligned, label = "User"
- **AiMessage**: left‑aligned, label = "AI"
- **ErrorMessage**: left‑aligned, label = "Error"

## API Reference

### Constructors

- `ChatMessageComponent()`
  - Loads FXML and sets up change listener on the `chatMessage` property.

- `ChatMessageComponent(ChatMessage chatMessage, Consumer<ChatMessageComponent> onDeleteCallback)`
  - Convenience constructor to set the initial message and delete callback.

### Properties

- `ObjectProperty<ChatMessage> chatMessage`
  - The message being displayed. Setting a new value triggers a reload.

- `ObjectProperty<Consumer<ChatMessageComponent>> onDelete`
  - Callback fired when the delete button is clicked.

### Public Methods

- `void setChatMessage(ChatMessage message)`
- `ChatMessage getChatMessage()`
- `void setOnDelete(Consumer<ChatMessageComponent> callback)`

### Internal Methods

- `private void loadChatMessage()`
  - Determines message type, updates styling/orientation, renders Markdown to HTML, and loads into the `WebView`.

- `@FXML private void initialize()`
  - Binds the visibility of the delete button to hover state.

- `@FXML public void onDeleteClick()`
  - Invokes the delete callback if present.

- `private void setColor(String fillColor, String borderColor)`
  - Applies inline CSS to the message box for background and border.

---


# Tooltip Behavior in `LinkedFilesEditor`

## Overview

This document describes the tooltip behavior for linked files in the `LinkedFilesEditor` component of JabRef. It explains the original limitation, the reasoning for the change, and how tooltips are now dynamically updated when the file path or description changes (e.g., after a file is renamed or moved).

---

## Problem Statement

Originally, tooltips in the `LinkedFilesEditor` were created using a static call:

```java
.withStringTooltip(LinkedFileViewModel::getDescriptionAndLink)
```

While this worked initially, it did **not update dynamically** when the underlying `LinkedFileViewModel` was modified. For example, if the file path or description was changed, the tooltip would remain stale until the UI was manually refreshed.

---

## Solution: Dynamic Tooltip Binding

To address this limitation, the tooltip logic was updated to **bind the tooltip text property** to the relevant observable properties of the `LinkedFileViewModel`.

```java
.withTooltip(viewModel -> {
    Tooltip tooltip = new Tooltip();
    tooltip.textProperty().bind(Bindings.createStringBinding(
        viewModel::getDescriptionAndLink,
        viewModel.linkProperty(),
        viewModel.descriptionProperty()
    ));
    return tooltip;
})
```

- Tooltips now **update automatically** when either the file path or description is modified.
- There is **no need to refresh the entire list** to reflect changes.
- This approach uses **JavaFX property binding**, ensuring reactive UI behavior and improved user experience.

---

## Testing

- Manual test cases were developed to verify tooltip updates when:
  - A file is renamed
  - A file's path is edited
  - A file is deleted
- Unit tests targeted the underlying method `getDescriptionAndLink()` to ensure string output correctness based on different combinations of file paths and descriptions.

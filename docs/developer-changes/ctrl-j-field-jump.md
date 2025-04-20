# Ctrl+J Field Jump Feature in EntryEditor

## Overview

This document describes the feature enhancement added to JabRef's `EntryEditor` which allows users to press `Ctrl+J` to quickly jump to a specific field by typing its name. This functionality significantly improves the usability of the entry editing experience, especially in large entries with many fields.

---

## Problem Statement

Users often have difficulty navigating to specific fields in the `EntryEditor`, especially when many fields are present and distributed across multiple tabs. The lack of a search-and-jump shortcut meant users had to manually scroll and locate the field.

---

## Feature Description

The newly introduced feature binds the keyboard shortcut `Ctrl+J` to a popup dialog that allows the user to type part of a field name. The dialog filters suggestions as the user types and allows selection via arrow keys or mouse click. Once a field is selected, focus is moved to the corresponding field editor.

---

## Implementation Summary

### Key Binding

A new key binding was added to `KeyBinding.java`:

```java
FOCUS_FIELD_BY_NAME("Focus field by name", "ctrl+j", "Entry editor"),

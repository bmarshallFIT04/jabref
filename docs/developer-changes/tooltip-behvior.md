# Tooltip Behavior in LinkedFilesEditor

## Overview

This document describes the tooltip behavior for linked files in the `LinkedFilesEditor` component of JabRef. It explains the original limitation, the reasoning for the change, and how tooltips are now dynamically updated when the file path changes (e.g., after a file is renamed or moved).

---

## Problem Statement

Originally, tooltips in the `LinkedFilesEditor` were created using a static call:

```java
.withStringTooltip(LinkedFileViewModel::getDescriptionAndLink)

# DuckyLib

A modern GUI library for Minecraft Forge 1.20.1 with TOML-based layouts and a live in-game editor.

## Features

- TOML-defined GUI layouts — design screens declaratively
- Built-in widgets: Button, Label, TextField, Slider, Checkbox, Dropdown, Panel, ScrollPanel
- Custom theme system with hot-reloadable TOML themes
- In-game visual editor with property panel and widget selector
- Undo/redo command system via `EditorCommand`
- Simple API for loading layouts and opening screens

## Usage

Load a layout and open a screen:
```java
DuckyLib.openScreen(DuckyLib.loadLayout(
    new ResourceLocation("duckylib", "guis/config.toml")
));
```

## Build

```sh
./gradlew build
```

The built JAR will be in `build/libs/`.

## License

MIT

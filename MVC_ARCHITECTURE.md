# MVC Architecture Refactoring

## Overview

The project has been refactored to properly follow the **Model-View-Controller (MVC)** design pattern.

## Structure

### 📁 **Model Layer** (`src/model/`)

**File:** `Menu.java`

**Responsibilities:**

- Contains game state data (`nbplayers`, `currentBots`, `currentType`, `currentUser`)
- Business logic and validation methods
- Data persistence (reading/writing user logs)
- **NO** UI logic or user input handling

**Key Methods:**

- `validateGameStart()` - Validates if game can start
- `validatePlayerCount()` - Validates player count
- `validateGameType()` - Validates game type
- `selectUser()` - Retrieves user from logs
- `writeLogsUser()` - Saves user to logs
- `isUserLogsEmpty()` - Checks if logs exist

### 🎮 **Controller Layer** (`src/controller/`)

**File:** `MenuController.java`

**Responsibilities:**

- Mediates between Model and View
- Handles business flow and coordination
- Processes user actions from the View
- Updates the Model based on user input
- Provides data to the View

**Key Methods:**

- `startGame()` - Validates and initiates game start
- `configureBots()` - Configures bot settings
- `setNbPlayers()` - Sets player count with validation
- `setType()` - Sets game type with validation
- `createNewUser()` - Creates and saves new user
- `selectExistingUser()` - Selects existing user by ID
- `exitGame()` - Handles game exit

### 🖼️ **View Layer** (`src/view/`)

**File:** `MenuGUI.java`

**Responsibilities:**

- Displays the graphical user interface
- Captures user interactions (button clicks, slider changes)
- Delegates actions to the Controller
- Updates display based on Model state
- **NO** business logic or validation

**Key Components:**

- Player slider
- Game type selector
- User management dialogs
- Settings display panels
- Action buttons

### 🧪 **Test Layer** (`src/test/`)

**File:** `GameConsoleTest.java`

**Purpose:**

- Console-based UI for testing the game menu
- Contains the old `MenuGame()` console functionality
- **NOT** part of the MVC architecture
- Used for testing and demonstration

## Data Flow

```
User Interaction (View)
    ↓
MenuGUI captures event
    ↓
MenuGUI calls MenuController method
    ↓
MenuController validates/processes
    ↓
MenuController updates Menu (Model)
    ↓
MenuGUI reads Menu state
    ↓
MenuGUI updates display
```

## Example Flow: Creating a New User

1. **View:** User clicks "Create New User" button in `MenuGUI`
2. **View:** `MenuGUI.createNewUser()` displays dialog and collects input
3. **View:** User submits form → calls `controller.createNewUser(name, age, avatar)`
4. **Controller:** `MenuController.createNewUser()` creates User object
5. **Controller:** Calls `menu.setCurrentUser()` and `menu.writeLogsUser()`
6. **Model:** `Menu` updates state and persists to file
7. **View:** `MenuGUI` reads `menu.getCurrentUser()` and updates display

## Benefits of This Architecture

✅ **Separation of Concerns:** Each layer has a single, well-defined responsibility

✅ **Testability:** Model and Controller can be tested independently of the UI

✅ **Maintainability:** Changes to UI don't affect business logic and vice versa

✅ **Reusability:** The same Model and Controller can work with different Views (GUI, Console, Web)

✅ **Flexibility:** Easy to add new features or change implementations

## Running the Application

### GUI Version (MVC):

```java
// Run MenuGUI.java
public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        MenuGUI menu = new MenuGUI();
        menu.setVisible(true);
    });
}
```

### Console Version (Testing):

```java
// Run GameConsoleTest.java
public static void main(String[] args) {
    GameConsoleTest test = new GameConsoleTest();
    test.MenuGame();
}
```

## Migration Notes

### What Changed:

- ❌ Removed `MenuGame()` console UI from `Menu.java`
- ❌ Removed `exitGame()` from `Menu.java`
- ❌ Removed `startGame()` console output from `Menu.java`
- ✅ Added `MenuController.java` as intermediary
- ✅ Added validation methods to `Menu.java`
- ✅ Moved console UI to `GameConsoleTest.java`
- ✅ Updated `MenuGUI.java` to use Controller

### What Stayed the Same:

- All functionality is preserved
- User logs still work the same way
- GUI appearance and behavior unchanged
- Game start logic unchanged

## File Organization

```
src/
├── model/
│   ├── Menu.java          ← Model (data + business logic)
│   └── User.java
├── view/
│   └── MenuGUI.java       ← View (UI only)
├── controller/
│   └── MenuController.java ← Controller (coordination)
└── test/
    └── GameConsoleTest.java ← Console UI for testing
```

---

**Refactored:** December 31, 2025
**Pattern:** Model-View-Controller (MVC)
**Status:** ✅ Fully MVC Compliant

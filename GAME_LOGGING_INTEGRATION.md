# Game Logging Integration

## ✅ Integration Complete!

The `logs.java` functionality has been successfully integrated into the MVC architecture.

---

## 📝 What Was Done

### 1. **Created GameLogger Class** (`src/model/GameLogger.java`)

- Moved logging functionality from `carte/controller/logs.java` to the Model layer
- Enhanced with `writeGameLogs()` method that includes full game configuration
- Maintains backward compatibility with original `writeLogs()` method

### 2. **Updated Menu Model** (`src/model/Menu.java`)

- Added `GameLogger` instance
- Added constructor to initialize the logger
- Created `logGameSession()` method to log game information

### 3. **Updated MenuController** (`src/controller/MenuController.java`)

- Modified `startGame()` to automatically log game sessions
- Logs are written to `logs/GameLogs.txt` when game starts

---

## 🎯 How It Works

### Flow Diagram:

```
User clicks "Start Game"
    ↓
MenuGUI.startGame()
    ↓
MenuController.startGame()
    ↓
Menu.validateGameStart() ✓
    ↓
Menu.logGameSession("logs/GameLogs.txt")
    ↓
GameLogger.writeGameLogs(...)
    ↓
Game information written to file
```

---

## 📊 What Gets Logged

When a game starts, the following information is logged to `logs/GameLogs.txt`:

```
============================================================
Game Session Log - 2025-12-31 03:23:50
============================================================

Game Configuration:
------------------------------------------------------------
  Total Players: 2
  Number of Bots: 1
  Game Type: Individual

Players:
------------------------------------------------------------
Player 1:
  ID: 1
  Name: John Doe
  Age: 25
  Victories: 10
  Avatar: /avatars/john.png

============================================================
```

---

## 🔧 Key Components

### **GameLogger.java** (Model Layer)

```java
public class GameLogger {
    // Logs complete game session with configuration
    public void writeGameLogs(List<User> users, int nbPlayers,
                             int nbBots, String gameType, String fileName)

    // Logs just player information (backward compatible)
    public void writeLogs(List<User> users, String fileName)

    // Displays logs to console
    public void displayLogs(String fileName)
}
```

### **Menu.java** (Model Layer)

```java
public class Menu {
    private GameLogger gameLogger;

    // Logs the current game session
    public void logGameSession(String fileName) {
        // Creates list with current user
        // Calls gameLogger.writeGameLogs(...)
    }
}
```

### **MenuController.java** (Controller Layer)

```java
public class MenuController {
    public Menu.ValidationResult startGame() {
        // Validates game settings
        if (validation.isValid()) {
            // Logs the game session
            menu.logGameSession("logs/GameLogs.txt");
        }
    }
}
```

---

## 📁 File Locations

### Log Files:

- **User Logs:** `logs/UserLogs.txt` - User registration/selection logs
- **Game Logs:** `logs/GameLogs.txt` - Game session logs (NEW)

### Source Files:

- **GameLogger:** `src/model/GameLogger.java` (NEW)
- **Menu:** `src/model/Menu.java` (Updated)
- **MenuController:** `src/controller/MenuController.java` (Updated)
- **Original logs.java:** `src/carte/controller/logs.java` (Preserved for reference)

---

## 🎮 Usage

### Automatic Logging:

Game sessions are **automatically logged** when you start a game through the GUI:

1. Configure game settings (players, bots, type)
2. Create or select a user
3. Click "Start Game"
4. ✅ Game information is automatically logged to `logs/GameLogs.txt`

### Manual Logging (if needed):

```java
Menu menu = new Menu();
menu.setNbPlayers(2);
menu.setNbBots(1);
menu.setType("Individual");
menu.setCurrentUser(new User("Alice", 25, 10, "/avatars/alice.png"));

// Manually log the session
menu.logGameSession("logs/GameLogs.txt");
```

---

## ✨ Benefits

1. ✅ **Automatic Logging:** Every game session is logged automatically
2. ✅ **Complete Information:** Logs include all game configuration details
3. ✅ **MVC Compliant:** Logger is in Model layer where it belongs
4. ✅ **Timestamped:** Each log entry has a timestamp
5. ✅ **Persistent:** Game history is saved for analysis
6. ✅ **Backward Compatible:** Original `writeLogs()` method still available

---

## 🔍 Example Log Output

After starting a game, check `logs/GameLogs.txt`:

```
============================================================
Game Session Log - 2025-12-31 03:23:50
============================================================

Game Configuration:
------------------------------------------------------------
  Total Players: 4
  Number of Bots: 3
  Game Type: Team

Players:
------------------------------------------------------------
Player 1:
  ID: 42
  Name: Alice Johnson
  Age: 28
  Victories: 15
  Avatar: /avatars/alice.png

============================================================
```

---

## 🚀 Testing

To test the logging functionality:

1. **Run the application:**

   ```bash
   java -cp bin Main
   ```

2. **Create a user** or select an existing one

3. **Configure game settings:**

   - Set number of players
   - Configure bots
   - Choose game type

4. **Start the game**

5. **Check the log file:**
   ```bash
   cat logs/GameLogs.txt
   # or
   type logs\GameLogs.txt
   ```

---

## 📌 Notes

- **Warning Message:** If no user is selected, a warning is printed but the game session is still logged
- **Append Mode:** Logs are appended to the file, preserving history
- **File Creation:** The log file is created automatically if it doesn't exist
- **Error Handling:** IOExceptions are caught and logged to console

---

**Status:** ✅ **Fully Integrated**  
**Log File:** `logs/GameLogs.txt`  
**Architecture:** MVC Compliant  
**Automatic:** Yes - logs on every game start

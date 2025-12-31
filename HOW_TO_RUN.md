# Running the Application

## ✅ Main.java Updated!

The `Main.java` file has been updated to properly launch the MenuGUI using the MVC architecture.

---

## 🚀 How to Run

### Option 1: Run Main.java (Recommended)

```bash
# Compile (if needed)
javac -d bin -sourcepath src src/Main.java

# Run
java -cp bin Main
```

### Option 2: Run MenuGUI directly

```bash
java -cp bin view.MenuGUI
```

### Option 3: Run Console Test Version

```bash
java -cp bin test.GameConsoleTest
```

---

## 📝 What Main.java Does

```java
import javax.swing.SwingUtilities;
import view.MenuGUI;

public class Main {
    public static void main(String[] args) {
        // Launch GUI on the Event Dispatch Thread for thread safety
        SwingUtilities.invokeLater(() -> {
            MenuGUI menu = new MenuGUI();
            menu.setVisible(true);
        });
    }
}
```

### Key Points:

- ✅ Uses `SwingUtilities.invokeLater()` for thread-safe GUI initialization
- ✅ Launches the MenuGUI (View layer)
- ✅ MenuGUI automatically creates MenuController and Menu (MVC pattern)
- ✅ Follows Java Swing best practices

---

## 🔄 Application Flow

```
Main.java
    ↓
Launches MenuGUI (View)
    ↓
MenuGUI creates:
    - Menu (Model)
    - MenuController (Controller)
    ↓
MVC Architecture Ready!
```

---

## 🎮 Using the Menu

Once the application starts, you can:

1. **Adjust Players:** Use the slider to set number of players (1-6)
2. **Set Game Type:** Choose between Individual or Team
3. **Configure Bots:** Click "Complete with Bots" to add AI players
4. **Create User:** Click "Create New User" to register a player
5. **Select User:** Click "Select a User" to load an existing player
6. **Start Game:** Click "Start Game" when ready (validates settings)
7. **Exit:** Click "Exit" to close the application

---

## ✨ Features

- **Full-width window** with maximized display
- **User management** with persistent logs
- **Bot configuration** for single-player mode
- **Settings validation** before game start
- **Clean MVC architecture** for maintainability

---

## 🐛 Troubleshooting

### If the window doesn't appear:

```bash
# Make sure you're in the project root directory
cd c:\Users\JL\OneDrive\UTBM\Cours\FISE-INFO\AP4\AP4A_Java\Projet_Trio\projet_ap4b

# Recompile everything
javac -d bin -sourcepath src src/Main.java src/model/*.java src/controller/*.java src/view/*.java

# Run again
java -cp bin Main
```

### If you get "logs/UserLogs.txt not found":

```bash
# Create the logs directory
mkdir logs
```

---

## 📚 Related Files

- `Main.java` - Application entry point
- `src/view/MenuGUI.java` - GUI View
- `src/controller/MenuController.java` - Controller
- `src/model/Menu.java` - Model
- `src/test/GameConsoleTest.java` - Console test version

---

**Status:** ✅ Ready to Run!  
**Entry Point:** `Main.java`  
**Architecture:** MVC Pattern

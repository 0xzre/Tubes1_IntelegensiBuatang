import javafx.scene.control.Button;

public abstract class Bot {
    protected static final int ROW = 8;
    protected static final int COL = 8;

    protected int updateGameBoard(boolean isBot, int i, int j, Button[][] buttons) {
        // Value of indices to control the lower/upper bound of rows and columns
        // in order to change surrounding/adjacent X's and O's only on the game board.
        // Four boundaries:  First & last row and first & last column.

        int startRow, endRow, startColumn, endColumn;

        if (i - 1 < 0)     // If clicked button in first row, no preceding row exists.
            startRow = i;
        else               // Otherwise, the preceding row exists for adjacency.
            startRow = i - 1;

        if (i + 1 >= ROW)  // If clicked button in last row, no subsequent/further row exists.
            endRow = i;
        else               // Otherwise, the subsequent row exists for adjacency.
            endRow = i + 1;

        if (j - 1 < 0)     // If clicked on first column, lower bound of the column has been reached.
            startColumn = j;
        else
            startColumn = j - 1;

        if (j + 1 >= COL)  // If clicked on last column, upper bound of the column has been reached.
            endColumn = j;
        else
            endColumn = j + 1;


        // Search for adjacency for X's and O's or vice versa, and replace them.
        // Update scores for X's and O's accordingly.
        int difference = 0;
        for (int x = startRow; x <= endRow; x++) {
            difference += this.setPlayerScore(isBot, x, j, buttons);
        }

        for (int y = startColumn; y <= endColumn; y++) {
            difference += this.setPlayerScore(isBot, i, y, buttons);
        }
        return difference;
    }

    protected int setPlayerScore(boolean isBot, int i, int j, Button[][] buttons) {
        if (isBot) {
            if (buttons[i][j].getText().equals("X")) {
                buttons[i][j].setText("O");
                return 1;
            }
        } else if (buttons[i][j].getText().equals("O")) {
            buttons[i][j].setText("X");
            return 1;
        }
        return 0;
    }

    public abstract int[] move(int roundsLeft, boolean isBotFirst, int playerOScore, int playerXScore, Button[][] buttons, boolean isMaximizingX);
}
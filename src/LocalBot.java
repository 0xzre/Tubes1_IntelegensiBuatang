import javafx.scene.control.Button;

public class LocalBot extends Bot {
    @Override
    public int[] move(int roundsLeft, boolean isBotFirst, int playerOScore, int playerXScore, Button[][] buttons,
                      boolean isMaximizingX) {

        int curPlayerOScore = playerOScore;
        int curPlayerXScore = playerXScore;
        Button[][] curButtons = new Button[ROW][COL];
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                curButtons[i][j] = new Button(buttons[i][j].getText()); // Create a new Button with the same properties
            }
        }

        int[] allValues;
        int[] rowValues;
        int[] colValues;
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                if (curButtons[i][j].getText().equals("")) {
                    if (isMaximizingX) {
                        curButtons[i][j].setText("X");
                    } else {
                        curButtons[i][j].setText("O");
                    }
                }
                int difference = updateGameBoard(true, i, j, curButtons);
                if (isMaximizingX) {
                    curPlayerXScore += (difference + 1);
                    curPlayerOScore -= difference;
                } else {
                    curPlayerOScore += (difference + 1);
                    curPlayerXScore -= difference;
                }
            }
        }
        int[] bestValues = new int[3];

        return new int[]{bestValues[1], bestValues[2]};
    }
}

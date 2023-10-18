import javafx.scene.control.Button;

public class MinimaxBot extends Bot {
    private int alpha = -64;
    private int beta = 64;

    @Override
    public int[] move(int roundsLeft, boolean isBotFirst, int playerOScore, int playerXScore, Button[][] buttons,
                      boolean isMaximizingX) {
        int maxDepth = roundsLeft * 2 - 1;

        int curDepth = 0;
        int[] maxValues = max(playerOScore, playerXScore, curDepth, maxDepth, buttons, isBotFirst, isMaximizingX);

        this.alpha = -64;
        this.beta = 64;
        return new int[]{maxValues[1], maxValues[2]};
    }

    public int[] max(int playerOScore, int playerXScore, int curDepth, int maxDepth, Button[][] buttons, boolean isBotFirst,
                     boolean isMaximizingX) {
        int[] maxValues = new int[3];
        int maxVal = -64;
        int maxRow = -1;
        int maxCol = -1;

        int curPlayerOScore = playerOScore;
        int curPlayerXScore = playerXScore;

        if (curDepth == maxDepth || curDepth == 5) {
            if (isMaximizingX) {
                maxValues[0] = curPlayerXScore - curPlayerOScore;
            } else {
                maxValues[0] = curPlayerOScore - curPlayerXScore;
            }
            maxValues[1] = maxRow;
            maxValues[2] = maxCol;
            return maxValues;
        }

        Button[][] curButtons = new Button[ROW][COL];
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                curButtons[i][j] = new Button(buttons[i][j].getText()); // Create a new Button with the same properties
            }
        }

        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                if (curButtons[i][j].getText().equals("")) {
                    if (isMaximizingX) {
                        curButtons[i][j].setText("X");
                    } else {
                        curButtons[i][j].setText("O");
                    }

                    int difference = updateGameBoard(true, i, j, curButtons);
                    if (isMaximizingX) {
                        curPlayerXScore += (difference + 1);
                        curPlayerOScore -= difference;
                    } else {
                        curPlayerOScore += (difference + 1);
                        curPlayerXScore -= difference;
                    }

                    int[] minValues = min(curPlayerOScore, curPlayerXScore, curDepth + 1, maxDepth, curButtons, isBotFirst, isMaximizingX);

                    curPlayerOScore = playerOScore;
                    curPlayerXScore = playerXScore;
                    curButtons = new Button[ROW][COL];
                    for (int k = 0; k < ROW; k++) {
                        for (int l = 0; l < COL; l++) {
                            curButtons[k][l] = new Button(buttons[k][l].getText()); // Create a new Button with the same properties
                        }
                    }

                    if (minValues[0] > maxVal) {
                        maxVal = minValues[0];
                        maxRow = i;
                        maxCol = j;
                    }
                    if (maxVal >= beta) {
                        maxValues[0] = maxVal;
                        maxValues[1] = maxRow;
                        maxValues[2] = maxCol;
                        return maxValues;
                    }
                    if (maxVal > alpha) {
                        alpha = maxVal;
                    }
                }
            }
        }
        maxValues[0] = maxVal;
        maxValues[1] = maxRow;
        maxValues[2] = maxCol;
        return maxValues;
    }

    public int[] min(int playerOScore, int playerXScore, int curDepth, int maxDepth, Button[][] buttons, boolean isBotFirst,
                     boolean isMaximizingX) {
        int[] minValues = new int[3];
        int minVal = 64;
        int minRow = -1;
        int minCol = -1;

        int curPlayerOScore = playerOScore;
        int curPlayerXScore = playerXScore;

        if (curDepth == maxDepth || curDepth == 5) {
            if (isMaximizingX) {
                minValues[0] = curPlayerXScore - curPlayerOScore;
            } else {
                minValues[0] = curPlayerOScore - curPlayerXScore;
            }
            minValues[1] = minRow;
            minValues[2] = minCol;
            return minValues;
        }

        Button[][] curButtons = new Button[ROW][COL];
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                curButtons[i][j] = new Button(buttons[i][j].getText()); // Create a new Button with the same properties
            }
        }

        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                if (curButtons[i][j].getText().equals("")) {
                    if (isMaximizingX) {
                        curButtons[i][j].setText("X");
                    } else {
                        curButtons[i][j].setText("O");
                    }

                    int difference = updateGameBoard(true, i, j, curButtons);
                    if (isMaximizingX) {
                        curPlayerXScore -= difference;
                        curPlayerOScore += (difference + 1);
                    } else {
                        curPlayerOScore -= difference;
                        curPlayerXScore += (difference + 1);
                    }

                    int[] maxValues = max(curPlayerOScore, curPlayerXScore, curDepth + 1, maxDepth, curButtons, isBotFirst, isMaximizingX);

                    curPlayerOScore = playerOScore;
                    curPlayerXScore = playerXScore;
                    curButtons = new Button[ROW][COL];
                    for (int k = 0; k < ROW; k++) {
                        for (int l = 0; l < COL; l++) {
                            curButtons[k][l] = new Button(buttons[k][l].getText()); // Create a new Button with the same properties
                        }
                    }

                    if (maxValues[0] < minVal) {
                        minVal = maxValues[0];
                        minRow = i;
                        minCol = j;
                    }
                    if (minVal <= alpha) {
                        minValues[0] = minVal;
                        minValues[1] = minRow;
                        minValues[2] = minCol;
                        return minValues;
                    }
                    if (minVal < beta) {
                        beta = minVal;
                    }
                }
            }
        }
        minValues[0] = minVal;
        minValues[1] = minRow;
        minValues[2] = minCol;
        return minValues;
    }
}

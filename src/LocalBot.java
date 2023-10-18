import javafx.scene.control.Button;

import java.util.*;

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

        List<List<Integer>> objectiveValues = new ArrayList<>();
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                if (curButtons[i][j].getText().equals("")) {
                    if (isMaximizingX) {
                        curButtons[i][j].setText("X");
                    } else {
                        curButtons[i][j].setText("O");
                    }
                    int difference = updateGameBoard(true, i, j, curButtons);
                    int curValue;
                    if (isMaximizingX) {
                        curPlayerXScore += (difference + 1);
                        curValue = curPlayerXScore-playerXScore;
                    } else {
                        curPlayerOScore += (difference + 1);
                        curValue = curPlayerOScore-playerOScore;
                    }
                    objectiveValues.add(List.of(curValue, i, j));
                }

                restoreButtons(curButtons, buttons);
                curPlayerOScore = playerOScore;
                curPlayerXScore = playerXScore;
            }
        }

        filterObjectiveValues(objectiveValues);
        return getRandomBestValues(objectiveValues);
    }

    public void restoreButtons(Button[][] curButtons, Button[][] backUpButtons) {
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                curButtons[i][j] = new Button(backUpButtons[i][j].getText()); // Create a new Button with the same properties
            }
        }
    }

    public void printValues(ArrayList<Integer> array) {
        System.out.println("Values: ");
        for (int value : array) {
            System.out.print(value + " ");
        }
    }

    public void filterObjectiveValues(List<List<Integer>> objectiveValues) {
        int maxLeftValue = objectiveValues.stream()
                .max(Comparator.comparing(triplet -> triplet.get(0)))
                .map(triplet -> triplet.get(0))
                .orElse(Integer.MIN_VALUE);
        objectiveValues.removeIf(triplet -> triplet.get(0) != maxLeftValue);
    }

    public int[] getRandomBestValues(List<List<Integer>> objectiveValues) {
        Collections.shuffle(objectiveValues, new Random());
        int[] bestValues = new int[3];
        if (!objectiveValues.isEmpty()) {
            List<Integer> randomTriplet = objectiveValues.get(0);
            for (int i = 0; i < 3 && i < randomTriplet.size(); i++) {
                bestValues[i] = randomTriplet.get(i);
            }
        }
        // Return a subset of bestValues
        return new int[]{bestValues[1], bestValues[2]};
    }
}

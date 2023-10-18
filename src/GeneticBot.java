
import javafx.scene.control.Button;

import java.sql.SQLOutput;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
public class GeneticBot extends Bot{
    private static final int ROW = 8;
    private static final int COL = 8;
    private static Instant startTime;
    private final int POPULATION_COUNT = 20;
    private final int maxDepthCheck = 5;
    private final double MUTATION_CHANCE = 0.5;
    private final int GENERATION_COUNT= 10;
    private int[][] availableMoves;
    private int selectedDepth;

    private Bot local = new LocalBot();
    private int localRoundsLeft;
    private boolean localIsBotFirst;
    private int localPlayerOScore;
    private int localPlayerXScore;
    private Button[][] localButtons;
    private boolean localIsMaximizingX;

    @Override
    public int[] move(int roundsLeft, boolean isBotFirst, int playerOScore, int playerXScore, Button[][] buttons,
                      boolean isMaximizingX) {
        startTime = Instant.now();
        this.localRoundsLeft = roundsLeft;
        this.localIsBotFirst = isBotFirst;
        this.localPlayerOScore = playerOScore;
        this.localPlayerXScore = playerXScore;
        this.localButtons = buttons;
        this.localIsMaximizingX = isMaximizingX;
        int maxDepth = roundsLeft;
        availableMoves = getAvailableMoves(buttons);

        selectedDepth = min(maxDepth,maxDepthCheck);

        int curDepth = 0;

        int[] value = genetic();
        return value;
    }

    public int min(int x, int y) {
        if (x<y) {
            return x;
        }
        return y;
    }

    public boolean contains(int[][] arr, int[] sub) {
        for (int[] a : arr) {
            if (Arrays.equals(sub,a)) {
                return true;
            }
        }
        return false;
    }
    public int[][] getAvailableMoves(Button[][] buttons) {
        int[][] res = new int[56][];
        int index = 0;
        for (int i = 0; i<ROW;i++) {
            for (int j = 0; j<COL; j++) {
                if (buttons[i][j].getText().equals("")) {
                    res[index] = new int[2];
                    res[index][0] = i;
                    res[index][1] = j;

                    index++;
                }
            }
        }

        return Arrays.copyOf(res,index);
    }

    public static int[][][] cross(int[][] arr1, int[][] arr2, int cpoint) {
        int[][] offspring1 = new int[arr1.length][];
        int[][] offspring2 = new int[arr2.length][];

        for (int i = 0; i < cpoint; i++) {
            offspring1[i] = arr1[i];
            offspring2[i] = arr2[i];
        }

        for (int i = cpoint; i < arr1.length; i++) {
            offspring1[i] = arr2[i];
            offspring2[i] = arr1[i];
        }

        int[][][] offspring = { offspring1, offspring2 };
        return offspring;
    }

    public int[][] getPartition(int[][] arr, int point){
        if (point+1<arr.length) {
            int[][] res = new int[arr.length - point + 1][];
            for (int i = point + 1; i < arr.length; i++) {
                res[i-(point+1)] = new int[2];
                res[i-(point+1)][0] = arr[i][0];
                res[i-(point+1)][1] = arr[i][1];
            }
            return res;
        }
        int[][] res = new int[0][2];
        return res;
    }

    public int fitnessValue(int[][] arr) {
        Button[][] curButtons = new Button[ROW][COL];
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                curButtons[i][j] = new Button(this.localButtons[i][j].getText()); // Create a new Button with the same properties
            }
        }
        int eval = 0;
        int difference = 0;
        for (int i = 0; i<selectedDepth;i++) {

            if (this.localIsMaximizingX) {
                curButtons[(arr[i][0])][(arr[i][1])].setText("X");
            } else {
                curButtons[(arr[i][0])][(arr[i][1])].setText("O");
            }
            difference = updateGameBoard(true, (arr[i][0]), (arr[i][1]), curButtons);
            int[] b = new int[2];
            b = this.local.move(this.localRoundsLeft, this.localIsBotFirst, this.localPlayerOScore, this.localPlayerXScore,
                    curButtons, !(this.localIsMaximizingX));


            if (contains(getPartition(arr,i),b)) {
                return 0;
            }
            if (this.localIsMaximizingX) {
                curButtons[(b[0])][(b[1])].setText("O");
            } else {
                curButtons[(b[0])][(b[1])].setText("X");
            }
            difference = updateGameBoard(true, (b[0]), (b[1]), curButtons);
        }

        int playerXScore = 0;
        int playerOScore = 0;
        for (int i= 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                if (curButtons[i][j].getText().equals("X")) {
                    playerXScore++;
                } else if (curButtons[i][j].getText().equals("O")) {
                    playerOScore++;
                }
            }
        }
        if (localIsMaximizingX) {
            return playerXScore-playerOScore;
        }

        return playerOScore-playerXScore;
    }

    public void evaluateList(int[][][] arr, int[] list){
        for (int i = 0; i<arr.length;i++) {
            list[i] =fitnessValue(arr[i])+65;
        }
    }

    public int sumList(int[] list) {
        int sum = 0;
        for (int i = 0; i<list.length;i++){
            sum += list[i];
        }

        return sum;
    }

    public int[] selectionlist(int[] list) {
        int[] temp = new int[list.length];
        if (list.length != 0) {
            temp[0] = list[0]-1;
            for (int i=1;i<list.length;i++) {
                if (list[i] != 0) {
                    temp[i] = temp[i-1] + list[i]-1;
                } else {
                    temp[i] = temp[i-1] + list[i];
                }

            }
        }

        return temp;

    }

    public int[][] getOffspring(int[][][] arr, int[] sumList, int sum) {
        int i = 0;
        Random random = new Random();
        int sel = random.nextInt(sum);
        boolean loop = true;
        while (sel>sumList[i] && loop) {
            if (i < sumList.length-1) {
                i++;
            } else {
                loop = false;
            }

        }
        return arr[i];

    }

    public static int getMaxIdx(int[] list) {
        int maxIdx = 0;  // Initialize the index of the maximum value

        for (int i = 1; i < list.length; i++) {
            if (list[i] > list[maxIdx]) {
                maxIdx = i;  // Update the index if a larger value is found
            }
        }

        return maxIdx;
    }

    public int[] genetic() {
        //Generate base population
        int[][][] base = new int[POPULATION_COUNT][selectedDepth][];
        Random random = new Random();

        for (int i=0; i<POPULATION_COUNT;i++) {
            for (int j=0; j<selectedDepth;j++) {
                base[i][j] = new int[2];
                int selectednum = random.nextInt(availableMoves.length);
                base[i][j][0] = availableMoves[selectednum][0];
                base[i][j][1] = availableMoves[selectednum][1];
            }
        }
        //Selection
        int generation_count = 1;
        int[] evalList = new int[POPULATION_COUNT];
        evaluateList(base, evalList);
        int[] sums = selectionlist(evalList);
        int totalsum = sumList(evalList);

        int[][][] offspring = new int[POPULATION_COUNT][selectedDepth][];
        for (int i = 0; i<POPULATION_COUNT;i++) {
            offspring[i] = getOffspring(base,sums,totalsum);
        }


        for (int j = 0; j<Math.floorDiv(offspring.length,2);j++) {
            int[][][] crossed = cross(offspring[j],offspring[j+1],Math.floorDiv(selectedDepth,2));
            offspring[j] = crossed[0];
            offspring[j+1] = crossed[1];
        }

        for (int k = 0; k< offspring.length; k++) {
            if (random.nextInt(101) < MUTATION_CHANCE * 100) {
                int selectednum = random.nextInt(availableMoves.length);
                int mutation_point = random.nextInt(offspring[k].length);
                offspring[k][mutation_point][0] = availableMoves[selectednum][0];
                offspring[k][mutation_point][1] = availableMoves[selectednum][1];
            }
        }


        while (generation_count<GENERATION_COUNT) {
            evaluateList(offspring, evalList);
            sums = selectionlist(evalList);
            totalsum = sumList(evalList);

            for (int i = 0; i<POPULATION_COUNT;i++) {
                offspring[i] = getOffspring(base,sums,totalsum);
            }


            for (int j = 0; j<Math.floorDiv(offspring.length,2);j++) {
                int[][][] crossed = cross(offspring[j],offspring[j+1],Math.floorDiv(selectedDepth,2));
                offspring[j] = crossed[0];
                offspring[j+1] = crossed[1];
            }

            for (int k = 0; k< offspring.length; k++) {
                if (random.nextInt(101) < MUTATION_CHANCE * 100) {
                    int selectednum = random.nextInt(availableMoves.length);
                    int mutation_point = random.nextInt(offspring[k].length);
                    offspring[k][mutation_point][0] = availableMoves[selectednum][0];
                    offspring[k][mutation_point][1] = availableMoves[selectednum][1];
                }
            }

            generation_count++;
        }
        evaluateList(offspring, evalList);
        int maxVal = getMaxIdx(evalList);

        return new int[]{offspring[maxVal][0][0],offspring[maxVal][0][1]};
    }

}

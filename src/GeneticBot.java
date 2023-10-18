
import javafx.scene.control.Button;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
public class GeneticBot extends Bot{
    private static final int ROW = 8;
    private static final int COL = 8;
    private static Instant startTime;
    private final int POPULATION_COUNT = 10;
    private final int maxDepthCheck = 5;
    private final double MUTATION_CHANCE = 0.05;
    private int[][] availableMoves;
    private int selectedDepth;

    @Override
    public int[] move(int roundsLeft, boolean isBotFirst, int playerOScore, int playerXScore, Button[][] buttons) {
        startTime = Instant.now();
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
    public static int[][] append(int[][] matrix, int[] array) {
        int[][] newMatrix = new int[matrix.length + 1][];

        // Copy existing elements
        for (int i = 0; i < matrix.length; i++) {
            newMatrix[i] = matrix[i];
        }

        // Append new array
        newMatrix[matrix.length] = array;

        return newMatrix;
    }
    public int[][] getAvailableMoves(Button[][] buttons) {
        int[][] res = new int[0][];
        for (int i = 0; i<ROW;i++) {
            for (int j = 0; j<COL; j++) {
                if (buttons[i][j].getText()=="") {
                    int[] temp = {i,j};
                    append(res,temp);
                }
            }
        }
        return res;
    }

    public int[] generateRandomMove(int[][] fromArr, int[][] except) {
        int[] selected = new int[];
        Random random = new Random();
        while (!contains(except,selected)) {
            selected = fromArr[random.nextInt(fromArr.length)];
        }
        return selected;
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

    public int[][] mutate(int[][] arr) {
        Random random = new Random();
        int MUTATION_POINT = random.nextInt(arr.length);
        int[][] res = new int[arr.length][];
        for (int i = 0;i<selectedDepth;i++) {
            if (i == MUTATION_POINT) {
                int[] mut = generateRandomMove(availableMoves, arr);
                res[i] = mut;
            } else {
                res[i] = arr[i];
            }
        }

        return res;
    }

    public int[][] getPartition(int[][] arr, int point){
        if (point+1<arr.length) {
            int[][] res = new int[arr.length - point - 1][];
            for (int i = point + 1; i < arr.length; i++) {
                res[i] = arr[i];
            }
            return res;
        }
        int[][] res = new int[0][];
        return res;
    }

    public int fitnessValue(int[][] arr) {
        int eval = 0;

        for (int i = 0; i<selectedDepth;i++) {
            //do a1
            int[] b = {0,0}; //get best action from local
            if (contains(getPartition(arr,i),b)) {
                return 0;
            }
            //do b1
        }
        return 0; //return board evaluation
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
        while (sel>sumList[i]) {
            i++;
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
        for (int i=0; i<POPULATION_COUNT;i++) {
            for (int j=0; j<selectedDepth;j++) {
                base[i][j]=generateRandomMove(availableMoves,base[i]);
            }
        }
        Random random = new Random();
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
                mutate(offspring[k]);
            }
        }

        while (generation_count<15) {
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
                    mutate(offspring[k]);
                }
            }

            generation_count++;
        }
        evaluateList(offspring, evalList);

        int maxVal = getMaxIdx(evalList);

        return offspring[maxVal][0];
    }





}

package lasers.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Safe class to store the data structure.
 */
public class Safe {
    private char[][] safe;
    private int rows;
    private int cols;

    /**
     * Initialize using a safe file.
     * @param safeFile
     * @throws FileNotFoundException
     */
    public Safe(String safeFile) throws FileNotFoundException {
        Scanner scanSafeFile = new Scanner(new File(safeFile));

        int rows = scanSafeFile.nextInt();
        int cols = scanSafeFile.nextInt();

        safe = new char[rows][cols];

        //initialize from a file
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                safe[i][j] = scanSafeFile.next().charAt(0);
            }
        }

        this.rows = rows;
        this.cols = cols;
    }

    /**
     * Add a laser using coordinates.
     * @param r row number
     * @param c column number
     * @return whether success or not.
     */
    public boolean add(int r, int c) {
        if (r < 0 || c < 0 || r >= rows || c >= cols) {
            System.out.printf("Error adding laser at: (%d, %d)\n", r, c);
            display();
            return false;
        }
        if (safe[r][c] != '.' && safe[r][c] != '*') {
            System.out.printf("Error adding laser at: (%d, %d)\n", r, c);
            display();
            return false;
        }

        System.out.printf("Laser added at: (%d, %d)\n", r, c);
        safe[r][c] = 'L';

        updateBeams(r, c);
        display();

        return true;
    }

    /**
     * Display the safe in proper format.
     */
    public void display() {
        System.out.print(" ");
        for (int i = 0; i < cols; i++) {
            System.out.print(" " + i % 10);
        }
        System.out.println();
        System.out.print("  ");
        for (int i = 0; i < cols; i++) {
            if (i == cols - 1) {
                System.out.print("-");
            } else {
                System.out.print("--");
            }
        }
        System.out.println();
        for (int i = 0; i < rows; i++) {
            System.out.print(i % 10 + "|");
            for (int j = 0; j < cols; j++) {
                if (j == cols - 1) {
                    System.out.print(safe[i][j]);
                } else {
                    System.out.print(safe[i][j] + " ");
                }
            }
            System.out.println();
        }
    }

    /**
     * Remove the laser in the coordinate.
     * @param r
     * @param c
     * @return whether success or not.
     */
    public boolean remove(int r, int c) {
        if (r < 0 || c < 0 || r >= rows || c >= cols) {
            System.out.printf("Error removing laser at: (%d, %d)\n", r, c);
            display();
            return false;
        }
        if (safe[r][c] != 'L') {
            System.out.printf("Error removing laser at: (%d, %d)\n", r, c);
            display();
            return false;
        }
        System.out.printf("Laser removed at: (%d, %d)\n", r, c);
        safe[r][c] = '.';
        removeBeams(r, c);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (safe[i][j] == 'L') {
                    updateBeams(i, j);
                }
            }
        }

        display();
        return true;
    }

    /**
     * Verify the grid for three conditions.
     */
    public void verify() {
        boolean verified = true;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (safe[i][j] == '.') {
                    verified = false;
                    System.out.printf("Error verifying at: (%d, %d)\n", i, j);
                    break;
                }
                if (Character.isDigit(safe[i][j])) {
                    int val = Integer.parseInt(String.valueOf(safe[i][j]));

                    if (countLasers(i, j) != val) {
                        verified = false;
                        System.out.printf("Error verifying at: (%d, %d)\n", i, j);
                        break;
                    }
                }
                if (safe[i][j] == 'L') {
                    verified = checkLasers(i, j);
                    if (!verified) {
                        System.out.printf("Error verifying at: (%d, %d)\n", i, j);
                        break;
                    }
                }
            }
            if (!verified) {
                break;
            }
        }

        if (verified) {
            System.out.println("Safe is fully verified!");
        }
        display();
    }

    /**
     * Verify a laser not to have other lasers on the way.
     * @param r
     * @param c
     * @return
     */
    private boolean checkLasers(int r, int c) {
        for (int i = c + 1; i < cols; i++) {
            if (safe[r][i] == '*' || safe[r][i] == '.') {
                continue;
            } else if (safe[r][i] == 'L') {
                return false;
            } else {
                break;
            }
        }
        for (int i = r + 1; i < rows; i++) {
            if (safe[i][c] == '*' || safe[i][c] == '.') {
                continue;
            } else if (safe[i][c] == 'L') {
                return false;
            } else {
                break;
            }
        }

        for (int i = c - 1; i >= 0; i--) {
            if (safe[r][i] == '*' || safe[r][i] == '.') {
                continue;
            } else if (safe[r][i] == 'L') {
                return false;
            } else {
                break;
            }
        }
        for (int i = r - 1; i >= 0; i--) {
            if (safe[i][c] == '*' || safe[i][c] == '.') {
                continue;
            } else if (safe[i][c] == 'L') {
                return false;
            } else {
                break;
            }
        }
        return true;
    }

    /**
     * Remove laser beams from a given laser. Helper for the remove laser method.
     * @param r
     * @param c
     */
    private void removeBeams(int r, int c) {
        for (int i = c + 1; i < cols; i++) {
            if (safe[r][i] == '*' || safe[r][i] == '.') {
                safe[r][i] = '.';
            } else {
                break;
            }
        }
        for (int i = r + 1; i < rows; i++) {
            if (safe[i][c] == '*' || safe[i][c] == '.') {
                safe[i][c] = '.';
            } else {
                break;
            }
        }

        for (int i = c - 1; i >= 0; i--) {
            if (safe[r][i] == '*' || safe[r][i] == '.') {
                safe[r][i] = '.';
            } else {
                break;
            }
        }
        for (int i = r - 1; i >= 0; i--) {
            if (safe[i][c] == '*' || safe[i][c] == '.') {
                safe[i][c] = '.';
            } else {
                break;
            }
        }
    }

    /**
     * Update all the beams after removing a laser. Helper for removing a laser.
     * @param r
     * @param c
     */
    private void updateBeams(int r, int c) {
        for (int i = c + 1; i < cols; i++) {
            if (safe[r][i] == '*' || safe[r][i] == '.') {
                safe[r][i] = '*';
            } else {
                break;
            }
        }
        for (int i = r + 1; i < rows; i++) {
            if (safe[i][c] == '*' || safe[i][c] == '.') {
                safe[i][c] = '*';
            } else {
                break;
            }
        }

        for (int i = c - 1; i >= 0; i--) {
            if (safe[r][i] == '*' || safe[r][i] == '.') {
                safe[r][i] = '*';
            } else {
                break;
            }
        }
        for (int i = r - 1; i >= 0; i--) {
            if (safe[i][c] == '*' || safe[i][c] == '.') {
                safe[i][c] = '*';
            } else {
                break;
            }
        }
    }

    /**
     * Count the number of lasers around a tile. Helper for verify.
     * @param r
     * @param c
     * @return
     */
    private int countLasers(int r, int c) {
        int count = 0;
        if (r > 0) {
            if (safe[r - 1][c] == 'L') {
                count++;
            }
        }
        if (r < rows - 1) {
            if (safe[r + 1][c] == 'L') {
                count++;
            }
        }
        if (c > 0) {
            if (safe[r][c - 1] == 'L') {
                count++;
            }
        }
        if (c < cols - 1) {
            if (safe[r][c + 1] == 'L') {
                count++;
            }
        }
        return count;
    }
}
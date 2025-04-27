/* *****************************************************************************
 *  Name: SiYingLiu
 *  Date: Apr/24/2025
 *  Description: Princeton University algorithms 4 coursera assigment: Slider Puzzle
 **************************************************************************** */

import java.util.ArrayList;

public class Board {
    private int[][] tiles;
    private int blankSquarei, blankSquarej, n;

    // n-n arr of tiles, tiles[row][column] = tile at (row,col)
    public Board(int[][] tiles) {
        if (tiles == null) throw new NullPointerException();
        if (tiles.length != tiles[0].length) throw new IllegalArgumentException();
        n = tiles.length;
        if (n < 2 || n >= 128) throw new IndexOutOfBoundsException();
        this.tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) {
                this.tiles[i][j] = tiles[i][j];
                if (this.tiles[i][j] < 0 || this.tiles[i][j] > n * n - 1)
                    throw new IllegalArgumentException(
                            "The number is less than 0 or greater than n*n - 1");
                if (this.tiles[i][j] == 0) {
                    blankSquarei = i;
                    blankSquarej = j;
                }
            }
    }

    // board dimension n
    public int dimension() {
        return n;
    }

    // string representation of this board
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(n).append("\n");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++)
                s.append(String.format(" %2d", tiles[i][j]));
            s.append("\n");
        }
        return s.toString();
    }

    // number of tiles out of place
    public int hamming() {
        int hammingDistance = 0;
        int expected = 1;
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) {
                if (tiles[i][j] != expected && tiles[i][j] != 0) hammingDistance++;
                expected++;
            }
        return hammingDistance;
    }

    // sum of Mahattan distance between tildes and goal
    public int manhattan() {
        int manhattanDistance = 0;
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) {
                int t = tiles[i][j];
                if (t == 0) continue;
                int goalRow = (t - 1) / n;
                int goalCol = (t - 1) % n;
                manhattanDistance += Math.abs(i - goalRow) + Math.abs(j - goalCol);
            }
        return manhattanDistance;
    }

    public boolean isGoal() {
        int expected = 1;
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) {
                if (i == n - 1 && j == n - 1) {
                    if (tiles[i][j] != 0) return false;
                }
                else {
                    if (tiles[i][j] != expected) return false;
                }
                expected++;
            }
        return true;
    }

    // is this board equal y?
    public boolean equals(Object y) {
        if (y == this) return true;
        if (y == null || y.getClass() != this.getClass()) return false;
        Board that = (Board) y;
        if (that.n != this.n) return false;
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                if (this.tiles[i][j] != that.tiles[i][j]) return false;
        return true;
    }

    public Iterable<Board> neighbors() {
        int blankRow = blankSquarei, blankCol = blankSquarej;
        ArrayList<Board> neighbors = new ArrayList<>();

        int[][] directions = {
                { -1, 0 }, // up
                { 1, 0 }, // down
                { 0, -1 }, // left
                { 0, 1 } // right
        }; // {row, col}

        for (int[] dir : directions) {
            int newRow = blankRow + dir[0];
            int newCol = blankCol + dir[1];

            if (newRow >= 0 && newRow < n && newCol >= 0 && newCol < n) {
                int[][] copy = copyTiles();
                swap(copy, blankRow, blankCol, newRow, newCol);
                neighbors.add(new Board(copy));
            }
        }
        return neighbors;
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {
        int[][] twin = copyTiles();
        if (twin[0][0] != 0 && twin[0][1] != 0)
            swap(twin, 0, 0, 0, 1);
        else swap(twin, 1, 0, 1,
                  1); // Since the board contains exactly one blank square, if either (0,0) or (0,1) is 0, then all squares except the blank must be nonzero.
        return new Board(twin);
    }

    private int[][] copyTiles() {
        int[][] copy = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                copy[i][j] = tiles[i][j];
        return copy;
    }

    private void swap(int[][] titles, int originalRow, int originalCol, int newRow, int newCol) {
        int temp = titles[originalRow][originalCol];
        titles[originalRow][originalCol] = titles[newRow][newCol];
        titles[newRow][newCol] = temp;
    }

    public static void main(String[] args) {
        int[][] tiles1 = {
                { 1, 2, 3 },
                { 4, 5, 6 },
                { 7, 8, 0 }
        };
        Board goal = new Board(tiles1);
        System.out.println("Goal: " + goal);
        assert goal.isGoal();

        int[][] tiles2 = {
                { 1, 0, 3 },
                { 4, 2, 5 },
                { 7, 8, 6 }
        };
        Board b = new Board(tiles2);
        System.out.println("Board b: " + b);
        assert b.hamming() == 3 : "Hamming dis = 3";
        assert b.manhattan() == 3 : "Manhattan dis = 3";

        Iterable<Board> nb = b.neighbors();
        int count = 0;
        for (Board n : nb) {
            System.out.println("Neighbor " + (++count) + ":\n" + n);
        }
        assert count == 3 : "空格在 (0,1)，neighbors 数量应为 3 (下左右)";

        Board t = b.twin();
        System.out.println("Twin of b:\n" + t);
        assert !t.equals(b) : "twin() 应该产生一个不同于原棋盘的新棋盘";

        System.out.println("All tests passed!");
    }
}

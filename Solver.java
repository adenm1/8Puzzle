/* *****************************************************************************
 *  Name: SiYingLiu
 *  Date: Apr/28/2025
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;

public class Solver {
    private class SearchNode implements Comparable<SearchNode> {
        private Board board;
        private int moves;
        private SearchNode prev;

        public SearchNode(Board board, int moves, SearchNode prev) {
            this.board = board;
            this.moves = moves;
            this.prev = prev;
        }

        private int priority() {
            return moves + board.manhattan();
        }

        public int compareTo(SearchNode that) {
            return compare(this.priority(), that.priority());
        }

        private int compare(int a, int b) {
            if (a < b) return -1;
            if (a == b) return 0;
            else return 1;
        }
    }

    private SearchNode finalNode;

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null) throw new IllegalArgumentException("Initial board is empty");
        MinPQ<SearchNode> minPQ = new MinPQ<SearchNode>();
        MinPQ<SearchNode> twinPQ = new MinPQ<SearchNode>();
        minPQ.insert(new SearchNode(initial, 0, null));
        twinPQ.insert(new SearchNode(initial.twin(), 0, null));
        while (true) {
            SearchNode currentNode = minPQ.delMin();
            if (currentNode.board.isGoal()) {
                this.finalNode = currentNode;
                break;
            }
            else {
                for (Board neighbor : currentNode.board.neighbors()) {
                    if (currentNode.prev == null || !neighbor.equals(currentNode.prev.board)) {
                        minPQ.insert(new SearchNode(neighbor, currentNode.moves + 1, currentNode));
                    }
                }
                SearchNode twinCurrent = twinPQ.delMin();
                if (twinCurrent.board.isGoal()) {
                    finalNode = null; // Unsolvable
                    break;
                }
                else {
                    for (Board neighbor : twinCurrent.board.neighbors()) {
                        if (twinCurrent.prev == null || !neighbor.equals(twinCurrent.prev.board))
                            twinPQ.insert(
                                    new SearchNode(neighbor, twinCurrent.moves + 1, twinCurrent));
                    }
                }
            }
        }
    }

    // is the initial board solvable? (see below)
    public boolean isSolvable() {
        return finalNode != null;
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        if (!isSolvable()) return -1;
        else return finalNode.moves;
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        if (!isSolvable()) return null;
        SearchNode node = finalNode;
        Stack<Board> boards = new Stack<>();
        while (node != null) {
            boards.push(node.board);
            node = node.prev;
        }
        return boards;
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);
        Solver solver = new Solver(initial);

        if (!solver.isSolvable()) System.out.println("No solution");
        else {
            System.out.println("Min num of moves = " + solver.moves());
            for (Board b : solver.solution())
                System.out.println(b);
        }
    }
}

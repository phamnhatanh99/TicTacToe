import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TicTacToe {
    // Some constants that we will be using
    public static final String EX = "x";
    public static final String OH = "o";
    public static final String BLANK = "";
    private final List<String> win_X_1      = Arrays.asList(EX, EX, EX);
    private final List<String> win_X_2      = Arrays.asList(OH, EX, EX, EX, EX);
    private final List<String> win_X_3      = Arrays.asList(EX, EX, EX, EX, OH);
    private final List<String> blocked_X_1  = Arrays.asList(OH, EX, EX, EX, OH);
    private final List<String> blocked_X_2  = Arrays.asList(OH, EX, EX, EX);
    private final List<String> blocked_X_3  = Arrays.asList(EX, EX, EX, OH);
    private final List<String> win_O_1      = Arrays.asList(OH, OH, OH);
    private final List<String> win_O_2      = Arrays.asList(OH, OH, OH, OH, EX);
    private final List<String> win_O_3      = Arrays.asList(EX, OH, OH, OH, OH);
    private final List<String> blocked_O_1  = Arrays.asList(EX, OH, OH, OH, EX);
    private final List<String> blocked_O_2  = Arrays.asList(OH, OH, OH, EX);
    private final List<String> blocked_O_3  = Arrays.asList(EX, OH, OH, OH);

    private final int size;          // Length of one side of board. E.g. size 3 => 3x3 board
    private int fill_count;          // Counter to keep track of how many spots have been filled (in case of draw)
    private final String[][] board;        // Board is represented as a 2D array
    private final Computer bot;            // Bot player

    // Constructor, initialize a board with the given size and fill all entries with the blank string
    // Also initialize a new computer player
    public TicTacToe(int n) {
        if (n > 7)
            throw new RuntimeException("Maximum board size allowed is 7!");
        board = new String[n][n];
        size = n;
        fillBlank(board);
        fill_count = 0;
        bot = new Computer(this);
        System.out.println("New game of size " + n + " was created");
    }

    // Getter method
    public String[][] getBoard() {
        return board;
    }

    // Getter method
    public int getSize() {
        return size;
    }

    // Mark a spot with either X or O, then check whether X or O at that position would win
    // Throw exception if marker is not X or O
    // Throw AlreadyMarkedException is someone tries to mark an already marked spot
    // Throw DrawException if the board is full after marking and no one wins
    public synchronized boolean mark(String marker, int row, int col) {
        if (!marker.equals(EX) && !marker.equals(OH))
            throw new RuntimeException("Invalid marker! Either X or O!");

        if (!board[row][col].equals(BLANK))
            throw new AlreadyMarkedException("This spot has already been marked! Catch me and ignore this action!");

        board[row][col] = marker;
        fill_count += 1;

        System.out.println("Player " + marker + " has marked (" + row + ", " + col + ")");

        boolean result = checkWinning(marker, row, col);

        if (fill_count == size * size && !result)
            throw new DrawException("It's a draw! Catch me and display a draw screen!");

        if (result && marker.equals(EX)) {
            System.out.println("Player " + marker + " won!");
            return true;
        }

        if (result)
            throw new BotWonException();

        if (marker.equals(EX))
            bot.play(row, col);

        return false;
    }

    // Check whether X or O win at the given coordinate. This should be performed after every marking.
    public boolean checkWinning(String mark, int row, int col) {
        List<String> horizontal     = Arrays.asList(board[row]);
        List<String> vertical       = getVertical(col);
        List<String> diagonal       = getDiagonal(row, col);
        List<String> anti_diagonal  = getAntiDiagonal(row, col);

        boolean checkHorizontal     = checkLine(mark, horizontal);
        boolean checkVertical       = checkLine(mark, vertical);
        boolean checkDiagonal       = checkLine(mark, diagonal);
        boolean checkAntiDiagonal   = checkLine(mark, anti_diagonal);

        return checkHorizontal || checkVertical || checkDiagonal || checkAntiDiagonal;
    }

    // Helper functions:

    // https://stackoverflow.com/questions/29703847/can-i-use-arrays-fill-with-a-2d-array-if-so-how-do-i-do-that
    // Fill a matrix with the blank string
    private void fillBlank(String[][] arr) {
        for (String[] strings : arr) {
            Arrays.fill(strings, BLANK);
        }
    }

    // https://stackoverflow.com/questions/1111998/how-to-get-a-column-from-a-2d-java-array
    // Returns the specified column from a matrix as a list, entries are from top to bottom
    private List<String> getVertical(int col) {
        ArrayList<String> res = new ArrayList<>();

        for (int row = 0; row < size; row++) {
            res.add(board[row][col]);
        }

        return res;
    }

    // Return the diagonal that the specified element belongs to as a list, entries are from top left to bottom right
    private List<String> getDiagonal(int row, int col) {
        String entry = board[row][col];
        ArrayList<String> pre  = new ArrayList<>();     // List for entries before current entry
        ArrayList<String> post = new ArrayList<>();     // List for entries after current entry

        int i = row;
        int j = col;
        while (i != 0 && j != 0) {
            i--;
            j--;
            pre.add(board[i][j]);
        }
        Collections.reverse(pre);       // Have to reverse because we are going from bottom up top
        pre.add(entry);                 // Add current entry to the end

        i = row;
        j = col;
        while (i != size - 1 && j != size - 1) {
            i++;
            j++;
            post.add(board[i][j]);
        }                               // No reverse since we are already going from top to bottom
        pre.addAll(post);               // Add the rest to the end of the first list

        return pre;
    }

    // Return the anti diagonal that the specified element belongs to as a list, entries are from top right to bottom left
    private List<String> getAntiDiagonal(int row, int col) {
        String entry = board[row][col];
        ArrayList<String> pre  = new ArrayList<>();     // List for entries before current entry
        ArrayList<String> post = new ArrayList<>();     // List for entries after current entry

        int i = row;
        int j = col;
        while (i != 0 && j != size - 1) {
            i--;
            j++;
            pre.add(board[i][j]);
        }
        Collections.reverse(pre);       // Have to reverse because we are going from bottom up top
        pre.add(entry);                 // Add current entry to the end

        i = row;
        j = col;
        while (i != size - 1 && j != 0) {
            i++;
            j--;
            post.add(board[i][j]);
        }                               // No reverse since we are already going from top to bottom
        pre.addAll(post);               // Add the rest to the end of the first list

        return pre;
    }

    // Private methods:

    // Check whether X or O win at given line
    // Logic: When the board is larger than 3x3, if your marks are blocked by the opposite mark, you have to
    // have 4 marks instead. So the only possible winning solutions are XXXXO, OXXXX, OOOOX, XOOOO, XXX, OOO.
    // Knowing this we can check from the biggest set to smallest set to see if any of those solutions exist.
    private boolean checkLine(String mark, List<String> line) {
        switch (mark) {
            case EX -> {
                // Case XXXXO or OXXXX
                if (Collections.indexOfSubList(line, win_X_2) != -1 || Collections.indexOfSubList(line, win_X_3) != -1)
                    return true;
                // Case OXXXO / OXXX_ / _XXXO, then X cannot win
                if (Collections.indexOfSubList(line, blocked_X_1) != -1 ||
                    Collections.indexOfSubList(line, blocked_X_2) != -1 ||
                    Collections.indexOfSubList(line, blocked_X_3) != -1)
                    return false;
                // Case XXX, with them not being blocked by any O
                return Collections.indexOfSubList(line, win_X_1) != -1;
            }
            case OH -> {
                // Case OOOOX or XOOOO
                if (Collections.indexOfSubList(line, win_O_2) != -1 || Collections.indexOfSubList(line, win_O_3) != -1)
                    return true;
                // Case XOOOX / _OOOX / XOOO_, then O cannot win
                if (Collections.indexOfSubList(line, blocked_O_1) != -1 ||
                    Collections.indexOfSubList(line, blocked_O_2) != -1 ||
                    Collections.indexOfSubList(line, blocked_O_3) != -1)
                    return false;
                // Case OOO, with them not being blocked by any X
                return Collections.indexOfSubList(line, win_O_1) != -1;
            }
        }
        // This should never be reached, since all the possible options are already checked above
        return false;
    }
}
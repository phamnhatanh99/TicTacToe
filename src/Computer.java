import java.util.HashSet;
import java.util.Random;
import java.util.Set;

// A simple computer player
// Strategy: Whenever a new X is placed, it will check for a radius of 2 surrounding that new X to see whether
// or not there is another X nearby. If there is, it will randomly block one of the open spaces near the X to
// prevent a cluster of XXX to be formed.
public class Computer {

    // Coordinates are stored as a string of length 2, with the first character being the row index and
    // the second one the column index. This is possible because the maximum allowed board size is 7
    // which is single digit. This allows for easy coordinate comparison without having to implement
    // a new class
    private final Set<String> blank_space = new HashSet<>();     // To keep track of all blank spaces ;)
    private final Set<String> marked_O    = new HashSet<>();      // To keep track of all O-marked spaces
    private final Set<String> marked_X    = new HashSet<>();

    private final TicTacToe game;
    private final int size;

    // Constructor, also fill the blankSpace map with all coordinates of the board
    public Computer(TicTacToe game) {
        this.game = game;
        size = game.getSize();

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                blank_space.add(toCoordinate(row, col));
            }
        }
    }

    // Make a move. Arguments are the coordinate of the last placed X
    public void play(int row, int col) {

        marked_X.add(toCoordinate(row, col));

        if (marked_O.isEmpty()) {
            playRandom(blank_space);
        }
        else {
            Set<String> possible_moves = possibleMoves(row, col);
            if (possible_moves.isEmpty()) {
                playRandom(blank_space);
            } else {
                playRandom(possible_moves);
            }
        }
    }

    // Make a random move within a given amount of options
    private void playRandom(Set<String> set) {
        set.removeAll(marked_X);
        String coordinate = getRandom(set);
        int row = Character.getNumericValue(coordinate.charAt(0));
        int col = Character.getNumericValue(coordinate.charAt(1));

        blank_space.remove(coordinate);
        marked_O.add(coordinate);
        String oh = "o";
        game.mark(oh, row, col);
    }

    //Helper functions:

    // https://stackoverflow.com/questions/124671/picking-a-random-element-from-a-set
    private String getRandom(Set<String> set) {
        return set.stream().skip(new Random().nextInt(set.size())).findFirst().orElse(null);
    }

    // Check if the given coordinate is valid
    private boolean isInBound(int row, int col) {
        return 0 <= row && row < size && 0 <= col && col < size;
    }

    // Convert a coordinate to its String representation
    private String toCoordinate(int row, int col) {
        return Integer.toString(row) + col;
    }

    // Private methods:

    // Return all the possible moves computer can makes to block the last placed X
    private Set<String> possibleMoves(int row, int col) {
        Set<String> result      = new HashSet<>();
        Set<String> horizontal  = new HashSet<>();
        Set<String> vertical    = new HashSet<>();
        Set<String> diagonal    = new HashSet<>();
        Set<String> a_diagonal  = new HashSet<>();

        Set<String> horizontal_check  = checkHorizontal(row, col);
        Set<String> vertical_check    = checkVertical(row, col);
        Set<String> diagonal_check    = checkDiagonal(row, col);
        Set<String> a_diagonal_check  = checkAntiDiagonal(row, col);

        for (int i = col - 2; i <= col + 2; i++)
            if (isInBound(row, i)) horizontal.add(toCoordinate(row, i));

        for (int i = row - 2; i < row + 2; i++)
            if (isInBound(i, col)) vertical.add(toCoordinate(i, col));

        if (isInBound(row - 2, col - 2)) diagonal.add(toCoordinate(row - 2, col - 2));
        if (isInBound(row - 1, col - 1)) diagonal.add(toCoordinate(row - 1, col - 1));
        if (isInBound(row + 1, col + 1)) diagonal.add(toCoordinate(row + 1, col + 1));
        if (isInBound(row + 2, col + 2)) diagonal.add(toCoordinate(row + 2, col + 2));

        if (isInBound(row - 2, col + 2)) a_diagonal.add(toCoordinate(row - 2, col + 2));
        if (isInBound(row - 1, col + 1)) a_diagonal.add(toCoordinate(row - 1, col + 1));
        if (isInBound(row + 1, col - 1)) a_diagonal.add(toCoordinate(row + 1, col - 1));
        if (isInBound(row + 2, col - 2)) a_diagonal.add(toCoordinate(row + 2, col - 2));

        if (!horizontal_check.isEmpty()) {
            horizontal.removeAll(horizontal_check);
            result.addAll(horizontal);
        }
        if (!vertical_check.isEmpty()) {
            vertical.removeAll(vertical_check);
            result.addAll(vertical);
        }
        if (!diagonal_check.isEmpty()) {
            diagonal.removeAll(diagonal_check);
            result.addAll(diagonal);
        }
        if (!a_diagonal_check.isEmpty()) {
            a_diagonal.removeAll(a_diagonal_check);
            result.addAll(a_diagonal);
        }
        result.remove(toCoordinate(row, col));
        result.removeAll(marked_O);
        System.out.println("My possible moves are: " + result);

        return result;
    }

    // Check the horizontal line that contains the newly placed X to see if there is any other Xs nearby
    private Set<String> checkHorizontal(int row, int col) {
        Set<String> result  = new HashSet<>();
        String[][]  board   = game.getBoard();

        int i = col < 2 ? 0 : col - 2;

        while (i <= col + 2) {
            if (i == col || !isInBound(row, i)) {
                i++;
                continue;
            }
            if (board[row][i].equals(TicTacToe.EX))
                result.add(toCoordinate(row, i));
            i++;
        }
        return result;
    }

    // Check the vertical line that contains the newly placed X to see if there is any other Xs nearby
    private Set<String> checkVertical(int row, int col) {
        Set<String> result  = new HashSet<>();
        String[][]  board   = game.getBoard();

        int i = row < 2 ? 0 : row - 2;

        while (i <= row + 2) {
            if (i == row || !isInBound(i, col)) {
                i++;
                continue;
            }
            if (board[i][col].equals(TicTacToe.EX))
                result.add(toCoordinate(i, col));
            i++;
        }
        return result;
    }

    // Check the diagonal line that contains the newly placed X to see if there is any other Xs nearby
    private Set<String> checkDiagonal(int row, int col) {
        Set<String> result  = new HashSet<>();
        String[][]  board   = game.getBoard();

        int i = row < 2 ? 0 : row - 2;
        int j = col < 2 ? 0 : col - 2;

        while (i <= row + 2 &&  j <= col + 2) {
            if (i == row && j == col || !isInBound(i, j)) {
                i++;
                j++;
                continue;
            }
            if (board[i][j].equals(TicTacToe.EX))
                result.add(toCoordinate(i, j));
            i++;
            j++;
        }
        return result;
    }

    // Check the anti diagonal line that contains the newly placed X to see if there is any other Xs nearby
    private Set<String> checkAntiDiagonal(int row, int col) {
        Set<String> result  = new HashSet<>();
        String[][]  board   = game.getBoard();

        int i = row < 2 ? 0 : row - 2;
        int j = col > size - 2 ? size - 1 : col + 2;

        while (i <= row + 2 && j >= col - 2) {
            if (i == row && j == col || !isInBound(i, j)) {
                i++;
                j--;
                continue;
            }
            if (board[i][j].equals(TicTacToe.EX))
                result.add(toCoordinate(i, j));
            i++;
            j--;
        }
        return result;
    }
}

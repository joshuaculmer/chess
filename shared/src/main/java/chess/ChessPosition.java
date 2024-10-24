package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private final int row;
    private final int col;
    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return col;
    }

    @Override
    public String toString() {
        return "(" + row +
                "," + col +
                ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ChessPosition that=(ChessPosition) o;

        if (row != that.row) {
            return false;
        }
        return col == that.col;
    }


    @Override
    public int hashCode() {
        int result=row;
        result=31 * result + col;
        return result;
    }

    public boolean isOnBoard()
    {
        return row <= 8 && row >= 1 && col <= 8 && col >= 1;
    }


    // Test functions for ChessPosition Class
    public static void main(String[]args)
    {
        ChessPosition testA=new ChessPosition(0, 0);
        ChessPosition testB=new ChessPosition(0,0);
        ChessPosition testC=new ChessPosition(3,3);
        System.out.print(testA);
        System.out.println(testB);
        if(testA.equals(testB)) {
            System.out.printf("%s = %s%n", testA, testB);
        }
        if(testA.equals(testC)) {
            System.out.printf("%s = %s%n :/", testA, testB);
        }
    }
}

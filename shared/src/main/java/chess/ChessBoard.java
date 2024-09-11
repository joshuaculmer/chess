package chess;

import javax.swing.*;
import java.util.Arrays;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPiece[][] squares = new ChessPiece[8][8];
    public ChessBoard() {
        resetBoard();
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow()][position.getColumn()]=piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow()][position.getColumn()];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        for(int i=0;i<8;i++)
            for(int j=0;j<8;j++)
                squares[i][j]=null;
    }


    @Override
    public String toString() {
        String boardRepresentation = "  0  1  2  3  4  5  6  7\n";
        for(int i=7;i>=0;i--) {
            boardRepresentation+=Integer.toString(i);
            for (int j=0; j < 8; j++) {

                if (squares[i][j] != null)
                    boardRepresentation+=squares[i][j].toString() + "|";
                else
                    boardRepresentation+="  |";
            }
            boardRepresentation+=System.lineSeparator();
        }
        return boardRepresentation;
    }

    public static void main(String[]args)
    {
        ChessBoard testBoard = new ChessBoard();
        System.out.println("Piece at (0,0):");
        System.out.println(testBoard.getPiece(new ChessPosition(0,0)));

        testBoard.addPiece(new ChessPosition(0,0), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));
        System.out.println("Piece at (0,0):");
        System.out.println(testBoard.getPiece(new ChessPosition(0,0)));

        System.out.println("The whole test board:");
        System.out.println(testBoard);
    }
}

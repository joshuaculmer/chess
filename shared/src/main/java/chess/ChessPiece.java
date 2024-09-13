package chess;

import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private PieceType type;
    private ChessGame.TeamColor color;
    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.type = type;
        this.color = pieceColor;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public String toString() {
        if(color== ChessGame.TeamColor.WHITE) {
            switch (type) {
                case KING -> {
                    return "K";
                }
                case PAWN -> {
                    return "P";
                }
                case ROOK -> {
                    return "R";
                }
                case KNIGHT -> {
                    return "N";
                }
                case BISHOP -> {
                    return "B";
                }
                case QUEEN -> {
                    return "Q";
                }
            }
        }
        else
        {
            switch (type) {
                case KING -> {
                    return "k";
                }
                case PAWN -> {
                    return "p";
                }
                case ROOK -> {
                    return "r";
                }
                case KNIGHT -> {
                    return "n";
                }
                case BISHOP -> {
                    return "b";
                }
                case QUEEN -> {
                    return "q";
                }
            }
        }
            throw  new RuntimeException("Invalid Piece, unable to run toString()");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that=(ChessPiece) o;
        if (type != that.type) return false;
        return color == that.color;
    }

    @Override
    public int hashCode() {
        int result=type.hashCode();
        result=31 * result + color.hashCode();
        return result;
    }
}

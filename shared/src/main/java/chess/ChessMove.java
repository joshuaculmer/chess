package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {

    private ChessPosition start;
    private ChessPosition end;
    private ChessPiece.PieceType promotionPiece;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        start = startPosition;
        end = endPosition;
        this.promotionPiece = promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return start;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return end;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return promotionPiece;
    }

    @Override
    public String toString() {
        if(promotionPiece == null){
            return start.toString() + " -> " + end.toString();
        }
        else {
            return "P " + start.toString() + " -> " + promotionPiece.toString() + " " + end.toString();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessMove chessMove=(ChessMove) o;
        if (!Objects.equals(start, chessMove.start)) {
            return false;
        }
        if (!Objects.equals(end, chessMove.end)) {
            return false;
        }
        return promotionPiece == chessMove.promotionPiece;
    }

    @Override
    public int hashCode() {
        int result=start != null ? start.hashCode() : 0;
        result=31 * result + (end != null ? end.hashCode() : 0);
        result=31 * result + (promotionPiece != null ? promotionPiece.hashCode() : 0);
        return result;
    }
}

package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private PieceType type;
    private ChessGame.TeamColor color;
    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
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
        Collection<ChessMove> moves = new ArrayList<ChessMove>();
        ChessPiece currentPiece = board.getPiece(myPosition);
        PieceType currentType = currentPiece.getPieceType();

        switch (currentType){
            case KING -> {
                moves.addAll(kingMoves(myPosition));
                break;
            }
            case PAWN -> {
                moves.addAll(pawnMoves(myPosition));
                break;
            }
            case ROOK -> {
                moves.addAll(rookMoves(myPosition));
                break;
            }
            case KNIGHT -> {
                moves.addAll(knightMoves(myPosition));
                break;
            }
            case BISHOP -> {
                moves.addAll(bishopMoves(myPosition));
                break;
            }
            case QUEEN -> {
                moves.addAll(queenMoves(myPosition));
                break;
            }
        }
        for(int i=0; i<moves.size(); i++) {
            ChessMove move = ((ArrayList<ChessMove>) moves).get(i);
            System.out.println(move.toString());
            if (! isValidMove(board, move)) {
                moves.remove(move);
                i--;
            }
        }

        return moves;
    }
    public boolean isValidMove(ChessBoard board, ChessMove move)
    {
        // Move takes this piece out of bounds, therefore inValid
        ChessPosition endPos = move.getEndPosition();
        if(!endPos.isOnBoard())
            return false;

        // Pawn move validation checking
        else if(getPieceType() == PieceType.PAWN) {
            if(endPos.getColumn() == move.getStartPosition().getColumn())
            {
                // Moving up/down a column is ok if we move into an empty spot only
                if(board.getPiece(endPos) == null)
                    return true;
            }
            else if(board.getPiece(endPos).getTeamColor() != getTeamColor())
                return true;
            else
                return false;
        }

        // endPos is empty or this piece attacks an opponent
        else if(board.getPiece(endPos) == null || board.getPiece(endPos).getTeamColor() != getTeamColor())
            return true;
        // endPos has a piece of our color on it already
        return false;
    }

    private boolean isPathEmpty(ChessPosition start, ChessPosition end)
    {
        return false;
    }

    private Collection<ChessMove> kingMoves(ChessPosition position)
    {
        Collection<ChessMove> allPossibleKingMoves= new LinkedList<ChessMove>();
        int row = position.getRow();
        int col = position.getColumn();
        allPossibleKingMoves.add(new ChessMove(position, new ChessPosition(row-1, col), null));
        allPossibleKingMoves.add(new ChessMove(position, new ChessPosition(row-1, col+1), null));
        allPossibleKingMoves.add(new ChessMove(position, new ChessPosition(row, col+1), null));
        allPossibleKingMoves.add(new ChessMove(position, new ChessPosition(row+1, col+1), null));
        allPossibleKingMoves.add(new ChessMove(position, new ChessPosition(row+1, col), null));
        allPossibleKingMoves.add(new ChessMove(position, new ChessPosition(row+1, col-1), null));
        allPossibleKingMoves.add(new ChessMove(position, new ChessPosition(row, col-1), null));
        allPossibleKingMoves.add(new ChessMove(position, new ChessPosition(row-1, col-1), null));
        return allPossibleKingMoves;
    }
    private Collection<ChessMove> pawnMoves(ChessPosition position)
    {
        Collection<ChessMove> allPossiblePawnMoves= new LinkedList<ChessMove>();
        int row = position.getRow();
        int col = position.getColumn();

        if(this.getTeamColor() == ChessGame.TeamColor.WHITE)
        {
            allPossiblePawnMoves.add(new ChessMove(position, new ChessPosition(row+1, col-1), null  ));
            allPossiblePawnMoves.add(new ChessMove(position, new ChessPosition(row+1, col), null  ));
            allPossiblePawnMoves.add(new ChessMove(position, new ChessPosition(row+1, col+1), null  ));
            if (row == 2)
                allPossiblePawnMoves.add(new ChessMove(position, new ChessPosition(row+2, col), null  ));
            // All possible moves if this pawn can be promoted
            if (row + 1 == 8) {
                for(PieceType promoPiece : PieceType.values()) {
                    if(!(promoPiece.equals(PieceType.PAWN) || promoPiece.equals(PieceType.KING))) {
                        allPossiblePawnMoves.add(new ChessMove(position, new ChessPosition(row + 1, col - 1), promoPiece));
                        allPossiblePawnMoves.add(new ChessMove(position, new ChessPosition(row + 1, col), promoPiece));
                        allPossiblePawnMoves.add(new ChessMove(position, new ChessPosition(row + 1, col + 1), promoPiece));
                    }
                }
            }
        }
        else if(this.getTeamColor() == ChessGame.TeamColor.BLACK)
        {
            allPossiblePawnMoves.add(new ChessMove(position, new ChessPosition(row-1, col-1), null  ));
            allPossiblePawnMoves.add(new ChessMove(position, new ChessPosition(row-1, col), null  ));
            allPossiblePawnMoves.add(new ChessMove(position, new ChessPosition(row-1, col+1), null  ));
            if (row == 7)
                allPossiblePawnMoves.add(new ChessMove(position, new ChessPosition(row-2, col), null  ));
            // All possible moves if this pawn can be promoted
            if (row - 1 == 1) {
                for(PieceType promoPiece : PieceType.values()) {
                    if(!(promoPiece.equals(PieceType.PAWN) || promoPiece.equals(PieceType.KING))) {
                        allPossiblePawnMoves.add(new ChessMove(position, new ChessPosition(row - 1, col - 1), promoPiece));
                        allPossiblePawnMoves.add(new ChessMove(position, new ChessPosition(row - 1, col), promoPiece));
                        allPossiblePawnMoves.add(new ChessMove(position, new ChessPosition(row - 1, col + 1), promoPiece));
                    }
                }
            }
        }

        return allPossiblePawnMoves;
    }

    private Collection<ChessMove> rookMoves(ChessPosition position) {
        Collection<ChessMove> allPossibleRookMoves=new LinkedList<ChessMove>();
        int row=position.getRow();
        int col=position.getColumn();

        for(int i=-7; i<7; i++) {
            allPossibleRookMoves.add(new ChessMove(position, new ChessPosition(row+i, col), null));
            allPossibleRookMoves.add(new ChessMove(position, new ChessPosition(row, col+i), null));
        }
        return allPossibleRookMoves;
    }

    private Collection<ChessMove> knightMoves(ChessPosition position) {
        Collection<ChessMove> allPossibleKinghtMoves=new LinkedList<ChessMove>();
        int row=position.getRow();
        int col=position.getColumn();

        allPossibleKinghtMoves.add(new ChessMove(position, new ChessPosition(row-2,col-1), null));
        allPossibleKinghtMoves.add(new ChessMove(position, new ChessPosition(row-2,col+1), null));
        allPossibleKinghtMoves.add(new ChessMove(position, new ChessPosition(row-1,col+2), null));
        allPossibleKinghtMoves.add(new ChessMove(position, new ChessPosition(row+1,col+2), null));
        allPossibleKinghtMoves.add(new ChessMove(position, new ChessPosition(row+2,col+1), null));
        allPossibleKinghtMoves.add(new ChessMove(position, new ChessPosition(row+2,col-1), null));
        allPossibleKinghtMoves.add(new ChessMove(position, new ChessPosition(row+1,col-2), null));
        allPossibleKinghtMoves.add(new ChessMove(position, new ChessPosition(row-1,col-2), null));
        return allPossibleKinghtMoves;
    }

    private Collection<ChessMove> bishopMoves(ChessPosition position) {
        Collection<ChessMove> allPossibleBishopMoves=new LinkedList<ChessMove>();
        int row=position.getRow();
        int col=position.getColumn();

        for(int i=-7; i<7; i++)
        {
            allPossibleBishopMoves.add(new ChessMove(position, new ChessPosition(row+i, col+i), null));
            allPossibleBishopMoves.add(new ChessMove(position, new ChessPosition(row+i, col-i), null));
        }

        return allPossibleBishopMoves;
    }

    private Collection<ChessMove> queenMoves(ChessPosition position)
    {
        Collection<ChessMove> allPossibleQueenMoves=new LinkedList<ChessMove>();

        allPossibleQueenMoves.addAll(rookMoves(position));
        allPossibleQueenMoves.addAll(bishopMoves(position));

        return allPossibleQueenMoves;
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

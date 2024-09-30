package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard board;
    private ArrayList<ChessMove> moves;
    private TeamColor teamTurn;


    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        moves = new ArrayList<ChessMove>();
        teamTurn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     *
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessBoard board = getBoard();
        ChessPiece piece = board.getPiece(startPosition);
        if(piece != null)
            return piece.pieceMoves(board, startPosition);
        else
            return null;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {

        Collection<ChessMove> opponentMoveList;
        opponentMoveList = TeamColor.WHITE == teamColor ? validMovesForTeam(TeamColor.BLACK) : validMovesForTeam(TeamColor.WHITE);

        for(ChessMove move : opponentMoveList) {
            if(move.getEndPosition().equals(getKingPos(teamColor)))
                return true;
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return (! isInCheck(teamColor)) && (isKingSurrounded(teamColor));
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }


    private Collection<ChessMove> validMovesForTeam(ChessGame.TeamColor color)
    {
        LinkedList<ChessMove> movesList = new LinkedList<ChessMove>();
        for(int row = 1; row <= 8; row++) {
            for(int col = 1; col <= 8; col++) {
                ChessPiece currentPiece = getBoard().getPiece(new ChessPosition(row,col));
                if(currentPiece != null && currentPiece.getTeamColor() == color) {
                    movesList.addAll(validMoves(new ChessPosition(row,col)));
                }
            }
        }
        return movesList;
    }

    private ChessPosition getKingPos(TeamColor color){
        for(int row = 1; row <= 8; row++) {
            for(int col = 1; col <= 8; col++) {
                ChessPiece currentPiece = getBoard().getPiece(new ChessPosition(row,col));
                if(currentPiece != null && currentPiece.getTeamColor() == color && currentPiece.getPieceType() == ChessPiece.PieceType.KING)
                    return new ChessPosition(row,col);
            }
        }
        return null; // this should throw an exception, but the game should always have a king for both sides
    }

    private boolean isKingSurrounded(TeamColor color){
        Collection<ChessMove> kingMoveList;
        kingMoveList = validMoves(getKingPos(color));

        for(ChessMove move : kingMoveList){
            ChessBoard tempBoard = getBoard();
            ChessBoard originalBoard = getBoard();
            ChessPiece currentKing = tempBoard.removePiece(move.getStartPosition());
            tempBoard.addPiece(move.getEndPosition(), currentKing);
            board = tempBoard;
            if(!isInCheck(color)){
                setBoard(originalBoard);
                return false;
            }
            setBoard(originalBoard);
        }

        return true;
    }

}

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

        ChessPiece piece = board.getPiece(startPosition);
        if(piece != null) {
            Collection<ChessMove> movesList = new ArrayList<ChessMove>();
            movesList = piece.pieceMoves(board, startPosition);
            for(int i = 0; i < movesList.size(); i++){
                ChessMove move = ((ArrayList<ChessMove>)movesList).get(i);
                if(moveIntoCheck(move,piece.getTeamColor())){
                    ((ArrayList<ChessMove>) movesList).remove(i);
                    i--;
                }
            }
            return movesList;
        }
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
        opponentMoveList = TeamColor.WHITE == teamColor ? allPossibleTeamMoves(TeamColor.BLACK) : allPossibleTeamMoves(TeamColor.WHITE);

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
        Collection<ChessMove> moves = validMovesForTeam(teamColor);
        return isInCheck(teamColor) && moves.isEmpty();
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return validMovesForTeam(teamColor).isEmpty() && !isInCheck(teamColor);
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

    private Collection<ChessMove> allPossibleTeamMoves(ChessGame.TeamColor color)
    {
        LinkedList<ChessMove> movesList = new LinkedList<ChessMove>();
        for(int row = 1; row <= 8; row++) {
            for(int col = 1; col <= 8; col++) {
                ChessPiece currentPiece = getBoard().getPiece(new ChessPosition(row,col));
                if(currentPiece != null && currentPiece.getTeamColor() == color) {
                    movesList.addAll(currentPiece.pieceMoves(board, new ChessPosition(row,col))); // Error here where WHITE  rook is not adding a move to hit the black king, which does not register as still in check when the rook moves
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

//    private boolean kingHasSafeMoves(TeamColor color){
//        Collection<ChessMove> kingMoveList;
//        kingMoveList = validMoves(getKingPos(color));
//
//        for(ChessMove move : kingMoveList){
//            if (!moveIntoCheck(move, color)){
//                return true;
//            }
//        }
//        return false;
//    }


    private boolean moveIntoCheck(ChessMove move, TeamColor color) {
        ChessBoard tempBoard = getBoard();
        ChessBoard originalBoard = new ChessBoard(getBoard());
        ChessPiece currentKing = board.getPiece(getKingPos(color));
        tempBoard.addPiece(move.getEndPosition(), tempBoard.removePiece(move.getStartPosition()));
        board = tempBoard;
        if(isInCheck(color)){
            setBoard(originalBoard);
            return true;
        }
        else {
            setBoard(originalBoard);
            return false;
        }
    }

}

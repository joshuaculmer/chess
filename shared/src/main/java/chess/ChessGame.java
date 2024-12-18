package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;

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
    private boolean isGameOver = false;


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
        if(piece != null && !isGameOver) {
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
        else {
            return null;
        }
    }

    private void advanceTeamTurn(){
        if (getTeamTurn() == TeamColor.WHITE) {
            setTeamTurn(TeamColor.BLACK);
        } else {
            setTeamTurn(TeamColor.WHITE);
        }
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        Collection<ChessMove> movesList = validMoves(move.getStartPosition());

        if(movesList != null && movesList.contains(move)) {
            ChessPiece pieceToMove=getBoard().getPiece(move.getStartPosition());
            if (pieceToMove != null) {
                if (pieceToMove.getTeamColor() == getTeamTurn()) {
                    pieceToMove = getBoard().removePiece(move.getStartPosition());
                    if(move.getPromotionPiece() != null) {
                        pieceToMove=new ChessPiece(pieceToMove.getTeamColor(), move.getPromotionPiece());
                    }
                    getBoard().addPiece(move.getEndPosition(), pieceToMove);
                    advanceTeamTurn();
                }
                else {
                    throw new InvalidMoveException("Move out of turn");
                }
            }
            else {
                throw new InvalidMoveException("No piece at " + move.getStartPosition().toString());
            }
        }
        else {
            throw new InvalidMoveException(move.toString() + " is an illegal move");
        }
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
            if(move.getEndPosition().equals(getKingPos(teamColor))) {
                return true;
            }
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
                    movesList.addAll(currentPiece.pieceMoves(board, new ChessPosition(row,col)));
                }
            }
        }
        return movesList;
    }

    private ChessPosition getKingPos(TeamColor color){
        for(int row = 1; row <= 8; row++) {
            for(int col = 1; col <= 8; col++) {
                ChessPiece currentPiece = getBoard().getPiece(new ChessPosition(row,col));
                if(currentPiece != null && currentPiece.getTeamColor() == color && currentPiece.getPieceType() == ChessPiece.PieceType.KING) {
                    return new ChessPosition(row, col);
                }
            }
        }
        return null; // this should throw an exception, but the game should always have a king for both sides
    }


    private boolean moveIntoCheck(ChessMove move, TeamColor color) {
        ChessBoard tempBoard = getBoard();
        ChessBoard originalBoard = new ChessBoard(getBoard());
        ChessPosition kingPosition = getKingPos(color);
        if(kingPosition == null)  // Test case for when there is no king on the board, king can't be in check if not there
        {
            return false;
        }
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ChessGame chessGame=(ChessGame) o;

        if (!Objects.equals(board, chessGame.board)) {
            return false;
        }
        if (!Objects.equals(moves, chessGame.moves)) {
            return false;
        }
        return teamTurn == chessGame.teamTurn;
    }

    @Override
    public int hashCode() {
        int result=board != null ? board.hashCode() : 0;
        result=31 * result + (moves != null ? moves.hashCode() : 0);
        result=31 * result + (teamTurn != null ? teamTurn.hashCode() : 0);
        return result;
    }

    public void gameOver() {
        this.isGameOver = true;
    }

    public boolean isOver() {
        return isGameOver;
    }
}

package org.londero.amazingrace.grid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.londero.amazingrace.grid.GridCell.CellOccupant;
import org.londero.amazingrace.listeners.LevelCompleteListener;

import android.content.Context;
import android.util.Log;

/**
 * Class to represent the maze grid
 * 
 * @author Adam Londero
 */
public class Grid {

	private Context context;
	private GridCell[][] gridItems;
	private Move lastMove;
	private GridPosition currentPlayerPosition;
	private int width;
	private int height;
	private Random random;
	private Collection<LevelCompleteListener> gridCompleteListeners;

	public Grid(final Context context, final int width, final int height) {
		this.context = context;
		this.width = width;
		this.height = height;
		random = new Random();
		gridCompleteListeners = new LinkedList<LevelCompleteListener>();
	}

	/**
	 * Sets up a new level
	 */
	public void newGrid() {
		gridItems = new GridCell[width][height];
		lastMove = null;
		currentPlayerPosition = null;
		generate();
	}

	/**
	 * Generates a new random grid
	 */
	private void generate() {

		final int startingPosX = random.nextInt(width);
		final int startingPosY = random.nextInt(height);

		int itemPosX = startingPosX;
		int itemPosY = startingPosY;

		// Keep generating until item is not the same place as player starting
		// position
		while (itemPosX == startingPosX && itemPosY == startingPosY) {
			itemPosX = random.nextInt(width);
			itemPosY = random.nextInt(height);
		}

		for (int w = 0; w < width; w++) {
			for (int h = 0; h < height; h++) {

				final GridCell cell = new GridCell(context);

				// If starting pos place player there
				if (w == startingPosX && h == startingPosY) {
					cell.setOccupant(CellOccupant.PLAYER);
					currentPlayerPosition = new GridPosition(w, h);
				} else if (w == itemPosX && h == itemPosY) {
					cell.setOccupant(CellOccupant.ITEM);
				} else {
					// Generate an obstacle randomly
					final int randNum = random.nextInt(100);
					if (randNum < 20) {
						cell.setOccupant(CellOccupant.OBSTACLE);
					} else {
						cell.setOccupant(CellOccupant.EMPTY);
					}
				}

				gridItems[w][h] = cell;
			}
		}
	}

	/**
	 * @return the width
	 */
	public final int getWidth() {
		return width;
	}

	/**
	 * @param width the width to set
	 */
	public final void setWidth(int width) {
		this.width = width;
	}

	/**
	 * @return the height
	 */
	public final int getHeight() {
		return height;
	}

	/**
	 * @param height the height to set
	 */
	public final void setHeight(int height) {
		this.height = height;
	}

	public GridCell getGridObjectByIndex(final int index) {
		return gridItems[index / width][index % width];
	}

	/**
	 * Returns a list of valid moves for the player given his current position -
	 * this is used for when a player needs to make a random decision
	 * 
	 * @return A list of valid moves
	 */
	public List<Move> getValidMoves() {
		final List<Move> validMoves = new ArrayList<Move>();

		if (currentPlayerPosition != null) {

			// Check move north,east,south and west
			final Move north = new Move(currentPlayerPosition, new GridPosition(currentPlayerPosition.x, currentPlayerPosition.y - 1));
			final Move east = new Move(currentPlayerPosition, new GridPosition(currentPlayerPosition.x + 1, currentPlayerPosition.y));
			final Move south = new Move(currentPlayerPosition, new GridPosition(currentPlayerPosition.x, currentPlayerPosition.y + 1));
			final Move west = new Move(currentPlayerPosition, new GridPosition(currentPlayerPosition.x - 1, currentPlayerPosition.y));

			final Move[] movesToCheck = { north, east, south, west };

			for (final Move move : movesToCheck) {
				if (isValidMove(move)) {
					validMoves.add(move);
				}
			}
		}
		return validMoves;
	}

	/**
	 * Checks whether a move is valid or not
	 * 
	 * @param playerMove The move from the players current position to a new
	 *            position
	 * @return Whether the move is valid or not
	 */
	public boolean isValidMove(final Move playerMove) {
		boolean valid = false;

		// Check that the from position is the same as the players current
		// position
		if (currentPlayerPosition.equals(playerMove.getFrom())) {
			final GridPosition moveDest = playerMove.getTo();

			// Check that the destination is not out of bounds
			if (moveDest.x >= 0 && moveDest.x < width && moveDest.y >= 0 && moveDest.y < height) {
				// Check there are no obstacles in the destination
				final GridCell destCell = getCellAt(moveDest);
				if (destCell.getOccupant() != CellOccupant.OBSTACLE) {
					valid = true;
				}
			}
		}

		return valid;
	}

	public GridCell getCellAt(final GridPosition gridPos) {
		return gridItems[gridPos.x][gridPos.y];
	}

	/**
	 * Code to handle the next move. Algorithm is as follows:
	 * <p>
	 * First move or did not move last turn?<br>
	 * <ol>
	 * <li>Look for all legal moves
	 * <li>Choose one at random
	 * </ol>
	 * Moved last turn?<br>
	 * <ol>
	 * <li>Get direction last moved in
	 * <li>Can player move this direction again this turn?
	 * <li>If so, move
	 * <li>If not, attempt move turned at 90 degrees
	 * <li>If this fails, attempt move turned at -90 degrees
	 * <li>If this fails, attempt move turned back on itself
	 * <li>If no moves possible, do not move
	 * </ol>
	 */
	public void nextMove() {
		if (lastMove == null) {
			final List<Move> validMoves = getValidMoves();
			if (validMoves.size() > 0) {
				final int rand = random.nextInt(validMoves.size());
				makeMove(validMoves.get(rand));
			}
		} else {
			// Attempt to move in same direction
			int lastMoveX = lastMove.getTo().x - lastMove.getFrom().x;
			int lastMoveY = lastMove.getTo().y - lastMove.getFrom().y;
			Move sameMove = new Move(currentPlayerPosition, new GridPosition(currentPlayerPosition.x + lastMoveX, currentPlayerPosition.y + lastMoveY));
			if (isValidMove(sameMove)) {
				makeMove(sameMove);
			} else {
				// Attempt to move at 90 degrees to last move
				int rotatedMoveY = lastMoveX * -1;
				int rotatedMoveX = lastMoveY;
				sameMove = new Move(currentPlayerPosition, new GridPosition(currentPlayerPosition.x + rotatedMoveX, currentPlayerPosition.y + rotatedMoveY));
				if (isValidMove(sameMove)) {
					makeMove(sameMove);
				} else {
					// Attempt going the other way
					sameMove = new Move(currentPlayerPosition, new GridPosition(currentPlayerPosition.x - rotatedMoveX, currentPlayerPosition.y - rotatedMoveY));
					if (isValidMove(sameMove)) {
						makeMove(sameMove);
					} else {
						// Attempt to turn back on itself
						sameMove = new Move(currentPlayerPosition, lastMove.getFrom());
						if (isValidMove(sameMove)) {
							makeMove(sameMove);
						} else {
							// No move
							makeMove(null);
						}
					}
				}
			}
		}
	}

	private void makeMove(final Move move) {
		if (null == move) {
			// Move not possible
		} else {
			// Assumes move is valid
			assert isValidMove(move);

			Log.d("Grid", "Attempting to move player from " + move.getFrom() + " to " + move.getTo());

			// Set from cell to be empty
			final GridCell fromCell = getCellAt(move.getFrom());

			// Set to cell to be occupied by the player
			final GridCell toCell = getCellAt(move.getTo());

			// If destination cell contains the item then create the exit
			if (toCell.getOccupant() == CellOccupant.ITEM) {
				int exitPosX = currentPlayerPosition.x;
				int exitPosY = currentPlayerPosition.y;

				// Keep generating until item is not the same place as player
				// starting
				// position
				while ((exitPosX == currentPlayerPosition.x && exitPosY == currentPlayerPosition.y) || (exitPosX == move.getTo().x && exitPosY == move.getTo().y)
					|| !(getCellAt(new GridPosition(exitPosX, exitPosY)).getOccupant() == CellOccupant.EMPTY)) {
					exitPosX = random.nextInt(width);
					exitPosY = random.nextInt(height);
				}

				getCellAt(new GridPosition(exitPosX, exitPosY)).setOccupant(CellOccupant.EXIT);
			} else if (toCell.getOccupant() == CellOccupant.EXIT) {
				// Level complete
				fireLevelCompleteListeners();
			}

			fromCell.setOccupant(CellOccupant.EMPTY);
			toCell.setOccupant(CellOccupant.PLAYER);
			currentPlayerPosition = move.getTo();
		}
		lastMove = move;
	}

	private void fireLevelCompleteListeners() {
		for (final LevelCompleteListener listener : gridCompleteListeners) {
			listener.levelComplete();
		}
	}

	public void addLevelCompleteListener(final LevelCompleteListener listener) {
		gridCompleteListeners.add(listener);
	}
}

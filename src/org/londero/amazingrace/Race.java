/**
 * 
 */
package org.londero.amazingrace;

import org.londero.amazingrace.grid.Grid;
import org.londero.amazingrace.grid.GridCell;
import org.londero.amazingrace.listeners.LevelCompleteListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Class to represent a race
 * 
 * @author Adam Londero
 */
public class Race extends Activity implements LevelCompleteListener {

	private static final int WIDTH = 10;
	private static final int HEIGHT = 10;

	// Delay between moves in milliseconds
	private static final int MOVE_DELAY = 100;

	private static final String SCORE_TEXT = "Level %d";
	private TextView scoreText;
	private Grid grid;
	private RaceGridLayout gridHolder;
	private MoveHandler mover;
	private int currentLevel;

	// Handler which will handle the movement
	final Handler handler = new Handler();

	protected enum DialogBox {
		DIALOG_LEVEL_COMPLETE(0);

		int id;

		DialogBox(int id) {
			this.id = id;
		}
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.race);

		grid = new Grid(this, WIDTH, HEIGHT);
		grid.addLevelCompleteListener(this);

		scoreText = (TextView) findViewById(R.id.currentLevel);
		updateScore();

		LinearLayout boardHolder = (LinearLayout) findViewById(R.id.race_grid_holder);
		boardHolder.removeAllViews();

		gridHolder = new RaceGridLayout(this, WIDTH, HEIGHT);
		gridHolder.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

		boardHolder.addView(gridHolder);

		newLevel();

		mover = new MoveHandler(handler, grid, MOVE_DELAY);
	}

	@Override
	public void onResume() {
		super.onResume();

		startMovement();
	}

	@Override
	public void onPause() {
		super.onPause();
		suspendMovement();
	}

	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Level Complete").setCancelable(false);
		AlertDialog alert = builder.create();
		return alert;
	}

	private void updateScore() {
		scoreText.setText(String.format(SCORE_TEXT, currentLevel));
	}

	private void newLevel() {
		currentLevel++;
		updateScore();

		// Clear current grid
		gridHolder.removeAllViews();

		// Create a new grid
		grid.newGrid();
		for (int i = 0; i < WIDTH * HEIGHT; i++) {
			GridCell gamePiece = grid.getGridObjectByIndex(i);
			gridHolder.addView(gamePiece);
		}
	}

	@Override
	public void levelComplete() {
		suspendMovement();
		newLevel();
		startMovement();
	}

	private void suspendMovement() {
		handler.removeCallbacks(mover);
	}

	private void startMovement() {
		handler.postDelayed(mover, MOVE_DELAY);
	}

}

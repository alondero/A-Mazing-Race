/**
 * 
 */
package org.londero.amazingrace;

import org.londero.amazingrace.grid.Grid;

import android.os.Handler;

/**
 * @author Adam Londero
 */
public class MoveHandler implements Runnable {

	private Handler handler;

	private final Grid grid;
	private int moveDelay;

	public MoveHandler(final Handler handler, final Grid grid, final int moveDelay) {
		this.handler = handler;
		this.grid = grid;
		this.moveDelay = moveDelay;
	}

	@Override
	public void run() {
		grid.nextMove();
		handler.postDelayed(this, moveDelay);
	}
}

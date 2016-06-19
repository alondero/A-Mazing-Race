/**
 * 
 */
package org.londero.amazingrace.grid;

import org.londero.amazingrace.R;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * Abstract class which defines objects that can exist on the grid
 * 
 * @author Adam Londero
 */
public class GridCell extends Button implements OnClickListener {

	public enum CellOccupant {
		OBSTACLE, PLAYER, ITEM, EXIT, EMPTY
	}

	private CellOccupant occupant;

	public GridCell(Context context) {
		super(context);
		setOnClickListener(this);
	}

	/**
	 * @param occupant the occupant to set
	 */
	public void setOccupant(CellOccupant occupant) {
		this.occupant = occupant;

		// Also set the image of this grid cell to reflect the new occupant
		switch (occupant) {
		case OBSTACLE:
			setBackgroundResource(R.drawable.obstacle);
			break;
		case PLAYER:
			setBackgroundResource(R.drawable.player);
			break;
		case ITEM:
			setBackgroundResource(R.drawable.item);
			break;
		case EXIT:
			setBackgroundResource(R.drawable.stairs);
			break;
		case EMPTY:
		default:
			setBackgroundResource(R.drawable.blanktile);
			break;
		}
	}

	/**
	 * @return the occupant
	 */
	public CellOccupant getOccupant() {
		return occupant;
	}

	@Override
	public void onClick(View v) {
		// If an obstacle currently occupies this cell then change to blank and
		// vice versa
		switch (occupant) {
		case OBSTACLE:
			setOccupant(CellOccupant.EMPTY);
			break;
		case EMPTY:
			setOccupant(CellOccupant.OBSTACLE);
			break;
		default:
			break;
		}
	}

}

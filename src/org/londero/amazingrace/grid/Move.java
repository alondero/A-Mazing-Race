/**
 * 
 */
package org.londero.amazingrace.grid;

/**
 * @author Adam Londero
 */
public class Move {

	private GridPosition from;
	private GridPosition to;

	public Move(final GridPosition from, final GridPosition to) {
		this.setFrom(from);
		this.setTo(to);
	}

	/**
	 * @param from the from to set
	 */
	public void setFrom(GridPosition from) {
		this.from = from;
	}

	/**
	 * @return the from
	 */
	public GridPosition getFrom() {
		return from;
	}

	/**
	 * @param to the to to set
	 */
	public void setTo(GridPosition to) {
		this.to = to;
	}

	/**
	 * @return the to
	 */
	public GridPosition getTo() {
		return to;
	}
}

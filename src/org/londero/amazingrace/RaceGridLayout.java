package org.londero.amazingrace;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

public class RaceGridLayout extends LinearLayout {

	private int numRows;
	private int numCols;

	public RaceGridLayout(Context context, final int numRows, final int numCols) {
		super(context);
		this.numRows = numRows;
		this.numCols = numCols;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int availWidth = r - l;
		int availHeight = b - t;
		int edgeSize = Math.min(availHeight, availWidth);
		int pieceEdgeSize = Math.min(edgeSize / numRows, edgeSize / numCols);
		int xEdgeSize = pieceEdgeSize * numCols;
		int yEdgeSize = pieceEdgeSize * numRows;
		int startX = (availWidth - xEdgeSize) / 2;
		int startY = (availHeight - yEdgeSize) / 2;

		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			int xOffset = (i / numCols) * pieceEdgeSize;
			int yOffset = (i % numCols) * pieceEdgeSize;
			child.layout(startX + xOffset, startY + yOffset, startX + xOffset + pieceEdgeSize, startY + yOffset + pieceEdgeSize);
		}
	}

}

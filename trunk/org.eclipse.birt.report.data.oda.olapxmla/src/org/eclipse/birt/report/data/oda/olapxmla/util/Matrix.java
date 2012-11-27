package org.eclipse.birt.report.data.oda.olapxmla.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Matrix {
    private final Map<List<Integer>, MatrixCell> map =
        new HashMap<List<Integer>, MatrixCell>();
    private final int width;
    private final int height;

    /**
     * Creats a Matrix.
     *
     * @param width Width of matrix
     * @param height Height of matrix
     */
    public Matrix(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Sets the value at a particular coordinate
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param value Value
     */
    void set(int x, int y, String value) {
        set(x, y, value, false, false);
    }

    /**
     * Sets the value at a particular coordinate
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param value Value
     * @param right Whether value is right-justified
     * @param sameAsPrev Whether value is the same as the previous value.
     * If true, some formats separators between cells
     */
    void set(
        int x,
        int y,
        String value,
        boolean right,
        boolean sameAsPrev)
    {
        map.put(
            Arrays.asList(x, y),
            new MatrixCell(value, right, sameAsPrev));
        assert x >= 0 && x < getWidth() : x;
        assert y >= 0 && y < getHeight() : y;
    }

    /**
     * Returns the cell at a particular coordinate.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @return Cell
     */
    public MatrixCell get(int x, int y) {
        return map.get(Arrays.asList(x, y));
    }

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}


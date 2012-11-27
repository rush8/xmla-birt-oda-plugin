package org.eclipse.birt.report.data.oda.olapxmla.util;

/**
 * Contents of a cell in a matrix.
 */
public class MatrixCell {
    private final String value;
    private final boolean right;
    private final boolean sameAsPrev;

    /**
     * Creates a matrix cell.
     *
     * @param value Value
     * @param right Whether value is right-justified
     * @param sameAsPrev Whether value is the same as the previous value.
     * If true, some formats separators between cells
     */
    MatrixCell(
        String value,
        boolean right,
        boolean sameAsPrev)
    {
        this.value = value;
        this.right = right;
        this.sameAsPrev = sameAsPrev;
    }

	public String getValue() {
		return value;
	}

	public boolean isRight() {
		return right;
	}

	public boolean isSameAsPrev() {
		return sameAsPrev;
	}
}

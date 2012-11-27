package org.eclipse.birt.report.data.oda.olapxmla.util;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.olap4j.Cell;
import org.olap4j.CellSet;
import org.olap4j.CellSetAxis;
import org.olap4j.Position;
import org.olap4j.impl.CoordinateIterator;
import org.olap4j.impl.Olap4jUtil;
import org.olap4j.metadata.Member;



public class OlapXmlaUtil {
	public static Matrix convertMultiDimToTwoDim(CellSet cellSet) {
        // Compute how many rows are required to display the columns axis.
        final CellSetAxis columnsAxis;
        if (cellSet.getAxes().size() > 0) {
            columnsAxis = cellSet.getAxes().get(0);
        } else {
            columnsAxis = null;
        }
        AxisInfo columnsAxisInfo = computeAxisInfo(columnsAxis);

        // Compute how many columns are required to display the rows axis.
        final CellSetAxis rowsAxis;
        if (cellSet.getAxes().size() > 1) {
            rowsAxis = cellSet.getAxes().get(1);
        } else {
            rowsAxis = null;
        }
        AxisInfo rowsAxisInfo = computeAxisInfo(rowsAxis);

        if (cellSet.getAxes().size() > 2) {
        	
        	//TODO: How to handle more than two axes since it will not form tabular data
        	//Currently supports only two axes ROWS and COLUMNS        	       
            //int[] dimensions = new int[cellSet.getAxes().size() - 2];
            //for (int i = 2; i < cellSet.getAxes().size(); i++) {
            //    CellSetAxis cellSetAxis = cellSet.getAxes().get(i);
            //    dimensions[i - 2] = cellSetAxis.getPositions().size();
            //}
            //for (int[] pageCoords : CoordinateIterator.iterate(dimensions)) {
            //    return formatPage(
            //        cellSet,
            //        pageCoords,
            //        columnsAxis,
            //        columnsAxisInfo,
            //        rowsAxis,
            //        rowsAxisInfo);
        	
        	throw new UnsupportedOperationException("MDX queries having more than two axes are not supported.");
        	            
        } else {
            return formatPage(
                cellSet,
                new int[] {},
                columnsAxis,
                columnsAxisInfo,
                rowsAxis,
                rowsAxisInfo);
        }
    }

    /**
     * Formats a two-dimensional page.
     *
     * @param cellSet Cell set
     * @param pw Print writer
     * @param pageCoords Coordinates of page [page, chapter, section, ...]
     * @param columnsAxis Columns axis
     * @param columnsAxisInfo Description of columns axis
     * @param rowsAxis Rows axis
     * @param rowsAxisInfo Description of rows axis
     * @return 
     */
    private static Matrix formatPage(
        CellSet cellSet,
        int[] pageCoords,
        CellSetAxis columnsAxis,
        AxisInfo columnsAxisInfo,
        CellSetAxis rowsAxis,
        AxisInfo rowsAxisInfo)
    {
        //if (pageCoords.length > 0) {
        //    //pw.println();
        //    for (int i = pageCoords.length - 1; i >= 0; --i) {
        //        int pageCoord = pageCoords[i];
        //        final CellSetAxis axis = cellSet.getAxes().get(2 + i);
        //        //pw.print(axis.getAxisOrdinal() + ": ");
        //        final Position position =
        //            axis.getPositions().get(pageCoord);
        //        int k = -1;
        //        for (Member member : position.getMembers()) {
        //            if (++k > 0) {
        //                //pw.print(", ");
        //            }
        //            //pw.print(member.getUniqueName());
        //        }
        //        //pw.println();
        //   }
        //}
    	
        // Figure out the dimensions of the blank rectangle in the top left
        // corner.
        final int yOffset = columnsAxisInfo.getWidth();
        final int xOffsset = rowsAxisInfo.getWidth();

     // Populate a string matrix
        Matrix matrix = new Matrix(xOffsset + (columnsAxis == null? 1: columnsAxis.getPositions().size()), 
        		yOffset+ (rowsAxis == null? 1: rowsAxis.getPositions().size()));

        
        // Populate corner
        for (int x = 0; x < xOffsset; x++) {
            for (int y = 0; y < yOffset; y++) {
                matrix.set(x, y, "", false, x > 0);
            }
        }

        // Populate matrix with cells representing axes
        //noinspection SuspiciousNameCombination
        populateAxis(
            matrix, columnsAxis, columnsAxisInfo, true, xOffsset);
        populateAxis(
            matrix, rowsAxis, rowsAxisInfo, false, yOffset);

        // Populate cell values
        for (Cell cell : cellIter(pageCoords, cellSet)) {
            final List<Integer> coordList = cell.getCoordinateList();
            int x = xOffsset;
            if (coordList.size() > 0) {
                x += coordList.get(0);
            }
            int y = yOffset;
            if (coordList.size() > 1) {
                y += coordList.get(1);
            }
            String value =
            		cell.getValue() == null
                        ? ""
                        : cell.getFormattedValue().toString();
            matrix.set(
                x, y, value, true, false);
        }

        int[] columnWidths = new int[matrix.getWidth()];
        int widestWidth = 0;
        for (int x = 0; x < matrix.getWidth(); x++) {
            int columnWidth = 0;
            for (int y = 0; y < matrix.getHeight(); y++) {
                MatrixCell cell = matrix.get(x, y);
                if (cell != null) {
                    columnWidth =
                        Math.max(columnWidth, cell.getValue().length());
                }
            }
            columnWidths[x] = columnWidth;
            widestWidth = Math.max(columnWidth, widestWidth);
        }

        // Create a large array of spaces, for efficient printing.
        char[] spaces = new char[widestWidth + 1];
        Arrays.fill(spaces, ' ');
        char[] equals = new char[widestWidth + 1];
        Arrays.fill(equals, '=');
        char[] dashes = new char[widestWidth + 3];
        Arrays.fill(dashes, '-');

        return matrix;
        
    }

    /**
     * Populates cells in the matrix corresponding to a particular axis.
     *
     * @param matrix Matrix to populate
     * @param axis Axis
     * @param axisInfo Description of axis
     * @param isColumns True if columns, false if rows
     * @param offset Ordinal of first cell to populate in matrix
     */
    private static void populateAxis(
        Matrix matrix,
        CellSetAxis axis,
        AxisInfo axisInfo,
        boolean isColumns,
        int offset)
    {
        if (axis == null) {
            return;
        }
        Member[] prevMembers = new Member[axisInfo.getWidth()];
        Member[] members = new Member[axisInfo.getWidth()];
        for (int i = 0; i < axis.getPositions().size(); i++) {
            final int x = offset + i;
            Position position = axis.getPositions().get(i);
            int yOffset = 0;
            final List<Member> memberList = position.getMembers();
            for (int j = 0; j < memberList.size(); j++) {
                Member member = memberList.get(j);
                final AxisOrdinalInfo ordinalInfo =
                    axisInfo.ordinalInfos.get(j);
                while (member != null) {
                    if (member.getDepth() < ordinalInfo.minDepth) {
                        break;
                    }
                    final int y =
                        yOffset
                        + member.getDepth()
                        - ordinalInfo.minDepth;
                    members[y] = member;
                    member = member.getParentMember();
                }
                yOffset += ordinalInfo.getWidth();
            }
            boolean same = true;
            for (int y = 0; y < members.length; y++) {
                Member member = members[y];
                same =
                    same
                    && i > 0
                    && Olap4jUtil.equal(prevMembers[y], member);
                String value =
                    member == null
                        ? ""
                        : member.getCaption();
                if (isColumns) {
                    matrix.set(x, y, value, false, same);
                } else {
                    if (same) {
                        value = "";
                    }
                    //noinspection SuspiciousNameCombination
                    matrix.set(y, x, value, false, false);
                }
                prevMembers[y] = member;
                members[y] = null;
            }
        }
    }

    /**
     * Computes a description of an axis.
     *
     * @param axis Axis
     * @return Description of axis
     */
    private static AxisInfo computeAxisInfo(CellSetAxis axis)
    {
        if (axis == null) {
            return new AxisInfo(0);
        }
        final AxisInfo axisInfo =
            new AxisInfo(axis.getAxisMetaData().getHierarchies().size());
        int p = -1;
        for (Position position : axis.getPositions()) {
            ++p;
            int k = -1;
            for (Member member : position.getMembers()) {
                ++k;
                final AxisOrdinalInfo axisOrdinalInfo =
                    axisInfo.ordinalInfos.get(k);
                final int topDepth =
                    member.isAll()
                        ? member.getDepth()
                        : member.getHierarchy().hasAll()
                            ? 1
                            : 0;
                if (axisOrdinalInfo.minDepth > topDepth
                    || p == 0)
                {
                    axisOrdinalInfo.minDepth = topDepth;
                }
                axisOrdinalInfo.maxDepth =
                    Math.max(
                        axisOrdinalInfo.maxDepth,
                        member.getDepth());
            }
        }
        return axisInfo;
    }

    /**
     * Returns an iterator over cells in a result.
     */
    private static Iterable<Cell> cellIter(
        final int[] pageCoords,
        final CellSet cellSet)
    {
        return new Iterable<Cell>() {
            public Iterator<Cell> iterator() {
                int[] axisDimensions =
                    new int[cellSet.getAxes().size() - pageCoords.length];
                assert pageCoords.length <= axisDimensions.length;
                for (int i = 0; i < axisDimensions.length; i++) {
                    CellSetAxis axis = cellSet.getAxes().get(i);
                    axisDimensions[i] = axis.getPositions().size();
                }
                final CoordinateIterator coordIter =
                    new CoordinateIterator(axisDimensions, true);
                return new Iterator<Cell>() {
                    public boolean hasNext() {
                        return coordIter.hasNext();
                    }

                    public Cell next() {
                        final int[] ints = coordIter.next();
                        final AbstractList<Integer> intList =
                            new AbstractList<Integer>() {
                                public Integer get(int index) {
                                    return index < ints.length
                                        ? ints[index]
                                        : pageCoords[index - ints.length];
                                }

                                public int size() {
                                    return pageCoords.length + ints.length;
                                }
                            };
                        return cellSet.getCell(intList);
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }
    

    /**
     * Description of a particular hierarchy mapped to an axis.
     */
    private static class AxisOrdinalInfo {
        int minDepth = 1;
        int maxDepth = 0;

        /**
         * Returns the number of matrix columns required to display this
         * hierarchy.
         */
        public int getWidth() {
            return maxDepth - minDepth + 1;
        }
    }

    /**
     * Description of an axis.
     */
    private static class AxisInfo {
        final List<AxisOrdinalInfo> ordinalInfos;

        /**
         * Creates an AxisInfo.
         *
         * @param ordinalCount Number of hierarchies on this axis
         */
        AxisInfo(int ordinalCount) {
            ordinalInfos = new ArrayList<AxisOrdinalInfo>(ordinalCount);
            for (int i = 0; i < ordinalCount; i++) {
                ordinalInfos.add(new AxisOrdinalInfo());
            }
        }

        /**
         * Returns the number of matrix columns required by this axis. The
         * sum of the width of the hierarchies on this axis.
         *
         * @return Width of axis
         */
        public int getWidth() {
            int width = 0;
            for (AxisOrdinalInfo info : ordinalInfos) {
                width += info.getWidth();
            }
            return width;
        }
    }

    /**
     * Two-dimensional collection of string values.
     */

}



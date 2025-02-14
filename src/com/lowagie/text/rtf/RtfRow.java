/**
 * $Id: RtfRow.java,v 1.38 2006/02/09 17:25:25 hallm Exp $
 * $Name:  $
 *
 * Copyright 2001, 2002 by Mark Hall
 *
 * The contents of this file are subject to the Mozilla Public License Version 1.1
 * (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the License.
 *
 * The Original Code is 'iText, a free JAVA-PDF library'.
 *
 * The Initial Developer of the Original Code is Bruno Lowagie. Portions created by
 * the Initial Developer are Copyright (C) 1999, 2000, 2001, 2002 by Bruno Lowagie.
 * All Rights Reserved.
 * Co-Developer of the code is Paulo Soares. Portions created by the Co-Developer
 * are Copyright (C) 2000, 2001, 2002 by Paulo Soares. All Rights Reserved.
 *
 * Contributor(s): all the names of the contributors are added in the source code
 * where applicable.
 *
 * Alternatively, the contents of this file may be used under the terms of the
 * LGPL license (the ?GNU LIBRARY GENERAL PUBLIC LICENSE?), in which case the
 * provisions of LGPL are applicable instead of those above.  If you wish to
 * allow use of your version of this file only under the terms of the LGPL
 * License and not to allow others to use your version of this file under
 * the MPL, indicate your decision by deleting the provisions above and
 * replace them with the notice and other provisions required by the LGPL.
 * If you do not delete the provisions above, a recipient may use your version
 * of this file under either the MPL or the GNU LIBRARY GENERAL PUBLIC LICENSE.
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the MPL as stated above or under the terms of the GNU
 * Library General Public License as published by the Free Software Foundation;
 * either version 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Library general Public License for more
 * details.
 *
 * If you didn't download this code from the following link, you should check if
 * you aren't using an obsolete version:
 * http://www.lowagie.com/iText/
 */

package com.lowagie.text.rtf;

import com.lowagie.text.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.io.*;
import java.awt.Color;

/**
 * A Helper Class for the <CODE>RtfWriter</CODE>.
 * <P>
 * Do not use it directly
 * 
 * ONLY FOR USE WITH THE RtfWriter NOT with the RtfWriter2.
 *
 * Parts of this Class were contributed by Steffen Stundzig. Many thanks for the
 * improvements.
 * Code added by c
 * @deprecated Please move to the RtfWriter2 and associated classes.
 */
public class RtfRow {
    /** Table border solid */
    public static final byte[] tableBorder = "brdrs".getBytes();
    /** Table border width */
    public static final byte[] tableBorderWidth = "brdrw".getBytes();
    /** Table border color */
    public static final byte[] tableBorderColor = "brdrcf".getBytes();

    /** Table row defaults */
    private static final byte[] rowBegin = "trowd".getBytes();
    /** End of table row */
    private static final byte[] rowEnd = "row".getBytes();
    /** Table row autofit */
    private static final byte[] rowAutofit = "trautofit1".getBytes();
    private static final byte[] graphLeft = "trgaph".getBytes();
    /** Row border left */
    private static final byte[] rowBorderLeft = "trbrdrl".getBytes();
    /** Row border right */
    private static final byte[] rowBorderRight = "trbrdrr".getBytes();
    /** Row border top */
    private static final byte[] rowBorderTop = "trbrdrt".getBytes();
    /** Row border bottom */
    private static final byte[] rowBorderBottom = "trbrdrb".getBytes();
    /** Row border horiz inline */
    private static final byte[] rowBorderInlineHorizontal = "trbrdrh".getBytes();
    /** Row border bottom */
    private static final byte[] rowBorderInlineVertical = "trbrdrv".getBytes();
    /** Default cell spacing left */
    private static final byte[] rowSpacingLeft = "trspdl".getBytes();
    /** Default cell spacing right */
    private static final byte[] rowSpacingRight = "trspdr".getBytes();
    /** Default cell spacing top */
    private static final byte[] rowSpacingTop = "trspdt".getBytes();
    /** Default cell spacing bottom */
    private static final byte[] rowSpacingBottom = "trspdb".getBytes();
    /** Default cell spacing format left */
    private static final byte[] rowSpacingLeftStyle = "trspdfl3".getBytes();
    /** Default cell spacing format right */
    private static final byte[] rowSpacingRightStyle = "trspdfr3".getBytes();
    /** Default cell spacing format top */
    private static final byte[] rowSpacingTopStyle = "trspdft3".getBytes();
    /** Default cell spacing format bottom */
    private static final byte[] rowSpacingBottomStyle = "trspdfb3".getBytes();
    /** Default cell padding left */
    private static final byte[] rowPaddingLeft = "trpaddl".getBytes();
    /** Default cell padding right */
    private static final byte[] rowPaddingRight = "trpaddr".getBytes();
    /** Default cell padding format left */
    private static final byte[] rowPaddingLeftStyle = "trpaddfl3".getBytes();
    /** Default cell padding format right */
    private static final byte[] rowPaddingRightStyle = "trpaddfr3".getBytes();
    /** Row width format */
    private static final byte[] rowWidthStyle = "trftsWidth3".getBytes();
    /** Row width */
    private static final byte[] rowWidth = "trwWidth".getBytes();
    /**
     * Table row header. This row should appear at the top of every
     * page the current table appears on.
     */
    private static final byte[] rowHeader = "trhdr".getBytes();
    /**
     * Table row keep together. This row cannot be split by a page break.
     * This property is assumed to be off unless the control word is
     * present.
     */
    private static final byte[] rowKeep = "trkeep".getBytes();
    /** Table alignment left */
    private static final byte[] rowAlignLeft = "trql".getBytes();
    /** Table alignment center */
    private static final byte[] rowAlignCenter = "trqc".getBytes();
    /** Table alignment right */
    private static final byte[] rowAlignRight = "trqr".getBytes();

    /** List of <code>RtfCell</code>s in this <code>RtfRow</code> */
    private ArrayList cells = new ArrayList();
    /** The <code>RtfWriter</code> to which this <code>RtfRow</code> belongs */
    private RtfWriter writer = null;
    /** The <coce>RtfTable</code> to which this <code>RtfRow</code> belongs */
    private RtfTable mainTable = null;

    /** The width of this <code>RtfRow</code> (in percent) */
    private int width = 100;
    /** The default cellpadding of <code>RtfCells</code> in this
     * <code>RtfRow</code> */
    private int cellpadding = 115;
    /** The default cellspacing of <code>RtfCells</code> in this
     * <code>RtfRow</code> */
    private int cellspacing = 14;
    /** The borders of this <code>RtfRow</code> */
    private int borders = 0;
    /** The border color of this <code>RtfRow</code> */
    private java.awt.Color borderColor = null;
    /** The border width of this <code>RtfRow</code> */
    private float borderWidth = 0;

    /** Original Row */
    private Row origRow = null;

    /**
     * Create a new <code>RtfRow</code>.
     *
     * @param writer The <code>RtfWriter</code> that this <code>RtfRow</code> belongs to
     * @param mainTable The <code>RtfTable</code> that created this
     * <code>RtfRow</code>
     */
    public RtfRow(RtfWriter writer, RtfTable mainTable) {
        super();
        this.writer = writer;
        this.mainTable = mainTable;
    }

    /**
     * Pregenerate the <code>RtfCell</code>s in this <code>RtfRow</code>.
     *
     * @param columns The number of <code>RtfCell</code>s to be generated.
     */
    public void pregenerateRows(int columns) {
        for (int i = 0; i < columns; i++) {
            RtfCell rtfCell = new RtfCell(writer, mainTable);
            cells.add(rtfCell);
        }
    }

    /**
     * Import a <code>Row</code>.
     * <P>
     * All the parameters are taken from the <code>RtfTable</code> which contains
     * this <code>RtfRow</code> and they do exactely what they say
     * @param row
     * @param propWidths in percent
     * @param tableWidth in percent
     * @param pageWidth
     * @param cellpadding
     * @param cellspacing
     * @param borders
     * @param borderColor
     * @param borderWidth
     * @param y
     * @return true if importing the row succeeded
     */
    public boolean importRow(Row row, float[] propWidths, int tableWidth, int pageWidth, int cellpadding,
                             int cellspacing, int borders, java.awt.Color borderColor, float borderWidth,
                             int y) {
        // the width of this row is the absolute witdh, calculated from the
        // proportional with of the table and the total width of the page
        this.origRow = row;
        this.width = pageWidth / 100 * tableWidth;
        this.cellpadding = cellpadding;
        this.cellspacing = cellspacing;
        this.borders = borders;
        this.borderColor = borderColor;
        this.borderWidth = borderWidth;

        if (this.borderWidth > 2) this.borderWidth = 2;

        int cellLeft = 0;
        for (int i = 0; i < row.columns(); i++) {
            Element cell = (Element) row.getCell(i);

            // cellWidth is an absolute argument
            // it's based on the absolute of this row and the proportional
            // width of this column
            int cellWidth = (int) (width / 100 * propWidths[i]);
            if (cell != null) {
                if (cell.type() == Element.CELL) {
                    RtfCell rtfCell = (RtfCell) cells.get(i);
                    cellLeft = rtfCell.importCell((Cell) cell, cellLeft, cellWidth, i, y, cellpadding);
                }
            } else {
                RtfCell rtfCell = (RtfCell) cells.get(i);
                cellLeft = rtfCell.importCell(null, cellLeft, cellWidth, i, y, cellpadding);
            }
        }

        // recalculate the cell right border and the cumulative width
        // on col spanning cells.
        // col + row spanning cells are also handled by this loop, because the real cell of
        // the upper left corner in such an col, row matrix is copied as first cell
        // in each row in this matrix
        int columns = row.columns();
        for (int i = 0; i < columns; i++) {
            RtfCell firstCell = (RtfCell) cells.get(i);
            Cell cell = firstCell.getStore();
            int cols = 0;
            if(cell != null) {
                cols = cell.colspan();
            }
            if (cols > 1) {
                RtfCell lastCell = (RtfCell) cells.get(i + cols - 1);
                firstCell.setCellRight(lastCell.getCellRight());
                int width = firstCell.getCellWidth();
                for (int j = i + 1; j < i + cols; j++) {
                    RtfCell cCell = (RtfCell) cells.get(j);
                    width += cCell.getCellWidth();
                }
                firstCell.setCellWidth(width);
                i += cols - 1;
            }
        }
        return true;
    }

    /**
     * Write the <code>RtfRow</code> to the specified <code>OutputStream</code>.
     *
     * @param os The <code>OutputStream</code> to which this <code>RtfRow</code>
     * should be written to.
     * @param rowNum The <code>index</code> of this row in the containing table.
     * @param table The <code>Table</code> which contains the original <code>Row</code>.
     * @return true if writing the row succeeded
     * @throws DocumentException
     * @throws IOException
     */
    public boolean writeRow(ByteArrayOutputStream os, int rowNum, Table table) throws DocumentException,
            IOException {
        os.write(RtfWriter.escape);
        os.write(rowBegin);
        os.write((byte) '\n');
        os.write(RtfWriter.escape);
        os.write(rowWidthStyle);
        os.write(RtfWriter.escape);
        os.write(rowWidth);
        writeInt(os, width);
//        os.write(RtfWriter.escape);
//        os.write(rowAutofit);
        if (mainTable.getOriginalTable().hasToFitPageCells()) {
            os.write(RtfWriter.escape);
            os.write(rowKeep);
        }
        // check if this row is a header row
        if (rowNum < table.firstDataRow()) {
            os.write(RtfWriter.escape);
            os.write(rowHeader);
        }
        os.write(RtfWriter.escape);
        switch (this.origRow.horizontalAlignment()) {
            case Element.ALIGN_LEFT:
                os.write(rowAlignLeft);
                break;
            case Element.ALIGN_CENTER:
                os.write(rowAlignCenter);
                break;
            case Element.ALIGN_RIGHT:
                os.write(rowAlignRight);
                break;
            default :
                os.write(rowAlignLeft);
                break;
        }
        os.write(RtfWriter.escape);
        os.write(graphLeft);
        writeInt(os, 10);
        if (((borders & Rectangle.LEFT) == Rectangle.LEFT) && (borderWidth > 0)) {
            writeBorder(os, rowBorderLeft);
        }
        if (((borders & Rectangle.TOP) == Rectangle.TOP) && (borderWidth > 0)) {
            writeBorder(os, rowBorderTop);
        }
        if (((borders & Rectangle.BOTTOM) == Rectangle.BOTTOM) && (borderWidth > 0)) {
            writeBorder(os, rowBorderBottom);
        }
        if (((borders & Rectangle.RIGHT) == Rectangle.RIGHT) && (borderWidth > 0)) {
            writeBorder(os, rowBorderRight);
        }
        if (((borders & Rectangle.BOX) == Rectangle.BOX) && (borderWidth > 0)) {
            writeBorder(os, rowBorderInlineHorizontal);
            writeBorder(os, rowBorderInlineVertical);
        }

        if (cellspacing > 0) {
            os.write(RtfWriter.escape);
            os.write(rowSpacingLeft);
            writeInt(os, cellspacing / 2);
            os.write(RtfWriter.escape);
            os.write(rowSpacingLeftStyle);
            os.write(RtfWriter.escape);
            os.write(rowSpacingTop);
            writeInt(os, cellspacing / 2);
            os.write(RtfWriter.escape);
            os.write(rowSpacingTopStyle);
            os.write(RtfWriter.escape);
            os.write(rowSpacingBottom);
            writeInt(os, cellspacing / 2);
            os.write(RtfWriter.escape);
            os.write(rowSpacingBottomStyle);
            os.write(RtfWriter.escape);
            os.write(rowSpacingRight);
            writeInt(os, cellspacing / 2);
            os.write(RtfWriter.escape);
            os.write(rowSpacingRightStyle);
        }
        os.write(RtfWriter.escape);
        os.write(rowPaddingLeft);
        writeInt(os, cellpadding / 2);
        os.write(RtfWriter.escape);
        os.write(rowPaddingRight);
        writeInt(os, cellpadding / 2);
        os.write(RtfWriter.escape);
        os.write(rowPaddingLeftStyle);
        os.write(RtfWriter.escape);
        os.write(rowPaddingRightStyle);
        os.write((byte) '\n');

        Iterator cellIterator = cells.iterator();
        while (cellIterator.hasNext()) {
            RtfCell cell = (RtfCell) cellIterator.next();
            cell.writeCellSettings(os);
        }

        os.write(RtfWriter.escape);
        os.write("intbl".getBytes());

        cellIterator = cells.iterator();
        while (cellIterator.hasNext()) {
            RtfCell cell = (RtfCell) cellIterator.next();
            cell.writeCellContent(os);
        }
        os.write(RtfWriter.delimiter);
        os.write(RtfWriter.escape);
        os.write(rowEnd);
        return true;
    }


    private void writeBorder(ByteArrayOutputStream os, byte[] borderType) throws IOException {
        // horizontal and vertical, top, left, bottom, right
        os.write(RtfWriter.escape);
        os.write(borderType);
        // line style
        os.write(RtfWriter.escape);
        os.write(RtfRow.tableBorder);
        // borderwidth
        os.write(RtfWriter.escape);
        os.write(RtfRow.tableBorderWidth);
        writeInt(os, (int) (borderWidth * RtfWriter.TWIPSFACTOR));
        // border color
        os.write(RtfWriter.escape);
        os.write(RtfRow.tableBorderColor);
        if (borderColor == null) {
            writeInt(os, writer.addColor(new Color(0, 0, 0)));
        } else {
            writeInt(os, writer.addColor(borderColor));
        }
        os.write((byte) '\n');
    }


    /**
     * <code>RtfTable</code>s call this method from their own setMerge() to
     * specify that a certain other cell is to be merged with it.
     *
     * @param x The column position of the cell to be merged
     * @param mergeType The merge type specifies the kind of merge to be applied
     * (MERGE_HORIZ_PREV, MERGE_VERT_PREV, MERGE_BOTH_PREV)
     * @param mergeCell The <code>RtfCell</code> that the cell at x and y is to
     * be merged with
     */
    public void setMerge(int x, int mergeType, RtfCell mergeCell) {
        RtfCell cell = (RtfCell) cells.get(x);
        cell.setMerge(mergeType, mergeCell);
    }

    /*
     * Write an Integer to the Outputstream.
     *
     * @param out The <code>OutputStream</code> to be written to.
     * @param i The int to be written.
     */
    private void writeInt(ByteArrayOutputStream out, int i) throws IOException {
        out.write(Integer.toString(i).getBytes());
    }
}

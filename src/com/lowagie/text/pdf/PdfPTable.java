/*
 * $Id: PdfPTable.java,v 1.66 2005/11/19 18:17:51 psoares33 Exp $
 * $Name:  $
 *
 * Copyright 2001, 2002 Paulo Soares
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
 * LGPL license (the "GNU LIBRARY GENERAL PUBLIC LICENSE"), in which case the
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

package com.lowagie.text.pdf;

import java.util.ArrayList;
import com.lowagie.text.Phrase;
import com.lowagie.text.Element;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.ElementListener;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.events.PdfPTableEventForwarder;

/** This is a table that can be put at an absolute position but can also
 * be added to the document as the class <CODE>Table</CODE>.
 * In the last case when crossing pages the table always break at full rows; if a
 * row is bigger than the page it is dropped silently to avoid infinite loops.
 * <P>
 * A PdfPTableEvent can be associated to the table to do custom drawing
 * when the table is rendered.
 * @author Paulo Soares (psoares@consiste.pt)
 */

public class PdfPTable implements Element{
    
    /** The index of the original <CODE>PdfcontentByte</CODE>.
     */    
    public static final int BASECANVAS = 0;
    /** The index of the duplicate <CODE>PdfContentByte</CODE> where the background will be drawn.
     */    
    public static final int BACKGROUNDCANVAS = 1;
    /** The index of the duplicate <CODE>PdfContentByte</CODE> where the border lines will be drawn.
     */    
    public static final int LINECANVAS = 2;
    /** The index of the duplicate <CODE>PdfContentByte</CODE> where the text will be drawn.
     */    
    public static final int TEXTCANVAS = 3;
    
    protected ArrayList rows = new ArrayList();
    protected float totalHeight = 0;
    protected PdfPCell currentRow[];
    protected int currentRowIdx = 0;
    protected PdfPCell defaultCell = new PdfPCell((Phrase)null);
    protected float totalWidth = 0;
    protected float relativeWidths[];
    protected float absoluteWidths[];
    protected PdfPTableEvent tableEvent;
    
/** Holds value of property headerRows. */
    protected int headerRows;
    
/** Holds value of property widthPercentage. */
    protected float widthPercentage = 80;
    
/** Holds value of property horizontalAlignment. */
    private int horizontalAlignment = Element.ALIGN_CENTER;
    
/** Holds value of property skipFirstHeader. */
    private boolean skipFirstHeader = false;

    protected boolean isColspan = false;
    
    protected int runDirection = PdfWriter.RUN_DIRECTION_DEFAULT;

    /**
     * Holds value of property lockedWidth.
     */
    private boolean lockedWidth = false;
    
    /**
     * Holds value of property splitRows.
     */
    private boolean splitRows = true;
    
/** The spacing before the table. */
    protected float spacingBefore;
    
/** The spacing after the table. */
    protected float spacingAfter;
    
    /**
     * Holds value of property extendLastRow.
     */
    private boolean extendLastRow;
    
    /**
     * Holds value of property headersInEvent.
     */
    private boolean headersInEvent;
    
    /**
     * Holds value of property splitLate.
     */
    private boolean splitLate = true;
    
    /**
     * Defines if the table should be kept
     * on one page if possible
     */
    private boolean keepTogether;
    
    /**
     * Holds value of property footerRows.
     */
    private int footerRows;
    
    protected PdfPTable() {
    }
    
    /** Constructs a <CODE>PdfPTable</CODE> with the relative column widths.
     * @param relativeWidths the relative column widths
     */    
    public PdfPTable(float relativeWidths[]) {
        if (relativeWidths == null)
            throw new NullPointerException("The widths array in PdfPTable constructor can not be null.");
        if (relativeWidths.length == 0)
            throw new IllegalArgumentException("The widths array in PdfPTable constructor can not have zero length.");
        this.relativeWidths = new float[relativeWidths.length];
        System.arraycopy(relativeWidths, 0, this.relativeWidths, 0, relativeWidths.length);
        absoluteWidths = new float[relativeWidths.length];
        calculateWidths();
        currentRow = new PdfPCell[absoluteWidths.length];
        keepTogether = false;
    }
    
    /** Constructs a <CODE>PdfPTable</CODE> with <CODE>numColumns</CODE> columns.
     * @param numColumns the number of columns
     */    
    public PdfPTable(int numColumns) {
        if (numColumns <= 0)
            throw new IllegalArgumentException("The number of columns in PdfPTable constructor must be greater than zero.");
        relativeWidths = new float[numColumns];
        for (int k = 0; k < numColumns; ++k)
            relativeWidths[k] = 1;
        absoluteWidths = new float[relativeWidths.length];
        calculateWidths();
        currentRow = new PdfPCell[absoluteWidths.length];
        keepTogether = false;
    }
    
    /** Constructs a copy of a <CODE>PdfPTable</CODE>.
     * @param table the <CODE>PdfPTable</CODE> to be copied
     */    
    public PdfPTable(PdfPTable table) {
        copyFormat(table);
        for (int k = 0; k < currentRow.length; ++k) {
            if (table.currentRow[k] == null)
                break;
            currentRow[k] = new PdfPCell(table.currentRow[k]);
        }
        for (int k = 0; k < table.rows.size(); ++k) {
            PdfPRow row = (PdfPRow)(table.rows.get(k));
            if (row != null)
                row = new PdfPRow(row);
            rows.add(row);
        }
    }
    
    /**
     * Makes a shallow copy of a table (format without content).
     * @param table
     * @return a shallow copy of the table
     */
    public static PdfPTable shallowCopy(PdfPTable table) {
        PdfPTable nt = new PdfPTable();
        nt.copyFormat(table);
        return nt;
    }

    /**
     * Copies the format of the sourceTable without copying the content. 
     * @param sourceTable
     */
    private void copyFormat(PdfPTable sourceTable) {
        relativeWidths = new float[sourceTable.relativeWidths.length];
        absoluteWidths = new float[sourceTable.relativeWidths.length];
        System.arraycopy(sourceTable.relativeWidths, 0, relativeWidths, 0, relativeWidths.length);
        System.arraycopy(sourceTable.absoluteWidths, 0, absoluteWidths, 0, relativeWidths.length);
        totalWidth = sourceTable.totalWidth;
        totalHeight = sourceTable.totalHeight;
        currentRowIdx = 0;
        tableEvent = sourceTable.tableEvent;
        runDirection = sourceTable.runDirection;
        defaultCell = new PdfPCell(sourceTable.defaultCell);
        currentRow = new PdfPCell[sourceTable.currentRow.length];
        isColspan = sourceTable.isColspan;
        splitRows = sourceTable.splitRows;
        spacingAfter = sourceTable.spacingAfter;
        spacingBefore = sourceTable.spacingBefore;
        headerRows = sourceTable.headerRows;
        footerRows = sourceTable.footerRows;
        lockedWidth = sourceTable.lockedWidth;
        extendLastRow = sourceTable.extendLastRow;
        headersInEvent = sourceTable.headersInEvent;
        widthPercentage = sourceTable.widthPercentage;
        splitLate = sourceTable.splitLate;
        skipFirstHeader = sourceTable.skipFirstHeader;
        horizontalAlignment = sourceTable.horizontalAlignment;
        keepTogether = sourceTable.keepTogether;
    }

    /** Sets the relative widths of the table.
     * @param relativeWidths the relative widths of the table.
     * @throws DocumentException if the number of widths is different than the number
     * of columns
     */    
    public void setWidths(float relativeWidths[]) throws DocumentException {
        if (relativeWidths.length != this.relativeWidths.length)
            throw new DocumentException("Wrong number of columns.");
        this.relativeWidths = new float[relativeWidths.length];
        System.arraycopy(relativeWidths, 0, this.relativeWidths, 0, relativeWidths.length);
        absoluteWidths = new float[relativeWidths.length];
        totalHeight = 0;
        calculateWidths();
        calculateHeights();
    }

    /** Sets the relative widths of the table.
     * @param relativeWidths the relative widths of the table.
     * @throws DocumentException if the number of widths is different than the number
     * of columns
     */    
    public void setWidths(int relativeWidths[]) throws DocumentException {
        float tb[] = new float[relativeWidths.length];
        for (int k = 0; k < relativeWidths.length; ++k)
            tb[k] = relativeWidths[k];
        setWidths(tb);
    }

    private void calculateWidths() {
        if (totalWidth <= 0)
            return;
        float total = 0;
        for (int k = 0; k < absoluteWidths.length; ++k) {
            total += relativeWidths[k];
        }
        for (int k = 0; k < absoluteWidths.length; ++k) {
            absoluteWidths[k] = totalWidth * relativeWidths[k] / total;
        }
    }
    
    /** Sets the full width of the table.
     * @param totalWidth the full width of the table.
     */    
    public void setTotalWidth(float totalWidth) {
        if (this.totalWidth == totalWidth)
            return;
        this.totalWidth = totalWidth;
        totalHeight = 0;
        calculateWidths();
        calculateHeights();
    }

    /** Sets the full width of the table from the absolute column width.
     * @param columnWidth the absolute width of each column
     * @throws DocumentException if the number of widths is different than the number
     * of columns
     */    
    public void setTotalWidth(float columnWidth[]) throws DocumentException {
        if (columnWidth.length != this.relativeWidths.length)
            throw new DocumentException("Wrong number of columns.");
        totalWidth = 0;
        for (int k = 0; k < columnWidth.length; ++k)
            totalWidth += columnWidth[k];
        setWidths(columnWidth);
    }

    /** Sets the percentage width of the table from the absolute column width.
     * @param columnWidth the absolute width of each column
     * @param pageSize the page size
     * @throws DocumentException
     */    
    public void setWidthPercentage(float columnWidth[], Rectangle pageSize) throws DocumentException {
        if (columnWidth.length != this.relativeWidths.length)
            throw new IllegalArgumentException("Wrong number of columns.");
        float totalWidth = 0;
        for (int k = 0; k < columnWidth.length; ++k)
            totalWidth += columnWidth[k];
        widthPercentage = totalWidth / (pageSize.right() - pageSize.left()) * 100f;
        setWidths(columnWidth);
    }

    /** Gets the full width of the table.
     * @return the full width of the table
     */    
    public float getTotalWidth() {
        return totalWidth;
    }

    void calculateHeights() {
        if (totalWidth <= 0)
            return;
        totalHeight = 0;
        for (int k = 0; k < rows.size(); ++k) {
            PdfPRow row = (PdfPRow)rows.get(k);
            if (row != null) {
                row.setWidths(absoluteWidths);
                totalHeight += row.getMaxHeights();
            }
        }
    }
    
    /**
     * Calculates the heights of the table.
     */
    public void calculateHeightsFast() {
        if (totalWidth <= 0)
            return;
        totalHeight = 0;
        for (int k = 0; k < rows.size(); ++k) {
            PdfPRow row = (PdfPRow)rows.get(k);
            if (row != null)
                totalHeight += row.getMaxHeights();
        }
    }
    
    /** Gets the default <CODE>PdfPCell</CODE> that will be used as
     * reference for all the <CODE>addCell</CODE> methods except
     * <CODE>addCell(PdfPCell)</CODE>.
     * @return default <CODE>PdfPCell</CODE>
     */    
    public PdfPCell getDefaultCell() {
        return defaultCell;
    }
    
    /** Adds a cell element.
     * @param cell the cell element
     */    
    public void addCell(PdfPCell cell) {
        PdfPCell ncell = new PdfPCell(cell);
        int colspan = ncell.getColspan();
        colspan = Math.max(colspan, 1);
        colspan = Math.min(colspan, currentRow.length - currentRowIdx);
        ncell.setColspan(colspan);
        if (colspan != 1)
            isColspan = true;
        int rdir = ncell.getRunDirection();
        if (rdir == PdfWriter.RUN_DIRECTION_DEFAULT)
            ncell.setRunDirection(runDirection);
        currentRow[currentRowIdx] = ncell;
        currentRowIdx += colspan;
        if (currentRowIdx >= currentRow.length) {
            if (runDirection == PdfWriter.RUN_DIRECTION_RTL) {
                PdfPCell rtlRow[] = new PdfPCell[absoluteWidths.length];
                int rev = currentRow.length;
                for (int k = 0; k < currentRow.length; ++k) {
                    PdfPCell rcell = currentRow[k];
                    int cspan = rcell.getColspan();
                    rev -= cspan;
                    rtlRow[rev] = rcell;
                    k += cspan - 1;
                }
                currentRow = rtlRow;
            }
            PdfPRow row = new PdfPRow(currentRow);
            if (totalWidth > 0) {
                row.setWidths(absoluteWidths);
                totalHeight += row.getMaxHeights();
            }
            rows.add(row);
            currentRow = new PdfPCell[absoluteWidths.length];
            currentRowIdx = 0;
        }
    }
    
    /** Adds a cell element.
     * @param text the text for the cell
     */    
    public void addCell(String text) {
        addCell(new Phrase(text));
    }
    
    /**
     * Adds a nested table.
     * @param table the table to be added to the cell
     */    
    public void addCell(PdfPTable table) {
        defaultCell.setTable(table);
        addCell(defaultCell);
        defaultCell.setTable(null);
    }
    
    /**
     * Adds an Image as Cell.
     * @param image the <CODE>Image</CODE> to add to the table. This image will fit in the cell
     */    
    public void addCell(Image image) {
        defaultCell.setImage(image);
        addCell(defaultCell);
        defaultCell.setImage(null);
    }
    
    /**
     * Adds a cell element.
     * @param phrase the <CODE>Phrase</CODE> to be added to the cell
     */    
    public void addCell(Phrase phrase) {
        defaultCell.setPhrase(phrase);
        addCell(defaultCell);
        defaultCell.setPhrase(null);
    }
    
    /**
     * Writes the selected rows to the document.
     * <P>
     * <CODE>canvases</CODE> is obtained from <CODE>beginWritingRows()</CODE>.
     * @param rowStart the first row to be written, zero index
     * @param rowEnd the last row to be written + 1. If it is -1 all the
     * rows to the end are written
     * @param xPos the x write coodinate
     * @param yPos the y write coodinate
     * @param canvases an array of 4 <CODE>PdfContentByte</CODE> obtained from
     * <CODE>beginWrittingRows()</CODE>
     * @return the y coordinate position of the bottom of the last row
     * @see #beginWritingRows(com.lowagie.text.pdf.PdfContentByte)
     */    
    public float writeSelectedRows(int rowStart, int rowEnd, float xPos, float yPos, PdfContentByte[] canvases) {
        return writeSelectedRows(0, -1, rowStart, rowEnd, xPos, yPos, canvases);
    }
    
    /** Writes the selected rows and columns to the document.
     * This method does not clip the columns; this is only important
     * if there are columns with colspan at boundaries.
     * <P>
     * <CODE>canvases</CODE> is obtained from <CODE>beginWritingRows()</CODE>.
     * <P>
     * The table event is only fired for complete rows.
     * @param colStart the first column to be written, zero index
     * @param colEnd the last column to be written + 1. If it is -1 all the
     * columns to the end are written
     * @param rowStart the first row to be written, zero index
     * @param rowEnd the last row to be written + 1. If it is -1 all the
     * rows to the end are written
     * @param xPos the x write coodinate
     * @param yPos the y write coodinate
     * @param canvases an array of 4 <CODE>PdfContentByte</CODE> obtained from
     * <CODE>beginWrittingRows()</CODE>
     * @return the y coordinate position of the bottom of the last row
     * @see #beginWritingRows(com.lowagie.text.pdf.PdfContentByte)
     */    
    public float writeSelectedRows(int colStart, int colEnd, int rowStart, int rowEnd, float xPos, float yPos, PdfContentByte[] canvases) {
        if (totalWidth <= 0)
            throw new RuntimeException("The table width must be greater than zero.");
        int size = rows.size();
        if (rowEnd < 0)
            rowEnd = size;
        rowEnd = Math.min(rowEnd, size);
        if (rowStart < 0)
            rowStart = 0;
        if (rowStart >= rowEnd)
            return yPos;
        if (colEnd < 0)
            colEnd = absoluteWidths.length;
        colEnd = Math.min(colEnd, absoluteWidths.length);
        if (colStart < 0)
            colStart = 0;
        colStart = Math.min(colStart, absoluteWidths.length);
        float yPosStart = yPos;
        for (int k = rowStart; k < rowEnd; ++k) {
            PdfPRow row = (PdfPRow)rows.get(k);
            if (row != null) {
                row.writeCells(colStart, colEnd, xPos, yPos, canvases);
                yPos -= row.getMaxHeights();
            }
        }
        if (tableEvent != null && colStart == 0 && colEnd == absoluteWidths.length) {
            float heights[] = new float[rowEnd - rowStart + 1];
            heights[0] = yPosStart;
            for (int k = rowStart; k < rowEnd; ++k) {
                PdfPRow row = (PdfPRow)rows.get(k);
                float hr = 0;
                if (row != null)
                    hr = row.getMaxHeights();
                heights[k - rowStart + 1] = heights[k - rowStart] - hr;
            }
            tableEvent.tableLayout(this, getEventWidths(xPos, rowStart, rowEnd, headersInEvent), heights, headersInEvent ? headerRows : 0, rowStart, canvases);
        }
        return yPos;
    }
    
    /**
     * Writes the selected rows to the document.
     * 
     * @param rowStart the first row to be written, zero index
     * @param rowEnd the last row to be written + 1. If it is -1 all the
     * rows to the end are written
     * @param xPos the x write coodinate
     * @param yPos the y write coodinate
     * @param canvas the <CODE>PdfContentByte</CODE> where the rows will
     * be written to
     * @return the y coordinate position of the bottom of the last row
     */    
    public float writeSelectedRows(int rowStart, int rowEnd, float xPos, float yPos, PdfContentByte canvas) {
        return writeSelectedRows(0, -1, rowStart, rowEnd, xPos, yPos, canvas);
    }
    
    /**
     * Writes the selected rows to the document.
     * This method clips the columns; this is only important
     * if there are columns with colspan at boundaries.
     * <P>
     * The table event is only fired for complete rows.
     * 
     * @param colStart the first column to be written, zero index
     * @param colEnd the last column to be written + 1. If it is -1 all the
     * @param rowStart the first row to be written, zero index
     * @param rowEnd the last row to be written + 1. If it is -1 all the
     * rows to the end are written
     * @param xPos the x write coodinate
     * @param yPos the y write coodinate
     * @param canvas the <CODE>PdfContentByte</CODE> where the rows will
     * be written to
     * @return the y coordinate position of the bottom of the last row
     */    
    public float writeSelectedRows(int colStart, int colEnd, int rowStart, int rowEnd, float xPos, float yPos, PdfContentByte canvas) {
        if (colEnd < 0)
            colEnd = absoluteWidths.length;
        colEnd = Math.min(colEnd, absoluteWidths.length);
        if (colStart < 0)
            colStart = 0;
        colStart = Math.min(colStart, absoluteWidths.length);
        if (colStart != 0 || colEnd != absoluteWidths.length) {
            float w = 0;
            for (int k = colStart; k < colEnd; ++k)
                w += absoluteWidths[k];
            canvas.saveState();
            float lx = 0;
            float rx = 0;
            if (colStart == 0)
                lx = 10000;
            if (colEnd == absoluteWidths.length)
                rx = 10000;
            canvas.rectangle(xPos - lx, -10000, w + lx + rx, 20000);
            canvas.clip();
            canvas.newPath();
        }
        PdfContentByte[] canvases = beginWritingRows(canvas);
        float y = writeSelectedRows(colStart, colEnd, rowStart, rowEnd, xPos, yPos, canvases);
        endWritingRows(canvases);
        if (colStart != 0 || colEnd != absoluteWidths.length)
            canvas.restoreState();
        return y;
    }
    
    /** Gets and initializes the 4 layers where the table is written to. The text or graphics are added to
     * one of the 4 <CODE>PdfContentByte</CODE> returned with the following order:<p>
     * <ul>
     * <li><CODE>PdfPtable.BASECANVAS</CODE> - the original <CODE>PdfContentByte</CODE>. Anything placed here
     * will be under the table.
     * <li><CODE>PdfPtable.BACKGROUNDCANVAS</CODE> - the layer where the background goes to.
     * <li><CODE>PdfPtable.LINECANVAS</CODE> - the layer where the lines go to.
     * <li><CODE>PdfPtable.TEXTCANVAS</CODE> - the layer where the text go to. Anything placed here
     * will be over the table.
     * </ul><p>
     * The layers are placed in sequence on top of each other.
     * @param canvas the <CODE>PdfContentByte</CODE> where the rows will
     * be written to
     * @return an array of 4 <CODE>PdfContentByte</CODE>
     * @see #writeSelectedRows(int, int, float, float, PdfContentByte[])
     */    
    public static PdfContentByte[] beginWritingRows(PdfContentByte canvas) {
        return new PdfContentByte[]{
            canvas,
            canvas.getDuplicate(),
            canvas.getDuplicate(),
            canvas.getDuplicate(),
        };
    }
    
    /** Finishes writing the table.
     * @param canvases the array returned by <CODE>beginWritingRows()</CODE>
     */    
    public static void endWritingRows(PdfContentByte[] canvases) {
        PdfContentByte canvas = canvases[BASECANVAS];
        canvas.saveState();
        canvas.add(canvases[BACKGROUNDCANVAS]);
        canvas.restoreState();
        canvas.saveState();
        canvas.setLineCap(2);
        canvas.resetRGBColorStroke();
        canvas.add(canvases[LINECANVAS]);
        canvas.restoreState();
        canvas.add(canvases[TEXTCANVAS]);
    }
    
    /** Gets the number of rows in this table.
     * @return the number of rows in this table
     */    
    public int size() {
        return rows.size();
    }
    
    /** Gets the total height of the table.
     * @return the total height of the table
     */    
    public float getTotalHeight() {
        return totalHeight;
    }
    
    /** Gets the height of a particular row.
     * @param idx the row index (starts at 0)
     * @return the height of a particular row
     */    
    public float getRowHeight(int idx) {
        if (totalWidth <= 0 || idx < 0 || idx >= rows.size())
            return 0;
        PdfPRow row = (PdfPRow)rows.get(idx);
        if (row == null)
            return 0;
        return row.getMaxHeights();
    }
    
    /** Gets the height of the rows that constitute the header as defined by
     * <CODE>setHeaderRows()</CODE>.
     * @return the height of the rows that constitute the header
     */    
    public float getHeaderHeight() {
        float total = 0;
        int size = Math.min(rows.size(), headerRows);
        for (int k = 0; k < size; ++k) {
            PdfPRow row = (PdfPRow)rows.get(k);
            if (row != null)
                total += row.getMaxHeights();
        }
        return total;
    }
    
    /** Deletes a row from the table.
     * @param rowNumber the row to be deleted
     * @return <CODE>true</CODE> if the row was deleted
     */    
    public boolean deleteRow(int rowNumber) {
        if (rowNumber < 0 || rowNumber >= rows.size()) {
            return false;
        }
        if (totalWidth > 0) {
            PdfPRow row = (PdfPRow)rows.get(rowNumber);
            if (row != null)
                totalHeight -= row.getMaxHeights();
        }
        rows.remove(rowNumber);
        return true;
    }
    
    /** Deletes the last row in the table.
     * @return <CODE>true</CODE> if the last row was deleted
     */    
    public boolean deleteLastRow() {
        return deleteRow(rows.size() - 1);
    }
    
    /**
     * Removes all of the rows except headers
     */
    public void deleteBodyRows() {
        ArrayList rows2 = new ArrayList();
        for (int k = 0; k < headerRows; ++k)
            rows2.add(rows.get(k));
        rows = rows2;
        totalHeight = 0;
        if (totalWidth > 0)
            totalHeight = getHeaderHeight();
    }

    /** Gets the number of the rows that constitute the header.
     * @return the number of the rows that constitute the header
     */
    public int getHeaderRows() {
        return headerRows;
    }
    
    /** Sets the number of the top rows that constitute the header.
     * This header has only meaning if the table is added to <CODE>Document</CODE>
     * and the table crosses pages.
     * @param headerRows the number of the top rows that constitute the header
     */
    public void setHeaderRows(int headerRows) {
        if (headerRows < 0)
            headerRows = 0;
        this.headerRows = headerRows;
    }
    
    /**
     * Gets all the chunks in this element.
     *
     * @return	an <CODE>ArrayList</CODE>
     */
    public ArrayList getChunks() {
        return new ArrayList();
    }
    
    /**
     * Gets the type of the text element.
     *
     * @return	a type
     */
    public int type() {
        return Element.PTABLE;
    }
    
    /**
     * Processes the element by adding it (or the different parts) to an
     * <CODE>ElementListener</CODE>.
     *
     * @param	listener	an <CODE>ElementListener</CODE>
     * @return	<CODE>true</CODE> if the element was processed successfully
     */
    public boolean process(ElementListener listener) {
        try {
            return listener.add(this);
        }
        catch(DocumentException de) {
            return false;
        }
    }
    
    /** Gets the width percentage that the table will occupy in the page.
     * @return the width percentage that the table will occupy in the page
     */
    public float getWidthPercentage() {
        return widthPercentage;
    }
    
    /** Sets the width percentage that the table will occupy in the page.
     * @param widthPercentage the width percentage that the table will occupy in the page
     */
    public void setWidthPercentage(float widthPercentage) {
        this.widthPercentage = widthPercentage;
    }
    
    /** Gets the horizontal alignment of the table relative to the page.
     * @return the horizontal alignment of the table relative to the page
     */
    public int getHorizontalAlignment() {
        return horizontalAlignment;
    }
    
    /** Sets the horizontal alignment of the table relative to the page.
     * It only has meaning if the width percentage is less than
     * 100%.
     * @param horizontalAlignment the horizontal alignment of the table relative to the page
     */
    public void setHorizontalAlignment(int horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
    }
    
    /**
     * Gets a row with a given index
     * (added by Jin-Hsia Yang).
     * @param idx
     * @return the row at position idx
     */
    public PdfPRow getRow(int idx) {
        return (PdfPRow)rows.get(idx);
    }

    /**
     * Gets an arraylist with all the rows in the table.
     * @return an arraylist
     */
    public ArrayList getRows() {
        return rows;
    }

    /** Sets the table event for this table.
     * @param event the table event for this table
     */    
    public void setTableEvent(PdfPTableEvent event) {
    	if (event == null) this.tableEvent = null;
    	else if (this.tableEvent == null) this.tableEvent = event;
    	else if (this.tableEvent instanceof PdfPTableEventForwarder) ((PdfPTableEventForwarder)this.tableEvent).addTableEvent(event);
    	else {
    		PdfPTableEventForwarder forward = new PdfPTableEventForwarder();
    		forward.addTableEvent(this.tableEvent);
    		forward.addTableEvent(event);
    		this.tableEvent = forward;
    	}
    }
    
    /** Gets the table event for this page.
     * @return the table event for this page
     */    
    public PdfPTableEvent getTableEvent() {
        return tableEvent;
    }
    
    /** Gets the absolute sizes of each column width.
     * @return he absolute sizes of each column width
     */    
    public float[] getAbsoluteWidths() {
        return absoluteWidths;
    }
    
    float [][] getEventWidths(float xPos, int firstRow, int lastRow, boolean includeHeaders) {
        if (includeHeaders) {
            firstRow = Math.max(firstRow, headerRows);
            lastRow = Math.max(lastRow, headerRows);
        }
        float widths[][] = new float[(includeHeaders ? headerRows : 0) + lastRow - firstRow][];
        if (isColspan) {
            int n = 0;
            if (includeHeaders) {
                for (int k = 0; k < headerRows; ++k) {
                    PdfPRow row = (PdfPRow)rows.get(k);
                    if (row == null)
                        ++n;
                    else
                        widths[n++] = row.getEventWidth(xPos);
                }
            }
            for (; firstRow < lastRow; ++firstRow) {
                    PdfPRow row = (PdfPRow)rows.get(firstRow);
                    if (row == null)
                        ++n;
                    else
                        widths[n++] = row.getEventWidth(xPos);
            }
        }
        else {
            float width[] = new float[absoluteWidths.length + 1];
            width[0] = xPos;
            for (int k = 0; k < absoluteWidths.length; ++k)
                width[k + 1] = width[k] + absoluteWidths[k];
            for (int k = 0; k < widths.length; ++k)
                widths[k] = width;
        }
        return widths;
    }


    /** Getter for property skipFirstHeader.
     * @return Value of property skipFirstHeader.
     */
    public boolean isSkipFirstHeader() {
        return skipFirstHeader;
    }
    
    /** Skips the printing of the first header. Used when printing
     * tables in succession belonging to the same printed table aspect.
     * @param skipFirstHeader New value of property skipFirstHeader.
     */
    public void setSkipFirstHeader(boolean skipFirstHeader) {
        this.skipFirstHeader = skipFirstHeader;
    }

    /**
     * Sets the run direction of the contents of the table.
     * @param runDirection
     */
    public void setRunDirection(int runDirection) {
        if (runDirection < PdfWriter.RUN_DIRECTION_DEFAULT || runDirection > PdfWriter.RUN_DIRECTION_RTL)
            throw new RuntimeException("Invalid run direction: " + runDirection);
        this.runDirection = runDirection;
    }
    
    /**
     * Returns the run direction of the contents in the table.
     * @return One of the following values: PdfWriter.RUN_DIRECTION_DEFAULT, PdfWriter.RUN_DIRECTION_NO_BIDI, PdfWriter.RUN_DIRECTION_LTR or PdfWriter.RUN_DIRECTION_RTL.
     */
    public int getRunDirection() {
        return runDirection;
    }
    
    /**
     * Getter for property lockedWidth.
     * @return Value of property lockedWidth.
     */
    public boolean isLockedWidth() {
        return this.lockedWidth;
    }
    
    /**
     * Uses the value in <CODE>setTotalWidth()</CODE> in <CODE>Document.add()</CODE>.
     * @param lockedWidth <CODE>true</CODE> to use the value in <CODE>setTotalWidth()</CODE> in <CODE>Document.add()</CODE>
     */
    public void setLockedWidth(boolean lockedWidth) {
        this.lockedWidth = lockedWidth;
    }
    
    /**
     * Gets the split value.
     * @return true to split; false otherwise
     */
    public boolean isSplitRows() {
        return this.splitRows;
    }
    
    /**
     * When set the rows that won't fit in the page will be split. 
     * Note that it takes at least twice the memory to handle a split table row
     * than a normal table. <CODE>true</CODE> by default.
     * @param splitRows true to split; false otherwise
     */
    public void setSplitRows(boolean splitRows) {
        this.splitRows = splitRows;
    }
    
/**
 * Sets the spacing before this table.
 *
 * @param	spacing		the new spacing
 */
    
    public void setSpacingBefore(float spacing) {
        this.spacingBefore = spacing;
    }
    
/**
 * Sets the spacing after this table.
 *
 * @param	spacing		the new spacing
 */
    
    public void setSpacingAfter(float spacing) {
        this.spacingAfter = spacing;
    }    

/**
 * Gets the spacing before this table.
 *
 * @return	the spacing
 */
    
    public float spacingBefore() {
        return spacingBefore;
    }
    
/**
 * Gets the spacing before this table.
 *
 * @return	the spacing
 */
    
    public float spacingAfter() {
        return spacingAfter;
    }    
    
    /**
     *  Gets the value of the last row extension.
     * @return true if the last row will extend; false otherwise
     */
    public boolean isExtendLastRow() {
        return this.extendLastRow;
    }
    
    /**
     * When set the last row will be extended to fill all the remaining space to the
     * bottom boundary.
     * @param extendLastRow true to extend the last row; false otherwise
     */
    public void setExtendLastRow(boolean extendLastRow) {
        this.extendLastRow = extendLastRow;
    }
    
    /**
     * Gets the header status inclusion in PdfPTableEvent.
     * @return true if the headers are included; false otherwise
     */
    public boolean isHeadersInEvent() {
        return this.headersInEvent;
    }
    
    /**
     * When set the PdfPTableEvent will include the headers.
     * @param headersInEvent true to include the headers; false otherwise
     */
    public void setHeadersInEvent(boolean headersInEvent) {
        this.headersInEvent = headersInEvent;
    }
    
    /**
     * Gets the property splitLate.
     * @return the property splitLate
     */
    public boolean isSplitLate() {
        return this.splitLate;
    }
    
    /**
     * If true the row will only split if it's the first one in an empty page.
     * It's true by default.
     *<p>
     * It's only meaningful if setSplitRows(true).
     * @param splitLate the property value
     */
    public void setSplitLate(boolean splitLate) {
        this.splitLate = splitLate;
    }
    
    /**
     * If true the table will be kept on one page if it fits, by forcing a 
     * new page if it doesn't fit on the current page. The default is to
     * split the table over multiple pages.
     *
     * @param p_KeepTogether whether to try to keep the table on one page
     */
    public void setKeepTogether(boolean p_KeepTogether) {
        keepTogether = p_KeepTogether;
    }
     
    public boolean getKeepTogether() {
        return keepTogether;
    }
    
    /**
     * Gets the number of rows in the footer.
     * @return the number of rows in the footer
     */
    public int getFooterRows() {
        return this.footerRows;
    }
    
    /**
     * Sets the number of rows to be used for the footer. The number
     * of footer rows are subtracted from the header rows. For
     * example, for a table with two header rows and one footer row the
     * code would be:
     * <p>
     * <PRE>
     * table.setHeaderRows(3);
     * table.setFooterRows(1);
     * </PRE>
     * <p>
     * Row 0 and 1 will be the header rows and row 2 will be the footer row.
     * @param footerRows the number of rows to be used for the footer
     */
    public void setFooterRows(int footerRows) {
        if (footerRows < 0)
            footerRows = 0;
        this.footerRows = footerRows;
    }
    
}
/*
 * $Id: PdfPCell.java,v 1.59 2006/02/16 16:17:49 psoares33 Exp $
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

import com.lowagie.text.Element;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Image;
import com.lowagie.text.Chunk;
import com.lowagie.text.pdf.events.PdfPCellEventForwarder;

/** A cell in a PdfPTable.
 */

public class PdfPCell extends Rectangle{
    
    private ColumnText column = new ColumnText(null);
    
    /** Holds value of property verticalAlignment. */
    private int verticalAlignment = Element.ALIGN_TOP;
    
    /** Holds value of property paddingLeft. */
    private float paddingLeft = 2;
    
    /** Holds value of property paddingLeft. */
    private float paddingRight = 2;
    
    /** Holds value of property paddingTop. */
    private float paddingTop = 2;
    
    /** Holds value of property paddingBottom. */
    private float paddingBottom = 2;
    
    /** Holds value of property fixedHeight. */
    private float fixedHeight = 0;
    
    /** Holds value of property noWrap. */
    private boolean noWrap = false;
    
    /** Holds value of property table. */
    private PdfPTable table;
    
    /** Holds value of property minimumHeight. */
    private float minimumHeight;
    
    /** Holds value of property colspan. */
    private int colspan = 1;
    
    /** Holds value of property image. */
    private Image image;
    
    /** Holds value of property cellEvent. */
    private PdfPCellEvent cellEvent;

    /** Holds value of property useDescender. */
    private boolean useDescender;

    /** Increases padding to include border if true */
    private boolean useBorderPadding = false;


    /** The text in the cell. */
    protected Phrase phrase;

    /** Constructs an empty <CODE>PdfPCell</CODE>.
     * The default padding is 2.
     */
    public PdfPCell() {
        super(0, 0, 0, 0);
        borderWidth = 0.5f;
        border = BOX;
        column.setLeading(0, 1);
    }

    /** Constructs a <CODE>PdfPCell</CODE> with a <CODE>Phrase</CODE>.
     * The default padding is 2.
     * @param phrase the text
     */
    public PdfPCell(Phrase phrase) {
        super(0, 0, 0, 0);
        borderWidth = 0.5f;
        border = BOX;
        column.addText(this.phrase = phrase);
        column.setLeading(0, 1);
    }
    
    /** Constructs a <CODE>PdfPCell</CODE> with an <CODE>Image</CODE>.
     * The default padding is 0.
     * @param image the <CODE>Image</CODE>
     */
    public PdfPCell(Image image) {
        super(0, 0, 0, 0);
        borderWidth = 0.5f;
        border = BOX;
        column.addText(this.phrase = new Phrase(new Chunk(image, 0, 0)));
        column.setLeading(0, 1);
        setPadding(0);
    }
    
    /** Constructs a <CODE>PdfPCell</CODE> with an <CODE>Image</CODE>.
     * The default padding is 0.25 for a border width of 0.5.
     * @param image the <CODE>Image</CODE>
     * @param fit <CODE>true</CODE> to fit the image to the cell
     */
    public PdfPCell(Image image, boolean fit) {
        super(0, 0, 0, 0);
        if (fit) {
            borderWidth = 0.5f;
            border = BOX;
            this.image = image;
            column.setLeading(0, 1);
            setPadding(borderWidth / 2);
        }
        else {
            borderWidth = 0.5f;
            border = BOX;
            column.addText(this.phrase = new Phrase(new Chunk(image, 0, 0)));
            column.setLeading(0, 1);
            setPadding(0);
        }
    }
    
    /** Constructs a <CODE>PdfPCell</CODE> with a <CODE>PdfPtable</CODE>.
     * This constructor allows nested tables.
     * The default padding is 0.
     * @param table The <CODE>PdfPTable</CODE>
     */
    public PdfPCell(PdfPTable table) {
        super(0, 0, 0, 0);
        borderWidth = 0.5f;
        border = BOX;
        column.setLeading(0, 1);
        setPadding(0);
        this.table = table;
        table.setWidthPercentage(100);
        table.setExtendLastRow(true);
        column.addElement(table);
    }
    
    /** Constructs a deep copy of a <CODE>PdfPCell</CODE>.
     * @param cell the <CODE>PdfPCell</CODE> to duplicate
     */
    public PdfPCell(PdfPCell cell) {
        super(cell.llx, cell.lly, cell.urx, cell.ury);
        cloneNonPositionParameters(cell);
        verticalAlignment = cell.verticalAlignment;
        paddingLeft = cell.paddingLeft;
        paddingRight = cell.paddingRight;
        paddingTop = cell.paddingTop;
        paddingBottom = cell.paddingBottom;
        phrase = cell.phrase;
        fixedHeight = cell.fixedHeight;
        minimumHeight = cell.minimumHeight;
        noWrap = cell.noWrap;
        colspan = cell.colspan;
        if (cell.table != null)
            table = new PdfPTable(cell.table);
        image = Image.getInstance(cell.image);
        cellEvent = cell.cellEvent;
        useDescender = cell.useDescender;
        column = ColumnText.duplicate(cell.column);
        useBorderPadding = cell.useBorderPadding;
        rotation = cell.rotation;
    }
    
    /**
     * Adds an iText element to the cell.
     * @param element
     */
    public void addElement(Element element) {
        if (table != null) {
            table = null;
            column.setText(null);
        }
        column.addElement(element);
    }
    
    /** Gets the <CODE>Phrase</CODE> from this cell.
     * @return the <CODE>Phrase</CODE>
     */
    public Phrase getPhrase() {
        return phrase;
    }
    
    /** Sets the <CODE>Phrase</CODE> for this cell.
     * @param phrase the <CODE>Phrase</CODE>
     */
    public void setPhrase(Phrase phrase) {
        table = null;
        image = null;
        column.setText(this.phrase = phrase);
    }
    
    /** Gets the horizontal alignment for the cell.
     * @return the horizontal alignment for the cell
     */
    public int getHorizontalAlignment() {
        return column.getAlignment();
    }
    
    /** Sets the horizontal alignment for the cell. It could be
     * <CODE>Element.ALIGN_CENTER</CODE> for example.
     * @param horizontalAlignment The horizontal alignment
     */
    public void setHorizontalAlignment(int horizontalAlignment) {
        column.setAlignment(horizontalAlignment);
    }
    
    /** Gets the vertical alignment for the cell.
     * @return the vertical alignment for the cell
     */
    public int getVerticalAlignment() {
        return verticalAlignment;
    }
    
    /** Sets the vertical alignment for the cell. It could be
     * <CODE>Element.ALIGN_MIDDLE</CODE> for example.
     * @param verticalAlignment The vertical alignment
     */
    public void setVerticalAlignment(int verticalAlignment) {
        if (table != null)
            table.setExtendLastRow(verticalAlignment == Element.ALIGN_TOP);
        this.verticalAlignment = verticalAlignment;
    }
    
    /** Gets the effective left padding.  This will include
     *  the left border width if {@link #isUseBorderPadding()} is true.
     * @return effective value of property paddingLeft.
     */
    public float getEffectivePaddingLeft() {
        return paddingLeft + (isUseBorderPadding() ? (getBorderWidthLeft()/(isUseVariableBorders()?1f:2f)) : 0);
    }
    
    /**
     * @return Value of property paddingLeft.
     */
    public float getPaddingLeft() {
        return paddingLeft;
    }

    /**
     * Setter for property paddingLeft.
     * @param paddingLeft New value of property paddingLeft.
     */
    public void setPaddingLeft(float paddingLeft) {
        this.paddingLeft = paddingLeft;
    }
    
    /** Gets the effective right padding.  This will include
     *  the right border width if {@link #isUseBorderPadding()} is true.
     * @return effective value of property paddingRight.
     */
    public float getEffectivePaddingRight() {
        return paddingRight + (isUseBorderPadding() ? (getBorderWidthRight()/(isUseVariableBorders()?1f:2f)) : 0);
    }
    
    /**
     * Getter for property paddingRight.
     * @return Value of property paddingRight.
     */
    public float getPaddingRight() {
        return paddingRight;
    }

    /**
     * Setter for property paddingRight.
     * @param paddingRight New value of property paddingRight.
     */
    public void setPaddingRight(float paddingRight) {
        this.paddingRight = paddingRight;
    }
    
    /** Gets the effective top padding.  This will include
     *  the top border width if {@link #isUseBorderPadding()} is true.
     * @return effective value of property paddingTop.
     */
    public float getEffectivePaddingTop() {
        return paddingTop + (isUseBorderPadding() ? (getBorderWidthTop()/(isUseVariableBorders()?1f:2f)) : 0);
    }
    
    /**
     * Getter for property paddingTop.
     * @return Value of property paddingTop.
     */
    public float getPaddingTop() {
        return paddingTop;
    }

    /**
     * Setter for property paddingTop.
     * @param paddingTop New value of property paddingTop.
     */
    public void setPaddingTop(float paddingTop) {
        this.paddingTop = paddingTop;
    }
    
    /** Gets the effective bottom padding.  This will include
     *  the bottom border width if {@link #isUseBorderPadding()} is true.
     * @return effective value of property paddingBottom.
     */
    public float getEffectivePaddingBottom() {
        return paddingBottom + (isUseBorderPadding() ? (getBorderWidthBottom()/(isUseVariableBorders()?1f:2f)) : 0);
    }
    
    /**
     * Getter for property paddingBottom.
     * @return Value of property paddingBottom.
     */
    public float getPaddingBottom() {
        return paddingBottom;
    }

    /**
     * Setter for property paddingBottom.
     * @param paddingBottom New value of property paddingBottom.
     */
    public void setPaddingBottom(float paddingBottom) {
        this.paddingBottom = paddingBottom;
    }
    
    /**
     * Sets the padding of the contents in the cell (space between content and border).
     * @param padding
     */
    public void setPadding(float padding) {
        paddingBottom = padding;
        paddingTop = padding;
        paddingLeft = padding;
        paddingRight = padding;
    }

    /**
     * If true, then effective padding will include border widths
     * @return true if effective padding includes border widths
     */
    public boolean isUseBorderPadding() {
        return useBorderPadding;
    }

    /**
     * Adjusts effective padding to include border widths.
     * @param use adjust effective padding if true
     */
    public void setUseBorderPadding(boolean use) {
        useBorderPadding = use;
    }

    /**
     * Sets the leading fixed and variable. The resultant leading will be
     * fixedLeading+multipliedLeading*maxFontSize where maxFontSize is the
     * size of the bigest font in the line.
     * @param fixedLeading the fixed leading
     * @param multipliedLeading the variable leading
     */
    public void setLeading(float fixedLeading, float multipliedLeading) {
        column.setLeading(fixedLeading, multipliedLeading);
    }
    
    /**
     * Gets the fixed leading
     * @return the leading
     */
    public float getLeading() {
        return column.getLeading();
    }
    
    /**
     * Gets the variable leading
     * @return the leading
     */
    public float getMultipliedLeading() {
        return column.getMultipliedLeading();
    }
    
    /**
     * Sets the first paragraph line indent.
     * @param indent the indent
     */
    public void setIndent(float indent) {
        column.setIndent(indent);
    }
    
    /**
     * Gets the first paragraph line indent.
     * @return the indent
     */
    public float getIndent() {
        return column.getIndent();
    }
    
    /**
     * Gets the extra space between paragraphs.
     * @return the extra space between paragraphs
     */
    public float getExtraParagraphSpace() {
        return column.getExtraParagraphSpace();
    }
    
    /**
     * Sets the extra space between paragraphs.
     * @param extraParagraphSpace the extra space between paragraphs
     */
    public void setExtraParagraphSpace(float extraParagraphSpace) {
        column.setExtraParagraphSpace(extraParagraphSpace);
    }
    
    /**
     * Getter for property fixedHeight.
     * @return Value of property fixedHeight.
     */
    public float getFixedHeight() {
        return fixedHeight;
    }
    
    /**
     * Setter for property fixedHeight.
     * @param fixedHeight New value of property fixedHeight.
     */
    public void setFixedHeight(float fixedHeight) {
        this.fixedHeight = fixedHeight;
        minimumHeight = 0;
    }
    
    /**
     * Getter for property noWrap.
     * @return Value of property noWrap.
     */
    public boolean isNoWrap() {
        return noWrap;
    }
    
    /**
     * Setter for property noWrap.
     * @param noWrap New value of property noWrap.
     */
    public void setNoWrap(boolean noWrap) {
        this.noWrap = noWrap;
    }
    
    /**
     * Getter for property table.
     * @return Value of property table.
     */
    PdfPTable getTable() {
        return table;
    }
    
    void setTable(PdfPTable table) {
        this.table = table;
        column.setText(null);
        image = null;
        if (table != null) {
            table.setExtendLastRow(verticalAlignment == Element.ALIGN_TOP);
            column.addElement(table);
            table.setWidthPercentage(100);
        }
    }
    
    /** Getter for property minimumHeight.
     * @return Value of property minimumHeight.
     */
    public float getMinimumHeight() {
        return minimumHeight;
    }
    
    /** Setter for property minimumHeight.
     * @param minimumHeight New value of property minimumHeight.
     */
    public void setMinimumHeight(float minimumHeight) {
        this.minimumHeight = minimumHeight;
        fixedHeight = 0;
    }
    
    /** Getter for property colspan.
     * @return Value of property colspan.
     */
    public int getColspan() {
        return colspan;
    }
    
    /** Setter for property colspan.
     * @param colspan New value of property colspan.
     */
    public void setColspan(int colspan) {
        this.colspan = colspan;
    }
    
    /**
     * Sets the following paragraph lines indent.
     * @param indent the indent
     */
    public void setFollowingIndent(float indent) {
        column.setFollowingIndent(indent);
    }
    
    /**
     * Gets the following paragraph lines indent.
     * @return the indent
     */
    public float getFollowingIndent() {
        return column.getFollowingIndent();
    }
    
    /**
     * Sets the right paragraph lines indent.
     * @param indent the indent
     */
    public void setRightIndent(float indent) {
        column.setRightIndent(indent);
    }
    
    /**
     * Gets the right paragraph lines indent.
     * @return the indent
     */
    public float getRightIndent() {
        return column.getRightIndent();
    }
    
    /** Gets the space/character extra spacing ratio for
     * fully justified text.
     * @return the space/character extra spacing ratio
     */
    public float getSpaceCharRatio() {
        return column.getSpaceCharRatio();
    }
    
    /** Sets the ratio between the extra word spacing and the extra character spacing
     * when the text is fully justified.
     * Extra word spacing will grow <CODE>spaceCharRatio</CODE> times more than extra character spacing.
     * If the ratio is <CODE>PdfWriter.NO_SPACE_CHAR_RATIO</CODE> then the extra character spacing
     * will be zero.
     * @param spaceCharRatio the ratio between the extra word spacing and the extra character spacing
     */
    public void setSpaceCharRatio(float spaceCharRatio) {
        column.setSpaceCharRatio(spaceCharRatio);
    }
    
    /**
     * Sets the run direction of the text content in the cell (PdfWriter.RUN_DIRECTION_DEFAULT, PdfWriter.RUN_DIRECTION_NO_BIDI, PdfWriter.RUN_DIRECTION_LTR or PdfWriter.RUN_DIRECTION_RTL).
     * @param runDirection
     */
    public void setRunDirection(int runDirection) {
        column.setRunDirection(runDirection);
    }
    
    /**
     * Gets the run direction of the text content in the cell
     * @return One of the following values: PdfWriter.RUN_DIRECTION_DEFAULT, PdfWriter.RUN_DIRECTION_NO_BIDI, PdfWriter.RUN_DIRECTION_LTR or PdfWriter.RUN_DIRECTION_RTL.
     */
    public int getRunDirection() {
        return column.getRunDirection();
    }
    
    /** Getter for property image.
     * @return Value of property image.
     *
     */
    public Image getImage() {
        return this.image;
    }
    
    /** Setter for property image.
     * @param image New value of property image.
     *
     */
    public void setImage(Image image) {
        column.setText(null);
        table = null;
        this.image = image;
    }
    
    /** Gets the cell event for this cell.
     * @return the cell event
     *
     */
    public PdfPCellEvent getCellEvent() {
        return this.cellEvent;
    }
    
    /** Sets the cell event for this cell.
     * @param event the cell event
     *
     */
    public void setCellEvent(PdfPCellEvent event) {
    	if (event == null) this.cellEvent = null;
    	else if (this.cellEvent == null) this.cellEvent = event;
    	else if (this.cellEvent instanceof PdfPCellEventForwarder) ((PdfPCellEventForwarder)this.cellEvent).addCellEvent(event);
    	else {
    		PdfPCellEventForwarder forward = new PdfPCellEventForwarder();
    		forward.addCellEvent(this.cellEvent);
    		forward.addCellEvent(event);
    		this.cellEvent = forward;
    	}
    }
    
    /** Gets the arabic shaping options.
     * @return the arabic shaping options
     */
    public int getArabicOptions() {
        return column.getArabicOptions();
    }
    
    /** Sets the arabic shaping options. The option can be AR_NOVOWEL,
     * AR_COMPOSEDTASHKEEL and AR_LIG.
     * @param arabicOptions the arabic shaping options
     */
    public void setArabicOptions(int arabicOptions) {
        column.setArabicOptions(arabicOptions);
    }

    /** Gets state of first line height based on max ascender
     * @return true if an ascender is to be used.
     */
    public boolean isUseAscender() {
        return column.isUseAscender();
    }

    /** Enables/ Disables adjustment of first line height based on max ascender.
     *
     * @param use adjust height if true
     */
    public void setUseAscender(boolean use) {
        column.setUseAscender(use);
    }


    /** Getter for property useDescender.
     * @return Value of property useDescender.
     *
     */
    public boolean isUseDescender() {
        return this.useDescender;
    }

    /** Setter for property useDescender.
     * @param useDescender New value of property useDescender.
     *
     */
    public void setUseDescender(boolean useDescender) {
        this.useDescender = useDescender;
    }

    /**
     * Gets the ColumnText with the content of the cell.
     * @return a columntext object
     */
    public ColumnText getColumn() {
        return column;
    }
    
    /**
     * Sets the columntext in the cell.
     * @param column
     */
    public void setColumn(ColumnText column) {
        this.column = column;
    }

    /**
     * The rotation of the cell. Possible values are
     * 0, 90, 180 and 270.
     */
    private int rotation;

    /**
     * Gets the rotation of the cell.
     * @return the rotation of the cell.
     */
    public int getRotation() {
        return this.rotation;
    }

    /**
     * Sets the rotation of the cell. Possible values are
     * 0, 90, 180 and 270.
     * @param rotation the rotation of the cell
     */
    public void setRotation(int rotation) {
        rotation %= 360;
        if (rotation < 0)
            rotation += 360;
        if ((rotation % 90) != 0)
            throw new IllegalArgumentException("Rotation must be a multiple of 90.");
        this.rotation = rotation;
    }
}
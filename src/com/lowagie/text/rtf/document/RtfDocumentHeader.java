/*
 * $Id: RtfDocumentHeader.java,v 1.16 2005/12/24 13:14:59 hallm Exp $
 * $Name:  $
 *
 * Copyright 2003, 2004 by Mark Hall
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

package com.lowagie.text.rtf.document;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.lowagie.text.HeaderFooter;
import com.lowagie.text.rtf.RtfElement;
import com.lowagie.text.rtf.headerfooter.RtfHeaderFooter;
import com.lowagie.text.rtf.headerfooter.RtfHeaderFooterGroup;
import com.lowagie.text.rtf.list.RtfList;
import com.lowagie.text.rtf.list.RtfListTable;
import com.lowagie.text.rtf.style.RtfColor;
import com.lowagie.text.rtf.style.RtfColorList;
import com.lowagie.text.rtf.style.RtfFont;
import com.lowagie.text.rtf.style.RtfFontList;
import com.lowagie.text.rtf.style.RtfParagraphStyle;
import com.lowagie.text.rtf.style.RtfStylesheetList;


/**
 * The RtfDocumentHeader contains all classes required for the generation of
 * the document header area.
 * 
 * @version $Version:$
 * @author Mark Hall (mhall@edu.uni-klu.ac.at)
 */
public class RtfDocumentHeader extends RtfElement {
    /**
     * Constant for the title page
     */
    private static final byte[] TITLE_PAGE = "\\titlepg".getBytes();
    /**
     * Constant for facing pages
     */
    private static final byte[] FACING_PAGES = "\\facingp".getBytes();
    
    /**
     * The code page to use
     */
    private RtfCodePage codePage = null;
    /**
     * Stores all the colors used in the document
     */
    private RtfColorList colorList = null;
    /**
     * Stores all the fonts used in the document
     */
    private RtfFontList fontList = null;
    /**
     * Manages List tables
     */
    private RtfListTable listTable = null;
    /**
     * Stores all paragraph styles used in the document.
     */
    private RtfStylesheetList stylesheetList = null;
    /**
     * The information group with author/subject/keywords/title/producer/creationdate data
     */
    private RtfInfoGroup infoGroup = null;
    /**
     * The page settings
     */
    private RtfPageSetting pageSetting = null;
    /**
     * The current RtfHeaderFooterGroup for the header
     */
    private RtfHeaderFooterGroup header = null;
    /**
     * The current RtfHeaderFooterGroup for the footer
     */
    private RtfHeaderFooterGroup footer = null;

    /**
     * Constructs a RtfDocumentHeader for a RtfDocument
     * 
     * @param doc The RtfDocument this RtfDocumentHeader belongs to
     */
    protected RtfDocumentHeader(RtfDocument doc) {
        super(doc);
    }

    /**
     * Initialises the RtfDocumentHeader.
     */
    protected void init() {
        this.codePage = new RtfCodePage(this.document);
        this.colorList = new RtfColorList(this.document);
        this.fontList = new RtfFontList(this.document);
        this.listTable = new RtfListTable(this.document);
        this.stylesheetList = new RtfStylesheetList(this.document);
        this.infoGroup = new RtfInfoGroup(this.document);
        this.pageSetting = new RtfPageSetting(this.document);
        this.header = new RtfHeaderFooterGroup(this.document, RtfHeaderFooter.TYPE_HEADER);
        this.footer = new RtfHeaderFooterGroup(this.document, RtfHeaderFooter.TYPE_FOOTER);
    }
    
    /**
     * Write the contents of the document header area.
     * 
     * @return A byte array with the contents of the document header area
     */
    public byte[] write() {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        try {
            result.write(this.codePage.writeDefinition());
            result.write(this.fontList.writeDefinition());
            result.write(this.colorList.writeDefinition());
            result.write(this.stylesheetList.writeDefinition());
            result.write(this.listTable.writeDefinition());
            result.write(this.infoGroup.write());
            result.write(this.pageSetting.writeDefinition());
            result.write(writeSectionDefinition());
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
        return result.toByteArray();
    }

    /**
     * Writes the section definition data
     * 
     * @return A byte array with the section definition data
     */
    public byte[] writeSectionDefinition() {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        try {
            if(header.hasTitlePage() || footer.hasTitlePage()) {
                result.write(TITLE_PAGE);
                header.setHasTitlePage();
                footer.setHasTitlePage();
            }
            if(header.hasFacingPages() || footer.hasFacingPages()) {
                result.write(FACING_PAGES);
                header.setHasFacingPages();
                footer.setHasFacingPages();
            }
            result.write(footer.write());
            result.write(header.write());
            result.write(pageSetting.writeSectionDefinition());
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
        return result.toByteArray();
    }
    
    /**
     * Gets the number of the specified RtfFont
     *
     * @param font The RtfFont for which to get the number
     * @return The number of the font
     */
    public int getFontNumber(RtfFont font) {
        return this.fontList.getFontNumber(font);
    }

    /**
     * Gets the number of the specified RtfColor
     * 
     * @param color The RtfColor for which to get the number
     * @return The number of the color
     */
    public int getColorNumber(RtfColor color) {
        return this.colorList.getColorNumber(color);
    }
    
    /**
     * Gets the number of the specified RtfList
     * 
     * @param list The RtfList for which to get the number
     * @return The number of the list
     */
    public int getListNumber(RtfList list) {
        return this.listTable.getListNumber(list);
    }
    
    /**
     * Gets the RtfParagraphStyle with the given style name.
     * 
     * @param styleName The style name of the RtfParagraphStyle to get. 
     * @return The RtfParagraphStyle with the given style name or null.
     */
    public RtfParagraphStyle getRtfParagraphStyle(String styleName) {
        return this.stylesheetList.getRtfParagraphStyle(styleName);
    }
    
    /**
     * Removes a RtfList from the list table
     * 
     * @param list The RtfList to remove
     */
    public void freeListNumber(RtfList list) {
        this.listTable.freeListNumber(list);
    }
    
    /**
     * Gets the RtfPageSetting object of this RtfDocument
     * 
     * @return The RtfPageSetting object
     */
    public RtfPageSetting getPageSetting() {
        return this.pageSetting;
    }
    
    /**
     * Adds an RtfInfoElement to the list of RtfInfoElements
     * 
     * @param rtfInfoElement The RtfInfoElement to add
     */
    public void addInfoElement(RtfInfoElement rtfInfoElement) {
        this.infoGroup.add(rtfInfoElement);
    }
    
    /**
     * Sets the current header to use
     * 
     * @param header The HeaderFooter to use as header
     */
    public void setHeader(HeaderFooter header) {
        if(header != null) {
            if(header instanceof RtfHeaderFooterGroup) {
                this.header = new RtfHeaderFooterGroup(this.document, (RtfHeaderFooterGroup) header, RtfHeaderFooter.TYPE_HEADER);
            } else if(header instanceof RtfHeaderFooter) {
                this.header = new RtfHeaderFooterGroup(this.document, (RtfHeaderFooter) header, RtfHeaderFooter.TYPE_HEADER);
            } else {
                this.header = new RtfHeaderFooterGroup(this.document, header, RtfHeaderFooter.TYPE_HEADER);
            }
        } else {
            this.header = new RtfHeaderFooterGroup(this.document, RtfHeaderFooter.TYPE_HEADER);
        }
    }
    
    /**
     * Sets the current footer to use
     * 
     * @param footer The HeaderFooter to use as footer
     */
    public void setFooter(HeaderFooter footer) {
        if(footer != null) {
            if(footer instanceof RtfHeaderFooterGroup) {
                this.footer = new RtfHeaderFooterGroup(this.document, (RtfHeaderFooterGroup) footer, RtfHeaderFooter.TYPE_FOOTER);
            } else if(footer instanceof RtfHeaderFooter) {
                this.footer = new RtfHeaderFooterGroup(this.document, (RtfHeaderFooter) footer, RtfHeaderFooter.TYPE_FOOTER);
            } else {
                this.footer = new RtfHeaderFooterGroup(this.document, footer, RtfHeaderFooter.TYPE_FOOTER);
            }
        } else {
            this.footer = new RtfHeaderFooterGroup(this.document, RtfHeaderFooter.TYPE_FOOTER);
        }
    }
    
    /**
     * Registers the RtfParagraphStyle for further use in the document.
     * 
     * @param rtfParagraphStyle The RtfParagraphStyle to register.
     */
    public void registerParagraphStyle(RtfParagraphStyle rtfParagraphStyle) {
        this.stylesheetList.registerParagraphStyle(rtfParagraphStyle);
    }
}

/*
 * $Id: RtfFontList.java,v 1.17 2005/12/24 13:10:37 hallm Exp $
 * $Name:  $
 *
 * Copyright 2001, 2002, 2003, 2004 by Mark Hall
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

package com.lowagie.text.rtf.style;

import com.lowagie.text.rtf.RtfElement;
import com.lowagie.text.rtf.RtfExtendedElement;
import com.lowagie.text.rtf.document.RtfDocument;

import java.util.ArrayList;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * The RtfFontList stores the list of fonts used in the rtf document. It also
 * has methods for writing this list to the document
 *
 * Version: $Id: RtfFontList.java,v 1.17 2005/12/24 13:10:37 hallm Exp $
 * @author Mark Hall (mhall@edu.uni-klu.ac.at)
 */
public class RtfFontList extends RtfElement implements RtfExtendedElement {
    
    /**
     * Constant for the default font
     */
    private static final byte[] DEFAULT_FONT = "\\deff".getBytes();
    /**
     * Constant for the font table
     */
    private static final byte[] FONT_TABLE = "\\fonttbl".getBytes();
    /**
     * Constant for the font number
     */
    public static final byte[] FONT_NUMBER = "\\f".getBytes();
    
    /**
     * The list of fonts
     */
    private ArrayList fontList = new ArrayList();

    /**
     * Creates a RtfFontList
     *
     * @param doc The RtfDocument this RtfFontList belongs to
     */
    public RtfFontList(RtfDocument doc) {
        super(doc);
        fontList.add(new RtfFont(document, 0));
    }

    /**
     * Gets the index of the font in the list of fonts. If the font does not
     * exist in the list, it is added.
     *
     * @param font The font to get the id for
     * @return The index of the font
     */
    public int getFontNumber(RtfFont font) {
        if(font instanceof RtfParagraphStyle) {
            font = new RtfFont(this.document, (RtfParagraphStyle) font);
        }
        int fontIndex = -1;
        for(int i = 0; i < fontList.size(); i++) {
            if(fontList.get(i).equals(font)) {
                fontIndex = i;
            }
        }
        if(fontIndex == -1) {
            fontIndex = fontList.size();
            fontList.add(font);
        }
        return fontIndex;
    }

    /**
     * Writes the definition of the font list
     *
     * @return A byte array with the definition of the font list
     */
    public byte[] writeDefinition() {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        try {
            result.write(DEFAULT_FONT);
            result.write(intToByteArray(0));
            result.write(OPEN_GROUP);
            result.write(FONT_TABLE);
            for(int i = 0; i < fontList.size(); i++) {
                result.write(OPEN_GROUP);
                result.write(FONT_NUMBER);
                result.write(intToByteArray(i));
                result.write(((RtfFont) fontList.get(i)).writeDefinition());
                result.write(COMMA_DELIMITER);
                result.write(CLOSE_GROUP);
            }
            result.write(CLOSE_GROUP);
            result.write((byte)'\n');
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
        return result.toByteArray();
    }
}

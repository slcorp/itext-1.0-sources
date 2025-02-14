/**
 * $Id: RtfHeaderFooter.java,v 1.20 2006/02/09 17:25:25 hallm Exp $
 *
 * Copyright 2002 by 
 * <a href="http://www.smb-tec.com">SMB</a> 
 * <a href="mailto:Steffen.Stundzig@smb-tec.com">Steffen.Stundzig@smb-tec.com</a>
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
 * ANY WARRANTY; without right the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Library general Public License for more
 * details.
 *
 * If you didn't download this code from the following link, you should check if
 * you aren't using an obsolete version:
 * http://www.lowagie.com/iText/
 */
package com.lowagie.text.rtf;

import com.lowagie.text.Element;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Phrase;


/**
 * This HeaderFooter specialization extends the normal HeaderFooter impl.
 * by the new ctor with 'Element' param.
 * So we have the ability to add a table or some moe sophisticated stuff
 * as header or footer content.
 * 
 * ONLY FOR USE WITH THE RtfWriter NOT with the RtfWriter2.
 *
 * This class is based on the RtfWriter-package from Mark Hall.
 * @author <a href="mailto:Steffen.Stundzig@smb-tec.com">Steffen.Stundzig@smb-tec.com</a>
 * @author <a href="mailto:mhall@myrealbox.com">Mark.Hall@myrealbox.com</a>
 * @version $Revision: 1.20 $Date: 2006/02/09 17:25:25 $
 * @deprecated Please move to the RtfWriter2 and associated classes. com.lowagie.text.rtf.headerfooter.RtfHeaderFooter replaces the functionality of this class.
 */
public class RtfHeaderFooter extends HeaderFooter {


    private Element content = null;

        
    /**
     * Constructs a new header
     * @param before
     * @param after
     */
    public RtfHeaderFooter( Phrase before, Phrase after ) {
        super( before, after );    
    }    


    /**
     * Constructs a new header
     * @param before
     * @param numbered
     */
    public RtfHeaderFooter( Phrase before, boolean numbered ) {
        super( before, numbered );
    }    


    /**
     * Constructs a new header
     * @param content
     */
    public RtfHeaderFooter( Element content ) {
        super(new Phrase(content.toString()), false);
        this.content = content;
    }    


    /**
     * @return the element specified in the ctor or null;
     */
    public Element content() {
        return content;
    }
}

/*
 * Created on Aug 10, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.lowagie.text.rtf.headerfooter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Table;
import com.lowagie.text.rtf.RtfBasicElement;
import com.lowagie.text.rtf.document.RtfDocument;
import com.lowagie.text.rtf.field.RtfPageNumber;


/**
 * The RtfHeaderFooter represents one header or footer. This class can be used
 * directly.
 * 
 * @version $Id: RtfHeaderFooter.java,v 1.10 2006/07/16 16:36:23 hallm Exp $
 * @author Mark Hall (mhall@edu.uni-klu.ac.at)
 */
public class RtfHeaderFooter extends HeaderFooter implements RtfBasicElement {

    /**
     * Constant for the header type
     */
    public static final int TYPE_HEADER = 1;
    /**
     * Constant for the footer type
     */
    public static final int TYPE_FOOTER = 2;
    /**
     * Constant for displaying the header/footer on the first page
     */
    public static final int DISPLAY_FIRST_PAGE = 0;
    /**
     * Constant for displaying the header/footer on all pages
     */
    public static final int DISPLAY_ALL_PAGES = 1;
    /**
     * Constant for displaying the header/footer on all left hand pages
     */
    public static final int DISPLAY_LEFT_PAGES = 2;
    /**
     * Constant for displaying the header/footer on all right hand pages
     */
    public static final int DISPLAY_RIGHT_PAGES = 4;

    /**
     * Constant for a header on all pages
     */
    private static final byte[] HEADER_ALL = "\\header".getBytes();
    /**
     * Constant for a header on the first page
     */
    private static final byte[] HEADER_FIRST = "\\headerf".getBytes();
    /**
     * Constant for a header on all left hand pages
     */
    private static final byte[] HEADER_LEFT = "\\headerl".getBytes();
    /**
     * Constant for a header on all right hand pages
     */
    private static final byte[] HEADER_RIGHT = "\\headerr".getBytes();
    /**
     * Constant for a footer on all pages
     */
    private static final byte[] FOOTER_ALL = "\\footer".getBytes();
    /**
     * Constant for a footer on the first page
     */
    private static final byte[] FOOTER_FIRST = "\\footerf".getBytes();
    /**
     * Constnat for a footer on the left hand pages
     */
    private static final byte[] FOOTER_LEFT = "\\footerl".getBytes();
    /**
     * Constant for a footer on the right hand pages
     */
    private static final byte[] FOOTER_RIGHT = "\\footerr".getBytes();
    
    /**
     * The RtfDocument this RtfHeaderFooter belongs to
     */
    private RtfDocument document = null;
    /**
     * The content of this RtfHeaderFooter
     */
    private Object[] content = null;
    /**
     * The display type of this RtfHeaderFooter. TYPE_HEADER or TYPE_FOOTER
     */
    private int type = TYPE_HEADER;
    /**
     * The display location of this RtfHeaderFooter. DISPLAY_FIRST_PAGE,
     * DISPLAY_LEFT_PAGES, DISPLAY_RIGHT_PAGES or DISPLAY_ALL_PAGES
     */
    private int displayAt = DISPLAY_ALL_PAGES;
   
    /**
     * Constructs a RtfHeaderFooter based on a HeaderFooter with a certain type and displayAt
     * location. For internal use only.
     * 
     * @param doc The RtfDocument this RtfHeaderFooter belongs to
     * @param headerFooter The HeaderFooter to base this RtfHeaderFooter on
     * @param type The type of RtfHeaderFooter
     * @param displayAt The display location of this RtfHeaderFooter
     */
    protected RtfHeaderFooter(RtfDocument doc, HeaderFooter headerFooter, int type, int displayAt) {
        super(new Phrase(""), false);
        this.document = doc;
        this.type = type;
        this.displayAt = displayAt;
        Paragraph par = new Paragraph();
        par.setAlignment(headerFooter.alignment());
        if (headerFooter.getBefore() != null) {
            par.add(headerFooter.getBefore());
        }
        if (headerFooter.isNumbered()) {
            par.add(new RtfPageNumber(this.document));
        }
        if (headerFooter.getAfter() != null) {
            par.add(headerFooter.getAfter());
        }
        try {
            this.content = new Object[1];
            if(this.document != null) {
                this.content[0] = this.document.getMapper().mapElement(par);
                ((RtfBasicElement) this.content[0]).setInHeader(true);
            } else {
                this.content[0] = par;
            }
        } catch(DocumentException de) {
            de.printStackTrace();
        }
    }
    
    /**
     * Constructs a RtfHeaderFooter as a copy of an existing RtfHeaderFooter.
     * For internal use only.
     * 
     * @param doc The RtfDocument this RtfHeaderFooter belongs to
     * @param headerFooter The RtfHeaderFooter to copy
     * @param displayAt The display location of this RtfHeaderFooter
     */
    protected RtfHeaderFooter(RtfDocument doc, RtfHeaderFooter headerFooter, int displayAt) {
        super(new Phrase(""), false);
        this.document = doc;
        this.content = headerFooter.getContent();
        this.displayAt = displayAt;
        for(int i = 0; i < this.content.length; i++) {
            if(this.content[i] instanceof Element) {
                try {
                    this.content[i] = this.document.getMapper().mapElement((Element) this.content[i]);
                } catch(DocumentException de) {
                    de.printStackTrace();
                }
            }
            if(this.content[i] instanceof RtfBasicElement) {
                ((RtfBasicElement) this.content[i]).setInHeader(true);
            }
        }
    }
    
    /**
     * Constructs a RtfHeaderFooter for a HeaderFooter.
     *  
     * @param doc The RtfDocument this RtfHeaderFooter belongs to
     * @param headerFooter The HeaderFooter to base this RtfHeaderFooter on
     */
    protected RtfHeaderFooter(RtfDocument doc, HeaderFooter headerFooter) {
        super(new Phrase(""), false);
        this.document = doc;
        Paragraph par = new Paragraph();
        par.setAlignment(headerFooter.alignment());
        if (headerFooter.getBefore() != null) {
            par.add(headerFooter.getBefore());
        }
        if (headerFooter.isNumbered()) {
            par.add(new RtfPageNumber(this.document));
        }
        if (headerFooter.getAfter() != null) {
            par.add(headerFooter.getAfter());
        }
        try {
            this.content = new Object[1];
            this.content[0] = doc.getMapper().mapElement(par);
            ((RtfBasicElement) this.content[0]).setInHeader(true);
        } catch(DocumentException de) {
            de.printStackTrace();
        }
    }
    
    /**
     * Constructs a RtfHeaderFooter for any Element.
     *
     * @param element The Element to display as content of this RtfHeaderFooter
     */
    public RtfHeaderFooter(Element element) {
        this(new Element[]{element});
    }

    /**
     * Constructs a RtfHeaderFooter for an array of Elements.
     * 
     * @param elements The Elements to display as the content of this RtfHeaderFooter.
     */
    public RtfHeaderFooter(Element[] elements) {
        super(new Phrase(""), false);
        this.content = new Object[elements.length];
        for(int i = 0; i < elements.length; i++) {
            this.content[i] = elements[i];
        }
    }
    
    /**
     * Sets the RtfDocument this RtfElement belongs to
     * 
     * @param doc The RtfDocument to use
     */
    public void setRtfDocument(RtfDocument doc) {
        this.document = doc;
        if(this.document != null) {
            for(int i = 0; i < this.content.length; i++) {
                try {
                    if(this.content[i] instanceof Element) {
                        this.content[i] = this.document.getMapper().mapElement((Element) this.content[i]);
                        ((RtfBasicElement) this.content[i]).setInHeader(true);
                    } else if(this.content[i] instanceof RtfBasicElement){
                        ((RtfBasicElement) this.content[i]).setRtfDocument(this.document);
                        ((RtfBasicElement) this.content[i]).setInHeader(true);
                    }
                } catch(DocumentException de) {
                    de.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Writes the content of this RtfHeaderFooter
     * 
     * @return A byte array with the content of this RtfHeaderFooter
     */
    public byte[] write() {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        try {
            result.write(OPEN_GROUP);
            if(this.type == TYPE_HEADER) {
                if(this.displayAt == DISPLAY_ALL_PAGES) {
                    result.write(HEADER_ALL);
                } else if(this.displayAt == DISPLAY_FIRST_PAGE) {
                    result.write(HEADER_FIRST);
                } else if(this.displayAt == DISPLAY_LEFT_PAGES) {
                    result.write(HEADER_LEFT);
                } else if(this.displayAt == DISPLAY_RIGHT_PAGES) {
                    result.write(HEADER_RIGHT);
                }
            } else {
                if(this.displayAt == DISPLAY_ALL_PAGES) {
                    result.write(FOOTER_ALL);
                } else if(this.displayAt == DISPLAY_FIRST_PAGE) {
                    result.write(FOOTER_FIRST);
                } else if(this.displayAt == DISPLAY_LEFT_PAGES) {
                    result.write(FOOTER_LEFT);
                } else if(this.displayAt == DISPLAY_RIGHT_PAGES) {
                    result.write(FOOTER_RIGHT);
                }
            }
            result.write(DELIMITER);
            for(int i = 0; i < this.content.length; i++) {
                if(this.content[i] instanceof RtfBasicElement) {
                    result.write(((RtfBasicElement) this.content[i]).write());
                }
            }
            result.write(CLOSE_GROUP);
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
        return result.toByteArray();
    }
    
    
    /**
     * Sets the display location of this RtfHeaderFooter
     * 
     * @param displayAt The display location to use.
     */
    public void setDisplayAt(int displayAt) {
        this.displayAt = displayAt;
    }
    
    /**
     * Sets the type of this RtfHeaderFooter
     * 
     * @param type The type to use.
     */
    public void setType(int type) {
        this.type = type;
    }
    
    /**
     * Gets the content of this RtfHeaderFooter
     * 
     * @return The content of this RtfHeaderFooter
     */
    private Object[] getContent() {
        return this.content;
    }

    /**
     * Unused
     * @param inTable
     */
    public void setInTable(boolean inTable) {
    }
    
    /**
     * Unused
     * @param inHeader
     */
    public void setInHeader(boolean inHeader) {
    }
    
    /**
     * Set the alignment of this RtfHeaderFooter. Passes the setting
     * on to the contained element.
     */
    public void setAlignment(int alignment) {
        super.setAlignment(alignment);
        for(int i = 0; i < this.content.length; i++) {
            if(this.content[i] instanceof Paragraph) {
                ((Paragraph) this.content[i]).setAlignment(alignment);
            } else if(this.content[i] instanceof Table) {
                ((Table) this.content[i]).setAlignment(alignment);
            } else if(this.content[i] instanceof Image) {
                ((Image) this.content[i]).setAlignment(alignment);
            }     
        }
    }
}

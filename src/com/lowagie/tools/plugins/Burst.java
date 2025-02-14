/*
 * $Id: Burst.java,v 1.8 2005/11/29 21:05:02 blowagie Exp $
 * $Name:  $
 *
 * This code is free software. It may only be copied or modified
 * if you include the following copyright notice:
 *
 * This class by Mark Thompson. Copyright (c) 2002 Mark Thompson.
 *
 * This code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * itext-questions@list.sourceforge.net
 */
package com.lowagie.tools.plugins;

import java.io.File;
import java.io.FileOutputStream;

import javax.swing.JInternalFrame;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.tools.arguments.FileArgument;
import com.lowagie.tools.arguments.LabelAccessory;
import com.lowagie.tools.arguments.PdfFilter;
import com.lowagie.tools.arguments.ToolArgument;

/**
 * This tool lets you split a PDF in several separate PDF files (1 per page).
 */
public class Burst extends AbstractTool {

	static {
		addVersion("$Id: Burst.java,v 1.8 2005/11/29 21:05:02 blowagie Exp $");
	}

	/**
	 * Constructs a Burst object.
	 */
	public Burst() {
		FileArgument f = new FileArgument(this, "srcfile", "The file you want to split", false, new PdfFilter());
		f.setLabel(new LabelAccessory());
		arguments.add(f);
	}

	/**
	 * @see com.lowagie.tools.plugins.AbstractTool#createFrame()
	 */
	protected void createFrame() {
		internalFrame = new JInternalFrame("Burst", true, false, true);
		internalFrame.setSize(300, 80);
		internalFrame.setJMenuBar(getMenubar());
		System.out.println("=== Burst OPENED ===");
	}

	/**
	 * @see com.lowagie.tools.plugins.AbstractTool#execute()
	 */
	public void execute() {
        try {
			if (getValue("srcfile") == null) throw new InstantiationException("You need to choose a sourcefile");
			File src = (File)getValue("srcfile");
            File directory = src.getParentFile();
            String name = src.getName();
            name = name.substring(0, name.lastIndexOf("."));
        	// we create a reader for a certain document
			PdfReader reader = new PdfReader(src.getAbsolutePath());
			// we retrieve the total number of pages
			int n = reader.getNumberOfPages();
			int digits = 1 + (n / 10);
			System.out.println("There are " + n + " pages in the original file.");
			Document document;
			int pagenumber;
			String filename;
            for (int i = 0; i < n; i++) {
            	pagenumber = i + 1;
            	filename = String.valueOf(pagenumber);
            	while (filename.length() < digits) filename = "0" + filename;
            	filename = "_" + filename + ".pdf";
            	// step 1: creation of a document-object
            	document = new Document(reader.getPageSizeWithRotation(pagenumber));
				// step 2: we create a writer that listens to the document
            	PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(new File(directory, name + filename)));
            	// step 3: we open the document
            	document.open();
            	PdfContentByte cb = writer.getDirectContent();
				PdfImportedPage page = writer.getImportedPage(reader, pagenumber);
				int rotation = reader.getPageRotation(pagenumber);
				if (rotation == 90 || rotation == 270) {
					cb.addTemplate(page, 0, -1f, 1f, 0, 0, reader.getPageSizeWithRotation(pagenumber).height());
				}
				else {
					cb.addTemplate(page, 1f, 0, 0, 1f, 0, 0);
				}
				// step 5: we close the document
				document.close();
			}
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

	/**
	 * @see com.lowagie.tools.plugins.AbstractTool#valueHasChanged(com.lowagie.tools.arguments.ToolArgument)
	 */
	public void valueHasChanged(ToolArgument arg) {
		if (internalFrame == null) {
			// if the internal frame is null, the tool was called from the commandline
			return;
		}
		// represent the changes of the argument in the internal frame
	}


    /**
     * Divide PDF file into pages.
     * @param args
     */
	public static void main(String[] args) {
    	Burst tool = new Burst();
    	if (args.length < 1) {
    		System.err.println(tool.getUsage());
    	}
    	tool.setArguments(args);
        tool.execute();
	}

	/**
	 * @see com.lowagie.tools.plugins.AbstractTool#getDestPathPDF()
	 */
	protected File getDestPathPDF() throws InstantiationException {
		throw new InstantiationException("There is more than one destfile.");
	}
}

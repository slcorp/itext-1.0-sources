# itext-1.0-sources
Modified source code of iText libraries used by the PDF report generation functionality.

This directory contains all original source, plus any patched source 
to build iText.jar. For all patched files, the original is 
left in it's package location, but renamed to filename.orig.

List of Modified Files
=======================

	com\lowagie\text\pdf\PdfGraphics2D.java


Nature of Modification
======================

The code was changed to avoid an NPE that occurred when attempting to 
render a table with filter properties.  This was traced to this root cause: a 
null value gets passed as a rendering hint, and is an invalid argument to 

java.awt.RenderingHints

https://www.mail-archive.com/itext-questions@lists.sourceforge.net/msg28444.html

We modified the code to prevent passing down the null value, preventing the issue.

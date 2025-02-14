/*
 * $Id: BitsetArgument.java,v 1.5 2006/05/30 09:13:00 blowagie Exp $
 * $Name:  $
 *
 * Copyright 2005 by Bruno Lowagie.
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
package com.lowagie.tools.arguments;

import java.awt.event.ActionEvent;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;

import com.lowagie.tools.plugins.AbstractTool;

/**
 * Argument that results in a set of "1" and "0" values.
 */
public class BitsetArgument extends ToolArgument {
	/** These are the different options that can be true or false. */
	private JCheckBox[] options;

	/**
	 * Constructs an BitsetArgument.
	 * @param tool the tool that needs this argument
	 * @param name the name of the argument
	 * @param description the description of the argument
	 * @param options the different options that can be true or false
	 */
	public BitsetArgument(AbstractTool tool, String name, String description, String[] options) {
		super(tool, name, description, String.class.getName());
		this.options = new JCheckBox[options.length];
		for (int i = 0; i < options.length; i++) {
			this.options[i] = new JCheckBox(options[i]);
		}
	}

	/**
	 * Gets the argument as an object.
	 * @return an object
	 * @throws InstantiationException
	 */
	public Object getArgument() throws InstantiationException {
		return value;
	}

	/**
	 * @see com.lowagie.tools.arguments.ToolArgument#getUsage()
	 */
	public String getUsage() {
		StringBuffer buf = new StringBuffer(super.getUsage());
		buf.append("    possible options:\n");
		for (int i = 0; i < options.length; i++) {
			buf.append("    - ");
			buf.append(options[i].getText());
			buf.append("\n");
		}
		return buf.toString();
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent evt) {
		Object[] message = new Object[1 + options.length];
		message[0] = "Check the options you need:";
		for(int i = 0; i < options.length; i++ ) {
			message[i+1] = options[i];
		}
		int result = JOptionPane.showOptionDialog(
	 		    tool.getInternalFrame(),
	 		    message,
	 		    description,
	 		    JOptionPane.OK_CANCEL_OPTION,
	 		    JOptionPane.QUESTION_MESSAGE,
	 		    null,
	 		    null,
				null
	 		);
		if (result == 0) {
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < options.length; i++) {
				if (options[i].isSelected()) {
					buf.append("1");
				}
				else {
					buf.append("0");
				}
			}
			setValue(buf.toString());
		}
	}
}

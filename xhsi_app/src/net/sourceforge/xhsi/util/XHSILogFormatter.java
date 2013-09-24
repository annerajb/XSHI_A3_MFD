/**
* XHSILogFormatter.java
*
* Formats the date and time for the log
*
* Copyright (C) 2007  Georg Gruetter (gruetter@gmail.com)
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or (at your option) any later version.
*
* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this library; if not, write to the Free Software
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
*/
package net.sourceforge.xhsi.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class XHSILogFormatter extends Formatter {


    public void Formatter() {

    }

    public String format(LogRecord record) {
        return
            new String(
                    "[" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(record.getMillis())) + "] " +
                    record.getLevel().getName() + ": " +
                    record.getMessage() + " " +
                    "( " + record.getSourceClassName() + " / " +
                    record.getSourceMethodName() + " ) " +
                    "\n"
                    );
    }

}

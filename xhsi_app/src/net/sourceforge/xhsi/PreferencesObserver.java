/**
 * PreferencesObserver.java
 * 
 * Classes implementing this interface can subscribe for preference changes
 * at HSIPreferences. HSIPreferences notifies these classes through this 
 * interface when the specified preference changed.
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
package net.sourceforge.xhsi;

public interface PreferencesObserver {

    /**
     * Called by HSIPreferences when the value of the preference item with the
     * given key changed.
     *
     * @param key     - the key of the changed preference
     */
    void preference_changed(String key);

}

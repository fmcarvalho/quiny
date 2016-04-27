/*
 * Copyright (c) 2016, Miguel Gamboa
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package quiny;

import java.util.Iterator;
import java.util.function.Consumer;

/**
 * @author Miguel Gamboa
 *         created on 27-04-2016
 */
public class NonspliteratorIterator<T> implements Nonspliterator<T> {

    private final Iterator<T> dataSrc;

    public NonspliteratorIterator(Iterator<T> dataSrc) {
        this.dataSrc = dataSrc;
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        if (dataSrc.hasNext()) {
            action.accept(dataSrc.next());
            return true;
        }
        return false;
    }
}

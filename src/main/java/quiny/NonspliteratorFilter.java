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

import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author Miguel Gamboa
 *         created on 21-04-2016
 */
public class NonspliteratorFilter<T> implements Nonspliterator<T> {
    private final Spliterator<T> srcIterator;
    private final Predicate<T> p;

    public NonspliteratorFilter(Spliterator<T> dataSrc, Predicate<T> p) {
        this.srcIterator = dataSrc;
        this.p = p;
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        boolean [] found = {false};
        while(!found[0]) {
            boolean hasNext = srcIterator.tryAdvance(item -> {
                if(p.test(item)) {
                    action.accept(item);
                    found[0] = true;
                }
            });
            if(!hasNext) break;
        }
        return found[0];
    }
}

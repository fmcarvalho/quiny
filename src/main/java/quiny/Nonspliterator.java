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
import java.util.function.Function;

/**
 * @author Miguel Gamboa
 *         created on 21-04-2016
 */
public class Nonspliterator<T> implements Spliterator<T> {

    private final Function<Consumer<? super T>, Boolean> srcTryAdvance;

    public Nonspliterator(Function<Consumer<?super T>, Boolean> srcTryAdvance) {
        this.srcTryAdvance = srcTryAdvance;
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        return srcTryAdvance.apply(action);
    }

    @Override
    public Spliterator<T> trySplit() {
        return null; // this spliterator cannot be split
    }

    @Override
    public long estimateSize() {
        return Long.MAX_VALUE; // Long.MAX_VALUE means unknown, or too expensive to compute.
    }

    @Override
    public int characteristics() {
        return 0; // No characteristics
    }
}

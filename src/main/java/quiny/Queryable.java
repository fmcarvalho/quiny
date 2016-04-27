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

import java.util.Collection;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;

/**
 * <p>
 * {@code Queryable<T>} is a concise and functional implementation
 * of an equivalent API to the {@link java.util.stream.Stream}, which
 * preserves the internal iteration approach, the laziness behavior
 * and the fluent idiom.
 * This solution answers the question: How can I implement a lazy iterator
 * in Java 8?
 * </p>
 *
 * <p>
 * To achieve a short implementation, the {@code Queryable<T>} suppressed
 * the partitioning feature, which means that {@code Queryable<T>} does NOT
 * support parallel processing.
 * </p>
 *
 * <p>
 * The following example illustrates a use case of {@code Queryable<T>}
 * that is equivalent to the use of {@link java.util.stream.Stream}.
 * You can replace the {@code Queryable.of(dataSrc)} call with {@code
 * dataSrc.stream()} and you will get the same result.
 * </p>
 *
 * <pre>{@code
 *     Collection<String> dataSrc = ...  // something
 *     Queryable.of(dataSrc)             // <=> dataSrc.stream()
 *         .filter(w -> !w.startsWith("-"))
 *         .distinct()
 *         .map(String::length)
 *         .limit(5)
 *         .forEach(System.out::println);
 * }</pre>
 *
 * @author Miguel Gamboa
 *         created on 21-04-2016
 */
public class Queryable<T> {

    private final Spliterator<T> dataSrc;

    public Queryable(Spliterator<T> dataSrc) {
        this.dataSrc = dataSrc;
    }

    public static <T> Queryable<T> of(Collection<T> data) {
        throw new UnsupportedOperationException();
    }

    public void forEach(Consumer<T> action) {
        while (dataSrc.tryAdvance(action)) { }
    }

    public <R> Queryable<R> map(Function<T, R> mapper) {
        throw new UnsupportedOperationException();
    }

    public Queryable<T> limit(long maxSize) {
        throw new UnsupportedOperationException();
    }
    
    public Queryable<T> distinct() {
        throw new UnsupportedOperationException();
    }

    public Queryable<T> filter(Predicate<T> p) { throw new UnsupportedOperationException(); }

    public <A> A[] toArray(IntFunction<A[]> generator) {
        throw new UnsupportedOperationException();
    }
}

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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.BinaryOperator;
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
@FunctionalInterface
public interface Queryable<T> extends Nonspliterator<T> {

    static <T> boolean truth(Consumer<T> c, T item){
        c.accept(item);
        return true;
    }

    public static <T> Queryable<T> of(Collection<T> data) {
        final Iterator<T> dataSrc = data.iterator();
        return action -> dataSrc.hasNext() ? truth(action, dataSrc.next()) : false;
    }

    public default void forEach(Consumer<T> action) {
        while (tryAdvance(action)) {
        }
    }

    public default <R> Queryable<R> map(Function<T, R> mapper) {
        return action -> tryAdvance(item -> action.accept(mapper.apply(item)));
    }

    public default Queryable<T> limit(long maxSize) {
        final int[] count = {0};
        return action -> count[0]++ < maxSize ? tryAdvance(action) : false;
    }

    public default Queryable<T> distinct() {
        final Set<T> selected = new HashSet<>();
        return action -> {
            boolean [] found = {false};
            while(!found[0]) {
                boolean hasNext = tryAdvance(item -> {
                    if(selected.add(item)) {
                        action.accept(item);
                        found[0] = true;
                    }
                });
                if(!hasNext) break;
            }
            return found[0];
        };
    }

    public default Queryable<T> filter(Predicate<T> p) {
        return action -> {
            boolean [] found = {false};
            while(!found[0]) {
                boolean hasNext = tryAdvance(item -> {
                    if(p.test(item)) {
                        action.accept(item);
                        found[0] = true;
                    }
                });
                if(!hasNext) break;
            }
            return found[0];
        };
    }

    public default T reduce(T initial, BinaryOperator<T> accumulator) {
        final T[] res = (T[]) Array.newInstance(initial.getClass(), 1);
        res[0] = initial;
        while (tryAdvance(item ->
                res[0] = accumulator.apply(res[0], item)
        )) ;
        return res[0];
    }

    public default <A> A[] toArray(IntFunction<A[]> generator) {
        final List<T> res = new ArrayList<>();
        while (tryAdvance(item -> res.add(item))) ;
        return res.toArray(generator.apply(res.size()));
    }
}
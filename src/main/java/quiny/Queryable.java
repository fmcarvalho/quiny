/*
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;

public interface Queryable<T> {
	void forEach(Consumer<T> cons);
	
	default public T reduce(T identity, BinaryOperator<T> op) {
		class Box {
			T val = identity;
		}
		Box b = new Box();
		forEach(e -> b.val = op.apply(b.val, e));
		return b.val;
	}

	default public Optional<T> reduce(BinaryOperator<T> op) {
		class Box {
			boolean isPresent;
			T val;
		}
		Box b = new Box();
		forEach(e -> {
			if (b.isPresent)
				b.val = op.apply(b.val, e);
			else {
				b.val = e;
				b.isPresent = true;
			}
		});
		return b.isPresent ? Optional.empty() : Optional.of(b.val);
	}

	default public long count() {
		return map(e -> 1L).reduce(0L, Long::sum);
	}

	default public Optional<T> maxBy(Comparator<T> cmp) {
		return reduce(BinaryOperator.maxBy(cmp));
	}

	default public Optional<T> minBy(Comparator<T> cmp) {
		return reduce(BinaryOperator.maxBy(cmp));
	}

	default public <U> Queryable<U> map(Function<T, U> mapper) {
		return cons -> forEach(e -> cons.accept(mapper.apply(e)));
	}

	public default Queryable<T> limit(long maxSize) {
		final int[] count = {0};
		return cons -> forEach(e -> {
			if (count[0]++ < maxSize )
				cons.accept(e);
		});
	}

	default public Queryable<T> filter(Predicate<T> pred) {
		return cons -> forEach(e -> {
			if (pred.test(e))
				cons.accept(e);
		});
	}

	default public Queryable<T> distinct() {
		final Set<T> selected = new HashSet<>();
		return cons -> forEach(e -> {
			if (selected.add(e))
				cons.accept(e);
		});
	}

	default public <U> Queryable<U> flatMap(Function<T, Queryable<U>> mapper) {
		return cons -> forEach(e -> mapper.apply(e).forEach(cons));
	}

	default public Queryable<T> peek(Consumer<T> action) {
		return cons -> forEach(e -> {
			action.accept(e);
			cons.accept(e);
		});
	}

	default public Queryable<T> skip(long n) {
		return cons -> {
			long[] count = { 0 };
			forEach(e -> {
				if (++count[0] > n)
					cons.accept(e);
			});
		};
	}

	public default <A> A[] toArray(IntFunction<A[]> generator) {
		final List<T> res = new ArrayList<>();
		forEach(e -> res.add(e));
		return res.toArray(generator.apply(res.size()));
	}

	public static <T> Queryable<T> of(Iterable<T> elements) {
		return elements::forEach;
	}

	@SafeVarargs
	public static <T> Queryable<T> of(T... elements) {
		return of(Arrays.asList(elements));
	}

	public static Queryable<Integer> range(int from, int to) {
		return cons -> {
			for (int i = from; i < to; i++)
				cons.accept(i);
		};
	}
}

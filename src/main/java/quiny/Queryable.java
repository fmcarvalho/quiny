import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
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

	default public Queryable<T> filter(Predicate<T> pred) {
		return cons -> forEach(e -> {
			if (pred.test(e))
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

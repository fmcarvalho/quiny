import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public interface MyStream<T> {
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

	default public <U> MyStream<U> map(Function<T, U> mapper) {
		return cons -> forEach(e -> cons.accept(mapper.apply(e)));
	}

	default public MyStream<T> filter(Predicate<T> pred) {
		return cons -> forEach(e -> {
			if (pred.test(e))
				cons.accept(e);
		});
	}

	default public <U> MyStream<U> flatMap(Function<T, MyStream<U>> mapper) {
		return cons -> forEach(e -> mapper.apply(e).forEach(cons));
	}

	default public MyStream<T> peek(Consumer<T> action) {
		return cons -> forEach(e -> {
			action.accept(e);
			cons.accept(e);
		});
	}

	default public MyStream<T> skip(long n) {
		return cons -> {
			long[] count = { 0 };
			forEach(e -> {
				if (++count[0] > n)
					cons.accept(e);
			});
		};
	}

	public static <T> MyStream<T> of(Iterable<T> elements) {
		return elements::forEach;
	}

	@SafeVarargs
	public static <T> MyStream<T> of(T... elements) {
		return of(Arrays.asList(elements));
	}

	public static MyStream<Integer> range(int from, int to) {
		return cons -> {
			for (int i = from; i < to; i++)
				cons.accept(i);
		};
	}
}

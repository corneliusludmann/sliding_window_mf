package org.ludmann.recsys.data.rating_matrix;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.ludmann.recsys.data.rating.UserItemRating;

import com.google.common.collect.AbstractIterator;

/**
 * @author Cornelius A. Ludmann
 *
 */
abstract class AbstractRatingMatrix implements RatingMatrix {

	protected abstract Map<Long, Map<Long, UserItemRating>> userRatingsMap();

	protected abstract Map<Long, Map<Long, UserItemRating>> itemRatingsMap();

	@Override
	public Optional<UserItemRating> rating(final long user, final long item) {
		final Map<Long, UserItemRating> ratings = userRatingsMap().get(user);
		if (ratings == null) {
			return Optional.empty();
		}
		return Optional.ofNullable(ratings.get(item));
	}

	@Override
	public Collection<UserItemRating> ratingsOfUser(final long user) {
		final Map<Long, UserItemRating> ratings = userRatingsMap().get(user);
		return ratings.values();
	}

	@Override
	public Collection<UserItemRating> ratingsForItem(final long item) {
		final Map<Long, UserItemRating> ratings = itemRatingsMap().get(item);
		return ratings.values();
	}

	@Override
	public Set<Long> users() {
		return userRatingsMap().keySet();
	}

	@Override
	public Set<Long> items() {
		return itemRatingsMap().keySet();
	}

	@Override
	public Iterator<UserItemRating> iterator() {
		// final Collection<Iterator<UserItemRating>> iterators =
		// userRatingsMap.values().stream()
		// .map(x -> x.values().iterator()).collect(Collectors.toList());
		// return IteratorUtils.<UserItemRating>chainedIterator(iterators);

		return new AbstractIterator<UserItemRating>() {

			private final Iterator<Map<Long, UserItemRating>> userRatingsMapIterator = userRatingsMap().values()
					.iterator();
			private Iterator<UserItemRating> currentIterator = null;

			@Override
			protected UserItemRating computeNext() {
				while ((currentIterator == null || !currentIterator.hasNext())) {
					if (userRatingsMapIterator.hasNext()) {
						currentIterator = userRatingsMapIterator.next().values().iterator();
					} else {
						return endOfData();
					}
				}
				return currentIterator.next();
			}
		};
	}

	@Override
	public String asAsciiMatrix() {
		final StringBuilder sb = new StringBuilder();
		createAsciiMatrix(sb::append);
		return sb.toString();
	}

	@Override
	public void createAsciiMatrix(final Consumer<String> asciiConsumer) {

		final String horizontalDelim = " | ";
		final String noValuePlaceholder = "--";

		final List<String> usersAsString = users().stream().map(String::valueOf).collect(Collectors.toList());
		final int maxUserLength = Math.max(5, usersAsString.stream().mapToInt(x -> x.length()).max().orElse(0));

		final List<String> itemsAsString = items().stream().map(String::valueOf).collect(Collectors.toList());
		final int maxItemLength = itemsAsString.stream().mapToInt(x -> x.length()).max().orElse(0);

		// header
		asciiConsumer.accept(StringUtils.repeat(" ", maxItemLength));
		asciiConsumer.accept(usersAsString.stream().map(x -> String.format("%" + maxUserLength + "s", x))
				.collect(Collectors.joining(horizontalDelim, horizontalDelim, horizontalDelim)));
		asciiConsumer.accept(System.lineSeparator());

		for (final long item : items()) {
			asciiConsumer.accept(String.format("%" + maxItemLength + "s" + horizontalDelim, item));
			for (final long user : users()) {
				final double rating = rating(user, item).orElse(UserItemRating.nan()).rating();
				if (Double.isNaN(rating)) {
					asciiConsumer.accept(StringUtils.repeat(" ", maxUserLength - noValuePlaceholder.length()));
					asciiConsumer.accept(noValuePlaceholder);
					asciiConsumer.accept(horizontalDelim);
				} else {
					final String ratingString = String.format("%.2f", rating);
					asciiConsumer.accept(String.format("%" + maxUserLength + "s" + horizontalDelim, ratingString));
				}
			}
			asciiConsumer.accept(System.lineSeparator());
		}
	}

	@Override
	public String toString() {
		return String.format("RatingMatrixImpl [userRatingsMap=%s, itemRatingsMap=%s]", userRatingsMap(),
				itemRatingsMap());
	}
}

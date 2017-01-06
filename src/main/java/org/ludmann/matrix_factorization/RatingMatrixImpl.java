package org.ludmann.matrix_factorization;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Cornelius A. Ludmann
 *
 */
class RatingMatrixImpl implements RatingMatrix {

	private final Map<Long, Map<Long, UserItemRating>> userRatingsMap = new HashMap<>();
	private final Map<Long, Map<Long, UserItemRating>> itemRatingsMap = new HashMap<>();

	@Override
	public Optional<UserItemRating> add(final UserItemRating userItemRating) {

		final Optional<UserItemRating> oldUserRatingsMap = addToUserRatingsMap(userItemRating);
		final Optional<UserItemRating> oldItemRatingsMap = addToItemRatingsMap(userItemRating);
		if (!oldItemRatingsMap.equals(oldUserRatingsMap)) {
			throw new AssertionError("!oldItemRatingsMap.equals(oldUserRatingsMap)");
		}
		return oldUserRatingsMap;
	}

	private Optional<UserItemRating> addToUserRatingsMap(final UserItemRating userItemRating) {
		Map<Long, UserItemRating> ratings = userRatingsMap.get(userItemRating.user());
		if (ratings == null) {
			ratings = new HashMap<>();
			userRatingsMap.put(userItemRating.user(), ratings);
		}
		return Optional.ofNullable(ratings.put(userItemRating.item(), userItemRating));
	}

	private Optional<UserItemRating> addToItemRatingsMap(final UserItemRating userItemRating) {
		Map<Long, UserItemRating> ratings = itemRatingsMap.get(userItemRating.item());
		if (ratings == null) {
			ratings = new HashMap<>();
			itemRatingsMap.put(userItemRating.item(), ratings);
		}
		return Optional.ofNullable(ratings.put(userItemRating.user(), userItemRating));
	}

	@Override
	public Optional<UserItemRating> remove(final long user, final long item) {
		final Optional<UserItemRating> removedUserRatingsMap = removeFromUserRatingsMap(user, item);
		final Optional<UserItemRating> removedItemRatingsMap = removeFromItemRatingsMap(user, item);
		if (!removedUserRatingsMap.equals(removedItemRatingsMap)) {
			throw new AssertionError("!removedUserRatingsMap.equals(removedItemRatingsMap)");
		}
		return removedUserRatingsMap;
	}

	private Optional<UserItemRating> removeFromUserRatingsMap(final long user, final long item) {
		final Map<Long, UserItemRating> ratings = userRatingsMap.get(user);
		if (ratings == null) {
			return Optional.empty();
		}
		final UserItemRating result = ratings.remove(item);
		if (ratings.isEmpty()) {
			userRatingsMap.remove(user);
		}
		return Optional.ofNullable(result);
	}

	private Optional<UserItemRating> removeFromItemRatingsMap(final long user, final long item) {
		final Map<Long, UserItemRating> ratings = itemRatingsMap.get(item);
		if (ratings == null) {
			return Optional.empty();
		}
		final UserItemRating result = ratings.remove(user);
		if (ratings.isEmpty()) {
			itemRatingsMap.remove(item);
		}
		return Optional.ofNullable(result);
	}

	@Override
	public Optional<UserItemRating> rating(final long user, final long item) {
		final Map<Long, UserItemRating> ratings = userRatingsMap.get(user);
		if (ratings == null) {
			return Optional.empty();
		}
		return Optional.ofNullable(ratings.get(item));
	}

	@Override
	public Collection<UserItemRating> ratingsOfUser(final long user) {
		final Map<Long, UserItemRating> ratings = userRatingsMap.get(user);
		return ratings.values();
	}

	@Override
	public Collection<UserItemRating> ratingsForItem(final long item) {
		final Map<Long, UserItemRating> ratings = itemRatingsMap.get(item);
		return ratings.values();
	}

	@Override
	public Set<Long> users() {
		return userRatingsMap.keySet();
	}

	@Override
	public Set<Long> items() {
		return itemRatingsMap.keySet();
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((itemRatingsMap == null) ? 0 : itemRatingsMap.hashCode());
		result = prime * result + ((userRatingsMap == null) ? 0 : userRatingsMap.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final RatingMatrixImpl other = (RatingMatrixImpl) obj;
		if (itemRatingsMap == null) {
			if (other.itemRatingsMap != null) {
				return false;
			}
		} else if (!itemRatingsMap.equals(other.itemRatingsMap)) {
			return false;
		}
		if (userRatingsMap == null) {
			if (other.userRatingsMap != null) {
				return false;
			}
		} else if (!userRatingsMap.equals(other.userRatingsMap)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return String.format("RatingMatrixImpl [userRatingsMap=%s, itemRatingsMap=%s]", userRatingsMap, itemRatingsMap);
	}

}

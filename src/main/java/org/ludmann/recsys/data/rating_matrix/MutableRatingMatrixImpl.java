package org.ludmann.recsys.data.rating_matrix;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.ludmann.recsys.data.rating.UserItemRating;

/**
 * @author Cornelius A. Ludmann
 *
 */
class MutableRatingMatrixImpl extends AbstractRatingMatrix implements MutableRatingMatrix {

	private final Map<Long, Map<Long, UserItemRating>> userRatingsMap;
	private final Map<Long, Map<Long, UserItemRating>> itemRatingsMap;
	private int size;

	MutableRatingMatrixImpl() {
		userRatingsMap = new HashMap<>();
		itemRatingsMap = new HashMap<>();
		size = 0;
	}

	MutableRatingMatrixImpl(final RatingMatrix ratingMatrix) {
		userRatingsMap = new HashMap<>();
		itemRatingsMap = new HashMap<>();
		size = 0;
		for (final UserItemRating userItemRating : ratingMatrix) {
			add(userItemRating);
		}
	}

	@Override
	protected Map<Long, Map<Long, UserItemRating>> userRatingsMap() {
		return userRatingsMap;
	}

	@Override
	protected Map<Long, Map<Long, UserItemRating>> itemRatingsMap() {
		return itemRatingsMap;
	}

	@Override
	public Optional<UserItemRating> add(final UserItemRating userItemRating) {

		final Optional<UserItemRating> oldUserRatingsMap = addToUserRatingsMap(userItemRating);
		final Optional<UserItemRating> oldItemRatingsMap = addToItemRatingsMap(userItemRating);
		if (!oldItemRatingsMap.equals(oldUserRatingsMap)) {
			throw new AssertionError("!oldItemRatingsMap.equals(oldUserRatingsMap)");
		}
		if (!oldItemRatingsMap.isPresent()) {
			++size;
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
		if (!removedUserRatingsMap.isPresent()) {
			--size;
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
	public int size() {
		return size;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((itemRatingsMap == null) ? 0 : itemRatingsMap.hashCode());
		result = prime * result + size;
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
		final MutableRatingMatrixImpl other = (MutableRatingMatrixImpl) obj;
		if (itemRatingsMap == null) {
			if (other.itemRatingsMap != null) {
				return false;
			}
		} else if (!itemRatingsMap.equals(other.itemRatingsMap)) {
			return false;
		}
		if (size != other.size) {
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

}

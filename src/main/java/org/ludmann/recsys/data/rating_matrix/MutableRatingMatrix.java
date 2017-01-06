package org.ludmann.recsys.data.rating_matrix;

import java.util.Optional;

import org.ludmann.recsys.data.rating.UserItemRating;

/**
 * @author Cornelius A. Ludmann
 *
 */
public interface MutableRatingMatrix extends RatingMatrix {

	/**
	 * Adds the rating and returns the previous if exists.
	 * 
	 * @param userItemRating
	 *            The new rating.
	 * @return The previous if exists or Optional.empty() if not.
	 */
	Optional<UserItemRating> add(UserItemRating userItemRating);

	Optional<UserItemRating> remove(long user, long item);

}

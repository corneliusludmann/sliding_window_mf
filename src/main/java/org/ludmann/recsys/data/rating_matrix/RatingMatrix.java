package org.ludmann.recsys.data.rating_matrix;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import org.ludmann.recsys.data.rating.UserItemRating;

/**
 * @author Cornelius A. Ludmann
 *
 */
public interface RatingMatrix extends Iterable<UserItemRating> {

	public static MutableRatingMatrix newMutableRatingMatrix() {
		return new MutableRatingMatrixImpl();
	}

	public static ImmutableRatingMatrix immutableRatingMatrixOf(final RatingMatrix ratingMatrix) {
		if (ratingMatrix instanceof ImmutableRatingMatrix) {
			return (ImmutableRatingMatrix) ratingMatrix;
		}
		if (ratingMatrix instanceof AbstractRatingMatrix) {
			return ImmutableRatingMatrix.of((AbstractRatingMatrix) ratingMatrix);
		}
		return ImmutableRatingMatrix.of(ratingMatrix);
	}

	Optional<UserItemRating> rating(long user, long item);

	Collection<UserItemRating> ratingsOfUser(long user);

	Collection<UserItemRating> ratingsForItem(long item);

	Set<Long> users();

	Set<Long> items();

	String asAsciiMatrix();

	void createAsciiMatrix(Consumer<String> asciiConsumer);

	int size();
}

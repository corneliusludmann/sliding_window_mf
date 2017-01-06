package org.ludmann.matrix_factorization;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author Cornelius A. Ludmann
 *
 */
public interface RatingMatrix {
	public static RatingMatrix createInstance() {
		return new RatingMatrixImpl();
	}

	/**
	 * Adds the rating and returns the previous if exists.
	 * 
	 * @param userItemRating
	 *            The new rating.
	 * @return The previous if exists or Optional.empty() if not.
	 */
	Optional<UserItemRating> add(UserItemRating userItemRating);

	Optional<UserItemRating> remove(long user, long item);

	Optional<UserItemRating> rating(long user, long item);

	Collection<UserItemRating> ratingsOfUser(long user);

	Collection<UserItemRating> ratingsForItem(long item);

	Set<Long> users();

	Set<Long> items();

	String asAsciiMatrix();

	void createAsciiMatrix(Consumer<String> asciiConsumer);
}

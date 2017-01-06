package org.ludmann.matrix_factorization;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Cornelius A. Ludmann
 *
 */
public class UserItemRatingTest {

	@Test
	public void factoryMethodShouldCreateObject() {
		final Random random = new Random();
		final long user = random.nextLong();
		final long item = random.nextLong();
		final double rating = random.nextDouble();
		final UserItemRating userItemRating = UserItemRating.of(user, item, rating);
		Assert.assertEquals(user, userItemRating.user());
		Assert.assertEquals(item, userItemRating.item());
		Assert.assertEquals(rating, userItemRating.rating(), 1e-15);
	}
}

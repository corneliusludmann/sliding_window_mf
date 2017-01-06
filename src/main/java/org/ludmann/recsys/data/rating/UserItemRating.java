package org.ludmann.recsys.data.rating;

import com.google.auto.value.AutoValue;

/**
 * @author Cornelius A. Ludmann
 *
 */
@AutoValue
public abstract class UserItemRating {

	public static UserItemRating of(final long user, final long item, final double rating) {
		return new AutoValue_UserItemRating(user, item, rating);
	}

	public static UserItemRating zero() {
		return of(0, 0, 0.0);
	}

	public static UserItemRating nan() {
		return of(0, 0, Double.NaN);
	}

	public abstract long user();

	public abstract long item();

	public abstract double rating();

}

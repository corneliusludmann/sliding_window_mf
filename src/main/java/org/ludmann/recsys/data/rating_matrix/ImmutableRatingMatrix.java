package org.ludmann.recsys.data.rating_matrix;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.NotImplementedException;
import org.ludmann.recsys.data.rating.UserItemRating;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * @author Cornelius A. Ludmann
 *
 */
@AutoValue
public abstract class ImmutableRatingMatrix extends AbstractRatingMatrix {

	public static ImmutableRatingMatrix of(final AbstractRatingMatrix abstractRatingMatrix) {
		final Builder<Long, Map<Long, UserItemRating>> userRatingsMapBuilder = ImmutableMap.builder();
		for (final Entry<Long, Map<Long, UserItemRating>> entry : abstractRatingMatrix.userRatingsMap().entrySet()) {
			userRatingsMapBuilder.put(entry.getKey(), ImmutableMap.copyOf(entry.getValue()));
		}
		final Builder<Long, Map<Long, UserItemRating>> itemRatingsMapBuilder = ImmutableMap.builder();
		for (final Entry<Long, Map<Long, UserItemRating>> entry : abstractRatingMatrix.itemRatingsMap().entrySet()) {
			userRatingsMapBuilder.put(entry.getKey(), ImmutableMap.copyOf(entry.getValue()));
		}
		return new AutoValue_ImmutableRatingMatrix(userRatingsMapBuilder.build(), itemRatingsMapBuilder.build());
	}

	public static ImmutableRatingMatrix of(final RatingMatrix ratingMatrix) {
		// TODO Auto-generated method stub
		throw new NotImplementedException("not implemented yet");
	}

	@Override
	protected abstract Map<Long, Map<Long, UserItemRating>> userRatingsMap();

	@Override
	protected abstract Map<Long, Map<Long, UserItemRating>> itemRatingsMap();

}

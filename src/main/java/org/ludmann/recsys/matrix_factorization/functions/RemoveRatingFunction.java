package org.ludmann.recsys.matrix_factorization.functions;

import java.util.Optional;

import org.ludmann.recsys.data.rating.UserItemRating;
import org.ludmann.recsys.data.rating_matrix.MutableRatingMatrix;
import org.ludmann.recsys.matrix_factorization.model.FeatureMatrix;

/**
 * @author Cornelius A. Ludmann
 *
 */
@FunctionalInterface
public interface RemoveRatingFunction {

	/**
	 * @param ratingMatrix
	 * @param userFeatureMatrix
	 * @param itemFeatureMatrix
	 * @param rating
	 * @param prevRating
	 */
	void apply(MutableRatingMatrix ratingMatrix, FeatureMatrix userFeatureMatrix, FeatureMatrix itemFeatureMatrix,
			UserItemRating rating, Optional<UserItemRating> prevRating);

}

package org.ludmann.recsys.matrix_factorization.functions;

import org.ludmann.recsys.data.rating_matrix.RatingMatrix;
import org.ludmann.recsys.matrix_factorization.model.FeatureMatrix;

/**
 * @author Cornelius A. Ludmann
 *
 */
@FunctionalInterface
public interface InitialTrainFunction {

	/**
	 * @param ratingMatrix
	 * @param userFeatureMatrix
	 * @param itemFeatureMatrix
	 */
	void apply(RatingMatrix ratingMatrix, FeatureMatrix userFeatureMatrix, FeatureMatrix itemFeatureMatrix);

}

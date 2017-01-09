package org.ludmann.recsys.matrix_factorization.model;

import org.ludmann.recsys.data.rating_matrix.RatingMatrix;

/**
 * @author Cornelius A. Ludmann
 *
 */
public interface MfModel {

	void setRatingMatrix(RatingMatrix ratingMatrix);

	FeatureMatrix userFeatureMatrix();

	FeatureMatrix itemFeatureMatrix();

	double rating(long user, long item);
}

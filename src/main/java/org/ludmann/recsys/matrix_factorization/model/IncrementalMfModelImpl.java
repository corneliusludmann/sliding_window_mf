package org.ludmann.recsys.matrix_factorization.model;

import java.util.Optional;

import org.ludmann.recsys.data.rating.UserItemRating;
import org.ludmann.recsys.data.rating_matrix.MutableRatingMatrix;
import org.ludmann.recsys.data.rating_matrix.RatingMatrix;
import org.ludmann.recsys.matrix_factorization.functions.AddRatingFunction;
import org.ludmann.recsys.matrix_factorization.functions.InitialFeatureMatricesFunction;
import org.ludmann.recsys.matrix_factorization.functions.InitialTrainFunction;
import org.ludmann.recsys.matrix_factorization.functions.RemoveRatingFunction;

/**
 * @author Cornelius A. Ludmann
 *
 */
public class IncrementalMfModelImpl implements IncrementalMfModel {

	private final MutableRatingMatrix ratingMatrix;
	private final FeatureMatrix userFeatureMatrix;
	private final FeatureMatrix itemFeatureMatrix;
	private final AddRatingFunction addRatingFunction;
	private final RemoveRatingFunction removeRatingFunction;
	private final InitialFeatureMatricesFunction initialFeatureMatricesFunction;
	private final InitialTrainFunction initialTrainFunction;

	public IncrementalMfModelImpl(final int numberOfFeatures, final AddRatingFunction addRatingFunction,
			final RemoveRatingFunction removeRatingFunction,
			final InitialFeatureMatricesFunction initialFeatureMatricesFunction,
			final InitialTrainFunction initialTrainFunction) {
		ratingMatrix = RatingMatrix.newMutableRatingMatrix();
		userFeatureMatrix = FeatureMatrix.newMatrix(numberOfFeatures);
		itemFeatureMatrix = FeatureMatrix.newMatrix(numberOfFeatures);
		this.addRatingFunction = addRatingFunction;
		this.removeRatingFunction = removeRatingFunction;
		this.initialFeatureMatricesFunction = initialFeatureMatricesFunction;
		this.initialTrainFunction = initialTrainFunction;
	}

	@Override
	public void setRatingMatrix(RatingMatrix ratingMatrix) {
		ratingMatrix = RatingMatrix.mutableRatingMatrixOf(ratingMatrix);
		initialFeatureMatricesFunction.apply(ratingMatrix, userFeatureMatrix, itemFeatureMatrix);
		initialTrainFunction.apply(ratingMatrix, userFeatureMatrix, itemFeatureMatrix);
	}

	@Override
	public void addRating(final UserItemRating rating) {
		final Optional<UserItemRating> prevRating = ratingMatrix.add(rating);
		addRatingFunction.apply(ratingMatrix, userFeatureMatrix, itemFeatureMatrix, rating, prevRating);
	}

	@Override
	public void removeRating(final UserItemRating rating) {
		final Optional<UserItemRating> prevRating = ratingMatrix.remove(rating);
		removeRatingFunction.apply(ratingMatrix, userFeatureMatrix, itemFeatureMatrix, rating, prevRating);
	}

	@Override
	public FeatureMatrix userFeatureMatrix() {
		return userFeatureMatrix;
	}

	@Override
	public FeatureMatrix itemFeatureMatrix() {
		return itemFeatureMatrix;
	}

	@Override
	public double rating(final long user, final long item) {
		return FeatureMatrix.dotProduct(userFeatureMatrix, itemFeatureMatrix, user, item);
	}

}

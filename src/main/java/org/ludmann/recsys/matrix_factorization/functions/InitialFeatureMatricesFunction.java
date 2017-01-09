package org.ludmann.recsys.matrix_factorization.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import org.ludmann.recsys.data.rating_matrix.RatingMatrix;
import org.ludmann.recsys.matrix_factorization.model.FeatureMatrix;

/**
 * @author Cornelius A. Ludmann
 *
 */
@FunctionalInterface
public interface InitialFeatureMatricesFunction {

	public static Supplier<List<Double>> randomDoubleFeatureVectorSupplier(final Random random,
			final int noOfFeatures) {
		return () -> {
			final List<Double> result = new ArrayList<>();
			for (int i = 0; i < noOfFeatures; ++i) {
				result.add(random.nextDouble());
			}
			return result;
		};
	}

	public static InitialFeatureMatricesFunction of(final Supplier<List<Double>> initFeatureVectorSupplier) {
		return (final RatingMatrix ratingMatrix, final FeatureMatrix userFeatureMatrix,
				final FeatureMatrix itemFeatureMatrix) -> {
			for (final long user : ratingMatrix.users()) {
				userFeatureMatrix.initFeatureVectorIfNotExists(user, initFeatureVectorSupplier);
			}
			for (final long item : ratingMatrix.items()) {
				itemFeatureMatrix.initFeatureVectorIfNotExists(item, initFeatureVectorSupplier);
			}
		};
	}

	/**
	 * @param ratingMatrix
	 * @param userFeatureMatrix
	 * @param itemFeatureMatrix
	 */
	void apply(RatingMatrix ratingMatrix, FeatureMatrix userFeatureMatrix, FeatureMatrix itemFeatureMatrix);
}

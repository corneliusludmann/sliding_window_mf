package org.ludmann.recsys.matrix_factorization.model;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Cornelius A. Ludmann
 *
 */
public interface FeatureMatrix {

	public static FeatureMatrix newMatrix(final int features) {
		return new FeatureMatrixImpl(features);
	}

	public static double dotProduct(final FeatureMatrix userFeatureMatrix, final FeatureMatrix itemFeatureMatrix,
			final long user, final long item) {
		final List<Double> userFeatureVector = userFeatureMatrix.featureVector(user);
		final List<Double> itemFeatureVector = itemFeatureMatrix.featureVector(item);
		if (userFeatureVector.size() != itemFeatureVector.size()) {
			throw new IllegalArgumentException("Sizes of feature vectors are not the same.");
		}
		double result = 0.0;
		for (int i = 0; i < userFeatureVector.size(); ++i) {
			result += userFeatureVector.get(i) * itemFeatureVector.get(i);
		}
		return result;
	}

	/**
	 * @return The row IDs (users resp. items).
	 */
	Set<Long> rows();

	/**
	 * @param row
	 * @return
	 */
	List<Double> featureVector(long row);

	void removeFeatureVector(long row);

	/**
	 * @param row
	 * @param feature
	 * @param value
	 */
	void set(long row, int feature, double value);

	/**
	 * @param row
	 * @param initFunction
	 */
	void initFeatureVectorIfNotExists(long row, Supplier<List<Double>> initFunction);

}

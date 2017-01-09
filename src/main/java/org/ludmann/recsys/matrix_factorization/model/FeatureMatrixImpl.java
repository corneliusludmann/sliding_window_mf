package org.ludmann.recsys.matrix_factorization.model;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import org.apache.commons.collections4.map.HashedMap;

/**
 * @author Cornelius A. Ludmann
 *
 */
public class FeatureMatrixImpl implements FeatureMatrix {

	private final Map<Long, List<Double>> featureVectors;
	private final int features;

	/**
	 * @param features
	 *            Number of features (columns).
	 */
	public FeatureMatrixImpl(final int features) {
		featureVectors = new HashedMap<>();
		this.features = features;
	}

	@Override
	public void set(final long row, final int feature, final double value) {
		if (feature >= features || feature < 0) {
			throw new IllegalArgumentException("illegal feature number");
		}
		if (!featureVectors.containsKey(row)) {
			throw new IllegalArgumentException("row does not exist");
		}
		featureVectors.get(row).set(feature, value);
	}

	@Override
	public void initFeatureVectorIfNotExists(final long row, final Supplier<List<Double>> initFunction) {
		if (!featureVectors.containsKey(row)) {
			final List<Double> featureVector = initFunction.get();
			if (featureVector.size() != features) {
				throw new IllegalArgumentException("Feature vector has wrong size.");
			}
			featureVectors.put(row, featureVector);
		}
	}

	@Override
	public Set<Long> rows() {
		return featureVectors.keySet();
	}

	@Override
	public List<Double> featureVector(final long row) {
		return featureVectors.get(row);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ludmann.recsys.matrix_factorization.model.FeatureMatrix#
	 * removeFeatureVector(long)
	 */
	@Override
	public void removeFeatureVector(final long row) {
		featureVectors.remove(row);
	}

}

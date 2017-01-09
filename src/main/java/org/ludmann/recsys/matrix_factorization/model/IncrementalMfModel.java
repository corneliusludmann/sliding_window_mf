package org.ludmann.recsys.matrix_factorization.model;

import org.ludmann.recsys.data.rating.UserItemRating;

/**
 * @author Cornelius A. Ludmann
 *
 */
public interface IncrementalMfModel extends MfModel {

	void addRating(UserItemRating rating);

	void removeRating(UserItemRating rating);
}

package org.ludmann.sliding_window_mf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.ludmann.recsys.data.rating.UserItemRating;
import org.ludmann.recsys.data.rating_matrix.MutableRatingMatrix;
import org.ludmann.recsys.data.rating_matrix.RatingMatrix;
import org.ludmann.recsys.matrix_factorization.functions.AddRatingFunction;
import org.ludmann.recsys.matrix_factorization.functions.InitialFeatureMatricesFunction;
import org.ludmann.recsys.matrix_factorization.functions.InitialTrainFunction;
import org.ludmann.recsys.matrix_factorization.functions.ModelErrorFunction;
import org.ludmann.recsys.matrix_factorization.functions.RemoveRatingFunction;
import org.ludmann.recsys.matrix_factorization.model.FeatureMatrix;
import org.ludmann.recsys.matrix_factorization.model.IncrementalMfModel;
import org.ludmann.recsys.matrix_factorization.model.IncrementalMfModelImpl;
import org.ludmann.sliding_window_mf.io.ReadUserItemRatingCsv;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

/**
 * @author Cornelius A. Ludmann
 *
 */
public class Main {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {

		final OptionParser optionParser = new OptionParser("f:d:msrti");
		final OptionSet optionSet = optionParser.parse(args);

		final MutableRatingMatrix ratingMatrix = RatingMatrix.newMutableRatingMatrix();

		final String delim = optionSet.has("d") ? (String) optionSet.valueOf("d") : ",";

		if (optionSet.has("f")) {
			final String fileName = (String) optionSet.valueOf("f");
			if (fileName.equals("-")) {
				readFromStdIn(delim, ratingMatrix::add);
			} else {
				try (BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(fileName)))) {
					ReadUserItemRatingCsv.of(bufferedReader, ratingMatrix::add, delim).run();
				}
			}
		} else {
			readFromStdIn(delim, ratingMatrix::add);
		}

		// Print matrix
		if (optionSet.has("m")) {
			ratingMatrix.createAsciiMatrix(System.out::print);
		}

		// Print all ratings
		if (optionSet.has("r")) {
			int i = 0;
			for (final UserItemRating rating : ratingMatrix) {
				System.out.print((i++) + "  ");
				System.out.println(rating);
			}
		}

		// Print summary
		if (optionSet.has("s")) {
			System.out.format("Users:   %5d%n", ratingMatrix.users().size());
			System.out.format("Items:   %5d%n", ratingMatrix.items().size());
			System.out.format("Ratings: %5d%n", ratingMatrix.size());
		}

		if (optionSet.has("t")) {
			train(ratingMatrix, optionSet.has("i"));
		}
	}

	private static void readFromStdIn(final String delim, final Consumer<UserItemRating> consumer) throws IOException {
		try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));) {
			ReadUserItemRatingCsv.of(bufferedReader, consumer, delim).run();
		}
	}

	private static void train(final MutableRatingMatrix ratingMatrix, final boolean incremental) {

		final Random random = new Random(1234);
		final int noOfFeatures = 10;

		final ModelErrorFunction modelErrorFunction = new ModelErrorFunction() {
			/**
			 * The factor for the regularization influence (algorithm
			 * parameter).
			 */
			protected double regularizationFactor = 0.02;

			@Override
			public double apply(final RatingMatrix ratingMatrix, final FeatureMatrix userFeatureMatrix,
					final FeatureMatrix itemFeatureMatrix) {
				double modelError = 0;
				int count = 0;
				for (final UserItemRating userItemRating : ratingMatrix) {
					final long user = userItemRating.user();

					final long item = userItemRating.item();
					final double rating = userItemRating.rating();

					final double predRating = FeatureMatrix.dotProduct(userFeatureMatrix, itemFeatureMatrix, user,
							item);

					++count;
					// square error
					double error = Math.pow(rating - predRating, 2);

					// regularization
					double regularization = 0;
					for (int k = 0; k < noOfFeatures; ++k) {
						regularization += regularizationFactor
								* (Math.pow(userFeatureMatrix.featureVector(user).get(k), 2)
										+ Math.pow(itemFeatureMatrix.featureVector(item).get(k), 2));
					}
					error = 0.5 * (error + regularization);

					// mean square error
					modelError += (error - modelError) / count;

				}
				return Math.sqrt(modelError);
			}
		};

		final InitialTrainFunction initialTrainFunction = new InitialTrainFunction() {

			/**
			 * The maximum number of steps until the algorithms stops (algorithm
			 * parameter).
			 */
			protected int maxSteps = Integer.MAX_VALUE;
			/**
			 * The learning rate (algorithm parameter).
			 */
			protected double learningRate = 0.0002;
			/**
			 * The factor for the regularization influence (algorithm
			 * parameter).
			 */
			protected double regularizationFactor = 0.02;
			/**
			 * The algorithm stops when it converges, which means that the model
			 * error does not decrease anymore. This parameter controls when two
			 * model errors are regarded as equal.
			 */
			// protected double convergenceTolerance = 0.0001;
			protected double convergenceTolerance = 1e-8;

			@Override
			public void apply(final RatingMatrix ratingMatrix, final FeatureMatrix userFeatureMatrix,
					final FeatureMatrix itemFeatureMatrix) {
				double lastModelError = Double.MAX_VALUE;

				for (int i = 0; i < maxSteps; ++i) {
					for (final UserItemRating userItemRating : ratingMatrix) {
						final long user = userItemRating.user();
						final long item = userItemRating.item();
						final double rating = userItemRating.rating();
						final double predRating = FeatureMatrix.dotProduct(userFeatureMatrix, itemFeatureMatrix, user,
								item);
						final double error = rating - predRating;

						for (int k = 0; k < noOfFeatures; ++k) {
							double userFeatureValue = userFeatureMatrix.featureVector(user).get(k);
							final double itemFeatureValue = itemFeatureMatrix.featureVector(item).get(k);
							userFeatureMatrix.set(user, k, userFeatureValue + learningRate
									* (2 * error * itemFeatureValue - regularizationFactor * userFeatureValue));
							userFeatureValue = userFeatureMatrix.featureVector(user).get(k);
							itemFeatureMatrix.set(item, k, itemFeatureValue + learningRate
									* (2 * error * userFeatureValue - regularizationFactor * itemFeatureValue));
						}
					}

					final double modelError = modelErrorFunction.apply(ratingMatrix, userFeatureMatrix,
							itemFeatureMatrix);
					final boolean breakCondition = modelError + convergenceTolerance >= lastModelError;
					if (breakCondition || i % 10_000 == 0) {
						System.out.format("step: %7d       error diff: %s%n", i, "" + (lastModelError - modelError));
					}
					if (breakCondition) {
						break;
					}
					lastModelError = modelError;
				}
			}

		};

		final Supplier<List<Double>> initFunction = InitialFeatureMatricesFunction
				.randomDoubleFeatureVectorSupplier(random, noOfFeatures);

		final AddRatingFunction addRatingFunction = new AddRatingFunction() {

			@Override
			public void apply(final MutableRatingMatrix ratingMatrix, final FeatureMatrix userFeatureMatrix,
					final FeatureMatrix itemFeatureMatrix, final UserItemRating rating,
					final Optional<UserItemRating> prevRating) {
				final long user = rating.user();
				final long item = rating.item();
				userFeatureMatrix.initFeatureVectorIfNotExists(user, initFunction);
				itemFeatureMatrix.initFeatureVectorIfNotExists(item, initFunction);
				initialTrainFunction.apply(ratingMatrix, userFeatureMatrix, itemFeatureMatrix);
			}
		};
		final RemoveRatingFunction removeRatingFunction = new RemoveRatingFunction() {
			@Override
			public void apply(final MutableRatingMatrix ratingMatrix, final FeatureMatrix userFeatureMatrix,
					final FeatureMatrix itemFeatureMatrix, final UserItemRating rating,
					final Optional<UserItemRating> prevRating) {
				if (!ratingMatrix.users().contains(rating.user())) {
					userFeatureMatrix.removeFeatureVector(rating.user());
				}
				if (!ratingMatrix.items().contains(rating.item())) {
					itemFeatureMatrix.removeFeatureVector(rating.item());
				}
			}
		};

		final InitialFeatureMatricesFunction initialFeatureMatricesFunction = InitialFeatureMatricesFunction
				.of(initFunction);

		final IncrementalMfModel model = new IncrementalMfModelImpl(noOfFeatures, addRatingFunction,
				removeRatingFunction, initialFeatureMatricesFunction, initialTrainFunction);
		if (incremental) {
			for (final UserItemRating userItemRating : ratingMatrix) {
				model.addRating(userItemRating);
			}
		} else {
			model.setRatingMatrix(ratingMatrix);
		}
		System.out.println("Model error: "
				+ modelErrorFunction.apply(ratingMatrix, model.userFeatureMatrix(), model.itemFeatureMatrix()));
		final RatingMatrix predictedRatingMatrix = predictedRatingMatrix(ratingMatrix, model);
		predictedRatingMatrix.createAsciiMatrix(System.out::print);
	}

	/**
	 * @param ratingMatrix
	 * @param model
	 * @return
	 */
	private static RatingMatrix predictedRatingMatrix(final MutableRatingMatrix ratingMatrix,
			final IncrementalMfModel model) {
		final MutableRatingMatrix result = RatingMatrix.newMutableRatingMatrix();
		for (final UserItemRating rating : ratingMatrix) {
			result.add(UserItemRating.of(rating.user(), rating.item(), model.rating(rating.user(), rating.item())));
		}
		return result;
	}
}

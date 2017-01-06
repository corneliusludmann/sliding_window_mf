package org.ludmann.sliding_window_mf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.Consumer;

import org.ludmann.recsys.data.rating.UserItemRating;
import org.ludmann.recsys.data.rating_matrix.MutableRatingMatrix;
import org.ludmann.recsys.data.rating_matrix.RatingMatrix;
import org.ludmann.sliding_window_mf.io.ReadUserItemRatingCsv;

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

		final MutableRatingMatrix ratingMatrix = RatingMatrix.newMutableRatingMatrix();
		readFromStdIn("::", ratingMatrix::add);

		// System.out.println();
		// ratingMatrix.createAsciiMatrix(System.out::print);
		System.out.println();
		System.out.println("Users: " + ratingMatrix.users().size());
		System.out.println("Items: " + ratingMatrix.items().size());

		int i = 0;
		for (final UserItemRating rating : ratingMatrix) {
			System.out.print((i++) + "  ");
			System.out.println(rating);
		}
	}

	private static void readFromStdIn(final String delim, final Consumer<UserItemRating> consumer) throws IOException {
		// System.out.println("Reading data from std input.");
		// System.out.println("user (long)" + delim + " item (long)" + delim + "
		// rating (double)");
		// System.out.println("Blank line exits.");
		try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));) {
			ReadUserItemRatingCsv.of(bufferedReader, consumer, delim).run();
		}

	}

}

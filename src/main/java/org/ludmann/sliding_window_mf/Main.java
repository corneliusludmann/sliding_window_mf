package org.ludmann.sliding_window_mf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.Consumer;

import org.ludmann.matrix_factorization.RatingMatrix;
import org.ludmann.matrix_factorization.UserItemRating;
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

		final RatingMatrix ratingMatrix = RatingMatrix.createInstance();
		readFromStdIn("::", ratingMatrix::add);

		// System.out.println();
		ratingMatrix.createAsciiMatrix(System.out::print);
		System.out.println();
		System.out.println("Users: " + ratingMatrix.users().size());
		System.out.println("Items: " + ratingMatrix.items().size());
	}

	/**
	 * @throws IOException
	 * 
	 */
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

package org.ludmann.sliding_window_mf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.Consumer;

import org.ludmann.recsys.data.rating.UserItemRating;
import org.ludmann.recsys.data.rating_matrix.MutableRatingMatrix;
import org.ludmann.recsys.data.rating_matrix.RatingMatrix;
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

		final OptionParser optionParser = new OptionParser("f:d:msr");
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

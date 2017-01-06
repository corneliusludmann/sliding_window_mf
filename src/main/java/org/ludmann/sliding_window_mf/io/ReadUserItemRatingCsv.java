package org.ludmann.sliding_window_mf.io;

import java.io.BufferedReader;
import java.util.function.Consumer;

import org.ludmann.recsys.data.rating.UserItemRating;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.value.AutoValue;

/**
 * @author Cornelius A. Ludmann
 *
 */
@AutoValue
public abstract class ReadUserItemRatingCsv implements Runnable {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public static ReadUserItemRatingCsv of(final BufferedReader bufferedReader, final Consumer<UserItemRating> consumer,
			final String delimiter) {
		return new AutoValue_ReadUserItemRatingCsv(bufferedReader, consumer, delimiter);
	}

	abstract BufferedReader bufferedReader();

	abstract Consumer<UserItemRating> consumer();

	abstract String delimiter();

	@Override
	public void run() {
		try {
			for (String line; (line = bufferedReader().readLine()) != null;) {
				logger.info("Processing line '{}' ...", line);
				if (line.trim().equals(""))
					break;
				final String[] fields = line.split(delimiter());
				if (fields.length < 3) {
					logger.warn("Ignoring line '{}'. Field size is {} but should >= 3.", line, fields.length);
				} else {
					try {
						final long user = Long.parseLong(fields[0].trim());
						final long item = Long.parseLong(fields[1].trim());
						final double rating = Double.parseDouble(fields[2].trim());
						consumer().accept(UserItemRating.of(user, item, rating));
					} catch (final NumberFormatException | NullPointerException exception) {
						logger.warn("Error parsing line '{}'.", line, fields.length, exception);
					}
				}
			}
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

}

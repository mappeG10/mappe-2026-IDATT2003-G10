package edu.ntnu.idi.idatt.dal.dto;

import edu.ntnu.idi.idatt.model.transaction.TransactionType;
import java.math.BigDecimal;

/**
 * Data transfer object representing a single committed transaction in a player's history.
 *
 * <p>Stores the minimal set of fields needed to reconstruct a
 * {@link edu.ntnu.idi.idatt.model.transaction.Transaction} on game load: the direction,
 * the stock, the quantity, the price at which the trade was executed, and the week
 * in which it occurred.</p>
 *
 * @param type        the direction of the transaction (purchase or sale)
 * @param stockSymbol the ticker symbol of the stock involved in the transaction
 * @param quantity    the number of shares traded
 * @param price       the price per share at which the transaction was executed
 * @param week        the game week in which the transaction was committed
 */
public record TransactionDto(
    TransactionType type,
    String stockSymbol,
    BigDecimal quantity,
    BigDecimal price,
    int week
) {}

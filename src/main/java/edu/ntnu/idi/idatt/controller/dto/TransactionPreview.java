package edu.ntnu.idi.idatt.controller.dto;

import java.math.BigDecimal;

/**
 * Data transfer object carrying the cost or proceeds breakdown for a hypothetical transaction.
 *
 * <p>Produced by
 * {@link edu.ntnu.idi.idatt.controller.MarketController#previewBuy(String, BigDecimal)} and
 * {@link edu.ntnu.idi.idatt.controller.PortfolioController#previewSell(
 * edu.ntnu.idi.idatt.model.Share, BigDecimal)} to allow the user to review the financial
 * details of a trade before committing it.</p>
 *
 * @param gross      the pre-fee value of the transaction ({@code price × quantity})
 * @param commission the broker commission charged for the transaction
 * @param tax        the capital-gains tax applied to any realised profit; zero for purchases
 * @param total      the net amount that will be debited from or credited to the player's balance
 */
public record TransactionPreview(
    BigDecimal gross,
    BigDecimal commission,
    BigDecimal tax,
    BigDecimal total
) {}

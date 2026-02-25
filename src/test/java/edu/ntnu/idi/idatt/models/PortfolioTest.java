package edu.ntnu.idi.idatt.models;

import org.junit.jupiter.api.BeforeEach;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class PortfolioTest {

    Portfolio portfolio;
    Share share1;
    Share share2;
    Stock stockA;
    Stock stockB;

    @BeforeEach
    void setUp() {
        portfolio = new Portfolio();

        stockA = new Stock("AAPL", "Apple", new ArrayList<>(List.of(new BigDecimal("150.00"))));
        stockB = new Stock("GOOG", "Google", new ArrayList<>(List.of(new BigDecimal("2500.00"))));

        share1 = new Share(stockA, new BigDecimal("10"), new BigDecimal("1500.00")); // 10 shares of AAPL
        share2 = new Share(stockB, new BigDecimal("5"), new BigDecimal("12500.00")); // 5 shares of GOOG
    }

    @Test
    void testPortfolioConstructor() {
        assertNotNull(portfolio, "Portfolio should be initialized");
        assertTrue(portfolio.getShares().isEmpty(), "New portfolio should be empty");
    }

    @Test
    void testAddShare() {
        assertTrue(portfolio.addShare(share1), "Should successfully add share1");
        assertEquals(1, portfolio.getShares().size(), "Portfolio should contain one share");
        assertTrue(portfolio.contatins(share1), "Portfolio should contain share1");

        assertTrue(portfolio.addShare(share2), "Should successfully add share2");
        assertEquals(2, portfolio.getShares().size(), "Portfolio should contain two shares");
        assertTrue(portfolio.contatins(share2), "Portfolio should contain share2");

        assertTrue(portfolio.addShare(share1), "Should successfully add duplicate share1");
        assertEquals(3, portfolio.getShares().size(), "Portfolio should contain three shares including duplicate");

        // Test adding a null share (expect behavior of ArrayList.add, which is usually NullPointerException)
        assertThrows(NullPointerException.class, () -> portfolio.addShare(null), "Adding null share should throw NullPointerException");
    }

    @Test
    void testRemoveShare() {
        portfolio.addShare(share1);
        portfolio.addShare(share2);
        portfolio.addShare(share1); // Add a duplicate

        // Test removing an existing share
        assertTrue(portfolio.removeShare(share1), "Should successfully remove share1");
        assertEquals(2, portfolio.getShares().size(), "Portfolio should contain two shares after removing one instance of share1");
        // The contains method checks if *any* instance of the share exists. Since there was a duplicate, it should still return true.
        // Let's refine this assertion to check for the specific share object, or the number of remaining shares.
        // For now, let's assume 'contatins' checks for *presence*, not count.
        // After removing one share1, if a duplicate exists, 'contains' should still be true for share1.
        assertTrue(portfolio.contatins(share1), "Portfolio should still contain share1 as a duplicate existed");


        // Test removing the other instance of share1
        assertTrue(portfolio.removeShare(share1), "Should successfully remove the second instance of share1");
        assertEquals(1, portfolio.getShares().size(), "Portfolio should contain one share after removing all instances of share1");
        assertFalse(portfolio.contatins(share1), "Portfolio should not contain share1 anymore");

        // Test removing a non-existent share
        assertFalse(portfolio.removeShare(new Share(stockA, new BigDecimal("1"), new BigDecimal("100"))), "Should not remove a non-existent share");
        assertEquals(1, portfolio.getShares().size(), "Portfolio size should remain unchanged");

        // Test removing a null share (ArrayList.remove handles null by returning false if not found)
        assertFalse(portfolio.removeShare(null), "Removing null share should return false");
        assertEquals(1, portfolio.getShares().size(), "Portfolio size should remain unchanged after attempting to remove null");
    }

    @Test
    void testGetShares() {
        assertTrue(portfolio.getShares().isEmpty(), "getShares should return an empty list for an empty portfolio");

        portfolio.addShare(share1);
        assertEquals(1, portfolio.getShares().size(), "getShares should return list with one share");
        assertTrue(portfolio.getShares().contains(share1), "getShares should contain share1");

        portfolio.addShare(share2);
        assertEquals(2, portfolio.getShares().size(), "getShares should return list with two shares");
        assertTrue(portfolio.getShares().contains(share2), "getShares should contain share2");
    }

    @Test
    void testGetSharesBySymbol() {
        portfolio.addShare(share1); // AAPL
        portfolio.addShare(share2); // GOOG
        portfolio.addShare(new Share(stockA, new BigDecimal("5"), new BigDecimal("750.00"))); // another AAPL

        // Test getting shares for an existing symbol
        List<Share> aaplShares = portfolio.getShares("AAPL");
        assertNotNull(aaplShares, "Should not return null for existing symbol");
        assertEquals(2, aaplShares.size(), "Should return two shares for AAPL");
        assertTrue(aaplShares.stream().allMatch(share -> share.getStock().getSymbol().equals("AAPL")), "All returned shares should be AAPL");

        // Test getting shares for a non-existent symbol
        List<Share> msftShares = portfolio.getShares("MSFT");
        assertNotNull(msftShares, "Should not return null for non-existent symbol");
        assertTrue(msftShares.isEmpty(), "Should return an empty list for non-existent symbol");

        // Test getting shares when portfolio is empty
        Portfolio emptyPortfolio = new Portfolio();
        assertTrue(emptyPortfolio.getShares("AAPL").isEmpty(), "Should return empty list for empty portfolio");

        // Test getting shares with a null symbol (expect behavior of stream filter, likely no match)
        List<Share> nullSymbolShares = portfolio.getShares(null);
        assertNotNull(nullSymbolShares, "Should not return null for null symbol");
        assertTrue(nullSymbolShares.isEmpty(), "Should return empty list for null symbol");
    }

    @Test
    void testContainsShare() {
        assertFalse(portfolio.contatins(share1), "Empty portfolio should not contain any share");

        portfolio.addShare(share1);
        assertTrue(portfolio.contatins(share1), "Portfolio should contain share1 after adding");
        assertFalse(portfolio.contatins(share2), "Portfolio should not contain share2 before adding");

        portfolio.addShare(share2);
        assertTrue(portfolio.contatins(share2), "Portfolio should contain share2 after adding");

        portfolio.removeShare(share1);
        assertFalse(portfolio.contatins(share1), "Portfolio should not contain share1 after removing");

        // Test contains with a null share (ArrayList.contains handles null by returning false)
        assertFalse(portfolio.contatins(null), "Portfolio should not contain null share");
    }
}
package com.internal_compiler_error.Portfolio;

import java.math.BigDecimal;

public class MutualFund extends Investment {
    private final BigDecimal REDEMPTION_FEE = new BigDecimal("45");


    @Override
    public String toString() {
        return "Mutual Fund {\n" +
                "symbol = " + getSymbol() + ",\n" +
                "name = " + getName() + ",\n" +
                "quantity = " + getQuantity() + ",\n" +
                "price = " + getPrice() + ",\n" +
                "bookValue = " + getBookValue() + "\n" +
                '}';
    }

    /**
     * Constructor for MutualFund
     *
     * @param symbol   the symbol of the MutualFund product
     * @param name     the name of the product
     * @param quantity the quantity purchased
     * @param price    the price of the product
     */
    public MutualFund(String symbol, String name, int quantity, BigDecimal price) throws Exception {
        super(symbol, name, quantity, price);
        setInvestmentType(InvestmentType.MUTUAL_FUND);
    }

    /**
     * Constructor for MutualFund for reading from file
     *
     * @param symbol    the symbol of the MutualFund product
     * @param name      the name of the product
     * @param quantity  the quantity purchased
     * @param price     the price of the product
     * @param bookValue the cost of owning such product
     */
    public MutualFund(String symbol, String name, int quantity, BigDecimal price, BigDecimal bookValue) {
        super(symbol, name, quantity, price, bookValue);
        setInvestmentType(InvestmentType.MUTUAL_FUND);
    }

    /**
     * Dummy constructor only used for binary search. The returned object is not intended for actual use
     *
     * @param symbol the symbol of the MutualFund
     */
    public MutualFund(String symbol) {
        setSymbol(symbol);
        setInvestmentType(InvestmentType.MUTUAL_FUND);
    }


    /**
     * Calculate the current gain if all is sold at the current price.
     * Formula: bookValue - price * quantity - 45
     *
     * @return the total gain
     */
    @Override
    BigDecimal getGain() {
        var gainWithoutRedemptionFee = super.getGain();
        return gainWithoutRedemptionFee.subtract(REDEMPTION_FEE);
    }

    /**
     * Sell some or all investment products. The selling price will become the new price.
     *
     * @param quantity the quantity to be sold
     * @param price    the selling price
     * @return The gain from the trade
     * @throws IllegalQuantityException if the quantity is negative or exceeds the quantity possessed
     */
    @Override
    BigDecimal sell(int quantity, BigDecimal price) throws IllegalQuantityException {
        var profitWithoutRedemptionFee = super.sell(quantity, price);
        return profitWithoutRedemptionFee.subtract(REDEMPTION_FEE);
    }
}
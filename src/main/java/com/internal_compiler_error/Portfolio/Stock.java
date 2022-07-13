package com.internal_compiler_error.Portfolio;

import java.math.BigDecimal;
import java.util.Objects;

public class Stock extends Investment {
    private final BigDecimal COMMISSION = new BigDecimal("9.99");

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Stock stock = (Stock) o;
        return COMMISSION.equals(stock.COMMISSION);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), COMMISSION);
    }

    @Override
    public String toString() {
        return "Stock {\n" +
                "symbol = " + getSymbol() + ",\n" +
                "name = " + getName() + ",\n" +
                "quantity = " + getQuantity() + ",\n" +
                "price = " + getPrice() + ",\n" +
                "bookValue = " + getBookValue() + "\n" +
                '}';
    }

    /**
     * Constructor for Stock
     *
     * @param symbol   the symbol of the Stock product
     * @param name     the name of the product
     * @param quantity the quantity purchased
     * @param price    the price of the product
     */
    public Stock(String symbol, String name, int quantity, BigDecimal price) throws Exception {
        super(symbol, name, quantity, price);

        setInvestmentType(InvestmentType.STOCK);
        var bookValueWithoutCommission = getBookValue();
        setBookValue(bookValueWithoutCommission.add(COMMISSION));
    }

    /**
     * Dummy constructor only used for binary search. The returned object is not intended for actual use
     *
     * @param symbol the symbol of the MutualFund
     */
    public Stock(String symbol) {
        setSymbol(symbol);
        setInvestmentType(InvestmentType.STOCK);
    }

    /**
     * Constructor for stock for use of reading from a file
     *
     * @param symbol    the symbol of the investment
     * @param name      the name of the investment
     * @param quantity  the quantity to be purchased
     * @param price     the initial price of the product
     * @param bookValue the cost of owning the product
     */
    public Stock(String symbol, String name, int quantity, BigDecimal price, BigDecimal bookValue) {
        super(symbol, name, quantity, price, bookValue);
        setInvestmentType(InvestmentType.STOCK);
    }

    /**
     * Default constructor
     */
    public Stock() {
        setInvestmentType(InvestmentType.STOCK);
    }

    /**
     * Purchase additional product
     *
     * @param quantity the additional quantify
     * @param price    the new purchase price
     * @throws IllegalQuantityException if the quantity is negative
     */
    @Override
    void buy(int quantity, BigDecimal price) throws IllegalQuantityException {
        super.buy(quantity, price);

        var bookValueWithoutCommission = getBookValue();
        setBookValue(bookValueWithoutCommission.add(COMMISSION));
    }


    /**
     * Calculate the current gain if all is sold at the current price.
     * Formula: bookValue - price * quantity - 9.99
     *
     * @return the total gain
     */
    @Override
    BigDecimal getGain() {
        var gainWithoutCommission = super.getGain();
        return gainWithoutCommission.subtract(COMMISSION);
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
        var profitWithoutCommission = super.sell(quantity, price);
        return profitWithoutCommission.subtract(COMMISSION);
    }
}

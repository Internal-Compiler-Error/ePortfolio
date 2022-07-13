package com.internal_compiler_error.Portfolio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * An investment having a symbol, name, quantity and book value
 */
public abstract class Investment {
    private InvestmentType investmentType;
    private String symbol = "";
    private String name = "";
    private int quantity = 0;
    private BigDecimal price = new BigDecimal(0);
    private BigDecimal bookValue = new BigDecimal(0);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Investment that = (Investment) o;
        return getQuantity() == that.getQuantity() &&
                getInvestmentType() == that.getInvestmentType() &&
                getSymbol().equals(that.getSymbol()) &&
                getName().equals(that.getName()) &&
                getPrice().equals(that.getPrice()) &&
                getBookValue().equals(that.getBookValue());
    }

    /**
     * Return CSV string format for persistence.
     *
     * @return CSV string representation of an investment
     */
    public String toCSVString() {
        return String.format("%s,%s,%s,%s,%s,%s", investmentType == InvestmentType.STOCK ? "STOCK" : "MUTUAL FUND", symbol, name, quantity, price.toString(), bookValue.toString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getInvestmentType(), getSymbol(), getName(), getQuantity(), getPrice(), getBookValue());
    }

    /**
     * Getter for InvestmentType
     * @return the type of investment
     */
    public InvestmentType getInvestmentType() {
        return investmentType;
    }

    /**
     * Setter for InvestmentType
     * @param investmentType the type of investment
     */
    public void setInvestmentType(InvestmentType investmentType) {
        this.investmentType = investmentType;
    }

    /**
     * Getter for symbol
     * @return the symbol
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * Setter for symbol
     * @param symbol the symbol of the investment
     */
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    /**
     * Getter for name
     * @return the name of the investment
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for name
     * @param name the name of the investment
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for quantity
     * @return the quantity of the investment
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Setter for quantity
     * @param quantity
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Getter for price
     * @return the price of investment
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * Setter for price
     * @param price price of investment
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * Getter for book value
     * @return the book value
     */
    public BigDecimal getBookValue() {
        return bookValue;
    }

    /**
     * setter for book value
     * @param bookValue the book value
     */
    public void setBookValue(BigDecimal bookValue) {
        this.bookValue = bookValue;
    }


    /**
     * Calculate the current gain if all is sold at the current price without any fees.
     * Formula: bookValue - price * quantity
     *
     * @return the total gain
     */
    BigDecimal getGain() {
        var marketValue = price
                .multiply(new BigDecimal(quantity));
        return marketValue.subtract(bookValue);
    }

    /**
     * Update the price of the investment
     *
     * @param price the new price
     */
    void updatePrice(BigDecimal price) {
        this.setPrice(price);
    }

    /**
     * Sell some or all investment products. The selling price will become the new price.
     *
     * @param quantity the quantity to be sold
     * @param price    the selling price
     * @return The gain from the trade
     * @throws IllegalQuantityException if the quantity is negative or exceeds the quantity possessed
     */
    BigDecimal sell(int quantity, BigDecimal price) throws IllegalQuantityException {
        if (quantity < 0) {
            throw new IllegalQuantityException(IllegalQuantity.NEGATIVE);
        } else if (quantity == 0) {
            throw new IllegalQuantityException(IllegalQuantity.ZERO);
        } else if (quantity > this.quantity) {
            throw new IllegalQuantityException(IllegalQuantity.MORE_THAN_HOLDING);
        }


        var payment = price.multiply(new BigDecimal(quantity));

        // we must set scale here, otherwise we'll always round to 0
        var remainingProportion = new BigDecimal(quantity).setScale(32, RoundingMode.HALF_EVEN).divide(new BigDecimal(this.quantity), RoundingMode.HALF_EVEN);
        var bookValueForSell = bookValue.multiply(remainingProportion);
        bookValue = bookValue.subtract(bookValueForSell);
        this.price = price.setScale(2, RoundingMode.HALF_EVEN);
        this.quantity -= quantity;

        return payment.subtract(bookValueForSell);
    }

    /**
     * Purchase additional product
     *
     * @param quantity the additional quantify
     * @param price    the new purchase price
     * @throws IllegalQuantityException if the quantity is negative
     */
    void buy(int quantity, BigDecimal price) throws IllegalQuantityException {
        if (quantity < 0) {
            throw new IllegalQuantityException(IllegalQuantity.NEGATIVE);
        }


        // bookValue = bookValue + quantity * price
        bookValue = bookValue.
                add(price.multiply(new BigDecimal(quantity)));
        this.quantity += quantity;
        this.price = price.setScale(2, RoundingMode.HALF_EVEN);
    }

    /**
     * Default constructor
     */
    public Investment() {
    }

    /**
     * Constructor for Investment
     *
     * @param symbol   the symbol of the investment
     * @param name     the name of the investment
     * @param quantity the quantity to be purchased
     * @param price    the initial price of the product
     */
    public Investment(String symbol, String name, int quantity, BigDecimal price) throws Exception {
        if (symbol.isEmpty() || name.isEmpty() || quantity <= 0 || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new Exception("Illegal Inputs");
        }


        this.symbol = symbol;
        this.name = name;
        this.quantity = quantity;
        this.price = price.setScale(2, RoundingMode.HALF_EVEN);
        this.bookValue = new BigDecimal(quantity).setScale(2, RoundingMode.HALF_EVEN).multiply(price);
    }

    /**
     * Constructor for Investment for use of reading from a file
     *
     * @param symbol    the symbol of the investment
     * @param name      the name of the investment
     * @param quantity  the quantity to be purchased
     * @param price     the initial price of the product
     * @param bookValue the cost of owning the product
     */
    public Investment(String symbol, String name, int quantity, BigDecimal price, BigDecimal bookValue) {
        this.symbol = symbol;
        this.name = name;
        this.quantity = quantity;
        this.price = price.setScale(2, RoundingMode.HALF_EVEN);
        this.bookValue = bookValue.setScale(2, RoundingMode.HALF_EVEN);
    }
}

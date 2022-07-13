package com.internal_compiler_error.Portfolio;

public class IllegalQuantityException extends Exception {
    private IllegalQuantity type;

    /**
     * @param type stock or mutual fund
     */
    IllegalQuantityException(IllegalQuantity type) {
        this.type = type;
    }

    @Override
    public String toString() {
        switch (type) {
            case NEGATIVE:
                return "Negative quantity";
            case MORE_THAN_HOLDING:
                return "Quantity to sell is more than holding";
            case ZERO:
                return "Quantity to sell is zero";
            default:
                // note, this path will actually never be taken
                return "";
        }
    }
}

enum IllegalQuantity {
    NEGATIVE, MORE_THAN_HOLDING, ZERO
}
package com.internal_compiler_error.Portfolio;

public class InvestmentNotFoundException extends Exception {
    InvestmentNotFoundException() {
        super("Attempted to locate an investment not present in the list");
    }
}

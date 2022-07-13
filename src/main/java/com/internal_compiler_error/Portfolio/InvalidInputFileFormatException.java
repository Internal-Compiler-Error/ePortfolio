package com.internal_compiler_error.Portfolio;

public class InvalidInputFileFormatException extends Exception {
    InvalidInputFileFormatException() {
        super("Error when trying to parse input files");
    }
}

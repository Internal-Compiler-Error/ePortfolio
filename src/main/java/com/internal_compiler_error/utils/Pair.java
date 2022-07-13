package com.internal_compiler_error.utils;

/**
 * A java port of the C++ std::pair
 * @param <T> the type of the first element
 * @param <U> the type of the second element
 */
public class Pair<T, U> {
    private T first;
    private U second;


    public Pair() {
    }

    /**
     * Standard constructor
     * @param first first element
     * @param second second element
     */
    public Pair(T first, U second) {
        this.first = first;
        this.second = second;
    }

    // getters and setters for first and second element
    public T getFirst() {
        return first;
    }

    public void setFirst(T first) {
        this.first = first;
    }

    public U getSecond() {
        return second;
    }

    public void setSecond(U second) {
        this.second = second;
    }
}

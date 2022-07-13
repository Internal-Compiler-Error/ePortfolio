package com.internal_compiler_error.Portfolio;

import com.internal_compiler_error.utils.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

public class ePortfolio {
    private List<Investment> investments = new ArrayList<>();
    private Map<String, List<Integer>> investmentFinder = new HashMap<>();


    public List<Investment> getInvestments() {
        return investments;
    }

    public void setInvestments(List<Investment> investments) {
        this.investments = investments;
    }

    /**
     * Default constructor, only used for testing and potential serialization
     */
    public ePortfolio() {
    }

    /**
     * Load a list of investments from a file
     *
     * @param input the file containing the investments
     * @throws InvalidInputFileFormatException if error occurs while trying parse
     */
    public void loadInvestments(File input) throws InvalidInputFileFormatException {
        try {
            Scanner scanner = new Scanner(input);
            while (scanner.hasNext()) {
                String line = scanner.nextLine().replace(System.getProperty("line.separator"), "");
                String[] fields = line.split("[,]+");

                if (fields.length < 5) {
                    throw new InvalidInputFileFormatException();
                }

                if (fields[0].equals("STOCK")) {
                    var stock = new Stock(fields[1], fields[2], Integer.parseInt(fields[3]), new BigDecimal(fields[4]), new BigDecimal(fields[5]));
                    investments.add(stock);

                } else if (fields[0].equals("MUTUAL FUND")) {
                    var mutualFund = new MutualFund(fields[1], fields[2], Integer.parseInt(fields[3]), new BigDecimal(fields[4]), new BigDecimal(fields[5]));
                    investments.add(mutualFund);
                } else {
                    throw new InvalidInputFileFormatException();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        investments.sort((lhs, rhs) -> lhs.getSymbol().compareToIgnoreCase(rhs.getSymbol()));

        int index = 0;
        for (var investment : investments) {
            var nameTokens = investment.getName().split("[ ]+");

            for (var name : nameTokens) {
                name = name.toLowerCase(Locale.ROOT);
                var list = investmentFinder.getOrDefault(name, new ArrayList<>());
                list.add(index);
                investmentFinder.put(name, list);
            }
            ++index;
        }

    }

    /**
     * Save a list of investments to a file
     *
     * @param output the file containing the investments
     */
    public void saveInvestments(File output) {
        try (PrintWriter printWriter = new PrintWriter(output)) {
            for (var investment : investments) {
                printWriter.println(investment.toCSVString());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Determines if whether a specific investment exists in the portfolio
     *
     * @param symbol the symbol name
     * @return the index of the investment if found, or -(index) - 1 will be the location if inserted
     */
    public int has(String symbol) {
        return Collections.binarySearch(investments,
                new Stock(symbol),
                (lhs, rhs) -> lhs.getSymbol().compareToIgnoreCase(rhs.getSymbol()));
    }

    /**
     * Return the investment at position i
     *
     * @param i index
     * @return the investment
     */
    public Investment get(int i) {
        return investments.get(i);
    }

    /**
     * purchase a type of product
     *
     * @param type     the type of investment, stock or mutual fund
     * @param symbol   the symbol name for the product
     * @param name     the name of the product
     * @param price    the price that the product will be purchased at
     * @param quantity the quantity of the purchase
     * @throws IllegalQuantityException if the quantity is negative
     */
    public void buy(InvestmentType type, String symbol, String name, BigDecimal price, int quantity) throws Exception {
        var index = has(symbol);


        if (index < 0) {
            switch (type) {
                case STOCK: {
                    Stock newListing = new Stock(symbol.toUpperCase(Locale.ROOT), name, quantity, price);
                    investments.add(-index - 1, newListing);
                    break;
                }
                case MUTUAL_FUND:
                    MutualFund newListing = new MutualFund(symbol.toUpperCase(Locale.ROOT), name, quantity, price);
                    investments.add(-index - 1, newListing);
                    break;
            }

            String[] nameTokens = name.split("[ ]+");
            for (var token : nameTokens) {
                token = token.toLowerCase(Locale.ROOT);

                var list = investmentFinder.getOrDefault(token, new ArrayList<>());
                list.add(-index - 1);

                investmentFinder.put(token, list);
            }

        } else {
            var investment = investments.get(index);
            investment.buy(quantity, price);
        }
    }

    /**
     * sell a type of product
     *
     * @param symbol   the symbol name for the product
     * @param quantity the quantity of the purchase
     * @param price    the price that the product will be purchased at
     * @return the gain of the trade
     * @throws IllegalQuantityException if the quantity is negative
     */
    public BigDecimal sell(String symbol, int quantity, BigDecimal price) throws IllegalQuantityException, InvestmentNotFoundException {
        var index = has(symbol.toUpperCase(Locale.ROOT));

        if (index < 0) {
            throw new InvestmentNotFoundException();
        }

        var investment = investments.get(index);
        var gain = investment.sell(quantity, price);


        if (investment.getQuantity() == 0) {
            investments.remove(index);
        }


        // update the hash table to reflect the change of indexes in out list
        investmentFinder.clear();
        int counter = 0;
        for (var listing : investments) {
            for (var token : listing.getName().split("[ ]+")) {
                token = token.toLowerCase(Locale.ROOT);
                var list = investmentFinder.getOrDefault(token, new ArrayList<>());
                list.add(counter);
                investmentFinder.put(token, list);
            }
            ++counter;
        }


        return gain;
    }


    /**
     * Calculate the gain if all investment are sold at the current price
     *
     * @return total gain in string
     */
    public String calculateGain() {
        BigDecimal gain = new BigDecimal(0);

        for (var investment : investments) {
            gain = gain.add(investment.getGain());
        }

        gain = gain.setScale(2, RoundingMode.HALF_EVEN);
        return gain.toString();
    }

    /**
     * Query all the investment products using filter string. The symbol query string is case-insensitive. Name query
     * string matches word-wise and order doesn't matter, also case-insensitive. Price range is in the schema of
     * begin-end, both can be omitted.
     *
     * @param symbolQuery   filter all those that do not contain the symbol
     * @param nameQuery     filter all those at do not contain all the words of the name
     * @param priceRangeStr filter those do not fit into the [begin, end]
     * @return list of investments after filtering in string
     */
    public List<String> query(String symbolQuery, String nameQuery, String priceRangeStr) {
        symbolQuery = symbolQuery.trim().toLowerCase(Locale.ROOT);
        nameQuery = nameQuery.trim().toLowerCase(Locale.ROOT);
        priceRangeStr = priceRangeStr.trim().toLowerCase(Locale.ROOT);

        List<Investment> candidates;

        var nameSplits = Arrays.asList(nameQuery.split("[ ]+"));
        var priceRange = findPriceRange(priceRangeStr);

        // starting off, this is our universal set, we will continue to find the intersection to narrow down
        Set<Integer> nameMatchingCandidates = new HashSet<>();

        if (nameQuery.isEmpty()) {
            // as specification dictates, empty query means match all
            for (var indexList : investmentFinder.values()) {
                nameMatchingCandidates.addAll(indexList);
            }
        } else {
            // we use just use the first list to start off
            var firstCandidateList = investmentFinder.get(nameSplits.get(0));
            nameMatchingCandidates.addAll(firstCandidateList);

            // then compute the intersection of all of them
            List<Integer> candidatePool = new ArrayList<>();
            for (var split : nameSplits) {
                candidatePool.addAll(investmentFinder.get(split));
            }

            nameMatchingCandidates.retainAll(candidatePool);
        }

        // convert from the set of indexes to list of investments with duplicates removed
        candidates = nameMatchingCandidates
                .stream()
                .map((integer -> investments.get(integer)))
                .distinct()
                .collect(Collectors.toList());


        // get rid all investments that doesn't contain the symbol
        if (!symbolQuery.isEmpty()) {
            String finalSymbolQuery = symbolQuery;
            candidates.removeIf((Investment investment) -> !investment.getSymbol().contains(finalSymbolQuery.toUpperCase(Locale.ROOT)));
        }

        // remove all those that do not fit under [lower, upper]
        candidates.removeIf(investment -> {
            var price = investment.getPrice();
            var lowerBound = priceRange.getFirst();
            var upperBound = priceRange.getSecond();


            return !(lowerBound.compareTo(price) <= 0 && price.compareTo(upperBound) <= 0);
        });


        return
                candidates.stream()
                        .map(Object::toString)
                        .collect(Collectors.toList());
    }

    /**
     * Find the lower and upper bound from a string in the form of begin-end. Both ends can be omitted, which means no
     * restriction
     *
     * @param str the str
     * @return the pair of [begin, end]
     */
    Pair<BigDecimal, BigDecimal> findPriceRange(String str) {
        var doubleMax = BigDecimal.valueOf(Double.MAX_VALUE);
        var doubleMin = doubleMax.negate();
        Pair<BigDecimal, BigDecimal> priceRange = new Pair<>(doubleMin, doubleMax);


        // now to process the price range
        var dashIndex = str.indexOf("-");

        if (dashIndex != -1) {
            // a lower bound was specified
            if (dashIndex != 0) {
                var lowerBound = new BigDecimal(str.substring(0, dashIndex));
                priceRange.setFirst(lowerBound);
            }

            // upper bound exists
            if (dashIndex != str.length() - 1) {
                var upperBound = new BigDecimal(str.substring(dashIndex + 1));
                priceRange.setSecond(upperBound);
            }
        }

        int comp = priceRange.getFirst().compareTo(priceRange.getSecond());
        if (comp == 0) {
            System.out.println("Warning: price range begin is equal to end");
        } else if (comp > 0) {
            System.out.println("Warning: price range end is smaller than begin, assuming you want the opposite");
            priceRange = new Pair<>(priceRange.getSecond(), priceRange.getFirst());
        }
        return priceRange;
    }

    boolean isEmpty() {
        return investments.isEmpty();
    }

    int getSize() {
        return investments.size();
    }
}
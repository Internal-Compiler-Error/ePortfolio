## Project description

The ePortfolio manages user investment products. It allows the user to

1. buy new mutual funds and stocks, inputting new symbol, name, price and quantity in the process
2. sell existing product at a price, this will update the price of the product at the same time
3. calculate the total gain if we were to sell all the product now
4. update the price of products
5. search for products using symbol, name and/or price range

Upon quit, the contents of investments will be saved to a file specified by the user at the beginning of the program.
The file follows CSV format as the following
```
investment type,symbol,name,quanity,price,book value
```

## Project structure

## Portfolio.Investment

Each Investment instance represent oneInvestment, each object provides the core functionality of

1. buy
2. sell
3. update
4. calculate gain

### Portfolio.MutualFund

Each MutualFund instance represent one mutual fund product, mostly the functionality comes from Investment class and
MutualFund just adds the appropriate fees when necessary

### Portfolio.Stock

Each Stock instance represent one mutual fund product, mostly the functionality comes from Investment class and Stock
just adds the appropriate fees when necessary

### Portfolio.ePortfolio

This class a list of investments.Similar to the investment products, it provides the functionality of

1. buy
2. sell
3. update
4. calculate gain
5. search

Note function 1 - 4 is mainly done by through passing parameters to the correct product object

### Portfolio.Main

The class is mostly used to get input from the terminal and display information.

#### Portfolio.OptionPack

The class is a helper class that represent many commands that has the same semantics into one object

### utils.Pair

A java implementation of C++ `std::pair<T,U>`, more info about the C++ pair can be found here
https://en.cppreference.com/w/cpp/utility/pair

## Limitations and Assumptions

1. it performs no error check on symbol, it assumes all alpha numeric combinations are legal
2. it performs no error check on name, it could be anything or even empty
3. in most cases, once an error occurs, it would simply reask the input without indicating error has occurred

## Build

The project uses gradle as the build system, to build on linux. For other platforms and more info, see
https://docs.gradle.org/current/userguide/userguide.html.

```shell
./gradlew build
```

## Run
Note that it's not necessary to manually build it before running
```shell
./gradlew run
```



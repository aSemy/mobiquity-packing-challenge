# mobiquity-packing-challenge

## Introduction 

Coding challenge as part of an interview process. A parcel 

The computer science problem is the '0/1 knapsack problem'. Parsing and validation of input data is also required.

## Running

Main class is Packer.pack(filepath). This must be passed the location of a file with valid data.

Data must be formatted so that each line is an independent problem to be solved. The expected encoding is UTF-8.

The data must be in the correct format, with exact spacing

Format: `<parcel weight (positive rational number)> : (<item index (positive integer)>,<item weight (positive rational number),€<item cost in Euros (positive rational number)>)`

There can be up to 15 items.


### Notes

Packet has been renamed to Parcel throughout the code, as Packet is a restricted Java keyword so it makes it a big nicer to code with something unrestricted.

In the example where no solution was possible, the character `-` was returned, so that will always be the case for no solution.

### TODO 

Change the price to be any currency, not just Euros

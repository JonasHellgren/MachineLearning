# Electric price data reading

# Xls file format
There is one sheet for energy price data (SEK/MWh) and one for FCR price data (SEK/MW).
Each sheet has following name convention:  year-month-region. For example 24-jan-se3
In each sheet there are 24 rows, each row corresponds to hour. Each column corresponds to a day in the month.
Column 0, day 1 etc.
  
hour/day  | A | B | C | D | E | F | G | H | I |
* 1
* --
* 2
* --
* :
* :
* --
* 24

# Java database - ElPriceDataBase 
Implements interface DataBaseI<PriceData>
Provides low-level CRUD operations (Create, Read, Update, Delete). Beside CRUD following methods are present:
* List<DayId> getAllIds();
* void clear();
* int size();

The database implementation for one data type (energy or FCR) is the following map
* Map<DayId,PriceData>  priceDataMap

where DayId includes: (year,month,region). 

PriceData is an Entity including following: 
* DayId id;
* ElType type;
* List<Double> pricesAllHours;
* List<Double> prices00ToHour(int toHour)
* List<Double> pricesHourTo00(int fromHour)
* double priceAtHour(int hour)
* clear()

where ElType is an enum (ENERGY,FCR)


# Repository Class  - ElPriceRepo 
Acts as an intermediary between the application and the database. Provides a high-level abstraction for database operations.
Implements business logic related to data access. Includes following two databases 1) energy price and 2) FCR price data.
The databases are of type ElPriceDataBase

Major methods:
* List<DayId> getAllIds()
* List<DayId> getIdsHasNextDay()
* PriceData getPriceDataForDay(DayId id, ElType type)
* PriceData getPriceDataForDay(DayId id, ElType type)
* List<Double> pricesFromHourToHour(DayId id, Range<> fromToHour, ElType type)
* addDataForDay(PriceData,ElType type)


pricesFromHourToHour will throw exception if not passed DayId is in List from getIdsHasNextDay

# Xls reader class - ElPriceXlsRead
Injects data to ElPriceRepo. Holds ref to an ElPriceRepo.
* readDataFromFile(File file, ElType type)
* clear()




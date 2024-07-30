# Electric price data reading

# Xls file format
There is one sheet for energy price data (SEK/MWh) and one for FCR price data (SEK/MW).
Each sheet has a following name convention:  year-month-region. For example 24-jan-se3
In each sheet there are 24 rows, each row corresponds to hour. Each column corresponds to a day in the month.
Column 0, day 1 etc.

  
hour/day  | A | B | C | D | E | F | G | H | I |
1
--
2
--
:
:
--
24

# Java database - EnergyPriceDataBase, FCRPriceDataBase 
Provides low-level CRUD operations (Create, Read, Update, Delete). Beside CRUD following methods are present:
List<ElPriceId> getAllIds();
void clear();
int size();

The database implementation for one data type (energy or FCR) is the following map
Map<ElPriceId,PriceData>  priceDataMap
ElPriceId includes: (year,month,region). 

PriceData is an Entity including following: 
ElPriceId id;
List<Double> price;
List<Double> pricesAllData()
List<Double> prices00ToHour(int toHour)
List<Double> pricesHourTo00(int fromHour)
double priceAtHour(int hour)
clear()

The database ElectricPriceDataBase<V,A> implements DataBaseI<PriceData>

# Repository Class  - ElPriceRepo 
Acts as an intermediary between the application and the database. Provides a high-level abstraction for database operations.
Implements business logic related to data access. Includes following two databases 1) energy price and 2) FCR price data.

Major methods:
List<ElPriceId> getAllIds()
List<ElPriceId> getIdsHasNextDay()
PriceData getPriceDataForDay(ElPriceId, ElType type)
PriceData getPriceDataForDay(ElPriceId, ElType type)
List<Double> pricesFromHourToHour(ElPriceId, Range<> fromToHour, ElType type)
addDataForDay(PriceData,ElType type)

where ElType is enum (ENERGY,FCR)

pricesFromHourToHour will throw exception if not passed ElPriceId is in List from getIdsHasNextDay

# Xls reader class - ElPriceXlsRead
Injects data to ElPriceRepo.
readDataFromFile(ElPriceRepo repo, ElType type, File file)
clear()




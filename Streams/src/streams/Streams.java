package streams;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import static java.util.stream.Collectors.*;

enum MovieType{
	ADVENTURE,
	ACTION_ADVENTURE,
	MISTORY,
	HORROR,
	DOCUMENTARY
}

enum MovieQuality{
	GOOD,
	OK,
	BAD
}

class Movie{
	public MovieType MovieType;
	private String name;
	private int yearPublished;
	private double rating;
	
	public Movie(MovieType MovieType, String name, int yearPublished, double rating) {
		this.MovieType = MovieType;
		this.name = name;
		this.yearPublished = yearPublished;
		this.rating = rating;
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}

	public MovieType getMovieType() {
		return MovieType;
	}

	public void setMovieType(MovieType MovieType) {
		this.MovieType = MovieType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getYearPublished() {
		return yearPublished;
	}

	public void setYearPublished(int yearPublished) {
		this.yearPublished = yearPublished;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
}

public class Streams {

	public static void main(String[] args) {
		// --- Movies example --- //
		List<Movie> movies = new ArrayList<Movie>();
		movies.add(new Movie(MovieType.HORROR, "IT", 2017, 4.3));
		movies.add(new Movie(MovieType.ADVENTURE, "Lord of The Rings", 2003, 5));
		movies.add(new Movie(MovieType.DOCUMENTARY, "Planet Earth", 2012, 5));
		movies.add(new Movie(MovieType.ADVENTURE, "Star Wars", 1978, 4));
		movies.add(new Movie(MovieType.ADVENTURE, "Food Fight", 2012, 0));
		
		
	
		// --- File IO example ---- //
		try {
			//Stream stores each line of the file as a string
			Stream<String> lines = Files.lines(Paths.get("src/streams/test.txt"));
			
			//Use stream functions to print count of each line
			System.out.print("[FileIO] Token Count: ");
			lines.map(line -> line.split(" ").length).reduce(Integer::sum).ifPresent(System.out::println);
			lines.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		//IntStream
		IntStream is = IntStream.rangeClosed(1, 10);
		int fact = is.reduce(1, (x,y) -> x*y);
		
		/*
		 *  1. x=seed, y=1, prod=1*1
			2. x=1, 3. x=2, 4. x=3, ...
			y=2, prod=1*2
			y=3, prod=2*3
			y=4, prod=3*4
		 */
		System.out.println("[intstream] fact value: " + fact);
		
		
		
		
		
		// --- flatMap --- //
		
		//non-flat map version
		List<Integer> l1 = Arrays.asList(2,3,7,9);
		Stream<int[]> strm = l1.stream().map(i -> new int[] {1,i});
		System.out.print("[flatMap] strm contents: ");
		strm.forEach(a -> System.out.print(Arrays.toString(a)));
		System.out.println();
		
		//Construct multi-dimensional, no flatMap
		List<Integer> l2 = Arrays.asList(4,5,8);
		Stream<Stream<int[]>> strm2 = l1.stream().map(i -> l2.stream().map(j -> new int[] {i,j}));
		
		//This prints the object and memory location (useless). 
		System.out.println("[flatMap] strm2 contents: ");
//		strm2.forEach(System.out::print);
//		System.out.println();
		
		//now we get giberish for each int array returned.
//		System.out.print("[flatMap] strm2 contents (second loop): ");
//		strm2.forEach(s -> s.forEach(System.out::println));
		
		//This prints each int[]
		strm2.forEach(s -> s.forEach(a -> System.out.println(Arrays.toString(a))));
		
		//flatMap demonstration
		Stream<int[]> strmFM = l1.stream().flatMap(i -> l2.stream().map(j -> new int[] {i,j}));
		System.out.println("[flatMap] strmFM contents: ");
		strmFM.forEach(a -> System.out.println(Arrays.toString(a)));
		
		//Find average word length in input file
		try {
			Stream<String> lines = Files.lines(Paths.get("src/streams/test.txt"));
			
			//returns Stream<String[]>, wont work
//			lines.map(line -> line.split(" ")).forEach(System.out::println);
			
			//using flatmap, it returns individual words needed for Stream<String>
			//lines.map(line -> line.split(" ")).flatMap(Arrays::stream).forEach(System.out::println);
			
			//Knowing the above, we can find the average word length
			OptionalDouble avg = lines.map(line -> line.split(" ")).flatMap(Arrays::stream).mapToInt(String::length).average();
			System.out.print("[flatMap] Average word length: ");
			avg.ifPresent(System.out::println);
		}catch(IOException e){
			e.printStackTrace();
		}
		
		
		//flatmap with IntStream
		int[] arr1 = {2,3,7,9};
		int[] arr2 = {5,1,7,0};
		IntStream is1 = Arrays.stream(arr1);
		IntStream is2 = Arrays.stream(arr2);
		
		//the following will not compile because it will return Stream<int[]>
		/*
		 * is1.map(i -> new int[] {1, j}).forEach(a -> System.out.println(Arrays.toString(a)));
		 */
		
		//This will also not work because the stream gets closed due to it being an inner loop
		// Stream<int[]> pairs = is1.boxed().flatMap(i -> is2.boxed().map(j -> new int[] {i,j}));
		
		//This works because the call for Arrays.stream() will create a new stream for every item
		Stream<int[]> pairs = is1.boxed().flatMap(i -> Arrays.stream(arr2).boxed().map(j -> new int[] {i,j}));
		System.out.println("[IntStream] pairs values: ");
		pairs.forEach(pair -> System.out.println(Arrays.toString(pair)));
		
		
		
		
		
		// --- Useful Stream Operators (from table in stream_functions.png) --- //
		System.out.println("\n\n\n -- USEFUL STREAM OPERATIONS --");
		
		//Filter operation
		//Use filter operation to get movies that are good (3.5 rating and higher)
		List<Movie> goodMovies = movies.stream().filter(movie -> movie.getRating() >= 3.5).toList();
		System.out.println("[Filter Operation] All movies: " + movies.toString());
		System.out.println("[Filter Operation] goodMovies filter result: " + goodMovies.toString() + "\n");
		
		
		
		
		//Distinct Operation
		//Find all line-lengths in file
		try {
			//Stream stores each line of the file as a string
			Stream<String> lines = Files.lines(Paths.get("src/streams/test.txt"));
			
			//Use stream functions to print count of each line
			System.out.println("[Distinct Operation] Distinct line-length count: ");
			lines.map(line -> line.split(" ").length).distinct().forEach(System.out::println);
			lines.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println();
	
		//Limit Operation
		System.out.println("[Limit Operation] Movies without limit applied: " + movies.toString());
		System.out.println("[Limit Operation] Movies limited to range 2: " + movies.stream().limit(2).toList().toString() + "\n");
		
		
		
		//Map Operation
		//Create a list, convert to stream, and map lambda statement
		List<Integer> someNums = Arrays.asList(2,4,6,8);
		System.out.println("[Map Operation] Un-mapped list: " + someNums.toString());
		
		//map() returns a Stream<Integer> here, so we can directly call
		//forEach and pass it the static method println to print each value
		//someNums.stream().map(num -> num*num).forEach(System.out::println);
		
		//For cleaner printing, convert it back to a list and called List.toString()
		System.out.println("[Map Operation] Mapped list: " + someNums.stream().map(num -> num*num).toList().toString() + "\n");
		
		
		
		//Flatmap Operation (code ripped from GeeksForGeeks :) )
		// Creating a list of Prime Numbers
        List<Integer> PrimeNumbers = Arrays.asList(5, 7, 11,13);
          
        // Creating a list of Odd Numbers
        List<Integer> OddNumbers = Arrays.asList(1, 3, 5);
          
        // Creating a list of Even Numbers
        List<Integer> EvenNumbers = Arrays.asList(2, 4, 6, 8);
  
        List<List<Integer>> listOfListofInts =
                Arrays.asList(PrimeNumbers, OddNumbers, EvenNumbers);
  
        System.out.println("[Flatmap Operation] The Structure before flattening is: " +
                                                  listOfListofInts);
          
        // Using flatMap for transformating and flattening.
        List<Integer> listofInts  = listOfListofInts.stream()
                                    .flatMap(list -> list.stream())
                                    .collect(Collectors.toList());
  
        System.out.println("[Flatmap Operation] The Structure after flattening is: " +
                                                         listofInts + "\n");
        
        
        
        //Sorted Operation
        //Sorted takes in Comparator<T> and returns a Stream<T>
        //Here, i sent in a lambda statement that uses the String classes compareTo method
        List<Movie> sortedMovies = movies.stream().sorted((movie1,movie2) -> movie1.getName().compareTo(movie2.getName())).toList();
        System.out.println("[Sorted Operation] movies contents: " + movies.toString() + "\n[Sorted Operation] sortedMovies contents: " + sortedMovies.toString() + "\n");
        
        
        
        //Match Operations
        
        //anyMatch
        boolean matchFound = movies.stream().anyMatch(movie -> movie.getName().equals("IT"));
        System.out.println("[anyMatch] Found any movie named 'IT': " + matchFound);
        System.out.println("[anyMatch] Found any movie named 'Avengers: Endgame': " + movies.stream().anyMatch(movie -> movie.getName().equals("Avengers: Endgame")));
        
        //noneMatch
        System.out.println("[noneMatch] No movies stored are named 'Elf': " + movies.stream().noneMatch(movie -> movie.getName().equals("Elf")));
        System.out.println("[noneMatch] No movies stored are named 'Lord of The Rings': " + movies.stream().noneMatch(movie -> movie.getName().equals("Lord of The Rings")));

        //allMatch
        System.out.println("[allMatch] All movies are a named 'IT': " + movies.stream().allMatch(movie -> movie.getName().equals("IT")) + "\n");
        
        
        
        //Find Operations
        
        //findAny
        //Shown below, there is a list 'someDoubles' that contains all floating point values.
        //2.5 is the only value repeated twice. (the findAny is guarenteed to work; hardcoded).
        List<Double> someDoubles = Arrays.asList(2.5, 6.2, 8.5, 2.5, 10.43);
        double foundValue = someDoubles.stream().filter(d -> d == 2.5).findAny().get();
        System.out.println("[findAny] Found value: " + foundValue);
        
        //findFirst
        //Using the movies list, we can see that findFirst ends the second a match has been found.
        //If we wanted to find all movies with a rating of 5, we would not use findFirst. Filter 
        //would be use for all 5 star movies
        System.out.print("[findFirst] All movies and their ratings: [");
        for (Movie m: movies)
        	System.out.print(String.format("%s(%.1f), ",m.toString(),m.getRating()));
        System.out.println("]");
        System.out.println("[findFirst] found first movie with 5 rating: " + movies.stream().filter(m -> m.getRating() == 5).findFirst().get().toString() + "\n");
        
        
        
        //ForEach Operation
        //performs an action for each element in a stream
        List<Integer> someInts = Arrays.asList(1,2,3,4,5);
        System.out.println("[forEach] action performed on each stream element: ");
        someInts.stream().forEach(System.out::println);
        System.out.println();
        
        
        
        
        //Collect Operation
        //collect is a terminal operation, and collects and returns values
        //that are a result of a indeterminate operation (map, filter, sorted, etc).
        //Use Collectors class to return back the proper data type
        List<Integer> ints = Arrays.asList(2,4,6,8);
        List<Integer> intSquared = ints.stream().map(i -> i*i).collect(toList());
        System.out.println("[collect] ints array: " + ints.toString() + "\n[collect] intsSquared: " + intSquared.toString() + "\n");
        
        
        
        //Reduce Operation
        /*
         * reduce operation applies a binary operator to each element in the 
         * stream where the first argument to the operator is the return value of 
         * the previous application and second argument is the current stream element.
         */
        
        Optional<Double> optional = movies.stream().map(Movie::getRating).reduce(Double::sum);
        try {
        	//optonal.get() retrieves the result of the reduction, then the division is performed.
        	System.out.println("[reduce] Average rating of all movies: " + optional.get() / movies.stream().count() +"\n");
        }catch (NoSuchElementException e){
        	System.out.println("No movies in list");
        }
        
        
        //Count Operation
        //Simple operator, analogous to List.size()
        List<String> someStrings = Arrays.asList("Hello", "goodbye", "another string");
        long count = someStrings.stream().count();
        System.out.println("[count] someStrings contents: " + someStrings.toString() +  "\n[count] someStrings stream count: " + count);
        
        
        
        // COLLECTORS
        // must import static java.util.stream.Collectors.* for all functions to be known
        
        // groupBy
        System.out.println("\n -- COLLECTORS --\n[groupingBy]");
        Map<MovieType, List<Movie>> moviesByGenre = movies.stream().collect(groupingBy(Movie::getMovieType));
        System.out.print(moviesByGenre + "\n\n");
        
        
        //partitioningBy
        System.out.println("[partitioningBy] partitioned movies < 2014: " + movies.stream().collect(partitioningBy(movie -> movie.getYearPublished() < 2014)) + "\n");
        
        
        // maxBy
        Optional<Movie> maxRatedMovie = movies.stream().collect(maxBy(Comparator.comparingDouble(Movie::getRating)));
        System.out.print("[maxBY] Movie with the highest rating: ");
        maxRatedMovie.ifPresent(System.out::println);
        System.out.println();
        
        
        //averaging (this can be achieved without collectors, but this preferred
        double avgRating = movies.stream().collect(averagingDouble(Movie::getRating));
        System.out.println("[averaging]Average rating of movies: " + avgRating + "\n");
        
        
        
        //Summarizing 
        DoubleSummaryStatistics stats = movies.stream().collect(summarizingDouble(Movie::getRating));
        System.out.println("[summarizing] movie rating stats" + stats + "\n");
        
        
        //groupingBy
        Map<MovieQuality, List<Movie>> moviesByQuality = movies.stream().collect(groupingBy(movie -> {
        	if (movie.getRating() > 3) return MovieQuality.GOOD;
        	else if (movie.getRating() < 3) return MovieQuality.BAD;
        	else return MovieQuality.OK;
        }));
        
        System.out.println("[groupingBy] group movies using MovieQuality enum: " + moviesByQuality + "\n");
        System.out.println("[inner groupingBy] movies grouped by type, then inner grouped by year" + movies.stream().collect(groupingBy(Movie::getMovieType, groupingBy(Movie::getYearPublished))));
        
        //Two-level collection
        Map<MovieType, Optional<Movie>> topMoviesByGenre = movies.stream().collect(groupingBy(Movie::getMovieType, maxBy(Comparator.comparingDouble(Movie::getRating))));
	}

}



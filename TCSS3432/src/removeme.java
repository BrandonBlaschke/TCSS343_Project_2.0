

/**
 * Names: Brandon Blaschke
 * 		  Hasnah Said
 * December 1, 2017
 * Programming Assignment
 * TCSS 343 - Paulo Baretto
 * 
 * */


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Random;
import java.util.Scanner;

public class removeme {	
	
	private static Random random = new Random();
	
	public static int[][] tempCosts;
	public static int [][] inputList;

	
	public static ArrayList<Integer> bruteForce(final int theStart, final int theEnd, final int theCosts[][]) {
		
		//TODO: Check for bad input 
		
		//Hold all Power sets 
		ArrayList<ArrayList<Integer>> sets = new ArrayList<ArrayList<Integer>>(); 
		
		//Hold the numbers in between theStart and theEnd  
		int[] setNums = new int[theEnd - theStart + 1]; 
		
		//Length of the list
		int length = setNums.length; 
		
		//Make set of all posts 
		for(int i = 0; i < theEnd; i++) {
			
			setNums[i] = i + 1;
		}
		
		//Generate all the Power sets 
		for (int i = 0; i < (1 << length); i++) {
			
			ArrayList<Integer> temp = new ArrayList<>();
			sets.add(temp);
			
			for(int j = 0; j < length; j++) {
				if ((i & (1 << j)) > 0) {
					sets.get(i).add(j + 1);
				}
			}
		}
		
		//Check if sets have the start or end elements
		for (int i = 0; i < sets.size(); i++) {
			
			boolean checkEnd = false; 
			boolean checkStart = false;
			
			//for each element in the set check if it is the ending or starting element 
			for (int j = 0; j < sets.get(i).size(); j++) {
				
				if(sets.get(i).get(j) == theEnd) {
					
					checkEnd = true;
				}
				if(sets.get(i).get(j) == theStart) {
					checkStart = true; 
				}
			}
			
			//If the set has either starting or ending element remove it 
			if(!checkStart || !checkEnd) {
				sets.remove(i); 
				i--;
			}
		}
		
		//Get the costs 
		int[] costs = new int[sets.size()];
		
		//for each set
		for (int i = 0; i < sets.size(); i++) {
			int total = 0;
			
			//get the total cost for each move to a post 
			for (int j = 1; j < sets.get(i).size(); j++) {
				
				//This is getting the set#i and element#j and - 1 because were putting that 
				//value into an array and need to go down one. 
				total += theCosts[sets.get(i).get(j-1)-1][sets.get(i).get(j)-1];
			}
			costs[i] = total; 
		}
		
		//Find least of the sets 
		int leastSet = 0;
		int leastCost = costs[0]; 
		
		for (int i = 0; i < costs.length; i++) {
			if (costs[i] < leastCost) {
				leastSet = i;
				leastCost = costs[i];
			}
		}
		
		// debugging purpose
		/*for (int i = 0; i < sets.size(); i++) {
			System.out.print("Set " + i + ". [");
			for (int j = 0; j < sets.get(i).size(); j++) {
				System.out.print(sets.get(i).get(j) + ",");
			}
			System.out.print("]" + " Cost: " + costs[i] + '\n');
		}*/
		
		//Adding the total for the least set 
		sets.get(leastSet).add((costs[leastSet]));
		return sets.get(leastSet);
	}
	
	/**
	 * 
	 * @param inputCosts matrix with rental costs.
	 * @param theLeft left starting point.
	 * @param theRight ending point.
	 * @return the cheapest cost to rent a canoe form 1 to n.
	 */
	public static int divideAndConquer(int[][] inputCosts, int theLeft, int theRight) {
		//TODO: Print cheapest sequence.
		
		ArrayList<Integer> costs = new ArrayList<Integer>();
		ArrayList<Integer> sequences = new ArrayList<Integer>();
		
		//base case
		if (theLeft == theRight){
			return 0;
			} else {
				
				for (int i = theLeft + 1; i <= theRight; i++) {
				
//					int rCost = divideAndConquer(inputCosts, i, theRight);
					
					int cost = inputCosts[theLeft][i] + divideAndConquer(inputCosts, i, theRight);
					
//					System.out.println("costs[" +(theLeft + 1)+"]["+ (i+1) +"] ");

					costs.add(cost);	
			}
		}
		//find minimum cost
		int cheapest = costs.get(0);
		for (Integer i: costs) {
			if (i < cheapest) cheapest = i;
		}
		return cheapest;	
	}
	
	
	/** Dynamic programming solution to the problem
	 * 
	 * @param theStart Starting post
	 * @param theEnd Ending post
	 * @param theCosts 2D array for the cost of post x -> y
	 * @return Stack that where the head is the total cost for the solution and the rest being the steps in took to 
	 * get the final solution
	 */
	public static Deque<Integer> dynamic(final int theStart, final int theEnd, final int theCosts[][]) {
		
		//TODO: Check for bad input 
		
		//Difference from start and the end to get the total length 
		final int totalLength = theEnd - theStart + 1;
		
		//Array of all the post numbers from start to end 
		int[] posts = new int[totalLength];
		for (int i = 0; i < totalLength; i++) {
			posts[i] = i + theStart; 
		}
		
		//Table to keep track of least total for each step
		int[] leastTotals = new int[totalLength];
		
		//Keep track of the previous step to get that step. 
		int[] prevSteps = new int[totalLength];
		
		//Initial starting values for prevSteps
		prevSteps[0] = 0; 
		prevSteps[1] = theStart; 
		
		//Initial Starting values 
		leastTotals[0] = theCosts[theStart - 1][theStart - 1];
		leastTotals[1] = theCosts[theStart - 1][theStart]; 
				
		//For each step of 1->n check previous answers and get least		
		for (int i = 2; i < theEnd - theStart + 1; i++) {
			
			//This is the leastStep, this will eventually be at the end of the loop the 
			//total that was the smallest for that step. 
			int leastStep = leastTotals[i - 1] + theCosts[posts[0]- 1][posts[i] - 1];
			prevSteps[i] = i;
			
			//Go through all the past steps previous lowest solutions
			for (int j = 0; j < i; j++) {
				
				//Total cost for this one instance of a step
				int total = theCosts[j][i] + leastTotals[j];
				
				if (total <= leastStep) {
					leastStep = total;
					prevSteps[i] = j + 1;
				}
			}
			
			//This step gets the lowest
			leastTotals[i] = leastStep; 
		}
		
		//Debugging, shows the tables for finding the solution 
		/*System.out.println("\n Posts");
		for(int i: posts) {
			System.out.print(" " + i);
		}
		
		System.out.println("\n Least Totals");
		for(int i: leastTotals) {
			System.out.print(" " + i);
		}
		
		System.out.println("\nPrevious Steps");
		for(int i: prevSteps) {
			System.out.print(" " + i);
		}*/
		
		return retrace(prevSteps, posts, leastTotals[leastTotals.length - 1]);
	}
	
	/** Retraces the steps from the dynamic solution
	 * 
	 * @param thePrevSteps Previous Steps is the array of steps, with each step being the previous step it took to get there
	 * @param thePosts Posts from the start to the end numbered
	 * @param theLeastTotal The least total cost for the last post or ending post
	 * @return A Stack starting with the least total for the solution and the rest being the steps to get there
	 */
	public static Deque<Integer> retrace(final int[] thePrevSteps, final int[] thePosts, final int theLeastTotal) {
		
		//Create stack and the current step, which is the last step done 
		Deque<Integer> stack = new ArrayDeque<Integer>();
		int currentStep = thePosts.length;
		
		//Loop until we reach the starting post
		while (currentStep != 0) {
			
			//Push the post to the stack and go down to the previous step for that step
			stack.push(thePosts[currentStep - 1]);
			currentStep = thePrevSteps[currentStep - 1];
		}
		
		//This is the total cost for the solution, it is the head of the stack
		stack.push(theLeastTotal);
		return stack; 
	}
	
	/** Prints the stack from retrace to show the final solution and total cost
	 * @param theStack Stack to be printed with total cost
	 */
	public static void printStack(final Deque<Integer> theStack) {
		
		//get total
		int tempForTotal = theStack.pop();
		
		//Print posts 
		System.out.print("\nFinal solution for dynamic:");
		for (int i = 0; i < theStack.size(); i++) {
			System.out.print(" " + theStack.peek());
			theStack.pop();
			i--;
		}
		
		System.out.print("\nTotal is " + tempForTotal + "\n");
	}

	
	/**
	 * Generate a cost table of size n x n where n: 25, 50, 100, 200, 400, 800
	 * There are two scenarios: * costs are entirely random.
	 * 							* costs are random but increasing along each row.
	 * 
	 * @param theName name of the file.
	 * @param theSize number of posts.
	 * @param theType increasing: for random increasing costs.
	 * 				  random: for random positive costs.
	 */	
	public static void generateCostTable(String theName, int theSize, String theType) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(theName));
			
			int tableSize = theSize * theSize;
			int incCost = 0;
			
			/* Random increasing rental costs.*/
			if (theType.equals("0")) {
				for (int i = 0; i < theSize; i++) {
					for (int j= 0; j < theSize; j++) {
						if (i > j) {
							writer.write("NA");
						} else if (i == j) {
							writer.write("0");
						} else {
							incCost += random.nextInt(theSize);
							writer.write(String.valueOf(incCost));
						}
						writer.write("\t");
					}
					writer.write("\n");
				}
			/*Random positive rental costs.*/	
			} else if (theType.equals("1")) {
				for (int i = 0; i < theSize; i++) {
					for (int j = 0; j < theSize; j++) {
						if (i > j) {
							writer.write("NA");
						} else if (i == j) {
							writer.write("0");
						} else {
							writer.write(String.valueOf(random.nextInt(tableSize) + 1));
						}
						writer.write("\t");
					}
					writer.write("\n");
				}
			} else {
				System.out.println("Please enter a valid table type (increasing or random)."); 
			}
			
			writer.close();
		} catch (FileNotFoundException e) {
			System.out.println("File Not Found.");
		} catch (IOException e) {
			System.out.println("IO Exception");
		}
		
	}
	
	/**
	 * Read in the text file the user and insert the costs into a matrix.
	 * 
	 * @param theName name of the file.
	 * */
	public static void createCostTable (String theName) {
		ArrayList<Integer> input = new ArrayList<Integer>();
		File file = new File(theName);
		Scanner scanner = null;
		try{
			scanner = new Scanner (file);
		} catch (FileNotFoundException e) {
			
		}
		while (scanner.hasNext()) {
			if (scanner.hasNextInt()) {
				input.add(scanner.nextInt());
				
			} else if (scanner.hasNext("NA")) {
				input.add(0);
				scanner.next();
			} else {
				scanner.next();
			}
		}
		
		// Create the costs matrix
		int size = (int) Math.sqrt(input.size());
		int index = 0;
		inputList = new int[size][size];
		for (int i = 0; i < size && index < input.size(); i++) {
			for (int j = 0; j < size; j++) {
				inputList[i][j] = input.get(index);
				index++;
			}
		}
	}
	
	
	/***
	 * Main method.
	 * 
	 * To run from terminal: 
	 * 				javac tcss343.java 		 (compile the program)
	 * 				java tcss343 < input.txt (execute the program with given input.txt)
	 * 			OR  java tcss343 generate    (execute the program by generating a cost table)
	 * @param theArgs args to be passed into main.
	 */
	public static void main(String[] theArgs) {
		long bfStart, bfEnd, dcStart, dcEnd, dynamicStart, dynamicEnd;
		ArrayList<Integer> input = new ArrayList<Integer>();
		
		/* Use the file that the user specifies in terminal.*/
		if (theArgs.length == 0) {
			// Read input text file
		
			Scanner scanner = new Scanner (System.in);
			while (scanner.hasNext()) {
				System.out.println(scanner);
				if (scanner.hasNextInt()) {
					input.add(scanner.nextInt());
				} else if (scanner.hasNext("NA")) {
					input.add(0);
					scanner.next();
				} else {
					scanner.next();
				}
			}
			scanner.close();
			

			
			/*Create the cost table. */
			int size = (int) Math.sqrt(input.size());
			int index = 0;
			inputList = new int[size][size];
			for (int i = 0; i < size && index < input.size(); i++) {
				for (int j = 0; j < size; j++) {
					inputList[i][j] = input.get(index);
					index++;
				}
			}
		}
		
		/* Generate a cost table using the arguments the user passes in.
		 * Terminal Command: java tcss343 generate.		
		 */
		else {
			
			System.out.print("Enter filename: ");
			Scanner sc = new Scanner(System.in);
			String filename = sc.nextLine();
			
			System.out.print("Enter the size of the table: ");
			int tableSize = 0;
			try{
				tableSize = Integer.parseInt(sc.nextLine());
			} catch (IllegalArgumentException e) {
				System.out.println("Please enter a valid number");
			}
			
			System.out.print("Enter table type (increaseing = 0/random = 1): ");
			String tableType = sc.nextLine();

			generateCostTable(filename, tableSize, tableType);
			createCostTable(filename);
		}
		
		

		
		for (int a = 0; a < inputList.length; a++) {
			for (int b = 0; b < inputList.length; b++) {
				System.out.print(inputList[a][b] + "  ");
			}
			System.out.println();
		}
		

		/* Algorithms */
		int i = 1;
		int n = inputList.length;
		
		/* Brute Force */
		System.out.println("\nBrute Force");
		bfStart = System.nanoTime();
		ArrayList<Integer> solution = bruteForce(i, n, inputList);
		bfEnd = System.nanoTime();
		System.out.print("Soultion is [");
		for(int a = 0; a < solution.size() - 1; a++) {
			System.out.print(" " + solution.get(a));
		}
		System.out.print("]");
		System.out.println("\nTotal is " + solution.get(solution.size() - 1));
		System.out.println("Brute force running time: " + (bfEnd - bfStart)/ 1000000 + " ms");
		
		
		/* Divide and Conquer */
		dcStart = System.nanoTime();
		int result = divideAndConquer(inputList, 0, n - 1);
		dcEnd = System.nanoTime();
		System.out.println("\nDivide and Conquer");
		System.out.println("Cheapest: " + result);
		System.out.println("Divide and Conquer running time: " + (dcEnd - dcStart)/ 1000000 + " ms");
		
		

//		/* Dynamic */
		dynamicStart = System.nanoTime();
		printStack(dynamic(i,n,inputList));
		dynamicEnd = System.nanoTime();
		
		System.out.println("Dynamic running time: " + (dynamicEnd - dynamicStart)/ 1000000 + " ms");

	}

}

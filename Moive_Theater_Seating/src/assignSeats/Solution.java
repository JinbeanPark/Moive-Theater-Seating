package assignSeats;

import java.util.*;
import java.io.*;
import java.lang.*;

class Solution {
	
	private int lenRow, lenCol;
	private boolean[][] availableSeats; // Assigned seat = true, Empty seat = false
	private int totalAvailableSeats;
	private int indxIdentifier;
	private PrintStream outputFile;
	private PrintStream console;
	
	
	Solution() {
		availableSeats = new boolean[10][20];
		totalAvailableSeats = 10 * 20;
		indxIdentifier = 1;
		lenRow = availableSeats.length;
		lenCol = availableSeats[0].length;
	}
	
	
	// Print the current seating status.
	public void printSeatingStatus() {
		
		System.setOut(console);
		System.out.println("======== Screen ========");
		for (int r = 0; r < lenRow; r++) {
			System.out.print((char)('A' + r) + ":  ");
			for (int c = 0; c < lenCol; c++) {
				if (availableSeats[r][c])
					System.out.print("X");
				else
					System.out.print("S");
			}
			System.out.println();
		}
		System.out.println();
	}
	
	
	// Return the reservation identifier e.g. R001, R002 ... R###.
	public String getIdentifier() {
		
		String strIndxIdentifier = String.valueOf(indxIdentifier);
		int lenIndx = strIndxIdentifier.length();
		char[] zero = new char[3 - lenIndx];
		Arrays.fill(zero, '0');
		String identifier = "R" + String.valueOf(zero) + strIndxIdentifier + " ";
		indxIdentifier++;
		return identifier;
	}
	
	
	// When there is no available consecutive seats for group, 
	// the below funtion assigns people in group individually.
	public int assignSeatsIndividually(int row, int numSeatReq, PriorityQueue<String> assignedSeats) {
		
		for (int i = 0; i < lenCol && numSeatReq != 0; i++) {
			if (!availableSeats[row][i]) {
//				String rowLetter = Character.toString(((char)'A' + row));
				String rowLetter = Character.toString((char)('A' + row));
				assignedSeats.add(rowLetter + String.valueOf(i));
				availableSeats[row][i] = true;
				totalAvailableSeats--;
				numSeatReq--;
			}
		}
		
		return numSeatReq;
	}
	
	
	// Check out whether there is a valid column to assign group.
	// Consider two cases when the number of request is odd and even.
	public boolean validColmnToAssignGroup(int mi, int numSeatReq, int row, boolean isNumReqOdd) {
		
		int numMovLR = numSeatReq / 2;	
		
		if (availableSeats[row][mi])
			return false;
		if (isNumReqOdd) {
			for (int i = 1; i <= numMovLR; i++) {
				if ((-1 >= mi - i || availableSeats[row][mi - i]) || mi + i >= lenCol || availableSeats[row][mi + i]) {
					return false;
				}
			}
		}
		else {
			for (int i = 1; i <= numMovLR; i++) {
				if (i == numMovLR && (-1 >= mi - i || availableSeats[row][mi - i]))
					return false;
				else if (-1 >= mi - i || availableSeats[row][mi - i] || mi + i >= lenCol || availableSeats[row][mi + i])
					return false;
			}
		}
		
		return true;
	}
	
	
	// Assign seats from the middle of a row to provide the best angle of view for customer satisfaction.
	// If the seat in the middle of a row is already fulled, the program begin tracking the empty seat
	// from the left and right of the middle to edge of a row.
	public boolean assignSeatInRow(int numSeatReq, int row, String identifier) {

		int mi = lenCol / 2, numMovLR = numSeatReq / 2;
		boolean isNumReqOdd = numSeatReq % 2 == 1 ? true : false;
		boolean validAssignSeats = true;
		
		int validColmn = -1;
		// Check out whether the seat at the middle of a row is empty or not.
		if (validColmnToAssignGroup(mi, numSeatReq, row, isNumReqOdd))
			validColmn = mi;
		// If the seat at the middle of a row is already fulled, it starts tracking the empty seat
		// from the left and right of the middle to edge of a row.
		if (validColmn == -1) {
			for (int i = 1; i <= lenCol / 2; i++) {
				if (-1 < mi - i && validColmnToAssignGroup(mi - i, numSeatReq, row, isNumReqOdd)) {
					validColmn = mi - i;
					break;
				}
				if (mi + i < lenCol && validColmnToAssignGroup(mi + i, numSeatReq, row, isNumReqOdd)) {
					validColmn = mi + i;
					break;
				}
			}
		}
		// If there is no available seats to assgin for group, validColmn == -1
		validAssignSeats = validColmn != -1 ? true : false;
		// If the program found the available seat for group, the program put the assignment into output file.
		// The reason why priority queue was used is because the program put the assignment by ascending order
		// into the output file.
		if (validAssignSeats) {
//			String rowLetter = Character.toString(((char) 'A' + row));
			String rowLetter = Character.toString((char)('A' + row));
			PriorityQueue<String> assignedSeats = new PriorityQueue<>((a, b) -> a.charAt(0) == b.charAt(0)
					? ((a.length() == b.length()) ? a.compareTo(b) : a.length() - b.length())
					: a.compareTo(b));
			assignedSeats.add(rowLetter + String.valueOf(validColmn));
			availableSeats[row][validColmn] = true;

			if (isNumReqOdd) {
				for (int i = 1; i <= numMovLR; i++) {
					assignedSeats.add(rowLetter + String.valueOf(validColmn - i));
					assignedSeats.add(rowLetter + String.valueOf(validColmn + i));
					availableSeats[row][validColmn - i] = true;
					availableSeats[row][validColmn + i] = true;
				}
			} 
			else {
				for (int i = 1; i <= numMovLR; i++) {
					assignedSeats.add(rowLetter + String.valueOf(validColmn - i));
					availableSeats[row][validColmn - i] = true;
					if (i != numMovLR) {
						assignedSeats.add(rowLetter + String.valueOf(validColmn + i));
						availableSeats[row][validColmn + i] = true;
					}
				}
			}

			while (!assignedSeats.isEmpty())
				identifier += assignedSeats.poll() + ",";
			identifier = identifier.substring(0, identifier.length() - 1);

			System.setOut(outputFile);
			System.out.println(identifier);
			System.setOut(console);
			System.out.println(identifier);
			totalAvailableSeats -= numSeatReq;
			printSeatingStatus();

		} 
		else
			return false;

		return validAssignSeats;
	}
	
	
	// I assumed that the program is able to read input files multiple times if there is still available seat.
	// The program tracks the rows from the middle to edge while jumping the adjacent rows for the public safety.
	// For example, if there are rows A -> B -> C -> D -> E -> F -> G -> H -> I -> J,
	// the program tracks the rows by the following order: 
	// E(row=4) -> G(6) -> C(2) -> I(8) -> A(0).
	// However, if there are no available seats with the public safety condition,
	// the program starts tracking the left rows by the following order:
	// F(row=5) -> D(3) -> H(7) -> B(1) -> J(9)
	// For the customer satisfaction, program tries to assign seats as group instead of individual people
	// to prevent group from being separated.
	// However, if there are no available seats for GROUP, 
	// the program begins assigning people individually one by one.
	public void assignSeat(String pathInputFile) {
		
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(pathInputFile));
			String line = reader.readLine();
			while (line != null && totalAvailableSeats > 0) {
				
				String identifier = getIdentifier();
				String[] splitStrs = line.split(" ");
				int numSeatReq = Integer.parseInt(splitStrs[1]); //splitStrs indicates the number of seat request.
				// Starting to find the seat from the middle of row and column 
				// in order to provide customers the best angle of view.
				int oddRow = (lenRow / 2) - 1;
				// The order of tracking oddRow: E(row=4) -> G(6) -> C(2) -> I(8) -> A(0)
				// For the purpose of public safety, the program tracks the rows while jumping the adjacent rows.
				boolean isAssigned = false;
				int numMovRow = 2;
				
				// The program first tracks the odd rows E -> G -> C -> I -> A 
				if (assignSeatInRow(numSeatReq, oddRow, identifier))
					isAssigned = true;
				while (!isAssigned && -1 < (oddRow - numMovRow) && (oddRow + numMovRow) < lenRow ) {
					if (assignSeatInRow(numSeatReq, oddRow + numMovRow, identifier)) {
						isAssigned = true;
						break;
					}
					if (assignSeatInRow(numSeatReq, oddRow - numMovRow, identifier)) {
						isAssigned = true;
						break;
					}
					numMovRow += 2;
				}
				
				int evenRow = lenRow / 2;
				numMovRow = 2;
				// If the program couldn't find the available seats for group in odd rows,
				// the program tracks the even rows F(row=5) -> D(3) -> H(7) -> B(1) -> J(9).
				// The order of tracking EvenRow: F(row=5) -> D(3) -> H(7) -> B(1) -> J(9)
				if (!isAssigned && assignSeatInRow(numSeatReq, evenRow, identifier))
					isAssigned = true;
				while (!isAssigned && -1 < (evenRow - numMovRow) && (evenRow + numMovRow) < lenRow && !isAssigned) {
					if (assignSeatInRow(numSeatReq, evenRow + numMovRow, identifier)) {
						isAssigned = true;
						break;
					}
					if (assignSeatInRow(numSeatReq, evenRow - numMovRow, identifier)) {
						isAssigned = true;
						break;
					}
					numMovRow += 2;
				}
				
				// For the case that there is no available consecutive seats for group,
				// the program splits people in group and assigns them separately.
				// The order of tracking rows for assigning the individual people is the following order: 
				// E(row=4) -> F(5) -> G(6) -> D(3) -> C(2) -> H(7) -> I(8) -> B(1) -> A(0) -> J(9)
				if (!isAssigned) {
					oddRow = (lenRow / 2) - 1;
					evenRow = lenRow / 2;
					numMovRow = 2;
					PriorityQueue<String> assignedSeats = new PriorityQueue<>((a, b) -> a.charAt(0) == b.charAt(0) ? ((a.length() == b.length()) ? a.compareTo(b) : a.length() - b.length()) : a.compareTo(b));					
					
					if (!isAssigned) {
						numSeatReq = assignSeatsIndividually(oddRow, numSeatReq, assignedSeats);
						if (numSeatReq == 0)
							isAssigned = true;
					}
					if (!isAssigned) {
						numSeatReq = assignSeatsIndividually(evenRow, numSeatReq, assignedSeats);
						if (numSeatReq == 0)
							isAssigned = true;
					}
					while (!isAssigned && -1 < (oddRow - numMovRow) && (oddRow + numMovRow) < lenRow
							&& -1 < (evenRow - numMovRow) && (evenRow + numMovRow) < lenRow) {
						numSeatReq = assignSeatsIndividually(oddRow + numMovRow, numSeatReq, assignedSeats);
						if (numSeatReq == 0) {
							isAssigned = true;
							break;
						}
						numSeatReq = assignSeatsIndividually(evenRow - numMovRow, numSeatReq, assignedSeats);
						if (numSeatReq == 0) {
							isAssigned = true;
							break;
						}
						numSeatReq = assignSeatsIndividually(oddRow - numMovRow, numSeatReq, assignedSeats);
						if (numSeatReq == 0) {
							isAssigned = true;
							break;
						}
						numSeatReq = assignSeatsIndividually(evenRow + numMovRow, numSeatReq, assignedSeats);
						if (numSeatReq == 0) {
							isAssigned = true;
							break;
						}
						numMovRow += 2;
					}
					
					while (!assignedSeats.isEmpty())
						identifier += assignedSeats.poll() + ",";
					identifier = identifier.substring(0, identifier.length() - 1);
					
					System.setOut(outputFile);
					System.out.println(identifier);
					System.setOut(console);
					System.out.println(identifier);
					printSeatingStatus();
					
				}
				if (totalAvailableSeats == 0) {
					System.err.println("Every seat is fulled");
					break;
				}
				// The program reads next line of input file.
				line = reader.readLine();
			}
			
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	
	public static void main(String[] args) throws FileNotFoundException {
		
		Solution s = new Solution();		
		Scanner scanObj = new Scanner(System.in);
		String pathInputFile = "";
		File file = new File("/Users/jinbean/Desktop/Programming Languages/MovieTheaterSeating/output.txt");
		s.outputFile = new PrintStream(file);
		s.console = System.out;
		while (!pathInputFile.equals("exit")) {
			pathInputFile = scanObj.nextLine(); // Read the path of input file from the command in console.
			s.assignSeat(pathInputFile);
			System.setOut(s.console);
			s.printSeatingStatus();
			if (s.totalAvailableSeats <= 0) {
				System.out.println("There is no available seats");
				break;
			}
		}
		System.out.println("Program exited");
	}
}

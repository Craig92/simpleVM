package simpleVM;

import java.io.*;
import java.util.*;

public class MyAssembler {

	private MyVirtualMachine vm;
	private int programmCounter = 0;

	private static final int NOP = 0b0000;
	private static final int LOAD = 0b0001;
	private static final int MOV = 0b0010;
	private static final int ADD = 0b0011;
	private static final int SUB = 0b0100;
	private static final int MUL = 0b0101;
	private static final int DIV = 0b0110;
	private static final int PUSH = 0b0111;
	private static final int POP = 0b1000;
	private static final int JMP = 0b1001;
	private static final int JIZ = 0b1010;
	private static final int JIH = 0b1011;
	private static final int JSR = 0b1100;
	private static final int RTS = 0b1101;

	/**
	 * Erzeugt einen Assembler.
	 * @param vm Dazugehörige Virtuelle Maschine
	 */
	public MyAssembler(MyVirtualMachine vm) {

		this.vm = vm;
	}

	/**
	 * Startet den Assembler, der eine Textdatei zeilenweise liest und die
	 * runAssembler Methode für jede Zeile aufruft bis das Ende der Datei
	 * erreicht wurde
	 * 
	 * @param inputData
	 *            Dateiname des Assamblercodedatei
	 * @throws AssemblerException
	 */
	public void startAssembler(String inputData) throws AssemblerException {

		File inputFile = new File(inputData);

		// Prüft ob Datei existiert und lesbar ist.
		if (!inputFile.exists() || !inputFile.canRead() || !inputFile.isFile()) {
			throw new AssemblerException("Fehler bei der Öffnen der Datei!");

		} else {

			Scanner fileScanner = null;

			try {
				fileScanner = new Scanner(inputFile);

				// Prüft ob es eine nächste Zeile gibt und ruft die
				// run()-Methode auf, wenn es noch eine Zeile gibt.
				while (fileScanner.hasNextLine()) {
					runAssembler(fileScanner.nextLine(), programmCounter);
				}

			} catch (IOException ex) {
				throw new AssemblerException("Fehler beim Verarbeiten der Datei!");

			} finally {

				if (fileScanner != null) {
					fileScanner.close();
				}
			}
		}

//		System.out.println("Anzahl Programmzeilen: " + programmCounter);
		
		//Setzt die Größe des Profiler Arrays
		vm.setProfiler(programmCounter);
	}

	/**
	 * Wandelt zeilenweise den Programmcode in OpCode um.
	 * 
	 * @param programmCode
	 *            Aktueller Programmcode aus der Datei
	 * @param programmLine
	 *            Aktuelle Programmzeile (für das auffinden von Fehlern)
	 * @param vm
	 *            Die verwendete Virtuelle Maschine.
	 * @throws AssemblerException
	 */
	private void runAssembler(String programmCode, int programmLine) throws AssemblerException {

//		System.out.println("ProgrammCode: " + programmCode);
		String command = "";
		String temp = "";
		int wert = 0;
		int rx = 0;
		int ry = 0;
		int toMemory = 0;
		int fromMemory = 0;
		int opCode = 0;
		Scanner stringScanner = null;

		try {

			stringScanner = new Scanner(programmCode);

			// Liest den ersten Teil der Zeile mit dem Befehl.
			if (stringScanner.hasNext()) {
				command = stringScanner.next();
			}

			switch (command) {

			case "NOP":
				opCode = NOP;
				vm.getMemory()[programmCounter++] = opCode;		
//				System.out.println("OpCode: " + opCode);

				break;

			case "LOAD":
				opCode = LOAD;
				temp = stringScanner.next();

				// Überliest das #-Zeichen falls vorhanden.
				if (temp.charAt(0) == '#') {
					temp = temp.substring(1);
				} else {
					throw new AssemblerException("Code ist fehlerhaft in Zeile " + (programmLine + 1) + " !");
				}

				wert = Integer.parseInt(temp);
				opCode += (wert << 4);
				vm.getMemory()[programmCounter++] = opCode;
//				System.out.println("OpCode: " + opCode);
				
				break;

			case "MOV":
				opCode = MOV;
				temp = stringScanner.next();

				// Überliest die Klammern bei dem ersten Register.
				if (temp.charAt(0) == '(') {
					temp = temp.substring(1);
					temp = temp.substring(0, temp.length() - 1);
					toMemory = 1;
				} else {
					toMemory = 0;
				}

				// Überliest das R-Zeichen falls vorhanden.
				if (temp.charAt(0) == 'R') {
					temp = temp.substring(1);
				} else {
					throw new AssemblerException("Code ist fehlerhaft in Zeile " + (programmLine + 1) + " !");
				}

				rx = Integer.parseInt(temp);
				stringScanner.next();

				// Überliest das Komma.
				temp = stringScanner.next();

				// Überliest die Klammern bei dem zweiten Register.
				if (temp.charAt(0) == '(') {
					temp = temp.substring(1);
					temp = temp.substring(0, temp.length() - 1);
					fromMemory = 1;
				} else {
					fromMemory = 0;
				}

				// Überliest das R-Zeichen falls vorhanden.
				if (temp.charAt(0) == 'R') {
					temp = temp.substring(1);
				} else {
					throw new AssemblerException("Code ist fehlerhaft in Zeile " + (programmLine + 1) + " !");
				}

				ry = Integer.parseInt(temp);

				opCode += (rx << 4);
				opCode += (ry << 8);
				opCode += (fromMemory << 12);
				opCode += (toMemory << 13);
				vm.getMemory()[programmCounter++] = opCode;
//				System.out.println("OpCode: " + opCode);
				
				break;

			case "ADD":
				opCode = ADD;
				temp = stringScanner.next();

				// Überliest das R-Zeichen falls vorhanden.
				if (temp.charAt(0) == 'R') {
					temp = temp.substring(1);
				} else {
					throw new AssemblerException("Code ist fehlerhaft in Zeile " + (programmLine + 1) + " !");
				}

				rx = Integer.parseInt(temp);
				stringScanner.next();

				// Überliest das Komma.
				temp = stringScanner.next();

				// Überliest das R-Zeichen falls vorhanden.
				if (temp.charAt(0) == 'R') {
					temp = temp.substring(1);
				} else {
					throw new AssemblerException("Code ist fehlerhaft in Zeile " + (programmLine + 1) + " !");
				}

				ry = Integer.parseInt(temp);

				opCode += (rx << 4) + (ry << 8);
				vm.getMemory()[programmCounter++] = opCode;
//				System.out.println("OpCode: " + opCode);
				
				break;
			case "SUB":
				opCode = SUB;
				temp = stringScanner.next();

				// Überliest das R-Zeichen falls vorhanden.
				if (temp.charAt(0) == 'R') {
					temp = temp.substring(1);
				} else {
					throw new AssemblerException("Code ist fehlerhaft in Zeile " + (programmLine + 1) + " !");
				}

				rx = Integer.parseInt(temp);
				stringScanner.next();

				// Überliest das Komma.
				temp = stringScanner.next();

				// Überliest das R-Zeichen falls vorhanden.
				if (temp.charAt(0) == 'R') {
					temp = temp.substring(1);
				} else {
					throw new AssemblerException("Code ist fehlerhaft in Zeile " + (programmLine + 1) + " !");
				}

				ry = Integer.parseInt(temp);

				opCode += (rx << 4) + (ry << 8);
				vm.getMemory()[programmCounter++] = opCode;
//				System.out.println("OpCode: " + opCode);
				
				break;

			case "MUL":
				opCode = MUL;
				temp = stringScanner.next();

				// Überliest das R-Zeichen falls vorhanden.
				if (temp.charAt(0) == 'R') {
					temp = temp.substring(1);
				} else {
					throw new AssemblerException("Code ist fehlerhaft in Zeile " + (programmLine + 1) + " !");
				}

				rx = Integer.parseInt(temp);
				stringScanner.next();

				// Überliest das ,-Zeichen.
				temp = stringScanner.next();

				// Überliest das R-Zeichen falls vorhanden.
				if (temp.charAt(0) == 'R') {
					temp = temp.substring(1);
				} else {
					throw new AssemblerException("Code ist fehlerhaft in Zeile " + (programmLine + 1) + " !");
				}

				ry = Integer.parseInt(temp);

				opCode += (rx << 4) + (ry << 8);
				vm.getMemory()[programmCounter++] = opCode;
//				System.out.println("OpCode: " + opCode);
				
				break;

			case "DIV":
				opCode = DIV;
				temp = stringScanner.next();

				// Überliest das R-Zeichen falls vorhanden.
				if (temp.charAt(0) == 'R') {
					temp = temp.substring(1);
				} else {
					throw new AssemblerException("Code ist fehlerhaft in Zeile " + (programmLine + 1) + " !");
				}

				rx = Integer.parseInt(temp);
				stringScanner.next();

				// Überliest das ,-Zeichen.
				temp = stringScanner.next();

				// Überliest das R-Zeichen falls vorhanden.
				if (temp.charAt(0) == 'R') {
					temp = temp.substring(1);
				} else {
					throw new AssemblerException("Code ist fehlerhaft in Zeile " + (programmLine + 1) + " !");
				}

				ry = Integer.parseInt(temp);

				opCode += (rx << 4) + (ry << 8);
				vm.getMemory()[programmCounter++] = opCode;
//				System.out.println("OpCode: " + opCode);
				
				break;

			case "PUSH":
				
				opCode = PUSH;
				temp = stringScanner.next();

				// Überliest das R-Zeichen falls vorhanden.
				if (temp.charAt(0) == 'R') {
					temp = temp.substring(1);
				} else {
					throw new AssemblerException("Code ist fehlerhaft in Zeile " + (programmLine + 1) + " !");
				}

				rx = Integer.parseInt(temp);

				opCode += (rx << 4);
				vm.getMemory()[programmCounter++] = opCode;
//				System.out.println("OpCode: " + opCode);
				
				break;

			case "POP":
				
				opCode = POP;
				temp = stringScanner.next();

				// Überliest das R-Zeichen falls vorhanden.
				if (temp.charAt(0) == 'R') {
					temp = temp.substring(1);
				} else {
					throw new AssemblerException("Code ist fehlerhaft in Zeile " + (programmLine + 1) + " !");
				}

				rx = Integer.parseInt(temp);

				opCode += (rx << 4);
				vm.getMemory()[programmCounter++] = opCode;
//				System.out.println("OpCode: " + opCode);
				
				break;

			case "JMP":
				opCode = JMP;
				temp = stringScanner.next();

				// Überliest das #-Zeichen falls vorhanden.
				if (temp.charAt(0) == '#') {
					temp = temp.substring(1);
				} else {
					throw new AssemblerException();
				}

				wert = Integer.parseInt(temp);
				opCode += (wert << 4);
				vm.getMemory()[programmCounter++] = opCode;	
//				System.out.println("OpCode: " + opCode);
				
				break;

			case "JIZ":
				opCode = JIZ;
				temp = stringScanner.next();

				// Überliest das #-Zeichen falls vorhanden.
				if (temp.charAt(0) == '#') {
					temp = temp.substring(1);
				} else {
					throw new AssemblerException("Code ist fehlerhaft in Zeile " + (programmLine + 1) + " !");
				}

				wert = Integer.parseInt(temp);
				opCode += (wert << 4);
				vm.getMemory()[programmCounter++] = opCode;
//				System.out.println("OpCode: " + opCode);
				
				break;

			case "JIH":
				opCode = JIH;
				temp = stringScanner.next();

				// Überliest das #-Zeichen falls vorhanden.
				if (temp.charAt(0) == '#') {
					temp = temp.substring(1);
				} else {
					throw new AssemblerException("Code ist fehlerhaft in Zeile " + (programmLine + 1) + " !");
				}

				wert = Integer.parseInt(temp);
				opCode += (wert << 4);
				vm.getMemory()[programmCounter++] = opCode;
//				System.out.println("OpCode: " + opCode);
				
				break;

			case "JSR":
				opCode = JSR;
				temp = stringScanner.next();

				// Überliest das #-Zeichen falls vorhanden.
				if (temp.charAt(0) == '#') {
					temp = temp.substring(1);
				} else {
					throw new AssemblerException("Code ist fehlerhaft in Zeile " + (programmLine + 1) + " !");
				}

				wert = Integer.parseInt(temp);
				opCode += (wert << 4);
				vm.getMemory()[programmCounter++] = opCode;
//				System.out.println("OpCode: " + opCode);
				
				break;

			case "RTS":
				
				opCode = RTS;
				vm.getMemory()[programmCounter++] = opCode;
//				System.out.println("OpCode: " + opCode);
				
				break;

			default:
				
//				System.out.println("Kein OpCode");
				break;
			}

		} catch (IndexOutOfBoundsException | NumberFormatException | NoSuchElementException ex) {
			throw new AssemblerException("Code ist fehlerhaft in Zeile " + (programmLine + 1) + " !");

		} finally {

			if (stringScanner != null) {
				stringScanner.close();
			}

		}
	}
}

package simpleVM;

public class AssemblerException extends Exception {

	private static final long serialVersionUID = 1L;

	public AssemblerException() {

		System.err.println("ASSEMBLER ERROR");
	}

	public AssemblerException(String message) {

		System.err.println("ASSEMBLER ERROR " + message);
	}

}

package simpleVM;

public class VirtualMachineException extends Exception {

	private static final long serialVersionUID = 1L;

	public VirtualMachineException() {

		System.err.println("VIRTUALMACHINE ERROR");
	}

	public VirtualMachineException(String message) {

		System.err.println("VIRTUALMACHINE ERROR " + message);
	}

}

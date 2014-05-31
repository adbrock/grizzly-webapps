import jssc.SerialPortList;


public class ListPorts {
	
	public static void main(String[] args) {
		for (String name : SerialPortList.getPortNames()) {
			System.out.println(name);
		}
	}
}

package dca.ufrn.team.physio.model;

public class PhysioDevice {
	private String name;
	private String address;

	public PhysioDevice() {
		super();
	}

	public PhysioDevice(String name, String address) {
		super();
		this.name = name;
		this.address = address;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

}

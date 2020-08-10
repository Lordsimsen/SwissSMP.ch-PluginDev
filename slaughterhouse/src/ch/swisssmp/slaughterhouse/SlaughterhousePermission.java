package ch.swisssmp.slaughterhouse;

/**
 * Enum of all Permissions for the Plugin to centralize changes.
 * 
 * @author Plexon21
 *
 */
public enum SlaughterhousePermission {
	MEAT("p21.slaughterhouse.meat"), 
	OTHER("p21.slaughterhouse.other");
	private SlaughterhousePermission(String value) {
		this.value = value;
	}

	private final String value;

	public String getValue() {
		return value;
	}
}
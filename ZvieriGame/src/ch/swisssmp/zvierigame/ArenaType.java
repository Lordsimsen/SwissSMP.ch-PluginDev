package ch.swisssmp.zvierigame;

public enum ArenaType {
			
		ARENA("ZvieriArena", "ZVIERI_ARENA");
		
		private final String name;
		private final String icon;
		
		private ArenaType(String name, String icon) {
			this.name = name;
			this.icon = icon;
		}
		public String getIcon(){
			return icon;
		}		
		public String getName(){
			return name;
		}		
	}



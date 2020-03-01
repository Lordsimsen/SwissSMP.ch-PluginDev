package ch.swisssmp.livemap.markers;

public class MarkerIcon {
	
	private String id;
	private String label;
	private int size;
	private String fileUrl;
	
	public MarkerIcon(String id){
		this.id = id;
	}
	
	public String getId(){
		return id;
	}
	
	public String getLabel(){
		return label;
	}
	
	public void setLabel(String label){
		this.label = label;
	}
	
	public int getSize(){
		return size;
	}
	
	public void setSize(int size){
		this.size = size;
	}
	
	public String getFileUrl(){
		return fileUrl;
	}
	
	public void setFileUrl(String fileUrl){
		this.fileUrl = fileUrl;
	}
}

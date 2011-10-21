package hci;

//store the image's directory and labels directory

public class fileDirs {
	
	//Get Directory of the image
	private String i = null;
	public void setImDir(String i) {
		this.i = i;
	}
	public String getImDir() {
		return i;
	}
	
	//Get Directory of the labels
	private String l = null;
	public void setLbDir(String l) {
		this.l = l + ".label";
	}
	public String getLbDir() {
		return l;
	}

}

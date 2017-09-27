public class Particle{
	public String type;
	public int charge;

	public Particle(int charge){
		this.charge = charge;
		determineType();
	}

	private void determineType(){
		if(charge < 0){
			this.type = "electron";
		}else{
			this.type = "positron";
		}
	}
}
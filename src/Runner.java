public class Runner extends Thread{
	
	private int id;
	private Control controller;
	private Room localRoom;
	private int mutationRate;
	
	
	public Runner(int id, Control controller, Room localRoom, int mutationRate){
		this.id = id;
		this.controller = controller;
		this.localRoom = localRoom;
		this.mutationRate = mutationRate;
	}
	
	public void run(){
		localRoom.crossOver();
		localRoom.mutate(mutationRate);
		localRoom.updateTotalAffinity();
		controller.readAndSet(localRoom.totalAffinity, id);
	}
}

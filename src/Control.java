public class Control {
	private final int THREAD_COUNT = 32;
	private final int ROWS = 8;
	private final int COLS = 4;
	private final int MUTATION_RATE = 35; //a percentage %
	private final int GENERATIONS = 5000;
	private Room[] solutions;
	private Thread[] runners;
	private Room masterRoom;
	private double bestTotalAffinity;
	private int bestIndex;
	private boolean indexChanged;
	
	public Control(){
		bestTotalAffinity = 2.0; //2 is the highest net charge (lowest total affinity) possible
		bestIndex = -1;
		solutions = new Room[THREAD_COUNT];
		runners = new Thread[THREAD_COUNT];
		masterRoom = new Room(ROWS, COLS);
		updateSolutions();
		indexChanged = false;
	}
	
	public void go(){
		int generations = 0;
		while(generations < Math.floorDiv(GENERATIONS, THREAD_COUNT)){
			initThreads();
			startThreads();
			joinThreads();
			updateMaster();
			updateSolutions();
			generations++;
		}
		masterRoom.printParticles();
		System.out.println("\nAffinity: " + bestTotalAffinity);
	}
	
	private void initThreads(){
		for(int i = 0; i < THREAD_COUNT; i++){
			runners[i] = new Runner(i, this, solutions[i], MUTATION_RATE);
		}
	}
	
	private void startThreads(){
		for(int i = 0; i < THREAD_COUNT; i++){
			runners[i].start();
		}
	}
	
	private void joinThreads(){
		for(int i = 0; i < THREAD_COUNT; i++){
			try {
				runners[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void updateMaster(){
		if(indexChanged){
			masterRoom = new Room(ROWS, COLS, solutions[bestIndex].arrangement);
			indexChanged = false;
		}
	}
	
	private void updateSolutions(){
		for(int i = 0; i < solutions.length; i++){
			solutions[i] = new Room(ROWS, COLS, masterRoom.arrangement);
		}
	}
	
	public synchronized void readAndSet(double newAffinity, int newIndex){ //read and possibly change bestTotalAffinity and bestIndex
		if(bestTotalAffinity > newAffinity){
			bestTotalAffinity = newAffinity;
			bestIndex = newIndex;
			indexChanged = true;
		}
	}

}

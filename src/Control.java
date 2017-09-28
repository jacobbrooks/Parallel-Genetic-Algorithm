import processing.core.PApplet;
import processing.core.PImage;

public class Control extends PApplet{
	private final int THREAD_COUNT = 32;
	public final int ROWS = 8;
	public final int COLS = 4;
	private final int MUTATION_RATE = 30; //a percentage %
	private final int GENERATIONS = 10000;
	private Room[] solutions;
	private Thread[] runners;
	public Room masterRoom;
	private double bestTotalAffinity;
	private int bestIndex;
	private boolean indexChanged;
	private PImage[][] images;
	private PImage electron;
	private PImage positron;
	private int loopCount;
	
	public Control(){
		bestTotalAffinity = 2.0; //2 is the highest net charge (lowest total affinity) possible
		bestIndex = -1;
		solutions = new Room[THREAD_COUNT];
		runners = new Thread[THREAD_COUNT];
		masterRoom = new Room(ROWS, COLS);
		updateSolutions();
		indexChanged = false;
		loopCount = 0;
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
	
	private void loadImages(){
		images = new PImage[ROWS][COLS];
		for(int i = 0; i < ROWS; i++){
			for(int j = 0; j < COLS; j++){
				if(masterRoom.arrangement[i][j].charge < 0){
					images[i][j] = electron;
				}else{
					images[i][j] = positron;
				}
				images[i][j].resize(150,100);
			}
		}
	}
	
	private void displayImages(){
		for(int i = 0; i < ROWS; i++){
			for(int j = 0; j < COLS; j++){
				image(images[i][j], j * 150, i * 100);
			}
		}
	}
	
	public void settings(){
		size(600,800);
	}
	
	public void setup(){
		electron = loadImage("res/e.png");
		positron = loadImage("res/p.png");
		PImage bg = loadImage("res/bg.jpg");
		bg.resize(600, 800);
		background(bg);
		loadImages();
	}
	
	
	public void draw(){
		displayImages();
		if(loopCount < GENERATIONS / THREAD_COUNT){
			initThreads();
			startThreads();
			joinThreads();
			updateMaster();
			updateSolutions();
			loadImages();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			loopCount++;
			System.out.println(loopCount);
		}
	}

}

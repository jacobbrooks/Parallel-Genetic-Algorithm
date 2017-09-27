import java.util.Random;

public class Room{

	private int rows;
	private int cols;
	public Particle[][] arrangement;
	private Random r = new Random();
	public double totalAffinity; //the closer to zero, the higher the total affinity

	public Room(int rows, int cols){
		this.rows = rows;
		this.cols = cols;
		arrangement = new Particle[rows][cols];
		initArrangement();
		updateTotalAffinity();
	}

	public Room(int rows, int cols, Particle[][] arrangement){
		this.rows = rows;
		this.cols = cols;
		this.arrangement = new Particle[rows][cols];
		for(int i = 0; i < rows; i++){
			for(int j = 0; j < cols; j++){
				this.arrangement[i][j] = arrangement[i][j];
			}
		}
		updateTotalAffinity();
	}

	public void updateTotalAffinity(){
		this.totalAffinity = calculateTotalAffinity();
	}

	public void printParticles(){
		for(int i = 0; i < rows; i++){
			for(int j = 0; j < cols; j++){
				System.out.print("|" + arrangement[i][j].type + "|");
			}
			System.out.println();
		}
	}

	private void initArrangement(){
		for(int i = 0; i < rows; i++){
			for(int j = 0; j < cols; j++){
				int charge = 1 - (r.nextInt(2) * 2);
				arrangement[i][j] = new Particle(charge);
			}
		}
	}

	private double calculatePairAffinity(Particle a, Particle b){ //the lower the return value (net charge), the higher the affinity
		if(a.charge + b.charge < 0){
			return (double) (a.charge + b.charge) * -1;
		}else if(a.charge + b.charge > 0){
			return (double) a.charge + b.charge;
		}
		return 0.0;
	}	

	private double calculateHorizontalAffinity(){
		double pairs = 0.0;
		double affinity = 0.0;
		for(int i = 0; i < rows; i++){
			for(int j = 0; j < cols - 1; j++){
				affinity += calculatePairAffinity(arrangement[i][j], arrangement[i][j + 1]);
				pairs++;
			}
		}
		return affinity / pairs;
	}

	private double calculateVerticalAffinity(){
		double pairs = 0.0;
		double affinity = 0.0;
		for(int j = 0; j < cols; j++){
			for(int i = 0; i < rows - 1; i++){
				affinity += calculatePairAffinity(arrangement[i][j], arrangement[i + 1][j]);
				pairs++;
			}
		}
		return affinity / pairs;
	}

	private double calculateTotalAffinity(){
		double horizontalAffinity = calculateHorizontalAffinity();
		double verticalAffinity = calculateVerticalAffinity();
		return (horizontalAffinity + verticalAffinity) / 2.0;
	}

	private void swap(int row1, int col1, int row2, int col2){
		Particle temp = arrangement[row1][col1];
		arrangement[row1][col1] = arrangement[row2][col2];
		arrangement[row2][col2] = temp;
	}

	private void doHorizontalPairSwaps(){
		for(int i = 0; i < rows; i++){
			for(int j = 0; j < cols - 2; j++){
				double currentAffinity = calculatePairAffinity(arrangement[i][j], arrangement[i][j + 1]);
				double potentialAffinity = calculatePairAffinity(arrangement[i][j], arrangement[i][j + 2]);
				if(currentAffinity > potentialAffinity){
					swap(i, j + 1, i, j + 2);
				}
			}
		}
	}

	private void doVertivalPairSwaps(){
		for(int j = 0; j < cols; j++){
			for(int i = 0; i < rows - 2; i++){
				double currentAffinity = calculatePairAffinity(arrangement[i][j], arrangement[i + 1][j]);
				double potentialAffinity = calculatePairAffinity(arrangement[i][j], arrangement[i + 2][j]);
				if(currentAffinity > potentialAffinity){
					swap(i + 1, j, i + 2, j);
				}
			}
		}
	}

	public void crossOver(){
		doHorizontalPairSwaps();
		doVertivalPairSwaps();
	}

	public void mutate(int rate){
		double convertedRate = (double)(rate * ((rows * cols) / 2)) / 100.0;
		int swapAmount = (int) Math.floor(convertedRate);
		for(int i = 0; i < swapAmount; i++){
			int row1 = r.nextInt(rows);
			int col1 = r.nextInt(cols);
			int row2 = r.nextInt(rows);
			int col2 = r.nextInt(cols);
			swap(row1, col1, row2, col2);
		}
	}

}
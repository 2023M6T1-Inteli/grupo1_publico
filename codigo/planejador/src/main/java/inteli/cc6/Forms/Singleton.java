package inteli.cc6.Forms;

public class Singleton {

	// creating the singleton structure
    private static Singleton uniqueInstance;

	private Singleton() {
	}

	public static synchronized Singleton getInstance() {
		if (uniqueInstance == null)
			uniqueInstance = new Singleton();

		return uniqueInstance;
	}


	// defining singleton global variables
	private int population = 2000;

	private int populationSize = 200;

	// function that accesses the population variable in other scripts
	public int getPopulation(){
		return population;
	}

	// function that edits the value of the population variable in other scripts
	public void changePopulation(int newPopulation){
		population = newPopulation;
	}

	// function that accesses the populationSize variable in other scripts
	public int getPopulationSize(){
		return populationSize;
	}

	// function that edits the value of the populationSize variable in other scripts
	public void changePopulationSize(int newPopulationSize){
		populationSize = newPopulationSize;
	}
}

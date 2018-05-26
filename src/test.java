public class test {
	static beeColony bee=new beeColony();
	
	public static void main(String[] args) {
		int iter=0;
		int run=0;
		int j=0;
		double mean=0;
		//srand(time(NULL));
		/*This is important to read it will explain how to adapt the algorithm for our purpose:
		 * The problem is that this code is finding the best solution for math function. For adjust this algorithm for our problem we first need to added an array the will store objects of distribution or their indexes that is synchronised with the primitive array Foods, it's mean that for example distribution number 4 it will store all his parameters values in Foods[4].
		 * Then in function MemorizeBestSource() we should added to GlobalParams[j] a reference for the distribution that he saves his parameters. and we need to add this reference to GlobalMin too (make them a instance of a class instead of primitive objects).
		 * Then in SendEmployedBees we generate kind of random parameters for neighbour distribution the is close to the one of our distribution's parameters. So we need to find the distribution that his parameter's values are the closet to that neightbour's parameters and we will set change that neightbour parameters to be as that closest distribution and we will save a reference to that distribution (please use instances of a class the store a list of parameters and a reference to distribution)
		 * Then we will get a mutant neighbour (distribution mutant) that he is really exist and his parameters are his parameters and not some random ones.
		 * Then in CalculateProbabilities we keep it the same, then in SendOnlookerBees we should do the same changes as we did in SendEmployedBees because it performs in the same way.
		 * Then in SendScoutBees we discard the distribution that his parameters are giving a minimum values that is always staying in the algorithm. ITs means that we want to get rid of that distribution that is blocking that random flow of the algorithm because it has minimum values that is keep him inside the algirthm.
		  */
		
		for(run=0;run<bee.runtime;run++)
		{
		bee.initial();//Initialise each distribution's fitness with a random value.
		bee.MemorizeBestSource();//save the best distribution's value.
		for (iter=0;iter<bee.maxCycle;iter++)
		    {
			bee.SendEmployedBees();//employed bee phase - artificially employed bee generates a random solution that is a mutant of the original solution and replace it with the current solution if it have higher fitness (solution = distribution), and this is perform for each distribution*/
			bee.CalculateProbabilities();//Transfer fitness value to probability (still higher probability is better solution).
			bee.SendOnlookerBees();//Those bees perform the same operation as employed bees, they will create a mutants from each distribution and will replace his parameters with the tested distribution if it's have higher fitness. 
			bee.MemorizeBestSource();//save in globalMin the parameters of the distribution with the higher fitness.
			bee.SendScoutBees();//discard an distribution with minimum values for his parameter because we want to keep the random process.
		    }
		for(j=0;j<bee.D;j++)//bee.D is the number of parameters.
		{
			//System.out.println("GlobalParam[%d]: %f\n",j+1,GlobalParams[j]);
			System.out.println("GlobalParam["+(j+1)+"]:"+bee.GlobalParams[j]);//print the probabilities of each parameters of the distribution with the minimum values for his parameters.
		}
		//System.out.println("%d. run: %e \n",run+1,GlobalMin);
		System.out.println((run+1)+".run:"+bee.GlobalMin);
		bee.GlobalMins[run]=bee.GlobalMin;//print the minimum f value for the best distribution to chosed based on his parameters. lower f value mean higher fitness and that mean higher probabilty. f value will be in our case:  return sol[0]*1 + sol[1]*2 +sol[2]*3 + .. + sol[D]*(D-1), when each sol[i] is the f value of parameter number i for the distribution with higher probability to be choosed. lower f value mean higher fitness and that mean higher probabilty.  
		//Here we need to add that we will save the distribution with the minimum GlobalMin from all the iteration.
		mean=mean+bee.GlobalMin;//sum all the minimums of f values for each iteration. 
		}
		mean=mean/bee.runtime;//This return average value of all best solutions from all the runs, we don't need this.
		//here we need to print the distribution with the lowest GlobalMin value.
		//System.out.println("Means of %d runs: %e\n",runtime,mean);
		System.out.println("Means  of "+bee.runtime+"runs: "+mean);
		
	}

}

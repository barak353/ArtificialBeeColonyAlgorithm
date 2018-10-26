import java.lang.Math;

public  class beeColony {



	/* Control Parameters of ABC algorithm*/
	int NP=20; /* The number of colony size (employed bees+onlooker bees)*/
	int FoodNumber = NP/2; /*The number of food sources equals the half of the colony size*/
	int limit = 100;  /*A food source which could not be improved through "limit" trials is abandoned by its employed bee*/
	int maxCycle = 2500; /*The number of cycles for foraging {a stopping criteria}*/

	/* Problem specific variables*/
	int D = 100; /*The number of parameters of the problem to be optimized*/
	double lb = -5.12; /*lower bound of the parameters. */
	double ub = 5.12; /*upper bound of the parameters. lb and ub can be defined as arrays for the problems of which parameters have different bounds*/
/*
 * food sources = Deliveries distribution to a specific courier/
 * 
 * 
 */

	int runtime = 30;  /*Algorithm can be run many times in order to see its robustness*/

	int dizi1[]=new int[10];
	double Foods[][]=new double[FoodNumber][D];        /*Foods is the population of food sources. Each row of Foods matrix is a vector holding D parameters to be optimized. The number of rows of Foods matrix equals to the FoodNumber*/
	double f[]=new double[FoodNumber];        /*f is a vector holding objective function values associated with food sources */
	double fitness[]=new double[FoodNumber];      /*fitness is a vector holding fitness (quality) values associated with food sources*/
	double trial[]=new double[FoodNumber];         /*trial is a vector holding trial numbers through which solutions can not be improved*/
	double prob[]=new double[FoodNumber];          /*prob is a vector holding probabilities of food sources (solutions) to be chosen*/
	double solution[]=new double[D];            /*New solution (neighbour) produced by v_{ij}=x_{ij}+\phi_{ij}*(x_{kj}-x_{ij}) j is a randomly chosen parameter and k is a randomlu chosen solution different from i*/
	
                   
	double ObjValSol;              /*Objective function value of new solution*/
	double FitnessSol;              /*Fitness value of new solution*/
	int neighbour, param2change;                   /*param2change corrresponds to j, neighbour corresponds to k in equation v_{ij}=x_{ij}+\phi_{ij}*(x_{kj}-x_{ij})*/

	double GlobalMin;                       /*Optimum solution obtained by ABC algorithm*/
	double GlobalParams[]=new double[D];                   /*Parameters of the optimum solution*/
	double GlobalMins[]=new double[runtime];            
	         /*GlobalMins holds the GlobalMin of each run in multiple runs*/
	double r; /*a random number in the range [0,1)*/

	/*a function pointer returning double and taking a D-dimensional array as argument */
	/*If your function takes additional arguments then change function pointer definition and lines calling "...=function(solution);" in the code*/


//	typedef double (*FunctionCallback)(double sol[D]);  

	/*benchmark functions */

//	double sphere(double sol[D]);
//	double Rosenbrock(double sol[D]);
//	double Griewank(double sol[D]);
//	double Rastrigin(double sol[D]);

	/*Write your own objective function name instead of sphere*/
//	FunctionCallback function = &sphere;

	/*Fitness function*/
	double CalculateFitness(double fun) 
	 {
		 double result=0;
		 if(fun>=0)
		 {
			 result=1/(fun+1);
		 }
		 else
		 {
			 
			 result=1+Math.abs(fun);
		 }
		 return result;
	 }

	/*The best food source is memorised*/
	void MemorizeBestSource() // will save in GlobalMin the f value of the best distribution (f in our case can be sol[0]*1 + sol[1]*2 +...).
	{
	   int i,j;
	    
		for(i=0;i<FoodNumber;i++)
		{
		if (f[i]<GlobalMin)//instead of performing fitness > GlobalMax we perform a more intuitive compression, because fitness if equal to 1/f. 
			{
	        GlobalMin=f[i];//GlobalMin will save the f value of the best distribution, because lower f value means higher fitness because fitness = 1/f and higher fitness mean higher probability
	        //we need to add here that we will store the distribution in index i itself.
	        for(j=0;j<D;j++)
	           GlobalParams[j]=Foods[i][j];
	        }
		}
	 }

	/*Variables are initialized in the range [lb,ub]. If each parameter has different range, use arrays lb[j], ub[j] instead of lb and ub */
	/* Counters of food sources are also initialized in this function*/
	//Initialize the parameters of food source number index with the value in range [lb,ub].  
	void init(int index)
	{//Important: We have list of all deliveries that are valid for this courier, and we need to give each delivery an index and we will randomise D indexes for that courier, that means we randomise to each courier D deliveries. 
	   int j;
	   for (j=0;j<D;j++)//remove this we don't want to random parameters.
			{
	        r = (   (double)Math.random()*32767 / ((double)32767+(double)(1)) );//random number between 0 to 1.
	        Foods[index][j]=r*(ub-lb)+lb;
			solution[j]=Foods[index][j];
			}
		f[index]=calculateFunction(solution);//will store value the evaluate the fitness of food source/distribution number index (higher is batter for choosing this distribution for this courier).
		fitness[index]=CalculateFitness(f[index]);//Just perform 1/f[index] so f[index] should be higher if this distribution is worser.
		trial[index]=0;//initialise number of improved for this distribution that will be fail latter on.
	}

	/*All food sources are initialised */
	void initial()
	{
		int i;
		for(i=0;i<FoodNumber;i++)
		{
		init(i);
		}
		GlobalMin=f[0];//set an initial value for the best distribution.
	    for(i=0;i<D;i++)
	    GlobalParams[i]=Foods[0][i];//parameters of that initial value.
	}
	/* In this function an artificially employed bee generates a random solution that is a mutant of the original solution.
	 * for each distribution we calculate a mutant that is consist from the current distribution in the the current iteration and a random neighbour,
	 * and then we check if the mutant give a batter number from the fitness function, if so we take the mutant distribution parameter's instead of the current distribution.
	 * So we start with randomly choosed distributions list and after this function we get a better fitness for each distribution. Higher fitness mean batter probability for the distribution to be choosed.
	 */
	void SendEmployedBees()
	{
	  int i,j;
	  /*Employed Bee Phase
	   * Employed foragers share their information with a probability proportional to the profitability of the food source, and the sharing of this information through waggle dancing is longer in duration.
	   */
	   for (i=0;i<FoodNumber;i++)//for each distribution .
	        {
	        /*In each distribution the parameter to be changed is determined randomly*/
	        r = ((double) Math.random()*32767 / ((double)(32767)+(double)(1)) );
	        param2change=(int)(r*D);//parameter index to be changed.
	        
	        /*A randomly chosen solution is used in producing a mutant solution of the solution i*/
	        r = (   (double)Math.random()*32767 / ((double)(32767)+(double)(1)) );
	        neighbour=(int)(r*FoodNumber);//we will change parameter number param2change based on corresponding parameter of distribution number neighbour.

	        /*Randomly selected solution must be different from the solution i*/        
	       // while(neighbour==i)
	       // {
	       // r = (   (double)Math.random()*32767 / ((double)(32767)+(double)(1)) );
	       // neighbour=(int)(r*FoodNumber);
	       // }
	        for(j=0;j<D;j++)
	        solution[j]=Foods[i][j];//save the list of parameters value of distribution number i.

	        /*v_{ij}=x_{ij}+\phi_{ij}*(x_{kj}-x_{ij}) */
	        //
	        r = (   (double)Math.random()*32767 / ((double)(32767)+(double)(1)) );//random value between 0 to 1 to idicate how much from the neighbour parameter value's to take for making a mutant in parameter number param2change in distribution number i.
	        solution[param2change]=Foods[i][param2change]+(Foods[i][param2change]-Foods[neighbour][param2change])*(r-0.5)*2;//change parameter number param2change of distribution i to be a mutant that depend on the current parameter value and on the randomly chosen neighbour parameter's

	        /*if generated parameter value is out of boundaries, it is shifted onto the boundaries*/
	        if (solution[param2change]<lb)
	           solution[param2change]=lb;
	        if (solution[param2change]>ub)
	           solution[param2change]=ub;
	        ObjValSol=calculateFunction(solution);//get the number that represent how bad this distribution.
	        FitnessSol=CalculateFitness(ObjValSol);//perform 1/ObjValSol to get the fitness to choose this distribution to our solution because higher fitness is batter solution.
	        
	        /*a greedy selection is applied between the current solution i and its mutant*/
	        if (FitnessSol>fitness[i])//FitnessSol is a mutatnt distribution that was created from a mix with the current distribution and some random neighbour's distribution.
	        {
	        /*If the mutant solution is better than the current solution i, replace the solution with the mutant and reset the trial counter of solution i*/
	        trial[i]=0;
	        for(j=0;j<D;j++)
	        Foods[i][j]=solution[j];//copy the mutant parameter to replace the current distribution.
	        f[i]=ObjValSol;//save the number that represent how bad this distribution is.
	        fitness[i]=FitnessSol;//save the number that represent how good this distribution is.
	        }
	        else
	        {   /*if the solution i can not be improved, increase its trial counter*/
	            trial[i]=trial[i]+1;//we keep the number of failing to increase a distribution for each distribution.
	        }


	        }

	 // end of employed bee phase - artificially employed bee generates a random solution that is a mutant of the original solution and replace it with the current solution if it have higher fitness (solution = distribution), and this is perform for each distribution*/
	}

	/* A food source is chosen with the probability which is proportioal to its quality*/
	/*Different schemes can be used to calculate the probability values*/
	/*For example prob(i)=fitness(i)/sum(fitness)*/
	/*or in a way used in the metot below prob(i)=a*fitness(i)/max(fitness)+b*/
	/*probability values are calculated by using fitness values and normalized by dividing maximum fitness value*/
	void CalculateProbabilities()
	{
	     int i;
	     double maxfit;
	     maxfit=fitness[0];
	  for (i=1;i<FoodNumber;i++)
	        {
	           if (fitness[i]>maxfit)
	           maxfit=fitness[i];
	        }

	 for (i=0;i<FoodNumber;i++)
	        {
	         prob[i]=(0.9*(fitness[i]/maxfit))+0.1;
	        }

	}

	void SendOnlookerBees()
	{
/*
 * An onlooker on the dance floor, decides to employ herself at the most profitable source. 
 * There is a greater probability of onlookers choosing more profitable sources since more information is circulated about the more profitable sources.
 */
	  int i,j,t;
	  i=0;
	  t=0;
	  /*onlooker Bee Phase*/
	  while(t<FoodNumber)
	        {

	        r = (   (double)Math.random()*32767 / ((double)(32767)+(double)(1)) );
	        if(r<prob[i]) /*choose a food source depending on its probability to be chosen*/
	        {        
	        t++;
	        
	        /*The parameter to be changed is determined randomly*/
	        r = ((double)Math.random()*32767 / ((double)(32767)+(double)(1)) );
	        param2change=(int)(r*D);//parameter index to be changed.
	        
	        /*A randomly chosen solution is used in producing a mutant solution of the solution i*/
	        r = (   (double)Math.random()*32767 / ((double)(32767)+(double)(1)) );
	        neighbour=(int)(r*FoodNumber);//we will change parameter number neighbour.

	        /*Randomly selected solution must be different from the solution i*/        
	        while(neighbour == i)
	        {
	        	//System.out.println(Math.random()*32767+"  "+32767);
	        r = (   (double)Math.random()*32767 / ((double)(32767)+(double)(1)) );
	        neighbour=(int)(r*FoodNumber);//we will change parameter number param2change based on corresponding parameter of distribution number neighbour.
	        }
	        for(j=0;j<D;j++)
	        solution[j]=Foods[i][j];//save the list of parameters value of distribution number i.

	        /*v_{ij}=x_{ij}+\phi_{ij}*(x_{kj}-x_{ij}) */
	        r = (   (double)Math.random()*32767 / ((double)(32767)+(double)(1)) );//random value between 0 to 1 to idicate how much from the neighbour parameter value's to take for making a mutant in parameter number param2change in distribution number i.
	        solution[param2change]=Foods[i][param2change]+(Foods[i][param2change]-Foods[neighbour][param2change])*(r-0.5)*2;//change parameter number param2change of distribution i to be a mutant that depend on the current parameter value and on the randomly chosen neighbour parameter's

	        /*if generated parameter value is out of boundaries, it is shifted onto the boundaries*/
	        if (solution[param2change]<lb)
	           solution[param2change]=lb;
	        if (solution[param2change]>ub)
	           solution[param2change]=ub;
	        ObjValSol=calculateFunction(solution);//get the number that represent how bad this distribution.
	        FitnessSol=CalculateFitness(ObjValSol);//perform 1/ObjValSol to get the fitness to choose this distribution to our solution because higher fitness is batter solution.
	        
	        /*a greedy selection is applied between the current solution i and its mutant*/
	        if (FitnessSol>fitness[i])
	        {
	        /*If the mutant solution is better than the current solution i, replace the solution with the mutant and reset the trial counter of solution i*/
	        trial[i]=0;
	        for(j=0;j<D;j++)
	        Foods[i][j]=solution[j];//copy the mutant parameter to replace the current distribution.
	        f[i]=ObjValSol;//save the number that represent how bad this distribution is.
	        fitness[i]=FitnessSol;//save the number that represent how good this distribution is.
	        }
	        else
	        {   /*if the solution i can not be improved, increase its trial counter*/
	            trial[i]=trial[i]+1;//we keep the number of failing to increase a distribution for each distribution, it's include the trails from the employed bees.
	        }
	        } /*if */
	        i++;
	        if (i==FoodNumber)
	        i=0;
	        }/*while*/

	        /*end of onlooker bee phase     */
	}

	/*determine the food sources whose trial counter exceeds the "limit" value. In Basic ABC, only one scout is allowed to occur in each cycle
	 * The scouts carry out a random search process for discovering new food sources.
	 * trail - if the solution is can not be improved, increase its trial counter
	 */
	void SendScoutBees()
	{
	int maxtrialindex,i;
	maxtrialindex=0;
	for (i=1;i<FoodNumber;i++)
	        {
	         if (trial[i]>trial[maxtrialindex])
	         maxtrialindex=i;
	        }
	if(trial[maxtrialindex]>=limit) //maxtrialindex is the index of the distribution with the maximum trails, initialise this distribution's parameter with random values.
	{
		init(maxtrialindex);//The reason we perform this initialise and override this distribution with minimum values for his parameters its because we don't want to stuck with the same distribution and we want to keep the random search process.
	}
	}


	
	

double calculateFunction(double sol[])
{
return Rastrigin (sol);	
}
	double sphere(double sol[])
	{
	int j;
	double top=0;
	for(j=0;j<D;j++)
	{
	top=top+sol[j]*sol[j];
	}
	return top;
	}

	double Rosenbrock(double sol[])
	{
	int j;
	double top=0;
	for(j=0;j<D-1;j++)
	{
	top=top+100*Math.pow((sol[j+1]-Math.pow((sol[j]),(double)2)),(double)2)+Math.pow((sol[j]-1),(double)2);
	}
	return top;
	}

	 double Griewank(double sol[])
	 {
		 int j;
		 double top1,top2,top;
		 top=0;
		 top1=0;
		 top2=1;
		 for(j=0;j<D;j++)
		 {
			 top1=top1+Math.pow((sol[j]),(double)2);
			 top2=top2*Math.cos((((sol[j])/Math.sqrt((double)(j+1)))*Math.PI)/180);

		 }	
		 top=(1/(double)4000)*top1-top2+1;
		 return top;
	 }

	 double Rastrigin(double sol[])//sol is array of values in the range [lb,ub] that represent a distribution parameters and it return a number that represent how bad this distribution, higher number means the distribution is not good to take in our solution, lower number means that this is batter distribution to take in the solution.
	 {
		 int j;
		 double top=0;

		 for(j=0;j<D;j++)
		 {
			 top=top+(Math.pow(sol[j],(double)2)-10*Math.cos(2*Math.PI*sol[j])+10);
			 /*
			  * Firstly we want to change their function and use our function because we have parameter in different importance. To do that we will use this function instead: return sol[0]*1 + sol[1]*2 +sol[2]*3 + .. + sol[D]*(D-1). It should return higher number if we want to give this distribution lower probability to be chose. 
			  * We will define each parameter and will we set sol[0] to be with the lower importance and sol[D] with the higher importance, we should normalise each parameter to be in the range between 0 to 1.
			  * for example we can set parameter sol[0] to be the total distance that the courier should do on order to ending all the deliveries to their destinations, and sol[2] should be more important parameter that we want to give him higher importance. For example, sol[2] will be the percentage of urgent deliveries from the total deliveries in the distribution. sol[3] should be more important parameter such as the percentage of deliveries that are in the courier's perferres area.
			  */
		 }
		 return top;
	 }
}

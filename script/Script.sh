#!/bin/bash

cd ..
cat /dev/null > params/ltlgen.params



if [ -n "$1" ]
then

	if [[ $1 == "--help" ]]
	then
		echo -e "Default settings: \nalgorithm = spea2 \npopulations = 500 \ngenerations 50 \nfitnesses = 2 ComplexityFitness and CorrectnessFitness"
		echo ""
		echo "Usage: [OPTIONS]"
		echo "--help								descriptions of the options"
		echo "--algorithm [options]						chose one of the algorithm  [spea2 / nsga]"
		echo "--pop <int>							number of populations"
		echo "--generations	<int>						number of generations"
		echo "--fitness <int> [options_1] ... [options_n]			chose the fitness functions [mut / rand / scen / template]"

	else	
		echo "Non Default configuration"
		algo="parent.0 = ltlgen-spea2.params"
		subpop="\npop.subpop.0.size = 500"
		gen="generations = 50"
		fitnum="\nmulti.fitness.num-objectives = 2"
		fit="\neval.problem.fitness.0 = ltlgen.fitnesses.ComplexityFitness
					\neval.problem.fitness.0.threshold = 0.04
					\neval.problem.fitness.1 = ltlgen.fitnesses.CorrectnessFitness"




		while [ -n "$1" ]
		do
			#echo "Gloabaly use $1"
			case "$1" in

			--algorithm) 
				#echo "Found the -algorithm option" 
				param="$2"
				#echo "param == $param"
				if [[ $param == "nsga" ]]
				then
					algo="parent.0 = ltlgen-nsga2.params"
					#echo -e "parent.0 = ltlgen-nsga2.params" >> test1.params
				elif [[ $param == "spea2" ]]
				then
					algo="parent.0 = ltlgen-spea2.params"
					#echo -e "parent.0 = ltlgen-spea2.params" >> test1.params
				else
					echo "can't find $param /n algorithm will be Default = spea2 "
					#echo -e "parent.0 = ltlgen-spea2.params" >> test1.params
				fi
				shift;;

			--pop) 
				#echo "Found the --pop option"
				re='^[0-9]+$'
				param="$2"
				#echo "param == $param"

				if ! [[ $param =~ $re ]] ; then
	   				echo "error: $param  Not a number"; 
				else
                    subpop="\npop.subpop.0.size = $param"
				fi
				shift;;

			--generations) 
				#echo  "Found the --generations option"
					re='^[0-9]+$'
				param="$2"
				#echo "param == $param"

				if ! [[ $param =~ $re ]] ; then
	   				echo "error: $param Not a number"; 
				else
					if [[ $param -le 1 ]]
					then
						echo -e "$param can't less then 1 \ngenerations 50 by Default"
						#echo -e "\ngenerations = 50" >> test1.params
					else
						gen="\ngenerations = $param"
						#echo -e "\ngenerations = $param" >> test1.params
					fi
					
				fi
				shift;;
			# i need to do to select fitness functions [ mut scen rand template]
			# i also need to make --help with descriptions 
			# last thing what i need to do it's rename ltlgen.* --> params/ltlgen.*
			--fitness) 
				#echo  "Found the --fintess option"
					re='^[0-9]+$'
				param="$2"
				#echo "param == $param"

				if ! [[ $param =~ $re ]] ; then
	   				echo "error: $param Not a number"; 
				else
					if  [ $param -le 2 ] || [ $param -ge 7 ]
					then
						echo -e "Our param == $param can't less then 3 or more then 6 \nfitness number 2 by default"
						#echo -e "\nmulti.fitness.num-objectives = 2" >> test1.params
						#echo -e "\neval.problem.fitness.0 = ltlgen.fitnesses.ComplexityFitness
						#		\neval.problem.fitness.0.threshold = 0.04
						#		\neval.problem.fitness.1 = ltlgen.fitnesses.CorrectnessFitness" >> test1.params
						shift
					else
						fitnum="\nmulti.fitness.num-objectives = $param"
						counter=2
						let $((tmp= $param - $counter))
						
						shift
						shift
						for((i=1;i<=$tmp; i++))
						do
							#echo "Step $i"
							#echo "$1"
							case "$1" in
							mut) 
								echo  "Found the MutatedFitness"
								fit+="\n\neval.problem.fitness.$counter = ltlgen.fitnesses.MutatedFitness";;

							rand)
								echo  "Found the RandomFitness"
								fit+="\n\neval.problem.fitness.$counter = ltlgen.fitnesses.RandomFitness";;

							scen)
								echo  "Found the ScenarioFitness"
								fit+="\n\neval.problem.fitness.$counter = ltlgen.fitnesses.ScenarioFitness";;

							template)
								echo  "Found the TemplateFitness"
								fit+="\n\neval.problem.fitness.$counter = ltlgen.fitnesses.TemplateFitness";;

							*) echo "$1 is not an fitness function";;
							esac
							if [[ $i < $tmp ]]
							then
								shift
							fi
							let $((counter= $counter + 1))
							#echo "$1 after case"
						done

						#echo -e "\nmulti.fitness.num-objectives = $param" >> test1.params
						#echo -e "\neval.problem.fitness.0 = tlgen.fitnesses.ComplexityFitness
								#\neval.problem.fitness.0.threshold = 0.04
								#\neval.problem.fitness.1 = ltlgen.fitnesses.CorrectnessFitness" >> test1.params
					fi
					
				fi;;
				#shift;;

			*) echo "$1 is not an option";;

			esac
			shift

			#echo "Gloabaly use $1 after case"
		done
	fi





	echo -e "$algo" >> params/ltlgen.params
	echo -e "\n# \n# General \n#" >> params/ltlgen.params
	echo -e "$subpop" >> params/ltlgen.params
	echo -e "$gen" >> params/ltlgen.params
	echo -e "\n# \n# Fitness \n#" >> params/ltlgen.params
	echo -e "$fitnum" >> params/ltlgen.params
	echo -e "$fit" >> params/ltlgen.params



	#echo -e "\n# \n# Filters\n# \n\neval.problem.filters.number = 1
		 	#	\neval.problem.filters.0 = ltlgen.filters.TemporalFilter " >> test1.params



	echo -e "\n# \n# Filters\n# \n\neval.problem.filters.number = 1
	 		\neval.problem.filters.0 = ltlgen.filters.TemporalFilter " >> params/ltlgen.params



else
	echo "Default configuration "

	echo -e "parent.0 = ltlgen-spea2.params" >> params/ltlgen.params
	echo -e "\n# \n# General \n#" >> params/ltlgen.params
	echo -e "\npop.subpop.0.size = 500" >> params/ltlgen.params
	echo -e "generations = 50" >> params/ltlgen.params
	echo -e "\n# \n# Fitness \n#" >> params/ltlgen.params
	echo -e "\nmulti.fitness.num-objectives = 2" >> params/ltlgen.params
	echo -e "\neval.problem.fitness.0 = ltlgen.fitnesses.ComplexityFitness
			\neval.problem.fitness.0.threshold = 0.04
			\neval.problem.fitness.1 = ltlgen.fitnesses.CorrectnessFitness" >> params/ltlgen.params

	echo -e "\n# \n# Filters\n# \n\neval.problem.filters.number = 1
	 		\neval.problem.filters.0 = ltlgen.filters.TemporalFilter " >> params/ltlgen.params
fi



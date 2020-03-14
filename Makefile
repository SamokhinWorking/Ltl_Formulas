JAVAC = javac ${JAVACFLAGS} 

JAVACFLAGS = ${FLAGS} ${RESOURCES}
FLAGS = 

JAVA = java ${JAVAFLAGS}

JAVAFLAGS = ${JFLAGS} ${RESOURCES}
JFLAGS =


RESOURCES = -cp resources/ecj-26.jar:. 

DIRS = ltlgen/*.java \
       ltlgen/fitnesses/*.java \
       ltlgen/filters/*.java \
       ltlgen/formulas/*.java \
       ltlgen/formulas/input/*.java \
       ltlgen/formulas/output/*.java \
       ltlgen/formulas/predicate/*.java \
       automat/*.java \
       helpClass/*.java \
       parse/*.java \
       smv_model/*.java \
       tree/*.java \
       verifier/*.java \
       ltlgenCondition/*.java \
       ltlgenCondition/fitnesses/*.java \
       ltlgenCondition/formulas/*.java 
   
all:
	${JAVAC} ${DIRS}

run: all
	${JAVA} ec.Evolve -file params/ltlgen.params

clean:
	find automat -name "*.class" -exec rm -f {} \;
	find ltlgen -name "*.class" -exec rm -f {} \;
	find parse -name "*.class" -exec rm -f {} \;
	find helpClass -name "*.class" -exec rm -f {} \;
	find smv_model -name "*.class" -exec rm -f {} \;
	find tree -name "*.class" -exec rm -f {} \;
	find verifier -name "*.class" -exec rm -f {} \;
	find ltlgenCondition -name "*.class" -exec rm -f {} \;
	
	

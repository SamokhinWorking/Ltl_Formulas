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
       automaton/*.java \
       tree/*.java \
       tree/parser/*.java \
       tree/ast/*.java \
       tree/ast/nonterminal/*.java \
       tree/ast/terminal/*.java \
       verifier/*.java 
   
all:
	${JAVAC} ${DIRS}

run: all
	${JAVA} ec.Evolve -file params/ltlgen.params

clean:
	find automaton -name "*.class" -exec rm -f {} \;
	find ltlgen -name "*.class" -exec rm -f {} \;
	find tree -name "*.class" -exec rm -f {} \;
	find verifier -name "*.class" -exec rm -f {} \;

	
	

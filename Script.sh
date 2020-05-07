#!/bin/bash

make clean
make 


for((i=3;i<=35;i++))
do
	
	echo "number is $i"
	java ec.Evolve -file params/ltlgen$i.params -p "jobs="+10
	cp -a /home/vlasam/Downloads/LTL/output/. /home/vlasam/Downloads/LTL/Tests/$i
	echo "copy $i complite"

done

make clean
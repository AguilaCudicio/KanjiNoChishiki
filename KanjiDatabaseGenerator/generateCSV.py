# -*- coding: utf-8 -*-  

import csv 

newfile = open('kanjidic.csv', 'w')
f = open('kanjidic-utf8.txt', 'r')
discard = ["0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F","G","H",
"I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"]
"In the file, S indicates stroke count, G grade and J JLPT level"
for line in f:
	line= line.rstrip()
	readings = []
	meanings = []
	strokeCount = "0"
	grade = "0"
	jlpt = "0"
	elementlist = line.split(" ")
	for element in elementlist[2:]:
		if element[0] == 'S':
			stroke = element[1:]
		elif element[0] == 'G':
			grade = element[1:]
		elif element[0] == 'J':
			jlpt = element[1:]
		elif element[0] == '{':
			meanings.append(element[1:-1])
		elif not element[0] in discard:
			readings.append(element)
		reading = ' '.join(readings)
		meaning = ' '.join(meanings)
	kanjiInfoList= [elementlist[0],strokeCount,grade,jlpt,reading,meaning]
	wr = csv.writer(newfile, quoting=csv.QUOTE_ALL)
	wr.writerow(kanjiInfoList)
f.close()
newfile.close()

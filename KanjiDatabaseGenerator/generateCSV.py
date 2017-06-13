# -*- coding: utf-8 -*-  
#!/usr/bin/python


import csv 
import re

"Only read 6355 kanjis (EDICT 1)... change this number if you want more than that"
kanjiNumber=6354

def getKanjiInfo(line):
	line= line.rstrip()
	readings = []
	meanings = re.findall('\{.*?\}',line)
	strokeCount = "0"
	grade = "0"
	jlpt = "0"
	elementlist = line.split(" ")
	for element in elementlist[2:]:
		if element[0] == 'S':
			strokeCount = element[1:]
		elif element[0] == 'G':
			grade = element[1:]
		elif element[0] == 'J':
			jlpt = element[1:]
		elif not element[0] in discard:
			readings.append(element)
		reading = ' '.join(readings)
		meaning = ' '.join(meanings)
	kanjiInfoList= [elementlist[0],strokeCount,grade,jlpt,reading,meaning]
	return kanjiInfoList

def writeJouyouKanji(file,output):
	for i, line in enumerate(file):
		if i > kanjiNumber:
     		   	break
		kanjiInfoList = getKanjiInfo(line)
		"If this character is grade 8 or below, it's classified as Jouyou Kanji"
		"We discard grade 0 kanji as they are not classified, therefore not Jouyou"
		if float(kanjiInfoList[2]) > 0 and float(kanjiInfoList[2]) < 9 :
			wr = csv.writer(output)
			wr.writerow(kanjiInfoList)

def writeAdvancedKanji(file,output):
	for i, line in enumerate(file):
		if i > kanjiNumber:
     		   	break
		kanjiInfoList = getKanjiInfo(line)
		"If this character is grade 9 or above, it isn't classified as Jouyou Kanji"
		"We also need grade 0 kanji, since those are not classified"
		if float(kanjiInfoList[2]) == 0 or float(kanjiInfoList[2]) > 8 :
			wr = csv.writer(output)
			wr.writerow(kanjiInfoList)




newfile = open('kanjidic.csv', 'w')
f = open('kanjidic-utf8.txt', 'r')
discard = ["0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F","G","H", "I","J","K","L","M","N","O",
"P","Q","R","S","T","U","V","W","X","Y","Z",
"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q",
"r","s","t","u","v","w","x","y","z","{"]
"In the dictionary, S indicates stroke count, G grade and J JLPT level"

kanjiFields= ["symbol","strokes","grade","jlpt","readings","meanings"]
wr = csv.writer(newfile)
wr.writerow(kanjiFields)
"Let's write Jouyou kanji first. It sould write 2135 lines"
writeJouyouKanji(f,newfile)
f.seek(0, 0)
"Then we go on and append the rest of the characters in the end"
writeAdvancedKanji(f,newfile)
f.close()
newfile.close()

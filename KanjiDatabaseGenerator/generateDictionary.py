# -*- coding: utf-8 -*-  
#!/usr/bin/python

import pickle
import re
from collections import defaultdict

f = open('dictionary.txt', 'w')
dictionary = defaultdict(list)
fileK = open('kanjidic.csv', 'r')
for i, line in enumerate(fileK):
	if (i!=0):
		meanings = line.split(",")[5]
		meaningP = re.findall('\{.*?\}',meanings)
		for meaning in meaningP:
			words = meaning[1:-1].split(" ")
			for word in words:
				if ("(" not in word and ")" not in word):
					dictionary[word].append(i-1)




for key in dictionary:
	string = key+":::"
	for element in dictionary[key]:
		string=string+(str(element)+" ")
	f.write(string+"\n")

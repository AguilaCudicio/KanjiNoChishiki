# -*- coding: utf-8 -*-  
#!/usr/bin/python

import pickle
import re
from collections import defaultdict

hiragana = ["あ", "い", "う", "え", "お" ,"ゃ", "ゅ" , "ょ", "ゎ", "か", "き", "く", "け", "こ", "きゃ", "きゅ", "きょ", "くゎ", "さ", "し", "す", "せ", "そ", "しゃ", "しゅ", "しょ", "た", "ち", "つ", "て", "と", "ちゃ", "ちゅ", "ちょ", "な", "に", "ぬ", "ね", "の", "にゃ", "にゅ", "にょ", "は", "ひ", "ふ", "へ", "ほ", "ひゃ", "ひゅ", "ひょ", "ま", "み", "む", "め", "も", "みゃ", "みゅ", "みょ", "や", "ゆ", "よ", "ら", "り", "る" , "れ", "ろ" , "りゃ" , "りゅ", "りょ", "わ", "ゐ", "ゑ" , "を" , "ん", "が", "ぎ", "ぐ", "げ", "ご", "ぎゃ", "ぎゅ" , "ぎょ", "ぐゎ", "ざ", "じ" , "ず", "ぜ", "ぞ", "じゃ", "じゅ", "じょ", "だ", "ぢ", "づ", "で", "ど", "ぢゃ", "ぢゅ", "ぢょ", "ば", "び" , "ぶ", "べ", "ぼ", "びゃ", "びゅ", "びょ", "ぱ", "ぴ" , "ぷ" , "ぺ", "ぽ", "ぴゃ", "ぴゅ" , "ぴょ"]

katakana = ["ア","イ","ウ", "エ","オ","ャ","ュ","ョ","ヮ","カ","キ","ク","ケ","コ", "キャ","キュ","キョ", "クヮ", "サ" , "シ", "ス" , "セ", "ソ", "シャ", "シュ", "ショ", "タ", "チ", "ツ", "テ", "ト", "チャ", "チュ", "チョ" , "ナ" , "ニ", "ヌ", "ネ", "ノ", "ニャ", "ニュ" , "ニョ", "ハ", "ヒ", "フ", "ヘ", "ホ", "ヒャ" , "ヒュ" , "ヒョ", "マ", "ミ", "ム" , "メ" , "モ", "ミャ" , "ミュ", "ミョ" , "ヤ", "ユ", "ヨ" ,  "ラ", "リ", "ル", "レ", "ロ", "リャ" , "リュ", "リョ", "ワ" , "ヰ" , "ヱ" , "ヲ", "ン" , "ガ" ,"ギ", "グ", "ゲ", "ゴ" , "ギャ", "ギュ" , "ギョ", "グヮ", "ザ", "ジ" , "ズ", "ゼ", "ゾ" , "ジャ" , "ジュ" , "ジョ", "ダ", "ヂ", "ヅ", "デ", "ド", "ヂャ" , "ヂュ", "ヂョ", "バ", "ビ", "ブ", "ベ" , "ボ", "ビャ", "ビュ", "ビョ", "パ", "ピ", "プ", "ペ", "ポ", "ピャ", "ピュ", "ピョ"]

def katakanaToHiragana(string):
	if (len(string) > 1):
		pos=2
		strAux = string[0:2]
		if ( not (strAux in katakana) ):
			strAux = string[0]
			pos=1
	elif (len(string) > 0):
		strAux = string[0]
		pos=1
	else:
		return ""
	if (strAux in katakana):
		positionInList = katakana.index(strAux)
		return hiragana[positionInList] + katakanaToHiragana(string[pos:])
	else:
		return strAux + katakanaToHiragana(string[pos:])



f = open('dictionary.txt', 'w')
freadings = open('readings.txt', 'w')
dictionary = defaultdict(list)
dictionaryReadings = defaultdict(list)
fileK = open('kanjidic.csv', 'r')
for i, line in enumerate(fileK):
	if (i!=0):
		lineRead = line.split(",")
		meanings = lineRead[5]
		readings = lineRead[4].split(" ")

		for reading in readings:
			read = reading
			if ("." in reading):
				positionPoint = reading.index(".")
				read = reading[0:positionPoint]
			syllable  = katakanaToHiragana(read).replace("-","")
			if (not i-1 in dictionaryReadings[syllable]):
				dictionaryReadings[syllable].append(i-1)
				
			
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

for read in dictionaryReadings:
	string = read+":::"
	for element in dictionaryReadings[read]:
		string=string+(str(element)+" ")
	freadings.write(string+"\n")

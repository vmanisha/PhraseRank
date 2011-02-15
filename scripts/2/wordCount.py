from PorterStemmer import PorterStemmer
import sys
import os
import operator
stopList = []

def loadStopWords(stopFile):
	sFile=open(stopFile,"r")	
	stopList=sfile.read().split("\n")
	sFile.close()

def getWordCount(inputFile):
	iFile=open(inputFile,"r")
	data=iFile.read().split("\n")
	iFile.close()
	for line in data:
				
	

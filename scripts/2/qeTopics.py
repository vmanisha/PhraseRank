#get words for query and expansion on the basis of topics ..
from PorterStemmer import PorterStemmer
import sys
import os
import operator
def loadDocTopics(inputFile):
	ifile=open(inputFile,"r")
	data=ifile.read().split("\n")
	topicMapping={} #docid, #list of the topics
	patent=""
	total=0
	for line in data:
		if len(line) > 2 and line[0]!="#":
			split=line.split(" ")
			i=0
			total=0
			for value in split:
				if i == 1:
					patent=value[value.rfind("/")+1:]
					#print patent, value
					newList=[]					
					topicMapping[patent]=newList
				elif i % 2 ==0 and i >1:
					#print patent
					#print "value", value, "patent", patent
					#print "split",split[i+1]
					total += float(split[i+1])
					newList=topicMapping[patent]
					if len(newList) < 20:
						newList.append(value)
						topicMapping[patent]=newList
				if total > .90:
					break;
				i += 1
	#print topicMapping
	ifile.close()
	return topicMapping

def loadWords(inputFile):#,topicMapping):
	#p=PorterStemmer()
	ifile=open(inputFile,"r")
	patentWordDict={} #patent no --> word, topicNo | word, topicNo
	wordTopicDict={}
	currPat=""
	oldPat=""
	i=0;
	for line in ifile:
		if line[0] !="#" and len(line) > 2:
			split=line.split(" ")
			#get the word
			word=split[4]
			#word=p.stem(split[4], 0,len(split[4])-1)
			topicNo=split[5].strip()		
			if topicNo in wordTopicDict: #split[5] is the topic no
				wlist=wordTopicDict[topicNo]
				if word in wlist:				
					wlist[word]= wlist[word] + 1 
				else:
					wlist[word]=1			
				wordTopicDict[topicNo]=wlist
				#if word not in wlist:
				#	wlist.append(word)
				
			else:
				wlist={}
				wlist[word]=1
				wordTopicDict[topicNo]=wlist # topicNo --> w1, w2, w3

			currPat=split[1][split[1].rfind("/")+1:]
			if currPat != oldPat:
				#print currPat, oldPat
				if oldPat!="":
					patentWordDict[oldPat]=wordTopicDict
					wordTopicDict={}
				oldPat=currPat
	#print oldPat
	patentWordDict[oldPat]=wordTopicDict
	#print patentWordDict
	ifile.close()
	return patentWordDict

def getTopWordsPerTopic(wordTopicList,topicMapping):
	for k,v in topicMapping.iteritems(): #doc no --> topic1, topic2, 
		print "\n",k,		
		if k in wordTopicList:
			for ele in v: # topic list for a document k
				if ele in wordTopicList[k]:
					wordList=wordTopicList[k][ele];
					#sort the words with the tf
					sorted_x = sorted(wordList.iteritems(),key=operator.itemgetter(1))
					sorted_x.reverse()
					#print "\n",ele,"\t", sorted_x
					i=0
					print "\t",ele,":",
					for words in sorted_x:
						if words[1] > 2:
							print words[0],#+" "+str(words[1])+"\t",
						i += 1
						if i == 40:
							break;
				'''else:
					print "not there", ele'''
	
def main(argv):
	topicMapping=loadDocTopics(argv[1]) #file where the topic distribution for each query patent is given
	wordTopicList=loadWords(argv[2]) #file containing patent with word and topic that word belongs to 
	getTopWordsPerTopic(wordTopicList,topicMapping)

if __name__ == "__main__":
	main(sys.argv);	

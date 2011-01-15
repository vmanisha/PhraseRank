import os
import sys
#load the top combination query for each file by looking up a 
#list for Qno and reading the query from a folder
def findTopQuery(listFile,folder,outputFile):
	listf=open(listFile,"r")
	data=listf.read()
	topList=data.split("\n")

	outf=open(outputFile,"w")
	for line in topList:
		print line
		if "#" not in line and line!="":
			split=line.split()
			#split[0] == patent no
			#split[1] == query no
			file1=open(folder+"/"+split[0],"r")
			dataf=file1.read()
			totalList=dataf.split("\n")
			if "0205" in line:
				print totalList[int(split[1])]
			outf.write("\n"+totalList[int(split[1])])		
			file.close()
	listf.close()
	outf.close()
	

#checking the number of documents missed for each query in result for best combination of phrases
def checkOutput(relFile,outputFile): #relFile == relevance file outputFile = file containing the output
	outf=open(outputFile,"r")
	data=outf.read()
	listo=data.split("\n")

	relDict = {}
	for line in listo:
		if line !="":		
			split=line.split("\t")
			num=split[0]#num=split[2] #query number 0001
			pat=split[1]#pat=split[3] #output patent
			#print split
			if (num in relDict):
				relList=relDict[num]
				#print relList
				if pat not in relList:
					relList.append(pat)
					relDict[num]=relList
			else:
				relList=[pat]
				relDict[num]=relList
		
	#print relDict.keys()
	
	listf=open(relFile,"r")
	data=listf.read()
	topList=data.split("\n")
	score={}
	actualList={}
	for line in topList:
		if line!="":
			split=line.split("\t")
			num=split[0]
			pat=split[2]
			if num in relDict:			
				if num not in score:
					#print num				
					score[num]=0
					actualList[num]=0
				actualList[num] += 1
				relList=relDict[num]
				'''print relList	
				print pat
				print relList.index(pat)
				print pat not in relDict[num]
				break'''
				if pat not in relList:
					#print pat			
					score[num] += 1	
	#print score;	
	for k, v in score.iteritems():
		print k, v, actualList[k]
	listf.close()
	outf.close()

	
def findPercentage(relFile): #results containing the patno no_retr no_total
	listf=open(relFile,"r")
	data=listf.read()
	relList=data.split("\n")
	score={}
	for result in relList:
		if result!="" and result[0]!="#":
			split=result.split(" ")
			#print split
			percent=int((float(split[1])/float(split[2]))*100)
			if percent not in score:
				score[percent]=0;
			score[percent] += 1
	items=score.keys()
	items.sort()
	for k in items:
		print k, score[k]#, actualList[k]
	
	
	
def main(argv):
	#findTopQuery(argv[1],argv[2],argv[3])
	#checkOutput(argv[1],argv[2]) #relFile outputFile
	findPercentage(argv[1])

if __name__ == "__main__":
	main(sys.argv);	

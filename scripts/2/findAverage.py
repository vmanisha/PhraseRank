import os 
import sys

def loadDir(inputDir):
	dirList = os.listdir(inputDir)
	total = {}
	count =0;
	for ele in dirList:
		rfile=open(inputDir+"/"+ele, "r")
		data=rfile.read().split("\n")
		for line in data:
			if len(line) > 2:
				split=line.split("\t")		
				if split[0].strip() in total:
					total[split[0].strip()] = total[split[0].strip()] + float(split[1])	
				else:
					total[split[0].strip()]=float(split[1])
		rfile.close()
		count +=1;
	
	print "\n\n#", inputDir
	for k, v in total.iteritems():
		print k , (v/count)


def iterateDirs(inputDir): #inputDir is the master directory -- > relsb.top from this struct (relsb.top --> tf, idf, kea etc ----> Map, R@10, R@30 ...) 
		dirList = os.listdir(inputDir)
		for entry in dirList:
			subDirList=os.listdir(inputDir+"/"+entry)
			for ele in subDirList:
				if os.path.isdir(inputDir+"/"+entry+"/"+ele) == True:
					loadDir(inputDir+"/"+entry+"/"+ele)

def main(argv):
	iterateDirs(argv[1]) #input Master dir arranged in above format 

if __name__ == "__main__":
	main(sys.argv);	

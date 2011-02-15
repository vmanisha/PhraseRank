import sys
def wordIdMapping(inputFile):
	ifile=open(inputFile,"r")
	data=ifile.read().split("\n")
	wordIdMapping={}
	i=0
	wid=0
	for line in data:
		split=line.split(" ")
		i=0		
		if len(line) >2:
			print "\n |",
			for pair in split:
				pair.strip();
				if pair != "|" and pair != "":
					word=pair[:pair.index(":")]
					number=pair[pair.index(":")+1:]
					#print word,number
					if word in wordIdMapping:
						print str(wordIdMapping[word])+":"+number,
					else:
						print str(wid)+":"+number,
						wordIdMapping[word]=wid;
						wid += 1
				i +=1
	
	ifile.close()
	ofile=open("mapping","w")
	for k,v in wordIdMapping.iteritems():
		ofile.write("\n"+k+" "+str(v));
	ofile.close()

def main(argv):
	wordIdMapping(argv[1]);

if __name__ == "__main__":
	main(sys.argv);	
	
				

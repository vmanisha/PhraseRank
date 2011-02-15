import sys
def main(argv):
	ifile=open(argv[1])
	data=ifile.read().split("\n")
	data=data[0].split(" ")
	print "len ",len(data)
	ifile.close()
	ifile=open(argv[2])
	data=ifile.read().split("\n")
	data=data[0].split(" ")
	print "len2 ",len(data)
	ifile.close()

if __name__ == "__main__":
	main(sys.argv);	

